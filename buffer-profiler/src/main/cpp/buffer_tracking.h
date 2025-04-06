#ifndef BUFFER_TRACKING_H
#define BUFFER_TRACKING_H

#include <jni.h>
#include <vector>
#include <unordered_map>
#include <mutex>

// Data structures for buffer tracking
struct BufferMetrics {
    jint capacity;          // Maximum capacity of the buffer
    jint size;              // Current number of entries
    jlong totalMemoryUsage; // Total memory usage of all entries
    jlong creationTime;     // When the buffer was created
    jlong lastUpdateTime;   // Last time the buffer was updated
    jint totalEmissions;    // Total number of items emitted
    jint totalConsumptions; // Total number of items consumed
    jint totalSuspensions;  // Total number of suspensions
};

// Function declarations
void initializeBufferTracking();
jlong createBuffer(jint capacity);
jlong getBufferMemoryUsage(jlong bufferId);
jint getBufferSize(jlong bufferId);
void updateBufferMetrics(jlong bufferId, jint size, jlong memoryUsage);
void recordEmission(jlong bufferId);
void recordConsumption(jlong bufferId);
jint getTotalEmissions();
jint getTotalConsumptions();
jint getBufferEmissions(jlong bufferId);
jint getBufferConsumptions(jlong bufferId);
void clearTracking();

// Extern declaration for global buffer state
extern std::unordered_map<jlong, BufferMetrics> bufferMetrics;

#endif /* BUFFER_TRACKING_H */
