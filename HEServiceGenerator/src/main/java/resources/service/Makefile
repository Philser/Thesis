default: interface

run:
	./he_functions
	./interface

he_functions: he_functions.cpp
	g++ -fPIC -std=c++11 -I/usr/local/include/helib -c he_functions.cpp -DFHE_THREADS -DFHE_BOOT_THREADS
	
interface: he_functions
	g++ -fPIC -g -O2 -std=c++11 -pthread -DFHE_THREADS -DFHE_BOOT_THREADS -I"$$JAVA_HOME/include" -I"$$JAVA_HOME/include/linux" -I/usr/local/include/helib -fmax-errors=2 -o org_example_HECodeInterface.so org_example_HECodeInterface.cpp -Wl,--whole-archive he_functions.o -Wl,--no-whole-archive -L/usr/local/lib -lfhe -lntl -lgmp -shared

clean:
	rm -f main

####################
# for local development:
#   make devlibs && make main && LD_LIBRARY_PATH=cppcms/lib make run

devclean:
	rm -rf cppcms
	rm -f cppcms-1.0.5-linux-x64.tgz

devlibs: cppcms-1.0.5-linux-x64.tgz
	mkdir -p cppcms
	tar -m --directory cppcms -zxf cppcms-1.0.5-linux-x64.tgz

cppcms-1.0.5-linux-x64.tgz:
	wget -c 'https://github.com/flavorjones/cppcms-buildpack-test/blob/master/vendor/cflinuxfs2/cppcms-1.0.5-linux-x64.tgz?raw=true' -O cppcms-1.0.5-linux-x64.tgz
