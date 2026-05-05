import { useState } from 'react';
import { ShieldCheck } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/Button';
import { setCredentials } from '../features/auth/authSlice';
import { pharmaApi, useLoginMutation, useRegisterMutation } from '../services/pharmaApi';

export default function AuthPage() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ fullName: '', email: 'patient@pharma.local', password: 'Password123!', phone: '+15550001000', role: 'PATIENT' });
  const [login, loginState] = useLoginMutation();
  const [register, registerState] = useRegisterMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const busy = loginState.isLoading || registerState.isLoading;
  const error = loginState.error || registerState.error;

  async function submit(e) {
    e.preventDefault();
    const result = mode === 'login' ? await login({ email: form.email, password: form.password }) : await register(form);
    if (result.data?.data) {
      dispatch(pharmaApi.util.resetApiState());
      dispatch(setCredentials(result.data.data));
      const roles = result.data.data.roles;
      navigate(roles.includes('PHARMACIST') ? '/pharmacist' : roles.includes('DELIVERY_AGENT') ? '/delivery' : '/patient');
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-slate-100 px-4">
      <section className="w-full max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-6 flex items-center gap-3">
          <div className="grid h-10 w-10 place-items-center rounded-md bg-mint text-white"><ShieldCheck /></div>
          <div>
            <h1 className="text-lg font-bold text-ink">PharmaCare</h1>
            <p className="text-sm text-slate-500">Secure healthcare operations portal</p>
          </div>
        </div>
        <div className="mb-4 grid grid-cols-2 rounded-md bg-slate-100 p-1">
          {['login', 'register'].map((item) => (
            <button key={item} onClick={() => setMode(item)} className={`rounded px-3 py-2 text-sm font-semibold capitalize ${mode === item ? 'bg-white text-mint shadow-sm' : 'text-slate-500'}`}>{item}</button>
          ))}
        </div>
        <form onSubmit={submit} className="space-y-3">
          {mode === 'register' && <Input label="Full name" value={form.fullName} onChange={(v) => setForm({ ...form, fullName: v })} />}
          <Input label="Email" value={form.email} onChange={(v) => setForm({ ...form, email: v })} />
          <Input label="Password" type="password" value={form.password} onChange={(v) => setForm({ ...form, password: v })} />
          {mode === 'register' && (
            <>
              <Input label="Phone" value={form.phone} onChange={(v) => setForm({ ...form, phone: v })} />
              <select className="w-full rounded-md border border-slate-200 px-3 py-2 text-sm" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
                <option value="PATIENT">Patient</option>
                <option value="PHARMACIST">Pharmacist</option>
                <option value="DELIVERY_AGENT">Delivery agent</option>
              </select>
            </>
          )}
          {error && <p className="rounded-md bg-rose-50 px-3 py-2 text-sm text-rose-700">{error.data?.error || 'Authentication failed'}</p>}
          <Button className="w-full" disabled={busy}>{busy ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create account'}</Button>
        </form>
      </section>
    </main>
  );
}

function Input({ label, value, onChange, type = 'text' }) {
  return (
    <label className="block text-sm font-semibold text-slate-700">
      {label}
      <input type={type} className="mt-1 w-full rounded-md border border-slate-200 px-3 py-2 text-sm font-normal" value={value} onChange={(e) => onChange(e.target.value)} required />
    </label>
  );
}
