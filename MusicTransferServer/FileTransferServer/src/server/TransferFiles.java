package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Generic Windows User
 *
 */
public class TransferFiles implements Runnable {

	public final static int SOCKET_PORT = 4712;
	public final static int PATH_LEN = 1000;
	
	/**
	 * here be dragons!
	 * 
	 * This functionality is in one of the server test Projects on my Laptop
	 */
	public void run() {
		try {
			transaction();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void transaction() throws IOException{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		InputStream is = null;
		ServerSocket servsock = null;
		Socket sock = null;
		String requested = null;
		
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			while (true) {
				System.out.println("Waiting...");
				try {
					sock = servsock.accept();
					System.out.println("Accepted connection : " + sock);
					
					//receive requested file path.
					is = sock.getInputStream();
					byte [] bytes_in = new byte[PATH_LEN];
					is.read(bytes_in, 0, bytes_in.length);
					requested = new String(bytes_in).trim(); 
					System.out.println("request recieved!");
					
					// send file
					File myFile = new File (requested); // TODO: check whether this file actually exists! 
					byte [] mybytearray  = new byte [(int)myFile.length()];
					if(myFile.exists()) System.out.println("it exists. good!");
					
					fis = new FileInputStream(myFile);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray,0,mybytearray.length);
					os = sock.getOutputStream();
					System.out.println("Sending " + requested + "(" + mybytearray.length + " bytes)");
					os.write(mybytearray,0,mybytearray.length);
					os.flush();
					System.out.println("Done.");
				}
				finally {
					if (bis != null) bis.close();
					if (os != null) os.close();
					if (sock!=null) sock.close();
				}
			}
		}
		finally {
			if (servsock != null) servsock.close();
		}
	}

}
