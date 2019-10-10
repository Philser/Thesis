#include <NTL/BasicThreadPool.h>
#include "FHE.h"
#include "EncryptedArray.h"
#include "matmul.h"
#include "debugging.h"
#include "heuristics.h"

using namespace std;
using namespace NTL;

static long mValues[][14] = { 
//{ p, phi(m),  m,    d, m1,  m2, m3,   g1,    g2,    g3,ord1,ord2,ord3, c_m}
  {  2,  126, 127,   7,  127,  1, 0, 108,  24, 0,  6,-3,0, 100},
  {  2,    48,   105, 12,  3,  35,  0,    71,    76,    0,  2,  2,   0, 100},
  {  2,   600,  1023, 10, 11,  93,  0,   838,   584,    0, 10,  6,   0, 100}, // m=(3)*11*{31} m/phim(m)=1.7    C=24  D=2 E=1
  {  2,  1200,  1705, 20, 11, 155,  0,   156,   936,    0, 10,  6,   0, 100}, // m=(5)*11*{31} m/phim(m)=1.42   C=34  D=2 E=2
  {  2,  1728,  4095, 12,  7,  5, 117,  2341,  3277, 3641,  6,  4,   6, 100}, // m=(3^2)*5*7*{13} m/phim(m)=2.36 C=26 D=3 E=2
  {  2, 12800, 17425, 40, 41, 425,  0,  5951,  8078,    0, 40, -8,   0, 100}, // m=(5^2)*{17}*41 m/phim(m)=1.36 C=93  D=3 E=3
  {  2, 15004, 15709, 22, 23, 683,  0,  4099, 13663,    0, 22, 31,   0, 100}, // m=23*(683) m/phim(m)=1.04      C=73  D=2 E=1
  {  2, 18000, 18631, 25, 31, 601,  0, 15627,  1334,    0, 30, 24,   0, 100}, // m=31*(601) m/phim(m)=1.03      C=77  D=2 E=0
  {  2, 18816, 24295, 28, 43, 565,  0, 16386, 16427,    0, 42, 16,   0, 100}, // m=(5)*43*{113} m/phim(m)=1.29  C=84  D=2 E=2
  {  2, 21168, 27305, 28, 43, 635,  0, 10796, 26059,    0, 42, 18,   0, 100}, // m=(5)*43*{127} m/phim(m)=1.28  C=86  D=2 E=2
  {  2, 23040, 28679, 24, 17,  7, 241, 15184,  4098,28204, 16,  6, -10, 200}, // m=7*17*(241) m/phim(m)=1.24    C=63  D=4 E=3
  {  2, 24000, 31775, 20, 41, 775,  0,  6976, 24806,    0, 40, 30,   0, 100}, // m=(5^2)*{31}*41 m/phim(m)=1.32 C=88  D=2 E=2
  {  2, 26400, 27311, 55, 31, 881,  0, 21145,  1830,    0, 30, 16,   0, 100}, // m=31*(881) m/phim(m)=1.03      C=99  D=2 E=0
  {  2, 27000, 32767, 15, 31,  7, 151, 11628, 28087,25824, 30,  6, -10, 200},
  {  2, 31104, 35113, 36, 37, 949,  0, 16134,  8548,    0, 36, 24,   0, 300}, // m=(13)*37*{73} m/phim(m)=1.12  C=94  D=2 E=2
  {  2, 34848, 45655, 44, 23, 1985, 0, 33746, 27831,    0, 22, 36,   0, 100}, // m=(5)*23*{397} m/phim(m)=1.31  C=100 D=2 E=2
  {  2, 42336, 42799, 21, 127, 337, 0, 25276, 40133,    0,126, 16,   0, 200}, // m=127*(337) m/phim(m)=1.01     C=161 D=2 E=0
  {  2, 45360, 46063, 45, 73, 631,  0, 35337, 20222,    0, 72, 14,   0, 100}, // m=73*(631) m/phim(m)=1.01      C=129 D=2 E=0
  {  2, 46080, 53261, 24, 17, 13, 241, 43863, 28680,15913, 16, 12, -10, 100}, // m=13*17*(241) m/phim(m)=1.15   C=69  D=4 E=3
  {  2, 49500, 49981, 30, 151, 331, 0,  6952, 28540,    0,150, 11,   0, 100}, // m=151*(331) m/phim(m)=1        C=189 D=2 E=1
  {  2, 54000, 55831, 25, 31, 1801, 0, 19812, 50593,    0, 30, 72,   0, 100}, // m=31*(1801) m/phim(m)=1.03     C=125 D=2 E=0
  {  2, 60016, 60787, 22, 89, 683,  0,  2050, 58741,    0, 88, 31,   0, 200}, // m=89*(683) m/phim(m)=1.01      C=139 D=2 E=1

  {  7,    36,    57,  3,  3,  19,  0,    20,    40,    0,  2, -6,   0, 100}, // m=3*(19) :-( m/phim(m)=1.58 C=14 D=3 E=0

  { 17,    48,   105, 12,  3,  35,  0,    71,    76,    0,  2,  2,   0, 100}, // m=3*(5)*{7} m/phim(m)=2.18 C=14 D=2 E=2
  { 17,   576,  1365, 12,  7,   3, 65,   976,   911,  463,  6,  2,   4, 100}, // m=3*(5)*7*{13} m/phim(m)=2.36  C=22  D=3
  { 17, 18000, 21917, 30, 101, 217, 0,  5860,  5455,    0, 100, 6,   0, 100}, // m=(7)*{31}*101 m/phim(m)=1.21  C=134 D=2 
  { 17, 30000, 34441, 30, 101, 341, 0,  2729, 31715,    0, 100, 10,  0, 100}, // m=(11)*{31}*101 m/phim(m)=1.14 C=138 D=2
  { 17, 40000, 45551, 40, 101, 451, 0, 19394,  7677,    0, 100, 10,  0, 200}, // m=(11)*{41}*101 m/phim(m)=1.13 C=148 D=2
  { 17, 46656, 52429, 36, 109, 481, 0, 46658,  5778,    0, 108, 12,  0, 100}, // m=(13)*{37}*109 m/phim(m)=1.12 C=154 D=2
  { 17, 54208, 59363, 44, 23, 2581, 0, 25811,  5199,    0, 22, 56,   0, 100}, // m=23*(29)*{89} m/phim(m)=1.09  C=120 D=2
  { 17, 70000, 78881, 10, 101, 781, 0, 67167, 58581,    0, 100, 70,  0, 100}, // m=(11)*{71}*101 m/phim(m)=1.12 C=178 D=2

  {127,   576,  1365, 12,  7,   3, 65,   976,   911,  463,  6,  2,   4, 100}, // m=3*(5)*7*{13} m/phim(m)=2.36   C=22  D=3
  {127,  1200,  1925, 20,  11, 175, 0,  1751,   199,    0, 10, 6,    0, 100}, //  m=(5^2)*{7}*11 m/phim(m)=1.6   C=34 D=2
  {127,  2160,  2821, 30,  13, 217, 0,   652,   222,    0, 12, 6,    0, 100}, // m=(7)*13*{31} m/phim(m)=1.3     C=46 D=2
  {127, 18816, 24295, 28, 43, 565,  0, 16386, 16427,    0, 42, 16,   0, 100}, // m=(5)*43*{113} m/phim(m)=1.29   C=84  D=2
  {127, 26112, 30277, 24, 17, 1781, 0, 14249, 10694,    0, 16, 68,   0, 100}, // m=(13)*17*{137} m/phim(m)=1.15  C=106 D=2
  {127, 31752, 32551, 14, 43,  757, 0,  7571, 28768,    0, 42, 54,   0, 100}, // m=43*(757) :-( m/phim(m)=1.02   C=161 D=3
  {127, 46656, 51319, 36, 37, 1387, 0, 48546, 24976,    0, 36, -36,  0, 200}, //m=(19)*37*{73}:-( m/phim(m)=1.09 C=141 D=3
  {127, 49392, 61103, 28, 43, 1421, 0,  1422, 14234,    0, 42, 42,   0, 200}, // m=(7^2)*{29}*43 m/phim(m)=1.23  C=110 D=2
  {127, 54400, 61787, 40, 41, 1507, 0, 30141, 46782,    0, 40, 34,   0, 100}, // m=(11)*41*{137} m/phim(m)=1.13  C=112 D=2
  {127, 72000, 77531, 30, 61, 1271, 0,  7627, 34344,    0, 60, 40,   0, 100}  // m=(31)*{41}*61 m/phim(m)=1.07   C=128 D=2
};

