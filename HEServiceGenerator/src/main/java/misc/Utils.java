package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import database.DatabaseConnectionHandler;

/**
 * Holds various utility methods.
 * @author Philip Kaiser
 *
 */
public class Utils {

	
	/**
	 * Writes text to a file (overrides if existing)
	 * @param text Text
	 * @param targetFile Target file
	 */
	public static void writeToFile(String text, String targetFile) {
		File f = new File(targetFile);
		(new File(f.getParent())).mkdirs(); //create directories
		try(PrintWriter writer = new PrintWriter(targetFile, "UTF-8")) {
			writer.print(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deploys a file by creating a copy of the original.
	 * @param src Source file.
	 * @param dest Target file.
	 * @throws IOException
	 */
	public static void deployStaticResource(File src, File dest) throws IOException {
    	FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    FileInputStream fis = null;
	    FileOutputStream fos = null;
    	try {
    		if(!dest.exists()) {
    			dest.createNewFile();
    		}
            fis = new FileInputStream(src);
        	fos = new FileOutputStream(dest);
            sourceChannel = fis.getChannel();
            destChannel = fos.getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    	} 
    	finally {
    		if(sourceChannel != null)
               sourceChannel.close();
    		if(destChannel != null)
               destChannel.close();
    		if(fis != null)
               fis.close();
    		if(fos != null)
               fos.close();
       }
    	
    }
	
	public static String appendTrailingSlash(String path) {
		if(!path.endsWith("/")) {
			path += "/";
		}
		
		return path;
	}
	
	/**
	 * Cleans up a directory by recursively deleting its contents and, eventually, the directory itself.
	 * @param cleanupDir Path of the directory to be deleted
	 */
	public static void cleanup(String cleanupDir) {
		try {
			System.out.println("Executing: rm -r " + cleanupDir);
			int result = Runtime.getRuntime().exec("rm -r " + cleanupDir).waitFor();
			if(result == 0) {
				System.out.println("Successfully cleaned up temporary files");
			} else {
				System.out.println("Error during cleanup");
			}	
		}
		catch (Exception e) {
			System.out.println("Error while cleaning up: " + e.getMessage());
		}
	}

	/**
	 * Cleans up all files of a deployed service.
	 * @param serviceName Name of the service
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void removeServiceFiles(String serviceName) 
			throws InterruptedException, IOException, SQLException {
		Utils.cleanup(Constants.SERVICE_GENERATION_TARGET_ROOT + serviceName);
		int result = Runtime.getRuntime().exec("rm -r " + Constants.TOMCAT_WEBAPPS_ABSOLUTE_PATH + "/" + serviceName +"/").waitFor();
		if(result == 0) {
			System.out.println("Successfully cleaned up service files");
		} else {
			System.out.println("Error during cleanup");
		}
		
		result = Runtime.getRuntime().exec("rm " + Constants.TOMCAT_WEBAPPS_ABSOLUTE_PATH + "/" + serviceName + ".war").waitFor();
		if(result == 0) {
			System.out.println("Successfully cleaned up service files");
		} else {
			System.out.println("Error during cleanup");
		}
	}
}
