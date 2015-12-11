#include <string.h>
#include <jni.h>
#include "util.h"
#include "base64.h"
#include <openssl/evp.h>
#include <android/log.h>

#define KEY_PREFIX "A8P20vWlvfSu3JMO6tBjgr05UvjHAh2x"
#define LOG_TAG "JNI_LOG_TAG"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

const EVP_CIPHER *cipher;
const EVP_MD *dgst = NULL;
const unsigned char *salt = NULL;

static unsigned char *key;
static unsigned char *iv;

jbyteArray
    Java_com_getui_logful_util_CryptoTool_encrypt(JNIEnv *env,
                                                  jobject obj,
                                                  jstring app_id,
                                                  jstring text,
                                                  jint text_len) {
    if (key == NULL || sizeof(key) == 0 || iv == NULL || sizeof(iv) == 0) {
        const char *pkg_char = (*env)->GetStringUTFChars(env, app_id, NULL);
        char *key_contact = str_contact(pkg_char, KEY_PREFIX);
        char *key_char = base64_encode(key_contact, (int) strlen(key_contact));

        unsigned char key_tmp[EVP_MAX_KEY_LENGTH];
        unsigned char iv_tmp[EVP_MAX_IV_LENGTH];

        OpenSSL_add_all_algorithms();

        cipher = EVP_get_cipherbyname("aes-256-cbc");
        dgst = EVP_get_digestbyname("md5");

        if (!cipher) {
            return NULL;
        }

        if (!dgst) {
            return NULL;
        }

        if (!EVP_BytesToKey(cipher, dgst, salt,
                            (unsigned char *) key_char,
                            (int) strlen(key_char), 1, key_tmp, iv_tmp)) {
            return NULL;
        }

        key = (unsigned char *) malloc(EVP_MAX_KEY_LENGTH);
        iv = (unsigned char *) malloc(EVP_MAX_IV_LENGTH);

        memcpy(key, key_tmp, EVP_MAX_KEY_LENGTH);
        memcpy(iv, iv_tmp, EVP_MAX_IV_LENGTH);

        free(key_contact);
        free(key_char);
        (*env)->ReleaseStringUTFChars(env, app_id, pkg_char);
    }

    if (key != NULL && sizeof(key) != 0 && iv != NULL && sizeof(iv) != 0) {
        const char *input = (*env)->GetStringUTFChars(env, text, NULL);

        EVP_CIPHER_CTX ctx;
        EVP_CIPHER_CTX_init(&ctx);

        if (!EVP_EncryptInit_ex(&ctx, EVP_aes_256_cbc(), NULL, key, iv)) {
            return NULL;
        };

        int input_len = text_len + 1;
        unsigned char *cipher_text = (unsigned char *) malloc(input_len + EVP_CIPHER_CTX_block_size(&ctx));

        int bytes_written = 0;
        int ciphertext_len = 0;
        if (!EVP_EncryptUpdate(&ctx, cipher_text, &bytes_written, (unsigned char *) input, input_len)) {
            return NULL;
        };
        ciphertext_len += bytes_written;

        if (!EVP_EncryptFinal_ex(&ctx, cipher_text + bytes_written, &bytes_written)) {
            return NULL;
        };
        ciphertext_len += bytes_written;

        EVP_CIPHER_CTX_cleanup(&ctx);

        jbyteArray data = (*env)->NewByteArray(env, ciphertext_len);
        (*env)->SetByteArrayRegion(env, data, 0, ciphertext_len, (jbyte *) cipher_text);

        (*env)->ReleaseStringUTFChars(env, text, input);
        free(cipher_text);
        return data;
    }

    return NULL;
}
