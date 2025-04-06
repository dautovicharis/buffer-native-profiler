import { useEffect, useState, useRef } from 'react';
import {
  YAxis,
  XAxis,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Area,
  AreaChart
} from 'recharts';
import { BufferMonitorUtils } from '../stats/utils';

// Define props interface for better type safety
interface MemoryTimelineChartProps {
  memoryUsage: number;
  memoryHistory: number[];
}

// Interface for chart data
interface ChartDataPoint {
  memory: number;
}

const MemoryChart = ({memoryHistory = [] }: MemoryTimelineChartProps) => {
  // State to hold formatted data for Recharts
  const [chartData, setChartData] = useState<ChartDataPoint[]>([]);
  const startTimeRef = useRef<number>(Date.now());
  const isDarkMode = document.documentElement.classList.contains('dark');

  // Update chart data when memory history changes
  useEffect(() => {
    // If this is the first data point, reset the start time
    if (memoryHistory.length === 1 && chartData.length === 0) {
      startTimeRef.current = Date.now();
    }

    const newChartData = memoryHistory.map((memory) => {
      return { memory };
    });

    setChartData(newChartData);
  }, [memoryHistory]);


  return (
    <div className="mt-6 h-[300px] p-6 bg-light dark:bg-black/[0.03] rounded-md shadow-inner dark:shadow-black/30 relative">
      <h3 className="text-center text-light dark:text-dark mb-4">
        Total Memory Usage Over Time
      </h3>
      <ResponsiveContainer width="100%" height="85%">
        <AreaChart
          data={chartData}
        >
          <CartesianGrid strokeDasharray="3 3" stroke={isDarkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(0, 0, 0, 0.1)"} />
          <XAxis
            dataKey="memory"
            hide={true}
          />
          <YAxis
            stroke={isDarkMode ? "#E0E0E0" : "#333"}
            tick={{ fill: isDarkMode ? "#E0E0E0" : "#333" }}
            tickFormatter={(value) => BufferMonitorUtils.formatBytes(value, 0)}
          />
          <Legend wrapperStyle={{ color: isDarkMode ? '#E0E0E0' : '#333' }} />
          <Area
            type="monotoneX"
            dataKey="memory"
            name="Total Memory"
            stroke="#2196F3"
            fill="rgba(33, 150, 243, 0.2)"
            activeDot={{ r: 6, fill: '#2196F3' }}
            isAnimationActive={false}
            animationDuration={50}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MemoryChart;
