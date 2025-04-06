#ifndef MEMORY_TRACKING_H
#define MEMORY_TRACKING_H

#include <jni.h>
#include "jvmti_agent.h"
#include <string>
#include <unordered_map>
#include <mutex>

// Memory tracking structures
struct ClassMemoryInfo {
    std::string className;
    jlong instanceCount;
    jlong totalMemory;
    jlong lastUpdateTime;
};

// Function declarations
void initializeMemoryTrackingWithJvm(JavaVM* vm);
jlong getObjectSize(JNIEnv* env, jobject obj);
jlong getTotalTrackedMemory();
void clearMemoryTracking();

// Extern declaration for global memory state
extern std::unordered_map<std::string, ClassMemoryInfo> memoryByClass;

#endif /* MEMORY_TRACKING_H */
