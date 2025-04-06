
/**
 * Consumer component displays a consumer for a specific buffer type
 *
 * @param {Object} props - Component props
 * @param {string} props.type - Buffer type (string, bytearray, int)
 * @returns {JSX.Element} - Rendered component
 */
interface ConsumerProps {
  type: 'string' | 'bytearray' | 'int' | string;
}

const Consumer = ({ type }: ConsumerProps) => {
  // Get the display name for the buffer type
  const getDisplayName = () => {
    switch(type) {
      case 'string': return 'String';
      case 'bytearray': return 'ByteArray';
      case 'int': return 'Int';
      default: return type;
    }
  };

  // Get the background color based on consumer type
  const getConsumerColor = () => {
    switch(type) {
      case 'string': return 'bg-string-buffer dark:bg-string-buffer/80 text-white';
      case 'bytearray': return 'bg-bytearray-buffer dark:bg-bytearray-buffer/80 text-white';
      case 'int': return 'bg-int-buffer dark:bg-int-buffer/80 text-white';
      default: return 'bg-primary dark:bg-primary/80 text-white';
    }
  };

  return (
    <div
      className={`my-2 py-2 px-2 rounded-sm text-center font-bold transition-all duration-300 hover:shadow-md hover:scale-[1.02] ${getConsumerColor()}`}
    >
      {getDisplayName()}
    </div>
  );
};

export default Consumer;
