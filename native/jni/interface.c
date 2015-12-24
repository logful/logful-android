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

#define AES_KEY_SIZE 32

int calculate_aes_key(const char *pwd, int pwd_len, const unsigned char *salt, int salt_len);

static unsigned char *aes_key = NULL;

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

    if (!calculate_aes_key(pwd, (int) pwd_len, salt, (int) salt_len)) {
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
    if (!RSA_public_encrypt(AES_KEY_SIZE, aes_key, out, pub_key, RSA_PKCS1_PADDING)) {
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
                                                         jbyteArray pwd_data,
                                                         jint pwd_len,
                                                         jbooleanArray salt_data,
                                                         jint salt_len,
                                                         jbyteArray data,
                                                         jint data_len) {

    jboolean a;
    jbyte *pwd_bytes = (*env)->GetByteArrayElements(env, pwd_data, &a);
    char *pwd = (char *) (pwd_bytes);

    jboolean b;
    jbyte *salt_bytes = (*env)->GetByteArrayElements(env, salt_data, &b);
    unsigned char *salt = (unsigned char *) (salt_bytes);

    if (!calculate_aes_key(pwd, (int) pwd_len, salt, (int) salt_len)) {
        (*env)->ReleaseByteArrayElements(env, pwd_data, pwd_bytes, JNI_ABORT);
        (*env)->ReleaseByteArrayElements(env, salt_data, salt_bytes, JNI_ABORT);
        return NULL;
    }

    (*env)->ReleaseByteArrayElements(env, pwd_data, pwd_bytes, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, salt_data, salt_bytes, JNI_ABORT);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);

    if (!EVP_EncryptInit_ex(&ctx, EVP_aes_256_ecb(), NULL, aes_key, NULL)) {
        return NULL;
    };

    if (!EVP_CIPHER_CTX_set_padding(&ctx, 0)) {
        return NULL;
    }

    jboolean c;
    jbyte *data_bytes = (*env)->GetByteArrayElements(env, data, &c);
    unsigned char *plain_text = (unsigned char *) (data_bytes);

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

    (*env)->ReleaseByteArrayElements(env, data, data_bytes, JNI_ABORT);

    jbyteArray result = (*env)->NewByteArray(env, ciphertext_len);
    (*env)->SetByteArrayRegion(env, result, 0, ciphertext_len, (jbyte *) cipher_text);

    free(cipher_text);

    EVP_CIPHER_CTX_cleanup(&ctx);

    return result;
}

int calculate_aes_key(const char *pwd, int pwd_len, const unsigned char *salt, int salt_len) {
    if (aes_key != NULL) {
        return 1;
    }
    aes_key = (unsigned char *) malloc(AES_KEY_SIZE);
    if (!PKCS5_PBKDF2_HMAC_SHA1(pwd, pwd_len, salt, salt_len, 50, AES_KEY_SIZE, aes_key)) {
        free(aes_key);
        return 0;
    }
    return 1;
}
