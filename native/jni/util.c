#include "util.h"

char *str_contact(const char *str1, const char *str2) {
    char *result;
    result = (char *) malloc(strlen(str1) + strlen(str2) + 1);
    if (!result) {
        printf("Error: malloc failed in concat! \n");
        exit(EXIT_FAILURE);
    }
    strcpy(result, str1);
    strcat(result, str2);
    return result;
}

int *pkcs5_padding(unsigned char **cipher, unsigned int mod, const char *message, unsigned int blocksize) {
    if( mod != 0 ) {
        unsigned int pad = blocksize - mod, i;
        char p[1];
        sprintf(p, "%c", pad);
        for(i=0; i<pad; i++) {
            strcat(*cipher, p);
        }
    }
    return 0;
}

int *pkcs5_unpadding(unsigned char **plain, unsigned int plain_len) {
    unsigned char dp[1], int_dp[1];
    sprintf(dp, "%c", *(*plain + plain_len -1));
    sprintf(int_dp, "%d", *dp);
    int j = atoi(int_dp);
    if(dp != NULL && j != 0) {
        if(j < plain_len) {
            int i;
            for(i=plain_len - j; i<plain_len; i++) {
                *(*plain + i) = '\0';
            }
        }
    }
    return 0;
}
