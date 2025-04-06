#ifndef SUSPENSION_TRACKING_H
#define SUSPENSION_TRACKING_H

#include <jni.h>
#include <string>
#include <mutex>

// Function declarations
void recordSuspension(JNIEnv* env, jlong threadId, jstring threadName, jlong bufferId, jint bufferSize, jint bufferCapacity);
jint getSuspensionCount();
jint getBufferSuspensionCount(jlong bufferId);
void clearSuspensionTracking();

// Extern declaration for global suspension state
extern std::mutex suspensionMutex;

#endif /* SUSPENSION_TRACKING_H */
