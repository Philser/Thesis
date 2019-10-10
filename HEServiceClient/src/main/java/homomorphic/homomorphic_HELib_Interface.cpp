#include <jni.h>
#include "homomorphic_HELib_Interface.h"
#include "FHE.h"

using namespace std;
using namespace NTL;

void convert_ctxt_to_string(Ctxt& ctxt, string& target_string) {
    stringstream ss;
    ss << ctxt;
    target_string = ss.str();
    ss.flush();
}

void convert_string_to_ctxt(string value, Ctxt& ctxt) {
    stringstream ss(value);
    ss >> ctxt;
    ss.flush();
}

FHESecKey read_secret_key_from_file(FHEcontext* context, string secret_key_file_location) {
	ifstream secKey_file;
	secKey_file.open(secret_key_file_location);
	FHESecKey secKey((*context));
	readSecKeyBinary(secKey_file, secKey);
	secKey_file.close();
	return secKey;
}

JNIEXPORT jstring JNICALL Java_homomorphic_HELib_1Interface_decryptCtxt
  (JNIEnv *env, jobject jobj, jstring jctxt_string, jboolean needs_bootstrapping) {

    // read context
    unsigned long p = 2;
    unsigned long r = 16;
    unsigned long m = 0;
    vector<long> gens;
    vector<long> ords;

    string context_filename, secKey_filename;
    // if((bool) (needs_bootstrapping != JNI_FALSE)) {
    //     context_filename = "context_bootstrappable.txt";
    //     secKey_filename = "secKey_bootstrappable.txt";
    // } else {
        context_filename = "context.txt";
        secKey_filename = "secKey.txt";
    // }

	ifstream context_file;
	context_file.open(context_filename);
	readContextBaseBinary(context_file, m, p, r, gens, ords);
	FHEcontext context(m, p, r, gens, ords);
	readContextBinary(context_file, context);
	context_file.close();
    
    // init keys
    FHESecKey secKey = read_secret_key_from_file(&context, secKey_filename);
    FHEPubKey& pubKey = secKey;

    // convert string to ctxt object
    const char* raw_string = env->GetStringUTFChars(jctxt_string, 0);
    string ctxt_string(raw_string);
    Ctxt c1(pubKey);
    convert_string_to_ctxt(ctxt_string, c1);
    // decrypt
    ZZX value_result;
	secKey.Decrypt(value_result, c1);

	long plain_result;
	conv(plain_result, value_result[0]);
    const char* plain_result_string = to_string(plain_result).c_str();
    
    // return string
    return env->NewStringUTF(plain_result_string);
}


  JNIEXPORT jstring JNICALL Java_homomorphic_HELib_1Interface_encryptPlain
  (JNIEnv *env, jobject jobj, jstring jplain_string, jboolean needs_bootstrapping) {
    // read context
    unsigned long p = 2;
    unsigned long r = 16;
    unsigned long m = 0;
    vector<long> gens;
    vector<long> ords;

	ifstream context_file;
    string context_filename, secKey_filename;
    // if((bool) (needs_bootstrapping != JNI_FALSE)) {
    //     context_filename = "context_bootstrappable.txt";
    //     secKey_filename = "secKey_bootstrappable.txt";
    // } else {
        context_filename = "context.txt";
        secKey_filename = "secKey.txt";
    // }

    context_file.open(context_filename);
	readContextBaseBinary(context_file, m, p, r, gens, ords);
	FHEcontext context(m, p, r, gens, ords);
	readContextBinary(context_file, context);
	context_file.close();

    // init keys
    FHESecKey secKey = read_secret_key_from_file(&context, secKey_filename);
    FHEPubKey& pubKey = secKey;

    // encrypt
    const char* raw_string = env->GetStringUTFChars(jplain_string, 0);
    string plain_val_string(raw_string);
    long plain_value = stol(plain_val_string, nullptr);
    Ctxt c1(pubKey);
    pubKey.Encrypt(c1, to_ZZX(plain_value));
    string ctxt_string = "";

    // convert ctxt to string
    convert_ctxt_to_string(c1, ctxt_string);
    env->ReleaseStringUTFChars(jplain_string, raw_string);

    // return string
    return env->NewStringUTF(ctxt_string.c_str()); 
  }

