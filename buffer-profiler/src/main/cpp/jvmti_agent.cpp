#include "jvmti_agent.h"
#include <iostream>
#include <string.h>

// Global JVMTI environment
static jvmtiEnv* jvmti = nullptr;
static bool jvmtiInitialized = false;

// Initialize JVMTI environment
jint initializeJvmti(JavaVM* vm) {
    if (jvmtiInitialized) {
        return JNI_OK; // Already initialized
    }

    // Get JVMTI environment
    jint result = vm->GetEnv((void**)&jvmti, JVMTI_VERSION_1_0);
    if (result != JNI_OK) {
        std::cerr << "[Native] Failed to get JVMTI environment: " << result << std::endl;
        return result;
    }

    // Enable capabilities
    jvmtiCapabilities capabilities;
    memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_tag_objects = 1;

    jvmtiError error = jvmti->AddCapabilities(&capabilities);
    if (error != JVMTI_ERROR_NONE) {
        std::cerr << "[Native] Failed to add JVMTI capabilities: " << error << std::endl;
        return JNI_ERR;
    }

    jvmtiInitialized = true;
    std::cout << "[Native] JVMTI initialized successfully" << std::endl;
    return JNI_OK;
}

// Check if JVMTI is available
bool isJvmtiAvailable() {
    return jvmtiInitialized && jvmti != nullptr;
}

// Get the actual size of a Java object using JVMTI
jlong getObjectSizeJvmti(jobject obj) {
    if (!isJvmtiAvailable() || obj == nullptr) {
        return 0;
    }

    jlong size = 0;
    jvmtiError error = jvmti->GetObjectSize(obj, &size);

    if (error != JVMTI_ERROR_NONE) {
        std::cerr << "[Native] Failed to get object size: " << error << std::endl;
        return 0;
    }

    std::cout << "[Native] JVMTI measured object size: " << size << " bytes" << std::endl;
    return size;
}

// Agent_OnLoad function - called when the agent is loaded
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
    std::cout << "[Native] JVMTI Agent loaded" << std::endl;
    return initializeJvmti(vm);
}

// Agent_OnUnload function - called when the agent is unloaded
JNIEXPORT void JNICALL Agent_OnUnload(JavaVM* vm) {
    std::cout << "[Native] JVMTI Agent unloaded" << std::endl;
    jvmti = nullptr;
    jvmtiInitialized = false;
}
