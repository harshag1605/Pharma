import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

export function RequireAuth({ roles, children }) {
  const { user } = useSelector((state) => state.auth);
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.some((role) => user.roles?.includes(role))) {
    const fallback = user.roles?.includes('PHARMACIST') ? '/pharmacist' : user.roles?.includes('DELIVERY_AGENT') ? '/delivery' : '/patient';
    return <Navigate to={fallback} replace />;
  }
  return children;
}
