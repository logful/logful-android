#ifndef _BASE64_H_
#define _BASE64_H_

static const char base[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
char* base64_encode(const char*, int);
char* base64_decode(const char*, int);
static char find_pos(char);

#endif
