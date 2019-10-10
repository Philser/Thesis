main:
	g++ -fPIC -g -O2 -std=c++11 -pthread -DFHE_THREADS -DFHE_BOOT_THREADS -I"$$JAVA_HOME/include" -I"$$JAVA_HOME/include/linux" -I/usr/local/include/helib -fmax-errors=2 homomorphic_HELib_Interface.cpp -o libhomomorphic_HELib_Interface.so -L/usr/local/lib -lfhe -lntl -lgmp -shared
