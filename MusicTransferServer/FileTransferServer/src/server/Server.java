package server;
import java.io.IOException;

/**
 * The Server is part of the music transfer project I'm putting together.
 * the server side is always running in the background and waits for a  User 
 * to request either the format of the music library or to transfer a file.
 * 
 * The Client side is an Android app made by me.
 *  
 * @author Björn Eklöf
 * @version 0.1 I guess...
 *
 */
public class Server {

	private final static String LIB = "C:\\Users\\Generic Windows User\\Music";
	
	/**
	 * The main function initiates the server functionality. It starts up two 
	 * threads: one that is meant to handle the transfer of the music library 
	 * structure, and another for transferring requested files. 
	 * 
	 * @param args does not do anything!
	 */
	public static void main(String[] args) {
		
		Thread thread2 = new Thread(new TransferFiles());
		Thread thread1 = new Thread(new SendOutFileStructure(LIB));
		
		thread1.start();
		thread2.start();
		
	}

}
