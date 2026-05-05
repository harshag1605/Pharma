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
    <div className="min-h-screen flex flex-col bg-gray-50">

      {/* TOP BAR */}
      <div className="border-b bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">

          {/* LEFT */}
          <div className="flex items-center gap-4">
            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-green-500 text-white shadow">
              <Pill size={18} />
            </div>

            <div className="leading-tight">
              <h1 className="text-sm font-semibold text-gray-800">
                PharmaCare Ops
              </h1>
              <p className="text-xs text-gray-500">
                Manage prescriptions & orders
              </p>
            </div>
          </div>

          {/* CENTER NAV */}
          <div className="hidden md:flex items-center gap-2 bg-gray-100 p-1 rounded-lg">
            {links.map(([, href, Icon, label]) => (
              <NavLink
                key={href}
                to={href}
                className={({ isActive }) =>
                  `flex items-center gap-1 px-3 py-1.5 rounded-md text-sm transition ${
                    isActive
                      ? 'bg-white shadow text-green-600'
                      : 'text-gray-600 hover:bg-white'
                  }`
                }
              >
                <Icon size={15} />
                {label}
              </NavLink>
            ))}
          </div>

          {/* RIGHT */}
          <div className="flex items-center gap-3">

            <div className="hidden sm:flex items-center gap-2 text-sm text-gray-600 bg-gray-100 px-3 py-1 rounded-full">
              <UserRound size={14} />
              {user.fullName}
            </div>

            <Button
              variant="secondary"
              className="flex items-center gap-1"
              onClick={handleLogout}
            >
              <LogOut size={14} />
              Logout
            </Button>
          </div>
        </div>
      </div>

      {/* CONTENT */}
      <div className="flex-1 max-w-7xl mx-auto w-full px-4 py-6">
        <Outlet />
      </div>
    </div>
  );
}