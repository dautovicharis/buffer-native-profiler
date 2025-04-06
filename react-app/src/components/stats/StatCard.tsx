interface StatCardProps {
  label: string;
  value: number | string;
  unit: string;
  percentage: number;
  previousPercentage?: number; // Make this optional
  cssClass: string;
}

const StatCard = ({ label, value, unit, percentage, cssClass }: StatCardProps) => {
  const showProgressBar = cssClass === 'buffer';

  // Simple color mapping
  const colorMap: Record<string, string> = {
    buffer: '#4299e1',
    emitted: '#48bb78',
    waiting: '#ecc94b',
    suspensions: '#ed8936',
    usage: '#667eea',
    'avg-item': '#9f7aea'
  };

  // Get color based on cssClass
  const getColor = () => colorMap[cssClass] || '#6B7280';

  return (
    <div className={`p-4 rounded-lg bg-card-light dark:bg-card-dark shadow-md border-l-4 flex flex-col h-full min-h-[90px]`} style={{ borderLeftColor: getColor() }}>
      <div className="text-xs font-semibold uppercase mb-1">{label}</div>
      <div className="text-xl font-bold mb-2 flex items-baseline">
        {value}<span className="text-xs opacity-70 ml-1">{unit}</span>
      </div>
      <div className="flex-grow"></div>
      {showProgressBar && (
        <div className="h-2 w-full bg-gray-100 dark:bg-gray-700 rounded-full mt-1 overflow-hidden shadow-inner" style={{ height: '0.5rem' }}>
          <div
            className="rounded-full progress-bar-animate"
            style={{
              width: `${percentage}%`,
              backgroundColor: getColor(),
              color: getColor(),
              transition: 'width 0.6s cubic-bezier(0.34, 1.56, 0.64, 1)'
            }}
          ></div>
        </div>
      )}
    </div>
  );
};

export default StatCard;
