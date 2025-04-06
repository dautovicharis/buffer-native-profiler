#include "utils.h"
#include <chrono>

// Helper function to convert Java string to C++ string
std::string jstringToString(JNIEnv* env, jstring jStr) {
    if (!jStr) return "";
    const char* cstr = env->GetStringUTFChars(jStr, nullptr);
    std::string str(cstr);
    env->ReleaseStringUTFChars(jStr, cstr);
    return str;
}

// Get current timestamp in nanoseconds for higher precision
jlong getCurrentTimeMs() {
    return std::chrono::steady_clock::now().time_since_epoch().count();
}
