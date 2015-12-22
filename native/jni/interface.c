#include <android/log.h>
#include <jni.h>
#include <openssl/err.h>
#include <openssl/evp.h>
#include <openssl/pem.h>
#include <openssl/rsa.h>
#include <string.h>

#define LOG_TAG "JNI_LOG_TAG"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

jbyteArray Java_com_getui_logful_util_CryptoTool_security(JNIEnv *env,
                                                          jobject obj,
                                                          jbyteArray base_key_data,
                                                          jbyteArray rsa_key_data,
                                                          jint rsa_key_len) {
    jboolean a;
    jbyte *base_key_bytes = (*env)->GetByteArrayElements(env, base_key_data, &a);
    char *base_key = (char *) (base_key_bytes);

    jboolean b;
    jbyte *rsa_key_bytes = (*env)->GetByteArrayElements(env, rsa_key_data, &b);
    unsigned char *rsa_key = (unsigned char *) (rsa_key_bytes);

    LOGD("+++ step 01 +++");
    
    BIO *buffer;
    RSA *public_key;

    buffer = BIO_new_mem_buf((void *) rsa_key, (int) rsa_key_len);
    PEM_read_bio_RSAPublicKey(buffer, &public_key, 0, NULL);

    if (!RSA_check_key(public_key)) {
        return NULL;
    }

    LOGD("+++ step 02 +++");

    unsigned char *output = NULL;
    int encrypt_len = RSA_public_encrypt((int) strlen(base_key) + 1,
                                         (unsigned char *) base_key,
                                         output,
                                         public_key,
                                         RSA_PKCS1_PADDING);
    if (encrypt_len == -1) {
        return NULL;
    }
    
    LOGD("+++ step 03 +++");

    RSA_free(public_key);

    (*env)->ReleaseByteArrayElements(env, base_key_data, base_key_bytes, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, rsa_key_data, rsa_key_bytes, JNI_ABORT);

    jbyteArray result = (*env)->NewByteArray(env, encrypt_len);
    (*env)->SetByteArrayRegion(env, result, 0, encrypt_len, (jbyte *) output);

    return result;
}


jbyteArray Java_com_getui_logful_util_CryptoTool_encrypt(JNIEnv *env,
                                                         jobject obj,
                                                         jbyteArray base_key,
                                                         jbyteArray data,
                                                         jint data_len) {

    jboolean a;
    jbyte *key_byte = (*env)->GetByteArrayElements(env, base_key, &a);
    unsigned char *key = (unsigned char *) (key_byte);

    jboolean b;
    jbyte *data_byte = (*env)->GetByteArrayElements(env, data, &b);
    unsigned char *plain_text = (unsigned char *) (data_byte);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);

    if (!EVP_EncryptInit_ex(&ctx, EVP_aes_256_ecb(), NULL, key, NULL)) {
        return NULL;
    };

    if (!EVP_CIPHER_CTX_set_padding(&ctx, 0)) {
        return NULL;
    }

    unsigned char *cipher_text = (unsigned char *) malloc(data_len + EVP_CIPHER_CTX_block_size(&ctx));

    int bytes_written = 0;
    int ciphertext_len = 0;
    if (!EVP_EncryptUpdate(&ctx, cipher_text, &bytes_written, plain_text, data_len)) {
        return NULL;
    };
    ciphertext_len += bytes_written;

    if (!EVP_EncryptFinal_ex(&ctx, cipher_text + bytes_written, &bytes_written)) {

        return NULL;
    };
    ciphertext_len += bytes_written;

    (*env)->ReleaseByteArrayElements(env, base_key, key_byte, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, data, data_byte, JNI_ABORT);

    jbyteArray result = (*env)->NewByteArray(env, ciphertext_len);
    (*env)->SetByteArrayRegion(env, result, 0, ciphertext_len, (jbyte *) cipher_text);

    free(cipher_text);

    EVP_CIPHER_CTX_cleanup(&ctx);

    return result;
}
