package myP.Server;

//An example class that uses the secure server socket class

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

//import javafx.scene.shape.Line;

import java.security.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "LIUkeystore.ks";
	static final String TRUSTSTORE = "LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	/**
	 * Constructor
	 * 
	 * @param port The port where the server will listen for requests
	 */
	SecureAdditionServer(int port) {
		this.port = port;
	}

	/** The method that does the work for the class */
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(KEYSTORE), KEYSTOREPASS.toCharArray());

			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load(new FileInputStream(TRUSTSTORE), TRUSTSTOREPASS.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, KEYSTOREPASS.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket(port);
			sss.setNeedClientAuth(true);
			sss.setEnabledCipherSuites(sss.getSupportedCipherSuites());

			System.out.println("\n>>>> SecureAdditionServer: active ");
			SSLSocket incoming = (SSLSocket) sss.accept();

			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);

			String str;
			int arg = 0;
			String fileName = new String();
			String content = new String();
			File myFile = null;
			FileWriter myWriter = null;
			
			// While trying to readline from input, do stuff based on input.
			int LineCounter = 0;

			while ((str = in.readLine()) != null && str.length() > 0) {
				
				if (LineCounter == 0) {
					// Get action from client
					arg = Integer.parseInt(str.split(",")[0].trim());
					
					
					// Get filename from client
					fileName = str.split(",")[1].trim();
					if (arg == 1) {
						content = str.split(",")[2].trim();
						myFile = new File("./src/myP/Server/" + fileName);
						myWriter = new FileWriter(myFile);
						myWriter.write(content);
					}
				}
				
				if (arg == 1 && LineCounter > 0) {
					// Get content
					content = str;
					// Write to file
					myWriter.write("\n" + content.trim());
				}
				// Download file from server
				else if (arg == 2) {

					File returnFile = new File("./src/myP/Server/" + str.split(",")[1].trim());

					//System.out.println(returnFile.getAbsolutePath());
					
					// Check if file exists on server.
					if (returnFile.exists() && !returnFile.isDirectory()) {
						
						System.out.println("File exists");
						
						String returnString = new String();
						String s;
						BufferedReader myFileReader = new BufferedReader(new FileReader(returnFile));
						int counter = 0;
						while ((s = myFileReader.readLine()) != null) {
							if (counter == 0) {
								returnString += s;
								counter++;
							} else {
								returnString += "\n" + s;
							}
						}
						
						//System.out.println(returnString);
						
						//Send the data back to client
						System.out.println("Sending to client..");
						out.println(returnString);
						System.out.println("Done sending to client..");
						break;
						
					} else {
						//If file does not exist then send back nothing
						out.println("");
						break;
					}

					// Change returnFile's content to a formatted string and return to client.

				} else if (arg == 3) {
					// Delete file on server
					System.out.println("Deleting file..");
					
					File deleteFile = new File("./src/myP/Server/" + fileName);
					
					if(deleteFile.delete()) {
						System.out.println("File deleted sucessfully!");
					}else {
						System.out.println("Failed to delete the file..");
					}
					
					break;
				} else{
					// If invalid input. We should not get here!
					out.println("Invalid");
				}
				LineCounter++;
				
			}
			
			if(arg == 1) {
				System.out.println("Uploaded file successfully!");
				myWriter.close();
			}
			incoming.close();
			
		} catch (

		Exception x) {
			System.out.println(x);
			x.printStackTrace();
		}
	}

	/**
	 * The test method for the class
	 * 
	 * @param args[0] Optional port number in place of the default
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		SecureAdditionServer addServe = new SecureAdditionServer(port);
		addServe.run();
	}
}
