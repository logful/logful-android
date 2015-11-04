#include "stdio.h"
#include "string.h"
#include "stdlib.h"

char *str_contact(const char *str1, const char *str2);
int *pkcs5_padding(unsigned char **cipher, unsigned int mod, const char *message, unsigned int blocksize);
int *pkcs5_unpadding(unsigned char **plain, unsigned int plain_len);