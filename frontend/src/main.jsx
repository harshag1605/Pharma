import React, { Suspense, lazy } from 'react';
import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux';
import { Navigate, RouterProvider, createBrowserRouter } from 'react-router-dom';
import { store } from './services/store';
import { RequireAuth } from './layouts/RequireAuth';
import { AppLayout } from './layouts/AppLayout';
import './index.css';

const AuthPage = lazy(() => import('./pages/AuthPage'));
const PatientDashboard = lazy(() => import('./pages/PatientDashboard'));
const PharmacistDashboard = lazy(() => import('./pages/PharmacistDashboard'));
const DeliveryDashboard = lazy(() => import('./pages/DeliveryDashboard'));

const router = createBrowserRouter([
  { path: '/login', element: <AuthPage /> },
  {
    path: '/',
    element: <RequireAuth><AppLayout /></RequireAuth>,
    children: [
      { index: true, element: <Navigate to="/patient" replace /> },
      { path: 'patient', element: <RequireAuth roles={['PATIENT']}><PatientDashboard /></RequireAuth> },
      { path: 'pharmacist', element: <RequireAuth roles={['PHARMACIST']}><PharmacistDashboard /></RequireAuth> },
      { path: 'delivery', element: <RequireAuth roles={['DELIVERY_AGENT']}><DeliveryDashboard /></RequireAuth> }
    ]
  }
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Provider store={store}>
      <Suspense fallback={<div className="p-6 text-sm text-slate-600">Loading workspace...</div>}>
        <RouterProvider router={router} />
      </Suspense>
    </Provider>
  </React.StrictMode>
);
