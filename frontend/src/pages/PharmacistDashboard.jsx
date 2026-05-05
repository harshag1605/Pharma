import { useState } from 'react';
import { Check, ClipboardList, Package, Truck, X } from 'lucide-react';
import { Button } from '../components/Button';
import { Panel } from '../components/Panel';
import { StatusBadge } from '../components/StatusBadge';
import { useAllOrdersQuery, useAssignDeliveryMutation, useInventoryQuery, useSearchMedicinesQuery, useSetOrderItemsMutation, useUpdateOrderStatusMutation, useUpsertInventoryMutation, useUsersQuery } from '../services/pharmaApi';

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
      setNotice('Delivery agent assigned. The order is now visible in the agent panel.');
    } catch (error) {
      setNotice(error?.data?.error || 'Could not assign delivery agent.');
    }
  }

  async function addMedicineFromPrescription(orderId) {
    const edit = medicineEdits[orderId];
    if (!edit?.medicineId) {
      setNotice('Select a medicine before saving.');
      return;
    }
    try {
      await setOrderItems({ id: orderId, items: [{ medicineId: edit.medicineId, quantity: Number(edit.quantity || 1) }] }).unwrap();
      setNotice('Medicine added from prescription. You can approve the order now.');
    } catch (error) {
      setNotice(error?.data?.error || 'Could not add medicine to order.');
    }
  }

  return (
    <div className="grid gap-4 xl:grid-cols-[1.3fr_.9fr]">
      <Panel title="Incoming Orders" action={<ClipboardList size={18} className="text-slate-500" />}>
        {notice && <p className="mb-3 rounded-md bg-slate-100 px-3 py-2 text-sm font-semibold text-slate-700">{notice}</p>}
        <div className="space-y-3">
          {(orders?.data?.content || []).map((order) => (
            <article key={order.id} className="rounded-lg border border-slate-200 p-3">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="font-bold">Order {order.id.slice(0, 8)} · {order.patientName}</div>
                  <p className="text-sm text-slate-500">{order.items.map((i) => `${i.brandName} x${i.quantity}`).join(', ')}</p>
                  {!order.items.length && order.prescriptionId && <p className="text-sm font-semibold text-amber-700">Prescription-only request: add medicines after review.</p>}
                  {order.rejectionReason && <p className="mt-1 text-sm text-rose-700">{order.rejectionReason}</p>}
                </div>
                <StatusBadge value={order.status} />
              </div>
              <div className="mt-3 flex flex-wrap gap-2">
                {order.status === 'PENDING_VERIFICATION' && !order.items.length && order.prescriptionId && (
                  <div className="grid w-full gap-2 rounded-md bg-amber-50 p-3 md:grid-cols-[1fr_1fr_5rem_auto]">
                    <input className="rounded-md border border-amber-200 px-3 py-2 text-sm" placeholder="Search medicine" value={medicineSearch} onChange={(e) => setMedicineSearch(e.target.value)} />
                    <select className="rounded-md border border-amber-200 px-3 py-2 text-sm" value={medicineEdits[order.id]?.medicineId || ''} onChange={(e) => setMedicineEdits({ ...medicineEdits, [order.id]: { ...medicineEdits[order.id], medicineId: e.target.value } })}>
                      <option value="">Select medicine</option>
                      {medicines.map((medicine) => <option key={medicine.id} value={medicine.id}>{medicine.brandName} · {medicine.genericName}</option>)}
                    </select>
                    <input className="rounded-md border border-amber-200 px-3 py-2 text-sm" type="number" min="1" value={medicineEdits[order.id]?.quantity || 1} onChange={(e) => setMedicineEdits({ ...medicineEdits, [order.id]: { ...medicineEdits[order.id], quantity: e.target.value } })} />
                    <Button onClick={() => addMedicineFromPrescription(order.id)}>Add medicine</Button>
                  </div>
                )}
                {order.status === 'PENDING_VERIFICATION' && (
                  <>
                    {!!order.items.length && <Button onClick={() => transition({ id: order.id, status: 'APPROVED' })}><Check size={16} />Approve</Button>}
                    <Button variant="danger" onClick={() => transition({ id: order.id, status: 'REJECTED', reason: 'Prescription could not be verified' })}><X size={16} />Reject</Button>
                  </>
                )}
                {order.status === 'APPROVED' && <Button onClick={() => transition({ id: order.id, status: 'PROCESSING' })}><Package size={16} />Process</Button>}
                {order.status === 'PROCESSING' && (
                  <select className="rounded-md border border-slate-200 px-3 py-2 text-sm" onChange={(e) => assignAgent(order.id, e.target.value)}>
                    <option value="">Assign delivery agent</option>
                    {(agents?.data || []).map((agent) => <option key={agent.id} value={agent.id}>{agent.fullName}</option>)}
                  </select>
                )}
                {order.prescriptionId && <Button variant="secondary">Prescription gated</Button>}
              </div>
            </article>
          ))}
        </div>
      </Panel>

      <Panel title="Inventory" action={<Truck size={18} className="text-slate-500" />}>
        <div className="space-y-2">
          {(inventory?.data || []).map((row) => (
            <div key={row.id} className="grid grid-cols-[1fr_5rem_5rem_auto] items-center gap-2 rounded-md border border-slate-200 p-2 text-sm">
              <span className="font-semibold">{row.medicineName}</span>
              <input className="rounded border border-slate-200 px-2 py-1" type="number" value={stockEdits[row.id]?.quantityAvailable ?? row.quantityAvailable} onChange={(e) => setStockEdits({ ...stockEdits, [row.id]: { ...row, ...stockEdits[row.id], quantityAvailable: Number(e.target.value) } })} />
              <input className="rounded border border-slate-200 px-2 py-1" type="number" value={stockEdits[row.id]?.reorderLevel ?? row.reorderLevel} onChange={(e) => setStockEdits({ ...stockEdits, [row.id]: { ...row, ...stockEdits[row.id], reorderLevel: Number(e.target.value) } })} />
              <Button variant="secondary" onClick={() => upsert({ medicineId: row.medicineId, quantityAvailable: stockEdits[row.id]?.quantityAvailable ?? row.quantityAvailable, reorderLevel: stockEdits[row.id]?.reorderLevel ?? row.reorderLevel })}>Save</Button>
            </div>
          ))}
        </div>
      </Panel>
    </div>
  );
}
