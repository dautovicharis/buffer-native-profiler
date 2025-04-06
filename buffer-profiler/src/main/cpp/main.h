#ifndef MAIN_H
#define MAIN_H

#include <jni.h>

// JNI method declarations
extern "C" {
    // Initialization method
    JNIEXPORT jboolean JNICALL Java_NativeBufferMonitor_initialize(JNIEnv*, jclass);
    JNIEXPORT jlong JNICALL Java_NativeBufferMonitor_createBuffer(JNIEnv*, jclass, jint);
    JNIEXPORT void JNICALL Java_NativeBufferMonitor_updateBufferMetrics(JNIEnv*, jclass, jlong, jint, jlong);
    JNIEXPORT void JNICALL Java_NativeBufferMonitor_recordSuspension(JNIEnv*, jclass, jlong, jstring, jlong, jint, jint);

    // Memory measurement methods
    JNIEXPORT jlong JNICALL Java_NativeBufferMonitor_getObjectSize(JNIEnv*, jclass, jobject);
    JNIEXPORT jlong JNICALL Java_NativeBufferMonitor_getBufferMemoryUsage(JNIEnv*, jclass, jlong);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getBufferSize(JNIEnv*, jclass, jlong);
    JNIEXPORT jlong JNICALL Java_NativeBufferMonitor_getTotalTrackedMemory(JNIEnv*, jclass);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getSuspensionCount(JNIEnv*, jclass);

    // Emission and consumption tracking methods
    JNIEXPORT void JNICALL Java_NativeBufferMonitor_recordEmission(JNIEnv*, jclass, jlong);
    JNIEXPORT void JNICALL Java_NativeBufferMonitor_recordConsumption(JNIEnv*, jclass, jlong);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getTotalEmissions(JNIEnv*, jclass);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getTotalConsumptions(JNIEnv*, jclass);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getBufferEmissions(JNIEnv*, jclass, jlong);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getBufferConsumptions(JNIEnv*, jclass, jlong);
    JNIEXPORT jint JNICALL Java_NativeBufferMonitor_getBufferSuspensionCount(JNIEnv*, jclass, jlong);

    // Reset methods
    JNIEXPORT void JNICALL Java_NativeBufferMonitor_clearTracking(JNIEnv*, jclass);
}

#endif /* MAIN_H */
