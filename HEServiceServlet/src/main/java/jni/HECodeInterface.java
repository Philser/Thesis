package jni;


public class HECodeInterface {
	static {
		System.load("/opt/tomcat/latest/webapps/service/WEB-INF/classes/native_code/org_example_HECodeInterface.so");
	}

	public native String calculate(String[] values);

}