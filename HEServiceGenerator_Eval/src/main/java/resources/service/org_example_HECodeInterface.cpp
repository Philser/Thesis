#include "he_functions.h"
#include <jni.h>
#include "org_example_HECodeInterface.h"

void write_val_to_file(std::string filename, std::string val) {
  cout << "Writing ctxt to " << filename << "... " << endl;
	ofstream context_file;
	context_file.open(filename, ios::trunc);
	context_file << val;
	context_file.close();
	cout << "Done." << endl;
}

JNIEXPORT jstring JNICALL Java_jni_HECodeInterface_calculate
  (JNIEnv *env, jobject jobj, jobjectArray jstring_array) {

  int string_count = env->GetArrayLength(jstring_array);
  std::vector<std::string> string_ctxts;

  for (int i=0; i<string_count; i++) {
      jstring java_string = (jstring) (env->GetObjectArrayElement(jstring_array, i));
      const char *raw_string = env->GetStringUTFChars(java_string, 0);
      std::string val(raw_string);
      //write_val_to_file("/app/generated/test/ctxt" + std::to_string(i) + ".txt", val);
      string_ctxts.push_back(val);
      env->ReleaseStringUTFChars(java_string, raw_string);
  }

  std::string result = measure();
  cout << "Interface: Received result. Converting to char." << endl;
  const char *cstr = result.c_str();
  cout << "Done." << endl;
  cout << "Converting to jstring ... " << endl;
  jstring jstrBuf = env->NewStringUTF(cstr);
  cout << "Done.\nReturning..." << endl;
  return jstrBuf;
}