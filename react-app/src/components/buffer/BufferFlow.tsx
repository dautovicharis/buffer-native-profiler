import { useAppSelector } from '../../store/hooks';
import Producer from './Producer';
import Consumer from './Consumer';
import BufferContainer from './BufferContainer';
import { selectBufferData } from '../../store/slices/bufferFlowSlice';

/**
 * BufferFlow component displays the buffer flow visualization
 * Now using Redux for state management
 *
 * @returns {JSX.Element} - Rendered component
 */
const BufferFlow = () => {
  const bufferData = useAppSelector(selectBufferData);

  return (
    <section className="section overflow-hidden w-full max-w-full box-border">
      <h2>Buffer Flow Visualization</h2>
      <div className="flex justify-between items-center mt-6 relative">
        <div className="w-[150px] p-4 bg-light dark:bg-white/5 rounded-md transition-colors duration-300">
          <h3>Producers</h3>
          <Producer type="string" />
          <Producer type="bytearray" />
          <Producer type="int" />
        </div>

        <div className="flex-1 mx-4 flex flex-col gap-4">
          <BufferContainer
            type="string"
            size={bufferData.string.size}
            capacity={bufferData.string.capacity}
          />
          <BufferContainer
            type="bytearray"
            size={bufferData.bytearray.size}
            capacity={bufferData.bytearray.capacity}
          />
          <BufferContainer
            type="int"
            size={bufferData.int.size}
            capacity={bufferData.int.capacity}
          />
        </div>

        <div className="w-[150px] p-4 bg-light dark:bg-white/5 rounded-md transition-colors duration-300">
          <h3>Consumers</h3>
          <Consumer type="string" />
          <Consumer type="bytearray" />
          <Consumer type="int" />
        </div>
      </div>
    </section>
  );
};

export default BufferFlow;
