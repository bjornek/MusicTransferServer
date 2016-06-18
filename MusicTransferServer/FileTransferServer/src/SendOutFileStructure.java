import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * This class handles the management of xml indexing and transfering xml files
 * @author Generic Windows User
 *
 */
public class SendOutFileStructure implements Runnable {

	private final String XML_LOCATION = "./structure.xml";
	public final static int SOCKET_PORT = 13267;

	private String libRoot = null;

	/**
	 * The constructor simply saves the location of the music library.
	 * The rest is taken care of in the relevant methods.
	 * 
	 * @param libLoc the absolute path to the music library
	 */
	public SendOutFileStructure(String libRoot){
		this.libRoot=libRoot;
	}

	/**
	 * here be dragons!
	 * scary stuffs!
	 */
	public void run() {
		try {
			createXMLFile();
			transfer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates an XML representation of the music library pointed to by the
	 * String parsed to the constructor. 
	 * 
	 * @throws IOException 
	 */
	public void createXMLFile() throws IOException {

		File xmlFile = new File(XML_LOCATION);
		File lib = new File(libRoot);

		Document doc = new Document();
		Element theRoot = new Element("music");
		doc.setRootElement(theRoot);

		// remove the old file if such a file exists
		if(xmlFile.exists()) xmlFile.delete();

		// start building an XML file from the library location
		for (File artist : lib.listFiles()){ // Artists

			Element artistxml = new Element("artist");
			artistxml.setAttribute("artist_id", artist.getName());

			if(artist.isDirectory())
				for(File album : artist.listFiles()){ // Albums

					Element albumxml = new Element("album");
					albumxml.setAttribute("album_id",album.getName());
					if(album.isDirectory())
						for(File track : album.listFiles()){ // Tracks

							Element trackxml = new Element("track");
							trackxml.setAttribute("track_id",track.getName());
							trackxml.addContent(new Text(track.getAbsolutePath()));
							albumxml.addContent(trackxml);
						}//END tracks

					artistxml.addContent(albumxml);
				}//END Albums

			theRoot.addContent(artistxml);
		}// END Artists

		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());

		xmlOutput.output(doc, new FileOutputStream(xmlFile));

		System.out.println("Fingers crossed!");

	}

	/**
	 * I think this functionality can be found in a project on my Laptop ...?
	 * Otherwise see TransferFiles.run() after it's done :P
	 *   It's going to be similar (just remove the part where the client 
	 *   sends the file they wish to receive)
	 * 
	 * @throws IOException 
	 */
	private void transfer() throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		ServerSocket servsock = null;
		Socket sock = null;
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			while (true) {
				System.out.println("Waiting...");
				try {
					sock = servsock.accept();
					System.out.println("Accepted connection : " + sock);
					// send file
					File myFile = new File (XML_LOCATION);
					byte [] mybytearray  = new byte [(int)myFile.length()];
					fis = new FileInputStream(myFile);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray,0,mybytearray.length);
					os = sock.getOutputStream();
					System.out.println("Sending " + XML_LOCATION + "(" + mybytearray.length + " bytes)");
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
