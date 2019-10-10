package homomorphic;

public class HELib_Interface {
	static {
		System.loadLibrary("homomorphic_HELib_Interface");
	}

	public native String decryptCtxt(String ctxt, boolean needsBootstrapping);
	
	public native String encryptPlain(String plain, boolean needsBootstrapping);

}
