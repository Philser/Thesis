#ifndef _Included_HE_functions
#define _Included_HE_functions

#include <vector>
#include <NTL/ZZ.h>
#include <NTL/BasicThreadPool.h>
#include "FHE.h"
#include "timing.h"
#include "EncryptedArray.h"
#include <NTL/lzz_pXFactoring.h>
#include "chrono"
#include <fstream>
#include <iostream>

using namespace std;
using namespace NTL;

string calculate (vector<string> string_ctxts);

#endif