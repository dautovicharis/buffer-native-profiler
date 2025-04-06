#include "buffer_tracking.h"
#include "utils.h"
#include <iostream>
#include <sstream>
#include <chrono>

// Global map to store buffer metrics
std::unordered_map<jlong, BufferMetrics> bufferMetrics;

// Initialize buffer tracking system
void initializeBufferTracking() {
    bufferMetrics.clear();
    std::cout << "[Native] Buffer tracking initialized" << std::endl;
}

// Create a new buffer with the specified capacity
jlong createBuffer(jint capacity) {
    jlong now = getCurrentTimeMs();
    jlong bufferId = now; // Use current time as buffer ID

    // Initialize buffer metrics
    BufferMetrics metrics;
    metrics.capacity = capacity;
    metrics.size = 0;
    metrics.totalMemoryUsage = 0;
    metrics.creationTime = now;
    metrics.lastUpdateTime = now;
    metrics.totalEmissions = 0;
    metrics.totalConsumptions = 0;
    metrics.totalSuspensions = 0;

    // Store in global map
    bufferMetrics[bufferId] = metrics;

    std::cout << "[Native] Created buffer with id " << bufferId << " and capacity " << capacity << std::endl;
    return bufferId;
}

// Get the total memory usage for a specific buffer
jlong getBufferMemoryUsage(jlong bufferId) {
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in getBufferMemoryUsage" << std::endl;
        return 0;
    }

    jlong totalMemory = it->second.totalMemoryUsage;

    std::cout << "[Native] Buffer " << bufferId << " memory usage: "
              << totalMemory << " bytes" << std::endl;

    return totalMemory;
}

// Get the current size (number of entries) for a specific buffer
jint getBufferSize(jlong bufferId) {
    // Check if the buffer exists
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in getBufferSize" << std::endl;
        return 0;
    }

    // Return the size from metrics
    jint size = it->second.size;

    std::cout << "[Native] Buffer " << bufferId << " size: "
              << size << " entries" << std::endl;

    return size;
}

// Update metrics for a specific buffer
void updateBufferMetrics(jlong bufferId, jint size, jlong memoryUsage) {
    jlong now = getCurrentTimeMs();

    // Check if the buffer exists
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in updateBufferMetrics, creating it" << std::endl;
        // Create a new buffer with default capacity
        BufferMetrics metrics;
        metrics.capacity = 20; // Default capacity
        metrics.size = size;
        metrics.totalMemoryUsage = memoryUsage;
        metrics.creationTime = now;
        metrics.lastUpdateTime = now;
        metrics.totalEmissions = 0;
        metrics.totalConsumptions = 0;
        metrics.totalSuspensions = 0;
        bufferMetrics[bufferId] = metrics;
    } else {
        // Update existing buffer metrics
        BufferMetrics& metrics = it->second;
        metrics.size = size;
        metrics.totalMemoryUsage = memoryUsage;
        metrics.lastUpdateTime = now;
    }

    std::cout << "[Native] Updated metrics for buffer " << bufferId
              << ", size: " << size << " entries"
              << ", memory: " << memoryUsage << " bytes" << std::endl;
}

// Record an emission event for a specific buffer
void recordEmission(jlong bufferId) {
    // Check if the buffer exists
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in recordEmission" << std::endl;
        return;
    }

    // Increment emission counter
    BufferMetrics& metrics = it->second;
    metrics.totalEmissions++;

    std::cout << "[Native] Recorded emission for buffer " << bufferId
              << ", total emissions: " << metrics.totalEmissions << std::endl;
}

// Record a consumption event for a specific buffer
void recordConsumption(jlong bufferId) {
    // Check if the buffer exists
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in recordConsumption" << std::endl;
        return;
    }

    // Increment consumption counter
    BufferMetrics& metrics = it->second;
    metrics.totalConsumptions++;

    std::cout << "[Native] Recorded consumption for buffer " << bufferId
              << ", total consumptions: " << metrics.totalConsumptions << std::endl;
}

// Get the total number of emissions across all buffers
jint getTotalEmissions() {
    jint total = 0;
    for (const auto& pair : bufferMetrics) {
        total += pair.second.totalEmissions;
    }
    return total;
}

// Get the total number of consumptions across all buffers
jint getTotalConsumptions() {
    jint total = 0;
    for (const auto& pair : bufferMetrics) {
        total += pair.second.totalConsumptions;
    }
    return total;
}

// Get the number of emissions for a specific buffer
jint getBufferEmissions(jlong bufferId) {
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in getBufferEmissions" << std::endl;
        return 0;
    }
    return it->second.totalEmissions;
}

// Get the number of consumptions for a specific buffer
jint getBufferConsumptions(jlong bufferId) {
    auto it = bufferMetrics.find(bufferId);
    if (it == bufferMetrics.end()) {
        std::cout << "[Native] Warning: Buffer " << bufferId << " not found in getBufferConsumptions" << std::endl;
        return 0;
    }
    return it->second.totalConsumptions;
}

// Clear all buffer tracking data
void clearTracking() {
    bufferMetrics.clear();
}
