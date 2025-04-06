#include "suspension_tracking.h"
#include "buffer_tracking.h"
#include "utils.h"
#include <iostream>
#include <sstream>
#include <chrono>

// Global variables
std::mutex suspensionMutex;

// Record a suspension event for a specific buffer and thread
void recordSuspension(JNIEnv* env, jlong threadId, jstring threadName, jlong bufferId, jint bufferSize, jint bufferCapacity) {
    std::lock_guard<std::mutex> lock(suspensionMutex);

    // Convert jstring to std::string for logging
    std::string threadNameStr = jstringToString(env, threadName);

    // Increment the suspension counter in BufferMetrics
    auto it = bufferMetrics.find(bufferId);
    if (it != bufferMetrics.end()) {
        it->second.totalSuspensions++;
    }

    std::cout << "[Native] Thread " << threadNameStr
              << " suspended due to buffer full (" << bufferSize << "/" << bufferCapacity << ")" << std::endl;
}

// Get the total number of suspension events across all buffers
jint getSuspensionCount() {
    std::lock_guard<std::mutex> lock(suspensionMutex);

    // Sum up totalSuspensions from all BufferMetrics objects
    jint total = 0;
    for (const auto& pair : bufferMetrics) {
        total += pair.second.totalSuspensions;
    }
    return total;
}

// Get the number of suspension events for a specific buffer
jint getBufferSuspensionCount(jlong bufferId) {
    std::lock_guard<std::mutex> lock(suspensionMutex);

    // Use the counter from BufferMetrics instead of iterating through the vector
    auto it = bufferMetrics.find(bufferId);
    if (it != bufferMetrics.end()) {
        return it->second.totalSuspensions;
    }

    return 0;
}

// Clear all suspension tracking data
void clearSuspensionTracking() {
    std::lock_guard<std::mutex> lock(suspensionMutex);

    // Reset the totalSuspensions counters in all BufferMetrics objects
    for (auto& pair : bufferMetrics) {
        pair.second.totalSuspensions = 0;
    }

    std::cout << "[Native] Suspension tracking data cleared" << std::endl;
}
