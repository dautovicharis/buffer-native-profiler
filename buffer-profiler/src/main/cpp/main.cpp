#include "main.h"
#include "jvmti_agent.h"
#include "buffer_tracking.h"
#include "memory_tracking.h"
#include "suspension_tracking.h"
#include "utils.h"
#include <iostream>
#include <unordered_map>
#include <vector>
#include <mutex>
#include <sstream>

// Global state
std::unordered_map<std::string, ClassMemoryInfo> memoryByClass;
std::mutex stateMutex;

// Initialization function for all native components
JNIEXPORT jboolean JNICALL
Java_NativeBufferMonitor_initialize(JNIEnv* env, jclass clazz) {
    std::lock_guard<std::mutex> lock(stateMutex);
    std::cout << "[Native] Initializing all native components" << std::endl;

    // Initialize buffer tracking
    initializeBufferTracking();
    std::cout << "[Native] Buffer tracking initialized" << std::endl;

    // Initialize memory tracking with JVMTI if possible
    JavaVM* vm;
    env->GetJavaVM(&vm);
    initializeMemoryTrackingWithJvm(vm);

    // Return whether JVMTI is available for precise tracking
    return isJvmtiAvailable() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jlong JNICALL
Java_NativeBufferMonitor_createBuffer(JNIEnv* env, jclass clazz, jint capacity) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return createBuffer(capacity);
}

JNIEXPORT void JNICALL
Java_NativeBufferMonitor_updateBufferMetrics(
    JNIEnv* env, jclass clazz, jlong bufferId, jint size, jlong memoryUsage
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    updateBufferMetrics(bufferId, size, memoryUsage);
}

JNIEXPORT void JNICALL
Java_NativeBufferMonitor_recordSuspension(
    JNIEnv* env, jclass clazz, jlong threadId, jstring threadName, jlong bufferId,
    jint bufferSize, jint bufferCapacity
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    recordSuspension(env, threadId, threadName, bufferId, bufferSize, bufferCapacity);
}

JNIEXPORT jlong JNICALL
Java_NativeBufferMonitor_getBufferMemoryUsage(JNIEnv* env, jclass clazz, jlong bufferId) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getBufferMemoryUsage(bufferId);
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getBufferSize(JNIEnv* env, jclass clazz, jlong bufferId) {
    std::lock_guard<std::mutex> lock(stateMutex);
    jint size = getBufferSize(bufferId);
    return size;
}

JNIEXPORT jlong JNICALL
Java_NativeBufferMonitor_getTotalTrackedMemory(JNIEnv* env, jclass clazz) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getTotalTrackedMemory();
}

JNIEXPORT jlong JNICALL
Java_NativeBufferMonitor_getObjectSize(JNIEnv* env, jclass clazz, jobject obj) {
    return getObjectSize(env, obj);
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getSuspensionCount(JNIEnv* env, jclass clazz) {
    return getSuspensionCount();
}

JNIEXPORT void JNICALL
Java_NativeBufferMonitor_recordEmission(
    JNIEnv* env, jclass clazz, jlong bufferId
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    recordEmission(bufferId);
}

JNIEXPORT void JNICALL
Java_NativeBufferMonitor_recordConsumption(
    JNIEnv* env, jclass clazz, jlong bufferId
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    recordConsumption(bufferId);
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getTotalEmissions(
    JNIEnv* env, jclass clazz
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getTotalEmissions();
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getTotalConsumptions(
    JNIEnv* env, jclass clazz
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getTotalConsumptions();
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getBufferEmissions(
    JNIEnv* env, jclass clazz, jlong bufferId
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getBufferEmissions(bufferId);
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getBufferConsumptions(
    JNIEnv* env, jclass clazz, jlong bufferId
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getBufferConsumptions(bufferId);
}

JNIEXPORT jint JNICALL
Java_NativeBufferMonitor_getBufferSuspensionCount(
    JNIEnv* env, jclass clazz, jlong bufferId
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    return getBufferSuspensionCount(bufferId);
}

JNIEXPORT void JNICALL
Java_NativeBufferMonitor_clearTracking(
    JNIEnv* env, jclass clazz
) {
    std::lock_guard<std::mutex> lock(stateMutex);
    // Clear buffer tracking data
    clearTracking();

    // Clear memory tracking data
    clearMemoryTracking();

    // Clear suspension events
    clearSuspensionTracking();

    std::cout << "[Native] All tracking data cleared" << std::endl;
}
