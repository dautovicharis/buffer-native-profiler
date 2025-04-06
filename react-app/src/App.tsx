import { useEffect } from 'react';
import { useAppDispatch } from './store/hooks';
import Header from './components/header/Header';
import ThemeInitializer from './components/theme/ThemeInitializer';
import Stats from './components/stats/Stats';
import Backpressure from './components/backpressure/Backpressure';
import BufferFlow from './components/buffer/BufferFlow';
import Memory from './components/memory/Memory';
import OutputLog from './components/output/OutputLog';
import { connect } from './store/slices/websocketSlice';

function App() {
  const dispatch = useAppDispatch();
  useEffect(() => {
    dispatch(connect());
  }, [dispatch]);

  return (
    <>
      <ThemeInitializer />
      <Header />
      <main className="max-w-7xl mx-auto px-4">
        <Stats />
        <Backpressure />
        <BufferFlow />
        <Memory />
        <OutputLog />
      </main>
    </>
  );
}

export default App;
