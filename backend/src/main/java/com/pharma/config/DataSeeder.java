package com.pharma.config;

import com.pharma.catalog.Medicine;
import com.pharma.catalog.MedicineRepository;
import com.pharma.inventory.Inventory;
import com.pharma.inventory.InventoryRepository;
import com.pharma.security.CryptoService;
import com.pharma.user.Role;
import com.pharma.user.RoleName;
import com.pharma.user.RoleRepository;
import com.pharma.user.User;
import com.pharma.user.UserRepository;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
  private final RoleRepository roles;
  private final UserRepository users;
  private final MedicineRepository medicines;
  private final InventoryRepository inventory;
  private final PasswordEncoder encoder;
  private final CryptoService crypto;

  @Bean
  CommandLineRunner seedData() {
    return args -> seed();
  }

  void seed() {
    for (RoleName name : RoleName.values()) {
      roles.findByName(name).orElseGet(() -> {
        var r = new Role();
        r.setName(name);
        return roles.save(r);
      });
    }
    createUser("Patient Demo", "patient@pharma.local", RoleName.PATIENT);
    createUser("Pharmacist Demo", "pharmacist@pharma.local", RoleName.PHARMACIST);
    createUser("Delivery Demo", "delivery@pharma.local", RoleName.DELIVERY_AGENT);

    if (medicines.count() == 0) {
      Map<String, String[]> seed = Map.of(
          "Dolo 650", new String[]{"Paracetamol", "fever pain headache", "tablet", "false", "24.50", "500"},
          "Azithral 500", new String[]{"Azithromycin", "bacterial infection throat respiratory", "tablet", "true", "119.00", "120"},
          "Cetcip", new String[]{"Cetirizine", "allergy sneezing runny nose", "tablet", "false", "18.00", "320"},
          "Glycomet 500", new String[]{"Metformin", "diabetes blood sugar", "tablet", "true", "42.00", "180"}
      );
      seed.forEach((brand, v) -> {
        var m = new Medicine();
        m.setBrandName(brand);
        m.setGenericName(v[0]);
        m.setSymptoms(v[1]);
        m.setDosageForm(v[2]);
        m.setPrescriptionRequired(Boolean.parseBoolean(v[3]));
        m.setPrice(new BigDecimal(v[4]));
        var saved = medicines.save(m);
        var inv = new Inventory();
        inv.setMedicine(saved);
        inv.setQuantityAvailable(Integer.parseInt(v[5]));
        inv.setReorderLevel(40);
        inventory.save(inv);
      });
    }
  }

  private void createUser(String name, String email, RoleName roleName) {
    if (users.existsByEmailIgnoreCase(email)) return;
    var role = roles.findByName(roleName).orElseThrow();
    var u = new User();
    u.setFullName(name);
    u.setEmail(email);
    u.setPasswordHash(encoder.encode("Password123!"));
    u.setPhoneEncrypted(crypto.encrypt("+15550001000"));
    u.getRoles().add(role);
    users.save(u);
  }
}
