import { useMemo, useState } from 'react';
import { Bell, FileUp, Plus, Search, ShoppingCart } from 'lucide-react';
import { Button } from '../components/Button';
import { Panel } from '../components/Panel';
import { StatusBadge } from '../components/StatusBadge';
import {
  useCreateOrderMutation,
  useCreatePrescriptionRequestMutation,
  useMyOrdersQuery,
  useMyPrescriptionsQuery,
  useSearchMedicinesQuery,
  useUploadPrescriptionMutation
} from '../services/pharmaApi';

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

  const total = useMemo(
    () => cart.reduce((sum, line) => sum + Number(line.price) * line.quantity, 0),
    [cart]
  );

  function add(m) {
    setCart((cur) => {
      const f = cur.find((i) => i.id === m.id);
      if (f) return cur.map((i) => i.id === m.id ? { ...i, quantity: i.quantity + 1 } : i);
      return [...cur, { ...m, quantity: 1 }];
    });
  }

  async function checkout() {
    await createOrder({
      prescriptionId: prescriptionId || null,
      deliveryAddress: address,
      items: cart.map((i) => ({ medicineId: i.id, quantity: i.quantity }))
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
      const id = uploaded.data.id;
      setPrescriptionId(id);
      await createPrescriptionRequest({ prescriptionId: id, deliveryAddress: address }).unwrap();
      setUploadNotice('Prescription sent for review.');
    } catch (e) {
      setUploadNotice(e?.data?.error || 'Upload failed.');
    }
  }

  function handleDrop(e) {
    e.preventDefault();
    setIsDragging(false);
    submitPrescriptionFile(e.dataTransfer.files?.[0]);
  }

  return (
    <div className="grid gap-4 lg:grid-cols-[1.4fr_.8fr]">
      <div className="space-y-4">

        <Panel
          title="Medicine Catalog"
          action={
            <div className="relative">
              <Search className="absolute left-3 top-2.5 text-gray-400" size={16} />
              <input
                className="w-72 rounded-lg border px-9 py-2 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                placeholder="Search medicines..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
              />
            </div>
          }
        >
          {isFetching && <p className="text-sm text-gray-400 animate-pulse">Loading...</p>}

          <div className="grid gap-4 md:grid-cols-2">
            {medicines.map((m) => (
              <div key={m.id} className="p-4 border rounded-xl shadow-sm hover:shadow-lg transition bg-white">
                <div className="flex justify-between">
                  <div>
                    <h3 className="font-semibold text-gray-800">{m.brandName}</h3>
                    <p className="text-xs text-gray-500">{m.genericName} • {m.dosageForm}</p>
                    <p className="text-xs text-gray-400 mt-1 line-clamp-2">{m.symptoms}</p>
                  </div>
                  <span className="font-bold text-green-600 text-sm">₹{m.price}</span>
                </div>

                <div className="flex justify-between items-center mt-3">
                  <span className={`text-xs ${m.availableQuantity ? 'text-green-600' : 'text-red-500'}`}>
                    {m.availableQuantity ? 'In Stock' : 'Out of Stock'}
                  </span>

                  <Button
                    disabled={!m.availableQuantity}
                    className="px-3 py-1 rounded-lg"
                    onClick={() => add(m)}
                  >
                    <Plus size={14} /> Add
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </Panel>

        <Panel title="Order Tracking">
          <div className="space-y-3">
            {(orders?.data?.content || []).map((o) => (
              <OrderTimeline key={o.id} order={o} />
            ))}
            {!orders?.data?.content?.length && (
              <p className="text-sm text-gray-400">No orders yet</p>
            )}
          </div>
        </Panel>
      </div>

      <aside className="space-y-4">

        <Panel title="Prescription Upload">
          <label
            className={`flex flex-col items-center justify-center border-dashed border rounded-xl p-6 cursor-pointer transition ${
              isDragging ? 'bg-blue-50 border-blue-400' : 'bg-gray-50'
            }`}
            onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }}
            onDragLeave={() => setIsDragging(false)}
            onDrop={handleDrop}
          >
            <FileUp className="mb-2 text-blue-500" />
            <span className="text-sm font-medium">Upload Prescription</span>
            <input
              className="hidden"
              type="file"
              accept="image/png,image/jpeg,application/pdf"
              onChange={(e) => submitPrescriptionFile(e.target.files?.[0])}
            />
          </label>

          {uploadState.isLoading && <p className="text-sm text-gray-400 mt-2">Uploading...</p>}
          {uploadNotice && <p className="text-sm mt-2 bg-gray-100 p-2 rounded">{uploadNotice}</p>}

          <select
            className="w-full mt-3 border rounded-lg px-3 py-2 text-sm"
            value={prescriptionId}
            onChange={(e) => setPrescriptionId(e.target.value)}
          >
            <option value="">Select prescription</option>
            {(prescriptions?.data || []).map((p) => (
              <option key={p.id} value={p.id}>{p.originalFilename}</option>
            ))}
          </select>

          <Button
            className="w-full mt-3"
            variant="secondary"
            disabled={!prescriptionId || prescriptionRequestState.isLoading}
            onClick={submitPrescriptionOnly}
          >
            Submit Prescription
          </Button>
        </Panel>

        <Panel title="Cart" action={<ShoppingCart size={18} />}>
          <div className="space-y-2 max-h-40 overflow-y-auto">
            {cart.map((i) => (
              <div key={i.id} className="flex justify-between bg-gray-50 p-2 rounded-lg">
                <span>{i.brandName} x {i.quantity}</span>
                <span className="font-semibold text-green-600">
                  ₹{Number(i.price) * i.quantity}
                </span>
              </div>
            ))}
            {!cart.length && <p className="text-sm text-gray-400">Empty</p>}
          </div>

          <textarea
            className="w-full mt-3 border rounded-lg px-3 py-2 text-sm"
            rows="3"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />

          <div className="flex justify-between mt-3 font-semibold">
            <span>Total</span>
            <span>₹{total.toFixed(2)}</span>
          </div>

          <Button
            className="w-full mt-3 bg-green-600 hover:bg-green-700 text-white"
            disabled={!cart.length || orderState.isLoading}
            onClick={checkout}
          >
            Checkout
          </Button>
        </Panel>

        <Panel title="Refill Reminders" action={<Bell size={18} />}>
          <p className="text-sm text-gray-500">
            Notifications handled by backend scheduler.
          </p>
        </Panel>
      </aside>
    </div>
  );
}

function OrderTimeline({ order }) {
  const states = ['PENDING_VERIFICATION', 'APPROVED', 'PROCESSING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
  const index = states.indexOf(order.status);

  return (
    <div className="p-3 border rounded-xl bg-white shadow-sm">
      <div className="flex justify-between items-center">
        <span className="text-sm font-semibold">Order {order.id.slice(0, 8)}</span>
        <StatusBadge value={order.status} />
      </div>

      <div className="grid grid-cols-5 gap-1 mt-3">
        {states.map((s, i) => (
          <div key={s} className={`h-2 rounded ${i <= index ? 'bg-green-500' : 'bg-gray-200'}`} />
        ))}
      </div>

      <p className="text-sm text-gray-500 mt-2">
        {order.items.map((i) => `${i.brandName} x${i.quantity}`).join(', ')}
      </p>

      {order.deliveryOtp && (
        <div className="mt-3 bg-green-50 border p-2 rounded">
          <div className="text-xs text-green-700">OTP</div>
          <div className="font-mono text-lg">{order.deliveryOtp}</div>
        </div>
      )}
    </div>
  );
}