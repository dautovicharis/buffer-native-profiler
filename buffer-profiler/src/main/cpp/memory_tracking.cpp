#include "memory_tracking.h"
#include "buffer_tracking.h"
#include "utils.h"
#include <iostream>
#include <sstream>
#include <algorithm>

void initializeMemoryTrackingWithJvm(JavaVM* vm) {
    jint result = initializeJvmti(vm);
    if (result == JNI_OK) {
        std::cout << "[Native] Memory monitoring initialized with JVMTI" << std::endl;
    } else {
        std::cout << "[Native] Memory monitoring initialization failed" << std::endl;
    }
}

// Get object size using JVMTI, or return a default value if not available
jlong getObjectSize(JNIEnv* env, jobject obj) {
    if (obj == nullptr) {
        return 0;
    }

    // Try to get size using JVMTI (precise measurement)
    if (isJvmtiAvailable()) {
        jlong size = getObjectSizeJvmti(obj);
        if (size > 0) {
            return size;
        }
    }

    // If JVMTI is not available or failed, return 0
    // This indicates that we don't have a valid measurement
    std::cout << "[Native] JVMTI not available, cannot measure object size" << std::endl;
    return 0; // No measurement available
}

// Get total memory usage for all tracked objects
jlong getTotalTrackedMemory() {
    jlong totalMemory = 0;
    for (const auto& pair : bufferMetrics) {
        totalMemory += pair.second.totalMemoryUsage;
    }
    return totalMemory;
}

// Clear all memory tracking data
void clearMemoryTracking() {
    memoryByClass.clear();
}
