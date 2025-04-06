interface PressureGaugeProps {
  type: 'string' | 'bytearray' | 'int' | string;
  utilizationPct?: number;
  status?: 'NORMAL' | 'WARNING' | 'CRITICAL' | string;
}

const PressureGauge = ({ type, utilizationPct = 0, status = 'NORMAL' }: PressureGaugeProps) => {
  // Calculate the angle based on utilization percentage (left to right)
  const angle = -90 + (180 * utilizationPct / 100);
  const isSuspensionActive = status === 'CRITICAL';

  // Color and display name mapping
  const bufferTypes = {
    string: {
      displayName: 'String',
      color: '#3B82F6', // blue
      cssVar: 'var(--string-color)'
    },
    bytearray: {
      displayName: 'ByteArray',
      color: '#10B981', // green
      cssVar: 'var(--bytearray-color)'
    },
    int: {
      displayName: 'Int',
      color: '#F59E0B', // amber
      cssVar: 'var(--int-color)'
    }
  };

  // Get buffer info or default
  const bufferInfo = bufferTypes[type as keyof typeof bufferTypes] || {
    displayName: `${type} Buffer`,
    color: '#6B7280', // gray
    cssVar: 'var(--gray-500)'
  };

  return (
    <div className="flex flex-col items-center w-full">
      <h3 className="mb-4 text-base font-semibold text-center" style={{ color: bufferInfo.color }}>
        {bufferInfo.displayName}
      </h3>
      <div className="flex flex-col items-center w-full">
        {/* Gauge */}
        <div className={`w-40 h-20 rounded-t-full relative overflow-hidden border border-b-0 bg-gray-100 dark:bg-gray-800 ${isSuspensionActive ? 'animate-pulse' : ''}`}
             style={{
               borderColor: `${bufferInfo.color}${isSuspensionActive ? '80' : '40'}`
             }}>
          {/* Needle */}
          <div
            className="absolute bottom-0 left-1/2 w-1 h-[72px]"
            style={{
              backgroundColor: bufferInfo.color,
              transform: `translateX(-50%) rotate(${angle}deg)`,
              transformOrigin: 'bottom center',
              transition: 'transform 0.5s ease-out'
            }}
          ></div>
          {/* Labels */}
          <div className="absolute bottom-2 w-full flex justify-between px-5 text-xs">
            <span>Low</span>
            <span>Medium</span>
            <span>High</span>
          </div>
        </div>

        {/* Suspension Indicator */}
        <div
          className={`flex items-center mt-3 py-1 px-2 rounded-md text-xs w-40 h-8 justify-center ${isSuspensionActive ? 'opacity-100' : 'opacity-30'}`}
        >
          <div
            className="w-4 h-4 rounded-full mr-2"
            style={{
              backgroundColor: bufferInfo.color
            }}
          ></div>
          <span>Suspension</span>
        </div>
      </div>
    </div>
  );
};

export default PressureGauge;
