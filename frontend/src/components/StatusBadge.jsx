const colors = {
  PENDING_VERIFICATION: 'bg-amber-100 text-amber-800',
  APPROVED: 'bg-sky-100 text-sky-800',
  REJECTED: 'bg-rose-100 text-rose-800',
  PROCESSING: 'bg-indigo-100 text-indigo-800',
  OUT_FOR_DELIVERY: 'bg-orange-100 text-orange-800',
  DELIVERED: 'bg-emerald-100 text-emerald-800',
  ASSIGNED: 'bg-sky-100 text-sky-800',
  PICKED_UP: 'bg-orange-100 text-orange-800'
};

export function StatusBadge({ value }) {
  return <span className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${colors[value] || 'bg-slate-100 text-slate-700'}`}>{String(value || 'UNKNOWN').replaceAll('_', ' ')}</span>;
}
