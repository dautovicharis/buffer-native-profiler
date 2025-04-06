import { useState, useEffect } from 'react';
import StatCard from './StatCard';
import { BufferMonitorUtils } from "./utils";

interface Stats {
  bufferSize?: number;
  bufferCapacity?: number;
  totalEmissions?: number;
  waitingItems?: number;
  suspensions?: number;
  memoryUsage?: number;
  memoryPerItem?: number;
}

interface StatsDisplayProps {
  stats: Stats;
}

const StatsDisplay = ({ stats }: StatsDisplayProps) => {
  const [previousStats, setPreviousStats] = useState<Stats>({
    bufferSize: 0,
    totalEmissions: 0,
    waitingItems: 0,
    suspensions: 0,
    memoryUsage: 0,
    memoryPerItem: 0
  });

  // Current values
  const bufferSize = stats.bufferSize || 0;
  const bufferCapacity = stats.bufferCapacity || 20;
  const bufferPercentage = Math.min(100, Math.round((bufferSize / bufferCapacity) * 100));

  const totalEmissions = stats.totalEmissions || 0;
  const maxEmissions = Math.max(totalEmissions, 1000); // For progress bar scaling
  const emissionsPercentage = Math.min(100, Math.round((totalEmissions / maxEmissions) * 100));

  const waitingItems = stats.waitingItems || 0;
  const maxWaiting = bufferCapacity;
  const waitingPercentage = Math.min(100, Math.round((waitingItems / maxWaiting) * 100));

  const suspensions = stats.suspensions || 0;
  const maxSuspensions = 100; // Arbitrary max for progress bar
  const suspensionsPercentage = Math.min(100, Math.round((suspensions / maxSuspensions) * 100));

  const memoryUsage = stats.memoryUsage || 0;
  const maxMemory = 1024 * 1024 * 1024; // 1GB as max
  const memoryPercentage = Math.min(100, Math.round((memoryUsage / maxMemory) * 100));

  const memoryPerItem = stats.memoryPerItem || 0;
  const maxMemoryPerItem = 1024 * 1024; // 1MB as max per item
  const memoryPerItemPercentage = Math.min(100, Math.round((memoryPerItem / maxMemoryPerItem) * 100));

  // Previous values for smooth transitions
  const prevBufferPercentage = Math.min(100, Math.round(((previousStats.bufferSize || 0) / bufferCapacity) * 100));
  const prevMaxEmissions = Math.max(previousStats.totalEmissions || 0, 1000);
  const prevEmissionsPercentage = Math.min(100, Math.round(((previousStats.totalEmissions || 0) / prevMaxEmissions) * 100));
  const prevWaitingPercentage = Math.min(100, Math.round(((previousStats.waitingItems || 0) / maxWaiting) * 100));
  const prevSuspensionsPercentage = Math.min(100, Math.round(((previousStats.suspensions || 0) / maxSuspensions) * 100));
  const prevMemoryPercentage = Math.min(100, Math.round(((previousStats.memoryUsage || 0) / maxMemory) * 100));
  const prevMemoryPerItemPercentage = Math.min(100, Math.round(((previousStats.memoryPerItem || 0) / maxMemoryPerItem) * 100));

  // Update previous stats after render with a slight delay for animation
  useEffect(() => {
    const timer = setTimeout(() => {
      setPreviousStats({
        bufferSize,
        totalEmissions,
        waitingItems,
        suspensions,
        memoryUsage,
        memoryPerItem
      });
    }, 600); // Match the transition duration

    return () => clearTimeout(timer);
  }, [bufferSize, totalEmissions, waitingItems, suspensions, memoryUsage, memoryPerItem]);

  // Format memory values
  const formattedMemoryUsage = BufferMonitorUtils.formatBytes(memoryUsage, 1).split(' ');
  const formattedMemoryPerItem = BufferMonitorUtils.formatBytes(memoryPerItem, 1).split(' ');

  // Define stat cards
  const statCards = [
    {
      label: 'Buffer',
      value: bufferSize,
      unit: `/ ${bufferCapacity}`,
      percentage: bufferPercentage,
      previousPercentage: prevBufferPercentage,
      cssClass: 'buffer'
    },
    {
      label: 'Emitted',
      value: totalEmissions.toLocaleString(),
      unit: 'items',
      percentage: emissionsPercentage,
      previousPercentage: prevEmissionsPercentage,
      cssClass: 'emitted'
    },
    {
      label: 'Waiting',
      value: waitingItems,
      unit: 'items',
      percentage: waitingPercentage,
      previousPercentage: prevWaitingPercentage,
      cssClass: 'waiting'
    },
    {
      label: 'Suspensions',
      value: suspensions,
      unit: 'events',
      percentage: suspensionsPercentage,
      previousPercentage: prevSuspensionsPercentage,
      cssClass: 'suspensions'
    },
    {
      label: 'Buffer Usage',
      value: formattedMemoryUsage[0],
      unit: formattedMemoryUsage[1],
      percentage: memoryPercentage,
      previousPercentage: prevMemoryPercentage,
      cssClass: 'usage'
    },
    {
      label: 'Avg Per Item',
      value: formattedMemoryPerItem[0],
      unit: formattedMemoryPerItem[1],
      percentage: memoryPerItemPercentage,
      previousPercentage: prevMemoryPerItemPercentage,
      cssClass: 'avg-item'
    }
  ];

  return (
    <div id="stats-display" className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-3 mb-6">
      {statCards.map((card, index) => (
        <StatCard key={index} {...card} />
      ))}
    </div>
  );
};

export default StatsDisplay;
