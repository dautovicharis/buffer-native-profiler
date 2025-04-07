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
      <div className="flex flex-col md:flex-row justify-between items-stretch md:items-center mt-6 relative gap-4 md:gap-0">
        <div className="w-full md:w-[120px] lg:w-[150px] p-3 md:p-4 bg-light dark:bg-white/5 rounded-md transition-colors duration-300">
          <h3 className="text-center md:text-left">Producers</h3>
          <div className="flex md:flex-col justify-around md:justify-start gap-3 md:gap-2">
            <Producer type="string" />
            <Producer type="bytearray" />
            <Producer type="int" />
          </div>
        </div>

        <div className="flex-1 mx-0 md:mx-4 flex flex-col gap-3 md:gap-4">
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

        <div className="w-full md:w-[120px] lg:w-[150px] p-3 md:p-4 bg-light dark:bg-white/5 rounded-md transition-colors duration-300">
          <h3 className="text-center md:text-left">Consumers</h3>
          <div className="flex md:flex-col justify-around md:justify-start gap-3 md:gap-2">
            <Consumer type="string" />
            <Consumer type="bytearray" />
            <Consumer type="int" />
          </div>
        </div>
      </div>
    </section>
  );
};

export default BufferFlow;
