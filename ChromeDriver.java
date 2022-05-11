package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * Author : Amruta Alandkar 
 * Email: amrutasa007@gmail.com
 * This program provides the compatible chromedriver.exe with the installed chrome version.
 * Google chrome autoupdates every biweekly or so notoriously and your test script using 
 * chromedriver.exe might fail randomly with incompatible version error. Run this script 
 * before your selenium scripts in Jenkins/CI/CD pipeline to always get the right chromedriver.exe file.
 */

public class ChromeDriver {

	public static void main(String[] args) throws IOException {
		
		
		/*
		 * This program takes two arguments - 1): ChromeDriver Download location (Here a zip file will be downloaded from the web)
		 * 2)chromedriver.exe unzip location (This can be the location from where your selenium project is picking 
		 * the chromedriver.exe, so that it picks the compatible chromedriver.exe every time) 
		 * 
		 */
		
		String fileLoc = args[0];
		String target = args[1];
		String urlVersion = "";
		String version = chromeVersion();
		System.out.println("Installed chrome version: " + version);
		String[] s2 = version.split("\\.");
		
		if(s2.length==4) {
			urlVersion = s2[0]+"."+s2[1]+"."+s2[2];
		}else {
			urlVersion= s2[0]+"."+s2[1]+"."+s2[2];
		}
		
		String finalVersion = buildURL(urlVersion,fileLoc);
		
		downloadCD(finalVersion.trim(),fileLoc);
		
		unzipFile(fileLoc, target);
		

	}
	
	//Gets the installed chrome version
	public static String chromeVersion() throws IOException {
		
		String[] s1;
		
		Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("reg query " + "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon " +  "/v version");
        BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(proc.getErrorStream()));

            String line = stdInput.readLine();
            StringBuilder sb = new StringBuilder();
            
            while(line != null){
                sb.append(line).append("\n");
                line = stdInput.readLine();
            }
            
            String s = sb.toString();
            
            
            s1 = s.split("REG_SZ");
            
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            return s1[1].trim();
        
    }
	
	//Gets the latest chromedriver version for your installed chrome version
	public static String buildURL(String version, String versionFileLoc) throws MalformedURLException, IOException{
		
		String downloadLocation = versionFileLoc + "\\versionFile.txt";
		String latestURL = "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_"+version;
		URL url = new URL(latestURL);
		
		File file = new File(versionFileLoc);
		
		if(!file.exists()) {
		new File(versionFileLoc).mkdir();
		}
		
		try (InputStream in = url.openStream();
	            ReadableByteChannel rbc = Channels.newChannel(in);
	            FileOutputStream fos = new FileOutputStream(downloadLocation)) {
	            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        }
		
		BufferedReader br = new BufferedReader(new FileReader(downloadLocation));
		
		String line = br.readLine();
        StringBuilder sb = new StringBuilder();
        
        while(line != null){
            sb.append(line).append("\n");
            line = br.readLine();
        }
        
        String s = sb.toString();
        //System.out.println(s);
        
		
		return s;
	}
	
	//Downloads the chromedriver.zip file from the web to the given location
	public static void downloadCD(String version, String zipDwnldLoc) throws MalformedURLException, IOException {
		
		String zipDownLoc = zipDwnldLoc + "\\chromedriver_win32.zip";
		String download_url = "https://chromedriver.storage.googleapis.com/"+ version +"/chromedriver_win32.zip";
		System.out.println(download_url);
		URL url = new URL(download_url);
		
		try (InputStream in = url.openStream();
	            ReadableByteChannel rbc = Channels.newChannel(in);
	            FileOutputStream fos = new FileOutputStream(zipDownLoc)) {
	            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        }
		
		
	}
	
	//Unzips and copies the file to the target location as given by the user.
	public static void unzipFile(String fileLoc, String target) throws IOException {
		String fileZip =  fileLoc + "\\chromedriver_win32.zip";
		System.out.println("zip location "+fileLoc);
        File destDir = new File(target);
        System.out.println("unzipLoc " + target);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
        	File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        zipEntry = zis.getNextEntry();
       }
        
        zis.closeEntry();
        zis.close();
    }
	
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}

}
