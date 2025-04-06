import { useAppSelector } from '../../store/hooks';
import PressureGauge from './PressureGauge';
import { selectBackpressureData } from '../../store/slices/backpressureSlice';

const Backpressure = () => {
  const bufferData = useAppSelector(selectBackpressureData);
  return (
    <section className="section relative">
      <h2>Backpressure</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6 h-[calc(100%-4rem)]">
        <PressureGauge
          type="string"
          utilizationPct={bufferData.string.utilizationPct}
          status={bufferData.string.status}
        />
        <PressureGauge
          type="bytearray"
          utilizationPct={bufferData.bytearray.utilizationPct}
          status={bufferData.bytearray.status}
        />
        <PressureGauge
          type="int"
          utilizationPct={bufferData.int.utilizationPct}
          status={bufferData.int.status}
        />
      </div>
    </section>
  );
};

export default Backpressure;
