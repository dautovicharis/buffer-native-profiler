
/**
 * BufferContainer component displays a single buffer container
 *
 * @param {Object} props - Component props
 * @param {string} props.type - Buffer type (string, bytearray, int)
 * @param {number} props.size - Current buffer size
 * @param {number} props.capacity - Buffer capacity
 * @returns {JSX.Element} - Rendered component
 */
interface BufferContainerProps {
  type: 'string' | 'bytearray' | 'int' | string;
  size?: number;
  capacity?: number;
}

const BufferContainer = ({ type, size = 0, capacity = 20 }: BufferContainerProps) => {
  // Calculate the fill percentage
  const fillPercentage = Math.min(100, (size / capacity) * 100);

  // Maximum number of visible items for performance
  const maxVisibleItems = 20;
  const itemsToShow = Math.min(size, maxVisibleItems);

  // Create buffer items
  const bufferItems = Array.from({ length: itemsToShow }, (_, index) => (
    <div
      key={index}
      className="buffer-item"
      data-type={type}
    ></div>
  ));

  // Get the background color based on buffer type
  const getBufferColor = () => {
    switch(type) {
      case 'string': return 'bg-string-buffer/70 dark:bg-string-buffer/40';
      case 'bytearray': return 'bg-bytearray-buffer/70 dark:bg-bytearray-buffer/40';
      case 'int': return 'bg-int-buffer/70 dark:bg-int-buffer/40';
      default: return 'bg-primary/70 dark:bg-primary/40';
    }
  };

  // Get the item color based on buffer type
  const getItemColor = () => {
    switch(type) {
      case 'string': return 'bg-string-buffer dark:bg-string-buffer/70';
      case 'bytearray': return 'bg-bytearray-buffer dark:bg-bytearray-buffer/70';
      case 'int': return 'bg-int-buffer dark:bg-int-buffer/70';
      default: return 'bg-primary dark:bg-primary/70';
    }
  };

  return (
    <div className="relative h-10 bg-black/5 dark:bg-white/5 rounded-[20px] overflow-hidden mb-2.5 transition-colors duration-300" id={`${type}-buffer`}>
      <div
        className={`flex h-full transition-width duration-300 ease-in-out items-center justify-start ${getBufferColor()}`}
        style={{ width: `${fillPercentage}%` }}
      >
        {bufferItems.map((_, index) => (
          <div
            key={index}
            className={`w-2 h-4/5 mr-0.5 rounded-sm transition-colors duration-300 shadow-sm ${getItemColor()}`}
          ></div>
        ))}
      </div>
      <div className="absolute right-2.5 top-1/2 transform -translate-y-1/2 text-xs font-bold text-light dark:text-dark transition-colors duration-300">
        {size}/{capacity}
      </div>
    </div>
  );
};

export default BufferContainer;
