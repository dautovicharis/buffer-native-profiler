cmake_minimum_required(VERSION 3.10)

# Get version from Gradle (passed via -DPROJECT_VERSION)
project(flow_buffer_monitor VERSION ${PROJECT_VERSION} LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Add compiler flags
if(CMAKE_CXX_COMPILER_ID MATCHES "GNU|Clang")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -pedantic")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O2")
elseif(MSVC)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /W4")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /O2")
endif()

# Find JNI package
find_package(JNI REQUIRED)

# Include directories
include_directories(${JNI_INCLUDE_DIRS})

# Create the shared library
add_library(buffermonitor SHARED
    main.cpp
    buffer_tracking.cpp
    memory_tracking.cpp
    suspension_tracking.cpp
    utils.cpp
    jvmti_agent.cpp
)

# ARTIFACT_ID is passed from Gradle as the project name

# Create custom output name with artifact-version format
if(WIN32)
    set(LIB_SUFFIX ".dll")
elseif(APPLE)
    set(LIB_SUFFIX ".dylib")
else()
    set(LIB_SUFFIX ".so")
endif()

# Set library properties with simpler naming convention
set_target_properties(buffermonitor PROPERTIES
    POSITION_INDEPENDENT_CODE ON
    # Use a simpler naming convention: lib_{project.name}-{project.version}
    OUTPUT_NAME "${ARTIFACT_ID}-${PROJECT_VERSION}"
)

# Link against JNI libraries
target_link_libraries(buffermonitor ${JNI_LIBRARIES})

# Print debug information
message(STATUS "JNI_INCLUDE_DIRS: ${JNI_INCLUDE_DIRS}")
message(STATUS "JNI_LIBRARIES: ${JNI_LIBRARIES}")

# Define output directories for the library
set(RESOURCES_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../resources")
set(BRIDGE_RESOURCES_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../buffer-profiler-bridge/src/main/resources")

# Define the custom library name
set(LIB_NAME "${ARTIFACT_ID}-${PROJECT_VERSION}${LIB_SUFFIX}")

# Custom installation rules
add_custom_command(TARGET buffermonitor POST_BUILD
    # Create directories again just to be sure
    COMMAND ${CMAKE_COMMAND} -E make_directory ${RESOURCES_DIR}
    COMMAND ${CMAKE_COMMAND} -E make_directory ${BRIDGE_RESOURCES_DIR}

    # Copy to resources directory with custom name
    COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:buffermonitor> ${RESOURCES_DIR}/${LIB_NAME}
    # Copy to bridge resources directory with custom name
    COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:buffermonitor> ${BRIDGE_RESOURCES_DIR}/${LIB_NAME}
)

# Standard installation rules (optional)
install(TARGETS buffermonitor
    LIBRARY DESTINATION lib
    RUNTIME DESTINATION bin
)