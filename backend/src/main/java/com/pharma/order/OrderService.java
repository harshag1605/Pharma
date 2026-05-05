package com.pharma.order;

import com.pharma.catalog.MedicineRepository;
import com.pharma.common.AuditService;
import com.pharma.common.BusinessRuleException;
import com.pharma.common.NotFoundException;
import com.pharma.inventory.InventoryService;
import com.pharma.notification.NotificationService;
import com.pharma.order.OrderDtos.CreateOrderRequest;
import com.pharma.order.OrderDtos.OrderDto;
import com.pharma.order.OrderDtos.OrderItemDto;
import com.pharma.order.OrderDtos.PrescriptionOrderRequest;
import com.pharma.order.OrderDtos.SetOrderItemsRequest;
import com.pharma.order.OrderDtos.TransitionOrderRequest;
import com.pharma.prescription.PrescriptionRepository;
import com.pharma.security.CurrentUserService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orders;
  private final MedicineRepository medicines;
  private final PrescriptionRepository prescriptions;
  private final InventoryService inventory;
  private final CurrentUserService currentUser;
  private final OrderStateMachine stateMachine;
  private final AuditService audit;
  private final NotificationService notifications;

  public OrderDto create(CreateOrderRequest request) {
    var patient = currentUser.user();
    var order = new PharmacyOrder();
    order.setPatientId(patient.getId());
    order.setPatient(patient);
    order.setDeliveryAddress(request.deliveryAddress());
    if (request.prescriptionId() != null) {
      order.setPrescription(prescriptions.findById(request.prescriptionId()).orElseThrow(() -> new NotFoundException("Prescription not found")));
    }
    BigDecimal total = BigDecimal.ZERO;
    for (var line : request.items()) {
      var medicine = medicines.findById(line.medicineId()).orElseThrow(() -> new NotFoundException("Medicine not found"));
      if (medicine.isPrescriptionRequired() && order.getPrescription() == null) {
        throw new BusinessRuleException(medicine.getBrandName() + " requires a prescription");
      }
      inventory.reserve(medicine.getId(), line.quantity());
      var item = new OrderItem();
      item.setMedicine(medicine);
      item.setQuantity(line.quantity());
      item.setUnitPrice(medicine.getPrice());
      total = total.add(medicine.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
      order.getItems().add(item);
    }
    order.setTotalAmount(total);
    var saved = orders.save(order);
    notifications.orderUpdate(saved, "Order created and queued for pharmacist verification");
    return dto(saved);
  }

  public OrderDto createPrescriptionRequest(PrescriptionOrderRequest request) {
    var patient = currentUser.user();
    var prescription = prescriptions.findById(request.prescriptionId()).orElseThrow(() -> new NotFoundException("Prescription not found"));
    if (!prescription.getPatient().getId().equals(patient.getId())) {
      throw new BusinessRuleException("Prescription does not belong to current patient");
    }
    var order = new PharmacyOrder();
    order.setPatientId(patient.getId());
    order.setPatient(patient);
    order.setPrescription(prescription);
    order.setDeliveryAddress(request.deliveryAddress());
    order.setTotalAmount(BigDecimal.ZERO);
    var saved = orders.save(order);
    notifications.orderUpdate(saved, "Prescription-only request created for pharmacist review");
    return dto(saved);
  }

  public Page<OrderDto> mine(Pageable pageable) {
    return orders.findByPatientId(currentUser.user().getId(), pageable).map(this::dto);
  }

  public Page<OrderDto> all(Pageable pageable) {
    return orders.findAll(pageable).map(this::dto);
  }

  public OrderDto transition(UUID id, TransitionOrderRequest request) {
    var order = orders.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
    if ((request.status() == OrderStatus.APPROVED || request.status() == OrderStatus.PROCESSING) && order.getItems().isEmpty()) {
      throw new BusinessRuleException("Add medicines before approving a prescription-only request");
    }
    stateMachine.validate(order.getStatus(), request.status());
    order.setStatus(request.status());
    if (request.status() == OrderStatus.REJECTED) order.setRejectionReason(request.reason());
    audit.record("ORDER_" + request.status(), "Order", order.getId(), request.reason());
    var saved = orders.save(order);
    notifications.orderUpdate(saved, "Order status changed to " + request.status());
    return dto(saved);
  }

  public OrderDto setItems(UUID id, SetOrderItemsRequest request) {
    var order = orders.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
    if (order.getStatus() != OrderStatus.PENDING_VERIFICATION) {
      throw new BusinessRuleException("Medicines can be added only while the order is pending verification");
    }
    if (!order.getItems().isEmpty()) {
      throw new BusinessRuleException("This demo supports adding medicines once for prescription-only requests");
    }
    BigDecimal total = BigDecimal.ZERO;
    for (var line : request.items()) {
      var medicine = medicines.findById(line.medicineId()).orElseThrow(() -> new NotFoundException("Medicine not found"));
      inventory.reserve(medicine.getId(), line.quantity());
      var item = new OrderItem();
      item.setMedicine(medicine);
      item.setQuantity(line.quantity());
      item.setUnitPrice(medicine.getPrice());
      total = total.add(medicine.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
      order.getItems().add(item);
    }
    order.setTotalAmount(total);
    audit.record("ORDER_ITEMS_ADDED", "Order", order.getId(), "Pharmacist added medicines from prescription");
    var saved = orders.save(order);
    notifications.orderUpdate(saved, "Medicines added after prescription review");
    return dto(saved);
  }

  public OrderDto dto(PharmacyOrder o) {
    return new OrderDto(o.getId(), o.getPatient().getFullName(),
        o.getPrescription() == null ? null : o.getPrescription().getId(),
        o.getStatus(), o.getTotalAmount(), o.getRejectionReason(), o.getDeliveryAddress(), o.getCreatedAt(),
        o.getItems().stream().map(i -> new OrderItemDto(i.getMedicine().getId(), i.getMedicine().getBrandName(),
            i.getMedicine().getGenericName(), i.getQuantity(), i.getUnitPrice())).toList(),
        o.getDelivery() == null || o.getDelivery().getAgent() == null ? null : o.getDelivery().getAgent().getFullName(),
        o.getDelivery() == null ? null : o.getDelivery().getStatus().name(),
        o.getDelivery() == null || o.getStatus() == OrderStatus.DELIVERED ? null : o.getDelivery().getOtpCode());
  }
}
