import { useMemo, useState } from 'react';
import { Bell, FileUp, Plus, Search, ShoppingCart } from 'lucide-react';
import { Button } from '../components/Button';
import { Panel } from '../components/Panel';
import { StatusBadge } from '../components/StatusBadge';
import { useCreateOrderMutation, useCreatePrescriptionRequestMutation, useMyOrdersQuery, useMyPrescriptionsQuery, useSearchMedicinesQuery, useUploadPrescriptionMutation } from '../services/pharmaApi';

export default function PatientDashboard() {
  const [q, setQ] = useState('');
  const [cart, setCart] = useState([]);
  const [isDragging, setIsDragging] = useState(false);
  const [uploadNotice, setUploadNotice] = useState('');
  const [address, setAddress] = useState('221B Wellness Street, Bengaluru');
  const [prescriptionId, setPrescriptionId] = useState('');
  const { data: catalog, isFetching } = useSearchMedicinesQuery(q);
  const { data: prescriptions } = useMyPrescriptionsQuery();
  const { data: orders } = useMyOrdersQuery();
  const [upload, uploadState] = useUploadPrescriptionMutation();
  const [createOrder, orderState] = useCreateOrderMutation();
  const [createPrescriptionRequest, prescriptionRequestState] = useCreatePrescriptionRequestMutation();
  const medicines = catalog?.data?.content || [];
  const total = useMemo(() => cart.reduce((sum, line) => sum + Number(line.price) * line.quantity, 0), [cart]);

  function add(medicine) {
    setCart((current) => {
      const found = current.find((item) => item.id === medicine.id);
      if (found) return current.map((item) => item.id === medicine.id ? { ...item, quantity: item.quantity + 1 } : item);
      return [...current, { ...medicine, quantity: 1 }];
    });
  }

  async function checkout() {
    await createOrder({
      prescriptionId: prescriptionId || null,
      deliveryAddress: address,
      items: cart.map((item) => ({ medicineId: item.id, quantity: item.quantity }))
    });
    setCart([]);
  }

  async function submitPrescriptionOnly() {
    if (!prescriptionId) return;
    await createPrescriptionRequest({ prescriptionId, deliveryAddress: address });
  }

  async function submitPrescriptionFile(file) {
    if (!file) return;
    setUploadNotice('');
    try {
      const uploaded = await upload(file).unwrap();
      const uploadedPrescriptionId = uploaded.data.id;
      setPrescriptionId(uploadedPrescriptionId);
      await createPrescriptionRequest({ prescriptionId: uploadedPrescriptionId, deliveryAddress: address }).unwrap();
      setUploadNotice('Prescription sent to pharmacist for medicine review.');
    } catch (error) {
      setUploadNotice(error?.data?.error || 'Prescription upload failed.');
    }
  }

  function handleDrop(event) {
    event.preventDefault();
    setIsDragging(false);
    submitPrescriptionFile(event.dataTransfer.files?.[0]);
  }

  return (
    <div className="grid gap-4 lg:grid-cols-[1.4fr_.8fr]">
      <div className="space-y-4">
        <Panel title="Medicine Catalog" action={<div className="relative"><Search className="absolute left-3 top-2.5 text-slate-400" size={16} /><input className="w-72 rounded-md border border-slate-200 py-2 pl-9 pr-3 text-sm" placeholder="Search brand, generic, symptoms" value={q} onChange={(e) => setQ(e.target.value)} /></div>}>
          {isFetching && <p className="text-sm text-slate-500">Refreshing medicines...</p>}
          <div className="grid gap-3 md:grid-cols-2">
            {medicines.map((m) => (
              <article key={m.id} className="rounded-lg border border-slate-200 p-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="font-bold text-ink">{m.brandName}</h3>
                    <p className="text-sm text-slate-500">{m.genericName} · {m.dosageForm}</p>
                    <p className="mt-2 text-xs text-slate-500">{m.symptoms}</p>
                  </div>
                  <div className="text-right text-sm font-bold">₹{m.price}</div>
                </div>
                <div className="mt-3 flex items-center justify-between">
                  <span className={`text-xs font-semibold ${m.availableQuantity > 0 ? 'text-emerald-700' : 'text-rose-700'}`}>{m.availableQuantity} in stock</span>
                  <Button disabled={!m.availableQuantity} onClick={() => add(m)}><Plus size={16} />Add</Button>
                </div>
              </article>
            ))}
          </div>
        </Panel>

        <Panel title="Order Tracking">
          <div className="space-y-3">
            {(orders?.data?.content || []).map((order) => <OrderTimeline key={order.id} order={order} />)}
            {!orders?.data?.content?.length && <p className="text-sm text-slate-500">No orders yet.</p>}
          </div>
        </Panel>
      </div>

      <aside className="space-y-4">
        <Panel title="Prescription Upload">
          <label
            className={`flex cursor-pointer flex-col items-center justify-center rounded-lg border border-dashed px-4 py-8 text-center transition ${isDragging ? 'border-mint bg-teal-50' : 'border-slate-300 bg-slate-50'}`}
            onDragOver={(event) => {
              event.preventDefault();
              setIsDragging(true);
            }}
            onDragLeave={() => setIsDragging(false)}
            onDrop={handleDrop}
          >
            <FileUp className="mb-2 text-mint" />
            <span className="text-sm font-semibold">Drop or select JPG, PNG, PDF</span>
            <input className="hidden" type="file" accept="image/png,image/jpeg,application/pdf" onChange={(e) => submitPrescriptionFile(e.target.files?.[0])} />
          </label>
          {uploadState.isLoading && <p className="mt-2 text-sm text-slate-500">Uploading securely...</p>}
          {prescriptionRequestState.isLoading && <p className="mt-2 text-sm text-slate-500">Sending request to pharmacist...</p>}
          {uploadNotice && <p className="mt-2 rounded-md bg-slate-100 px-3 py-2 text-sm font-semibold text-slate-700">{uploadNotice}</p>}
          <select className="mt-3 w-full rounded-md border border-slate-200 px-3 py-2 text-sm" value={prescriptionId} onChange={(e) => setPrescriptionId(e.target.value)}>
            <option value="">No prescription selected</option>
            {(prescriptions?.data || []).map((p) => <option key={p.id} value={p.id}>{p.originalFilename}</option>)}
          </select>
          <Button className="mt-3 w-full" variant="secondary" disabled={!prescriptionId || prescriptionRequestState.isLoading} onClick={submitPrescriptionOnly}>
            Submit prescription only
          </Button>
        </Panel>

        <Panel title="Cart" action={<ShoppingCart size={18} className="text-slate-500" />}>
          <div className="space-y-2">
            {cart.map((item) => (
              <div key={item.id} className="flex justify-between rounded-md bg-slate-50 p-2 text-sm">
                <span>{item.brandName} x {item.quantity}</span>
                <span className="font-semibold">₹{Number(item.price) * item.quantity}</span>
              </div>
            ))}
            {!cart.length && <p className="text-sm text-slate-500">Cart is empty.</p>}
          </div>
          <textarea className="mt-3 w-full rounded-md border border-slate-200 px-3 py-2 text-sm" rows="3" value={address} onChange={(e) => setAddress(e.target.value)} />
          <div className="mt-3 flex items-center justify-between text-sm font-bold"><span>Total</span><span>₹{total.toFixed(2)}</span></div>
          <Button className="mt-3 w-full" disabled={!cart.length || orderState.isLoading} onClick={checkout}>Checkout</Button>
        </Panel>

        <Panel title="Refill Reminders" action={<Bell size={18} className="text-slate-500" />}>
          <p className="text-sm text-slate-600">Long-running medicine reminders are scheduled by the backend refill scanner and notification adapter.</p>
        </Panel>
      </aside>
    </div>
  );
}

function OrderTimeline({ order }) {
  const states = ['PENDING_VERIFICATION', 'APPROVED', 'PROCESSING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
  const index = states.indexOf(order.status);
  return (
    <article className="rounded-lg border border-slate-200 p-3">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="text-sm font-bold">Order {order.id.slice(0, 8)}</div>
        <StatusBadge value={order.status} />
      </div>
      <div className="mt-3 grid grid-cols-5 gap-1">
        {states.map((state, i) => <div key={state} className={`h-2 rounded ${i <= index ? 'bg-mint' : 'bg-slate-200'}`} title={state} />)}
      </div>
      <p className="mt-2 text-sm text-slate-500">{order.items.map((i) => `${i.brandName} x${i.quantity}`).join(', ')}</p>
      {order.deliveryOtp && (
        <div className="mt-3 rounded-md border border-emerald-200 bg-emerald-50 px-3 py-2">
          <div className="text-xs font-semibold uppercase text-emerald-700">Delivery OTP</div>
          <div className="mt-1 font-mono text-2xl font-bold tracking-widest text-emerald-900">{order.deliveryOtp}</div>
        </div>
      )}
    </article>
  );
}
