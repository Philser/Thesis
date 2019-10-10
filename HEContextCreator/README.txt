This tool is designed to create contexts and keys for the HELib encryption library.

Dependencies:
	This software requires NTL, GMP, HELib, and a C++-11 compatible compiler installed on your system.
	You can find the libraries under the links below.
	Build instructions for each library can be found under the respective link as well.
	
	NTL: https://www.shoup.net/ntl/download.html
	GMP: https://gmplib.org/#DOWNLOAD
	HELib: https://github.com/shaih/HElib

Build:
	To build the tool, simply run make in the directory.


Usage:
	You can either a modulo m for which the context should be created, or you can provide the security parameter k.
	Then, the tool will then choose an m for which k will be equal to or higher then the specified value.
	Additional parameters can be specified. These are:
		L - Level parameter.
		p - Plaintext base (must be a prime number, e.g. 2).
		r - Plaintext lifting, p^r determine the plaintext space. Default is p^r = 2^16.
		bootstrappable - 1 or 0, specifies whether the chosen context needs to support bootstrapping.

Example usage:
	./he_context_creator k=110 L=800 p=2 r=32 bootstrappable=1
	./he_context_creator k=80 L=600 p=2 r=8 bootstrappable=0
	./he_context_creator m=35113 L=800 bootstrappable=1
	./he_context_creator k=130 bootstrappable=1