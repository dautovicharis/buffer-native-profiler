import { useRef } from 'react';
import { useAppSelector } from '../../store/hooks';
import { selectLogs, selectConnected } from '../../store/slices/outputLogSlice';

/**
 * OutputLog component displays log messages and WebSocket data
 * @returns {JSX.Element} - Rendered component
 */
const OutputLog = () => {
  const logs = useAppSelector(selectLogs);
  const connected = useAppSelector(selectConnected);
  const logContainerRef = useRef(null);

  return (
    <section className="section relative mb-8">
      <h2>
        Output Log
        <span className={`ml-2 text-sm ${connected ? 'text-success' : 'text-danger'}`}>
          ({connected ? 'Connected' : 'Disconnected'})
        </span>
      </h2>
      <pre
        id="output"
        className="bg-card-dark dark:bg-dark text-light-secondary dark:text-dark
                  p-4 rounded-md font-mono text-sm leading-relaxed
                  overflow-x-auto overflow-y-auto max-h-[400px] whitespace-pre-wrap
                  shadow-light dark:shadow-dark"
        ref={logContainerRef}
      >
        {logs.join('\n')}
      </pre>
    </section>
  );
};

export default OutputLog;
