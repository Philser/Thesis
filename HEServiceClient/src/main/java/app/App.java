package app;

import java.io.File;
import java.util.Scanner;

import client.ServiceClient;
import gui.TextGUI;


public class App 
{
    public static void main( String[] args )
    {
    	String peerId = "";
    	String targetService = "";
    	for(int i = 0; i < args.length; i++) {
    		String[] kvPair = args[i].split("=");
    		switch(kvPair[0]) {
    		case "peerId":
    			peerId = kvPair[1];
    			break;
    		case "target":
    			targetService = kvPair[1];
    			break;
    		}
    	}
    	
    	if(peerId.equals("")) {
    		System.out.println("Error: No peer id provided.");
    		return;
    	}
    	
    	if(targetService.equals("")) {
    		System.out.println("Error: No target provided.");
    		return;
    	}
    	
    	if(!helibFilesAreAvailable())
    		return;
    	
    	Scanner scanner = new Scanner(System.in);
    	ServiceClient client = new ServiceClient(peerId, targetService);
    	TextGUI gui = new TextGUI(client, scanner);
    	gui.interact();
    	scanner.close();
    }

	private static boolean helibFilesAreAvailable() {
		File sharedHELib = new File("libhomomorphic_HELib_Interface.so");
		if(!sharedHELib.exists()) {
			System.out.println("Missing HE library. Make sure that this client is in the same directory as "
					+ "the HE library libhomomorphic_HELib_Interface.so");
			return false;
		}
		
		File context = new File("context.txt");
		if(!context.exists()) {
			System.out.println("Missing context file. Make sure that this client is in the same directory as your context.txt");
			return false;
		}
		
		File secKey = new File("secKey.txt");
		if(!secKey.exists() ) {
			System.out.println("Missing secret key file. Make sure that this client is in the same directory as your secKey.txt");
			return false;
		}
		
		return true;
	}
}
