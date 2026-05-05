import { LogOut, PackageCheck, Pill, Truck, UserRound } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import { NavLink, Outlet } from 'react-router-dom';
import { logout } from '../features/auth/authSlice';
import { pharmaApi } from '../services/pharmaApi';
import { Button } from '../components/Button';

export function AppLayout() {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const links = [
    ['PATIENT', '/patient', Pill, 'Patient'],
    ['PHARMACIST', '/pharmacist', PackageCheck, 'Pharmacist'],
    ['DELIVERY_AGENT', '/delivery', Truck, 'Delivery']
  ].filter(([role]) => user?.roles?.includes(role));

  function handleLogout() {
    dispatch(pharmaApi.util.resetApiState());
    dispatch(logout());
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-3">
            <div className="grid h-9 w-9 place-items-center rounded-md bg-mint text-white"><Pill size={20} /></div>
            <div>
              <div className="text-sm font-bold text-ink">PharmaCare Ops</div>
              <div className="text-xs text-slate-500">Prescription order management</div>
            </div>
          </div>
          <nav className="hidden items-center gap-1 md:flex">
            {links.map(([, href, Icon, label]) => (
              <NavLink key={href} to={href} className={({ isActive }) => `rounded-md px-3 py-2 text-sm font-semibold ${isActive ? 'bg-slate-100 text-mint' : 'text-slate-600 hover:bg-slate-50'}`}>
                <Icon className="mr-2 inline" size={16} />{label}
              </NavLink>
            ))}
          </nav>
          <div className="flex items-center gap-3">
            <div className="hidden items-center gap-2 text-sm text-slate-600 sm:flex"><UserRound size={16} />{user.fullName}</div>
            <Button variant="secondary" onClick={handleLogout}><LogOut size={16} />Logout</Button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
