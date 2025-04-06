import { useSelector } from 'react-redux';
import StatsDisplay from './StatsDisplay';
import BatchConfig from './BatchConfig';
import {
    selectStats,
    selectBatchConfig,
    selectConfigVisible
} from '../../store/slices/statsSlice';
import { selectConnected } from "../../store/slices/outputLogSlice";

const Stats = () => {
    const stats = useSelector(selectStats);
    const batchConfig = useSelector(selectBatchConfig);
    const configVisible = useSelector(selectConfigVisible);
    const isConnected = useSelector(selectConnected);

    return (
        <section className="section relative">
            <h2>Buffer Metrics</h2>
            <div
                className={`absolute top-6 right-6 w-3 h-3 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`}
                style={{ animation: 'pulse 2s infinite' }}
            />

            <BatchConfig config={batchConfig} visible={configVisible} />
            <StatsDisplay stats={stats} />
        </section>
    );
};

export default Stats;
