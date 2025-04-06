#ifndef UTILS_H
#define UTILS_H

#include <jni.h>
#include <string>

// Utility function declarations
std::string jstringToString(JNIEnv* env, jstring jStr);
// Returns current time in nanoseconds for high precision
jlong getCurrentTimeMs();

#endif /* UTILS_H */
