#ifndef JVMTI_AGENT_H
#define JVMTI_AGENT_H

#include <jvmti.h>

// Initialize JVMTI environment
jint initializeJvmti(JavaVM* vm);

// Get the actual size of a Java object using JVMTI
jlong getObjectSizeJvmti(jobject obj);

// Check if JVMTI is available
bool isJvmtiAvailable();

#endif /* JVMTI_AGENT_H */
