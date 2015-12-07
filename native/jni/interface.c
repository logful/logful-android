#include <string.h>
#include <jni.h>
#include "util.h"
#include "base64.h"
#include <openssl/evp.h>
#include <android/log.h>

#define KEY_PREFIX "A8P20vWlvfSu3JMO6tBjgr05UvjHAh2x"
#define LOG_TAG "JNI_LOG_TAG"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

const EVP_CIPHER *cipher;
const EVP_MD *dgst = NULL;
const unsigned char *salt = NULL;

jbyteArray
Java_com_getui_logful_util_CryptoTool_encrypt(JNIEnv *env,
                                           jobject obj,
                                           jstring pkg_name,
                                           jstring content) {
    const char *pkg_char = (*env)->GetStringUTFChars(env, pkg_name, NULL);
    char *key_contact = str_contact(pkg_char, KEY_PREFIX);
    char *key_char = base64_encode(key_contact, strlen(key_contact));

    const char *input = (*env)->GetStringUTFChars(env, content, NULL);

    unsigned char key[EVP_MAX_KEY_LENGTH];
    unsigned char iv[EVP_MAX_IV_LENGTH];

    OpenSSL_add_all_algorithms();

    cipher = EVP_get_cipherbyname("aes-256-cbc");
    dgst = EVP_get_digestbyname("md5");

    if (!cipher) {
        LOGE("ENCRYPT_GET_CIPHER_ERROR");
        return NULL;
    }

    if (!dgst) {
        LOGE("ENCRYPT_GET_DIGEST_ERROR");
        return NULL;
    }

    if (!EVP_BytesToKey(cipher, dgst, salt,
                        (unsigned char *) key_char,
                        strlen(key_char), 1, key, iv)) {
        LOGE("ENCRYPT_BYTES_TO_KEY_ERROR");
        return NULL;
    }

    int input_len;
    unsigned char *cipher_text;

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);

    if (!EVP_EncryptInit_ex(&ctx, EVP_aes_256_cbc(), NULL, key, iv)) {
        LOGE("ENCRYPT_INIT_ERROR");
        return NULL;
    };

    input_len = strlen(input) + 1;
    cipher_text = (unsigned char *) malloc(input_len + EVP_CIPHER_CTX_block_size(&ctx));

    int bytes_written = 0;
    int ciphertext_len = 0;
    if (!EVP_EncryptUpdate(&ctx, cipher_text, &bytes_written, input, input_len)) {
        LOGE("ENCRYPT_UPDATE_ERROR");
        return NULL;
    };
    ciphertext_len += bytes_written;

    if (!EVP_EncryptFinal_ex(&ctx, cipher_text + bytes_written, &bytes_written)) {
        LOGE("ENCRYPT_FINAL_ERROR");
        return NULL;
    };
    ciphertext_len += bytes_written;

    EVP_CIPHER_CTX_cleanup(&ctx);

    jbyteArray data = (*env)->NewByteArray(env, ciphertext_len);
    (*env)->SetByteArrayRegion(env, data, 0, ciphertext_len, cipher_text);

    (*env)->ReleaseStringUTFChars(env, pkg_name, pkg_char);
    (*env)->ReleaseStringUTFChars(env, content, input);

    free(cipher_text);
    free(key_contact);
    free(key_char);

    return data;
}
