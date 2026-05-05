import { useState } from 'react';
import { Check, ClipboardList, Package, Truck, X } from 'lucide-react';
import { Button } from '../components/Button';
import { Panel } from '../components/Panel';
import { StatusBadge } from '../components/StatusBadge';
import {
  useAllOrdersQuery,
  useAssignDeliveryMutation,
  useInventoryQuery,
  useSearchMedicinesQuery,
  useSetOrderItemsMutation,
  useUpdateOrderStatusMutation,
  useUpsertInventoryMutation,
  useUsersQuery
} from '../services/pharmaApi';

export default function PharmacistDashboard() {
  const { data: orders } = useAllOrdersQuery();
  const { data: inventory } = useInventoryQuery();
  const { data: agents } = useUsersQuery('DELIVERY_AGENT');

  const [medicineSearch, setMedicineSearch] = useState('');
  const { data: catalog } = useSearchMedicinesQuery(medicineSearch);

  const [transition] = useUpdateOrderStatusMutation();
  const [assign] = useAssignDeliveryMutation();
  const [setOrderItems] = useSetOrderItemsMutation();
  const [upsert] = useUpsertInventoryMutation();

  const [stockEdits, setStockEdits] = useState({});
  const [medicineEdits, setMedicineEdits] = useState({});
  const [notice, setNotice] = useState('');

  const medicines = catalog?.data?.content || [];

  async function assignAgent(orderId, agentId) {
    if (!agentId) return;
    setNotice('');
    try {
      await assign({ orderId, agentId }).unwrap();
      setNotice('Delivery agent assigned.');
    } catch (e) {
      setNotice(e?.data?.error || 'Assignment failed.');
    }
  }

  async function addMedicineFromPrescription(orderId) {
    const edit = medicineEdits[orderId];
    if (!edit?.medicineId) {
      setNotice('Select medicine first.');
      return;
    }

    try {
      await setOrderItems({
        id: orderId,
        items: [{ medicineId: edit.medicineId, quantity: Number(edit.quantity || 1) }]
      }).unwrap();

      setNotice('Medicine added.');
    } catch (e) {
      setNotice(e?.data?.error || 'Error adding medicine.');
    }
  }

  return (
    <div className="flex flex-col gap-6 xl:flex-row">

      {/* LEFT SIDE */}
      <div className="flex-1 space-y-5">

        <Panel title="Orders Queue" action={<ClipboardList size={18} />}>

          {notice && (
            <div className="mb-4 rounded-lg bg-gray-100 px-3 py-2 text-sm font-medium">
              {notice}
            </div>
          )}

          <div className="flex flex-col gap-4">

            {(orders?.data?.content || []).map((order) => (

              <div key={order.id} className="border rounded-xl p-4 bg-white shadow-sm">

                {/* HEADER */}
                <div className="flex justify-between items-start gap-3">
                  <div>
                    <h3 className="font-semibold text-sm">
                      #{order.id.slice(0, 8)} • {order.patientName}
                    </h3>

                    <p className="text-xs text-gray-500 mt-1">
                      {order.items.map(i => `${i.brandName} x${i.quantity}`).join(', ')}
                    </p>

                    {!order.items.length && order.prescriptionId && (
                      <p className="text-xs text-yellow-600 mt-1">
                        Prescription requires review
                      </p>
                    )}

                    {order.rejectionReason && (
                      <p className="text-xs text-red-500 mt-1">
                        {order.rejectionReason}
                      </p>
                    )}
                  </div>

                  <StatusBadge value={order.status} />
                </div>

                {/* ACTIONS */}
                <div className="mt-4 flex flex-col gap-2">

                  {order.status === 'PENDING_VERIFICATION' && !order.items.length && order.prescriptionId && (
                    <div className="grid gap-2 md:grid-cols-4 bg-yellow-50 p-3 rounded-lg">

                      <input
                        className="border rounded px-2 py-1 text-sm"
                        placeholder="Search"
                        value={medicineSearch}
                        onChange={(e) => setMedicineSearch(e.target.value)}
                      />

                      <select
                        className="border rounded px-2 py-1 text-sm"
                        value={medicineEdits[order.id]?.medicineId || ''}
                        onChange={(e) =>
                          setMedicineEdits({
                            ...medicineEdits,
                            [order.id]: {
                              ...medicineEdits[order.id],
                              medicineId: e.target.value
                            }
                          })
                        }
                      >
                        <option value="">Select</option>
                        {medicines.map((m) => (
                          <option key={m.id} value={m.id}>
                            {m.brandName}
                          </option>
                        ))}
                      </select>

                      <input
                        type="number"
                        min="1"
                        className="border rounded px-2 py-1 text-sm"
                        value={medicineEdits[order.id]?.quantity || 1}
                        onChange={(e) =>
                          setMedicineEdits({
                            ...medicineEdits,
                            [order.id]: {
                              ...medicineEdits[order.id],
                              quantity: e.target.value
                            }
                          })
                        }
                      />

                      <Button onClick={() => addMedicineFromPrescription(order.id)}>
                        Add
                      </Button>
                    </div>
                  )}

                  {/* STATUS BUTTONS */}
                  <div className="flex flex-wrap gap-2">

                    {order.status === 'PENDING_VERIFICATION' && (
                      <>
                        {!!order.items.length && (
                          <Button onClick={() => transition({ id: order.id, status: 'APPROVED' })}>
                            <Check size={14} /> Approve
                          </Button>
                        )}

                        <Button
                          variant="danger"
                          onClick={() =>
                            transition({
                              id: order.id,
                              status: 'REJECTED',
                              reason: 'Invalid prescription'
                            })
                          }
                        >
                          <X size={14} /> Reject
                        </Button>
                      </>
                    )}

                    {order.status === 'APPROVED' && (
                      <Button onClick={() => transition({ id: order.id, status: 'PROCESSING' })}>
                        <Package size={14} /> Start
                      </Button>
                    )}

                    {order.status === 'PROCESSING' && (
                      <select
                        className="border rounded px-2 py-1 text-sm"
                        onChange={(e) => assignAgent(order.id, e.target.value)}
                      >
                        <option value="">Assign agent</option>
                        {(agents?.data || []).map((a) => (
                          <option key={a.id} value={a.id}>
                            {a.fullName}
                          </option>
                        ))}
                      </select>
                    )}

                    {order.prescriptionId && (
                      <Button variant="secondary">Rx linked</Button>
                    )}

                  </div>
                </div>
              </div>
            ))}
          </div>
        </Panel>
      </div>

      {/* RIGHT SIDE */}
      <div className="w-full xl:w-[380px]">

        <Panel title="Stock Manager" action={<Truck size={18} />}>

          <div className="flex flex-col gap-3">

            {(inventory?.data || []).map((row) => (

              <div
                key={row.id}
                className="flex items-center justify-between gap-2 border rounded-lg p-2 bg-white"
              >

                <span className="text-sm font-medium w-1/3">
                  {row.medicineName}
                </span>

                <input
                  type="number"
                  className="w-16 border rounded px-1 text-sm"
                  value={stockEdits[row.id]?.quantityAvailable ?? row.quantityAvailable}
                  onChange={(e) =>
                    setStockEdits({
                      ...stockEdits,
                      [row.id]: {
                        ...row,
                        ...stockEdits[row.id],
                        quantityAvailable: Number(e.target.value)
                      }
                    })
                  }
                />

                <input
                  type="number"
                  className="w-16 border rounded px-1 text-sm"
                  value={stockEdits[row.id]?.reorderLevel ?? row.reorderLevel}
                  onChange={(e) =>
                    setStockEdits({
                      ...stockEdits,
                      [row.id]: {
                        ...row,
                        ...stockEdits[row.id],
                        reorderLevel: Number(e.target.value)
                      }
                    })
                  }
                />

                <Button
                  variant="secondary"
                  onClick={() =>
                    upsert({
                      medicineId: row.medicineId,
                      quantityAvailable:
                        stockEdits[row.id]?.quantityAvailable ?? row.quantityAvailable,
                      reorderLevel:
                        stockEdits[row.id]?.reorderLevel ?? row.reorderLevel
                    })
                  }
                >
                  Save
                </Button>
              </div>
            ))}
          </div>
        </Panel>
      </div>
    </div>
  );
}