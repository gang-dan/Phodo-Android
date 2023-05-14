#include <jni.h>

// empty

extern "C"
JNIEXPORT void JNICALL
Java_com_example_phodo_MainActivity_detectEdgeJNI(JNIEnv *env, jobject thiz, jlong input_image,
                                                  jlong output_image, jint th1, jint th2) {
    // TODO: implement detectEdgeJNI()
}