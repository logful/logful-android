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
                                                          jclass clz,
                                                          jbyteArray key_data,
                                                          jint key_len,
                                                          jbyteArray pwd_data,
                                                          jint pwd_len,
                                                          jbyteArray salt_data,
                                                          jint salt_len) {
    jboolean a;
    jbyte *key_bytes = (*env)->GetByteArrayElements(env, key_data, &a);
    char *key = (char *) (key_bytes);

    jboolean b;
    jbyte *pwd_bytes = (*env)->GetByteArrayElements(env, pwd_data, &b);
    char *pwd = (char *) (pwd_bytes);

    jboolean c;
    jbyte *salt_bytes = (*env)->GetByteArrayElements(env, salt_data, &c);
    unsigned char *salt = (unsigned char *) (salt_bytes);

    int aes_key_len = 32;
    unsigned char *aes_key = (unsigned char *) malloc(aes_key_len);
    if (!PKCS5_PBKDF2_HMAC_SHA1(pwd, (int) pwd_len, salt, (int) salt_len, 50, aes_key_len, aes_key)) {
        return NULL;
    }

    BIO *mem;
    mem = BIO_new(BIO_s_mem());
    BIO_puts(mem, key);

    RSA *pub_key = PEM_read_bio_RSA_PUBKEY(mem, NULL, NULL, NULL);
    if (pub_key == NULL) {
        return NULL;
    }
    BIO_free(mem);

    int length = RSA_size(pub_key);
    unsigned char *out = (unsigned char *) malloc(length);
    if (!RSA_public_encrypt(aes_key_len, aes_key, out, pub_key, RSA_PKCS1_PADDING)) {
        return NULL;
    }

    (*env)->ReleaseByteArrayElements(env, key_data, key_bytes, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, pwd_data, pwd_bytes, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, salt_data, salt_bytes, JNI_ABORT);

    jbyteArray result = (*env)->NewByteArray(env, length);
    (*env)->SetByteArrayRegion(env, result, 0, length, (jbyte *) out);

    return result;
}

jbyteArray Java_com_getui_logful_util_CryptoTool_encrypt(JNIEnv *env,
                                                         jclass clz,
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
