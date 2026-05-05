
export function Panel({
  title,
  subtitle,
  action,
  children,
  className = '',
  headerClass = '',
  bodyClass = '',
  variant = 'default',
  loading = false
}) {
  const variants = {
    default: 'bg-white border-slate-200',
    elevated: 'bg-white border-slate-200 shadow-md',
    outlined: 'bg-transparent border-slate-300',
    soft: 'bg-slate-50 border-slate-100'
  };

  return (
    <section
      className={`rounded-lg border ${variants[variant]} ${className}`}
    >
      {(title || action) && (
        <div
          className={`flex items-center justify-between border-b border-slate-200 px-4 py-3 ${headerClass}`}
        >
          <div>
            {title && (
              <h2 className="text-sm font-bold text-ink">{title}</h2>
            )}
            {subtitle && (
              <p className="text-xs text-slate-500">{subtitle}</p>
            )}
          </div>

          {action && <div>{action}</div>}
        </div>
      )}

      <div className={`p-4 ${bodyClass}`}>
        {loading ? (
          <div className="flex items-center justify-center py-6">
            <div className="h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-transparent"></div>
          </div>
        ) : (
          children
        )}
      </div>
    </section>
  );
}