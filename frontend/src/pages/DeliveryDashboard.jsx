import { useState } from 'react';
import { MapPin, ShieldCheck, Truck } from 'lucide-react';
import { Button } from '../components/Button';
import { Panel } from '../components/Panel';
import { StatusBadge } from '../components/StatusBadge';
import {
  useConfirmDeliveryMutation,
  useMyDeliveriesQuery,
  usePickedUpMutation,
} from '../services/pharmaApi';

export default function DeliveryDashboard() {
  const { data } = useMyDeliveriesQuery();
  const [pickedUp] = usePickedUpMutation();
  const [confirm] = useConfirmDeliveryMutation();
  const [otp, setOtp] = useState({});

  const deliveries = data?.data || [];

  const handleOtpChange = (id, value) => {
    setOtp((prev) => ({ ...prev, [id]: value }));
  };

  const handlePickedUp = (id) => {
    pickedUp(id);
  };

  const handleConfirm = (id) => {
    confirm({ id, otp: otp[id] });
  };

  return (
    <Panel
      title="Assigned Deliveries"
      action={<Truck size={18} className="text-slate-500" />}
    >
      <div className="grid gap-3 md:grid-cols-2">
        {deliveries.length > 0 ? (
          deliveries.map((delivery) => {
            const { id, orderId, address, status } = delivery;

            return (
              <article
                key={id}
                className="rounded-lg border border-slate-200 p-4"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <div className="font-bold">
                      Order {orderId?.slice(0, 8)}
                    </div>

                    <p className="mt-1 flex gap-2 text-sm text-slate-500">
                      <MapPin size={16} />
                      {address}
                    </p>
                  </div>

                  <StatusBadge value={status} />
                </div>

                <div className="mt-4 flex flex-wrap gap-2">
                  {status === 'ASSIGNED' && (
                    <Button onClick={() => handlePickedUp(id)}>
                      <Truck size={16} />
                      Picked up
                    </Button>
                  )}

                  {status === 'PICKED_UP' && (
                    <>
                      <input
                        className="w-32 rounded-md border border-slate-200 px-3 py-2 text-sm"
                        placeholder="OTP"
                        value={otp[id] || ''}
                        onChange={(e) =>
                          handleOtpChange(id, e.target.value)
                        }
                      />

                      <Button onClick={() => handleConfirm(id)}>
                        <ShieldCheck size={16} />
                        Confirm
                      </Button>
                    </>
                  )}
                </div>
              </article>
            );
          })
        ) : (
          <p className="text-sm text-slate-500">
            No deliveries assigned.
          </p>
        )}
      </div>
    </Panel>
  );
}