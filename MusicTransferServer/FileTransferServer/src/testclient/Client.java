package testclient;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

	public final static int SOCKET_PORT = 4712;
	public final static String SERVER = "127.0.0.1";
	
	public final static int FILE_SIZE = 7022386;

	public static void main (String [] args ) {
		
		try {
			receive("C:\\Users\\Generic Windows User\\Music\\Aesop Rock\\Appleseed EP\\01 - appleseed intro.mp3","./file1.mp3");
			receive("C:\\Users\\Generic Windows User\\Music\\Aesop Rock\\Appleseed EP\\02 - dryspell.mp3","./file2.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static void receive(String gimmefile, String putitthere)throws IOException {
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		
		try {
			sock = new Socket(SERVER, SOCKET_PORT);
			System.out.println("Connecting...");

			// send file path
			OutputStream os = sock.getOutputStream();
			os.write(gimmefile.getBytes(), 0, gimmefile.getBytes().length);
			System.out.println("request sent!");
			
			// receive file
			byte [] mybytearray  = new byte [FILE_SIZE];
			InputStream is = sock.getInputStream();
			fos = new FileOutputStream(putitthere);
			bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			current = bytesRead;
			System.out.println("bytesRead = "+bytesRead);

			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} while(bytesRead > -1);

			bos.write(mybytearray, 0 , current);
			bos.flush();
			System.out.println("File " + gimmefile + " downloaded (" + current + " bytes read)");
		}
		finally {
			if (fos != null) fos.close();
			if (bos != null) bos.close();
			if (sock != null) sock.close();
		}
	}

}