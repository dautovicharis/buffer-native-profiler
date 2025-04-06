import { useSelector } from 'react-redux';
import MemoryChart from './MemoryChart';
import { selectMemoryData, selectMemoryHistory } from '../../store/slices/memorySlice';

const Memory = () => {
  const memoryData = useSelector(selectMemoryData);
  const memoryHistory = useSelector(selectMemoryHistory);

  return (
    <section className="section relative p-4 mb-6">
      <h2>Buffer Memory Usage</h2>
      <MemoryChart
        memoryUsage={memoryData.memoryUsage || 0}
        memoryHistory={memoryHistory}
      />
    </section>
  );
};

export default Memory;