extern long fhe_force_chen_han;

long findGoodM(long k, long L, bool bootstrappable) {
    double curr_k = 0;
    double curr_bootstrappable = 0;
    double should_be_bootstrappable = bootstrappable ? 1 : 0;
    double curr_L = 0;
    for(int i = 0; i < sizeof(m_against_k_values) / sizeof(m_against_k_values[0]); i++) {
        curr_k = m_against_k_values[i][0];
        curr_bootstrappable = m_against_k_values[i][3];
        curr_L = m_against_k_values[i][1];
        cout << "curr k: " << curr_k << endl;
        cout << "is bootstrappable: " << curr_bootstrappable << endl;
        cout << "curr L: " << curr_L << endl;
        if(curr_k >= k && curr_L >= L && curr_bootstrappable == should_be_bootstrappable) {
            cout << "Found m: " << m_against_k_values[i][2] << endl;
            return m_against_k_values[i][2];
        }
        cout << endl;
    }
}

int main(int argc, char *argv[]) 
{
    ArgMapping amap;

    long p=2;
    long r=16;
    long c=3;
    long L=300;
    long N=0;
    long t=0;
    long nthreads=1;
    long k = 90;
    long scale = 0;

    long seed=0;
    long useCache=1;

    amap.arg("p", p, "plaintext base");

    amap.arg("r", r,  "exponent");
    amap.note("p^r is the plaintext-space modulus");

    amap.arg("c", c, "number of columns in the key-switching matrices");
    amap.arg("L", L, "# of levels in the modulus chain");
    amap.arg("N", N, "lower-bound on phi(m)");
    amap.arg("t", t, "Hamming weight of recryption secret key", "heuristic");
    amap.arg("nthreads", nthreads, "number of threads");
    amap.arg("seed", seed, "random number seed");
    amap.arg("useCache", useCache, "0: zzX cache, 1: DCRT cache");

    //  amap.arg("disable_intFactor", fhe_disable_intFactor);
    amap.arg("chen_han", fhe_force_chen_han);

    amap.arg("scale", scale, "scale parameter");

    double recryptThreshold = 100;
    amap.arg("recryptThreshold", recryptThreshold, "");


    amap.arg("k", k, "security parameter");
    long chosen_m=0;
    amap.arg("m", chosen_m, "use specified value as modulus", NULL);
    long s=0;
    amap.arg("s", s, "minimum number of slots");
    long d=1;
    amap.arg("d", d, "degree of the field extension");
    amap.note("d == 0 => factors[0] defines extension");

    bool bootstrappable = false;
    amap.arg("bootstrappable", bootstrappable, "If set creates bootstrappable context");
    long skHwt = t;
    amap.parse(argc, argv);

    if (seed) 
        SetSeed(ZZ(seed));

    SetNumThreads(nthreads);

    Vec<long> mvec;
    vector<long> gens;
    vector<long> ords;

    //find index of m
    if(chosen_m == 0) {
        if(k == 0){
            cout << "If no m is given, you need to specify the security parameter k" << endl;
            exit(1);
        }
        cout << "No m specified. Finding suitable m for given parameter k=" << k << endl;
        chosen_m = findGoodM(k, L, bootstrappable);
    }

     
    cout << "*** Context";
    if (isDryRun()) cout << " (dry run)";
    cout << ": p=" << p
	 << ", r=" << r
	 << ", L=" << L
	 << ", t=" << skHwt
	 << ", c=" << c
	 << ", m=" << chosen_m
	 << " (=" << mvec << "), gens="<<gens<<", ords="<<ords
	 << endl;
    cout << "Computing key-independent tables..." << std::flush;
  


    int idx ;
    for (idx = 0; idx < sizeof(mValues) / sizeof(mValues[0]); idx++)
        if(mValues[idx][2] == chosen_m)
            break;
    long phim = mValues[idx][1];
    long m = mValues[idx][2];
    cout << "m = " << m << endl;
    assert(GCD(p, m) == 1);

    append(mvec, mValues[idx][4]);
    if (mValues[idx][5]>1) append(mvec, mValues[idx][5]);
    if (mValues[idx][6]>1) append(mvec, mValues[idx][6]);
    gens.push_back(mValues[idx][7]);
    if (mValues[idx][8]>1) gens.push_back(mValues[idx][8]);
    if (mValues[idx][9]>1) gens.push_back(mValues[idx][9]);
    ords.push_back(mValues[idx][10]);
    if (abs(mValues[idx][11])>1) ords.push_back(mValues[idx][11]);
    if (abs(mValues[idx][12])>1) ords.push_back(mValues[idx][12]);
    
    cout << "Creating context ... " << endl;
    FHEcontext context(m, p, r, gens, ords);
    if (scale) {
        context.scale = scale;
    }

    context.zMStar.set_cM(mValues[idx][13]/100.0);
    
    if(bootstrappable) {
        buildModChain(context, L, c, true);
        context.makeBootstrappable(mvec,/*t=*/skHwt,useCache,/*alsoThick=*/false);
    }
    else
        buildModChain(context, L, c, false);

    cout << "Context created" << endl;

    cout << "Creating keys ... " << endl;
    FHESecKey secretKey(context);
    secretKey.GenSecKey(64);      // A Hamming-weight-64 secret key
    addSome1DMatrices(secretKey); // compute key-switching matrices that we need
    addFrbMatrices(secretKey);

    if(bootstrappable)
        secretKey.genRecryptData();


    FHEPubKey publicKey = secretKey;

    cout << "Keys created" << endl;

    string context_file = bootstrappable ? "context_bootstrappable.txt" : "context.txt";
    string sk_file = bootstrappable ?  "secKey_bootstrappable.txt" : "secKey.txt";
    string pk_file = bootstrappable ? "pubKey_bootstrappable.txt" : "pubKey.txt";


    fstream myfile;
    // cout << "Writing context to " << context_file << endl;
    cout << "Writing context to " << context_file << endl;
	myfile.open(context_file, fstream::out | fstream::binary);
	writeContextBaseBinary(myfile, context);
    writeContextBinary(myfile, context);
    //myfile << context << endl;
    myfile.close();
	cout << "Done." << endl;

    cout << "Writing secret key to " << sk_file << endl;
    myfile.open(sk_file, fstream::out | fstream::binary);
    writeSecKeyBinary(myfile, secretKey);
	//myfile << secretKey << endl;
	myfile.close();
	cout << "Done." << endl;

    cout << "Writing public key to " << pk_file << endl;
    myfile.open(pk_file, fstream::out | fstream::binary);
	writePubKeyBinary(myfile, publicKey);
	myfile.close();
	cout << "Done." << endl;

    // cout << "Multiplying .." << endl;
    // Ctxt x2(publicKey);
	// Ctxt con1(publicKey); publicKey.Encrypt(con1, to_ZZX(8));
	// publicKey.Encrypt(x2, to_ZZX(2));
    // x2.multiplyBy(x2);
    // publicKey.thinReCrypt(x2);


    // FHESecKey secretKey2(context);
    // ifstream imyfile;
    // imyfile.open(sk_file, ios::in);
    // imyfile >> secretKey2;
    // assert(secretKey == secretKey2);
    // imyfile.close();
    // cout << "Keys are identical" << endl;

    // cout << "Converting ctxt to string ... " << endl;
    // stringstream ss;
    // ss << x1;
    // string target_string = ss.str();
    // cout << "Done." << endl;
    // cout << "Writing c1 to " << "c1.txt" << endl;
    // myfile.open("c1.txt", ios::out | ios::binary);
	// myfile << target_string << endl;
	// myfile.close();
	// cout << "Done." << endl;

  
    return 0;
}