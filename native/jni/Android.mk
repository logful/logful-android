LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := lib_crypto
LOCAL_SRC_FILES := $(LOCAL_PATH)/libs/libcrypto.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := logful
LOCAL_SRC_FILES := \
	base64.c \
	util.c \
	interface.c
LOCAL_LDLIBS += -llog
LOCAL_SHARED_LIBRARIES := lib_crypto
include $(BUILD_SHARED_LIBRARY)