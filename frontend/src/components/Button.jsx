export function Button({ children, className = '', variant = 'primary', ...props }) {
  const styles = {
    primary: 'bg-mint text-white hover:bg-teal-800',
    secondary: 'bg-white text-ink border border-slate-200 hover:bg-slate-50',
    danger: 'bg-rose-600 text-white hover:bg-rose-700'
  };
  return (
    <button className={`focus-ring inline-flex items-center justify-center gap-2 rounded-md px-3 py-2 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-50 ${styles[variant]} ${className}`} {...props}>
      {children}
    </button>
  );
}
