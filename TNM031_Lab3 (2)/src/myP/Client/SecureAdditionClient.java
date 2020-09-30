// A client-side class that uses a secure TCP/IP socket
package myP.Client;

import java.io.*;
import java.net.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "LIUkeystore.ks";
	static final String TRUSTSTORE = "LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	// Constructor @param host Internet address of the host where the server is
	// located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}

	// The method used to start a client object
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
			SSLSocketFactory sslFact = sslContext.getSocketFactory();
			SSLSocket client = (SSLSocket) sslFact.createSocket(host, port);
			client.setEnabledCipherSuites(client.getSupportedCipherSuites());
			System.out.println("\n>>>> SSL/TLS handshake completed");
			// A secure connection has been established. Time to send stuff securely.

			BufferedReader socketIn;

			socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);

			// default sent
			System.out.println("FTP established, what do you want to do?");
			System.out.println("1. Upload \n2. Download \n3. Delete");

			String option = (new BufferedReader(new InputStreamReader(System.in))).readLine().trim();
			
			while(!option.equals("1") && !option.equals("2") && !option.equals("3")) {
				System.out.println("Incorrect, pick 1-3");
				System.out.println("1. Upload \n2. Download \n3. Delete");
				option = (new BufferedReader(new InputStreamReader(System.in))).readLine().trim();
			}
			
			while (true) {
				if (option.equals("1")) {
					System.out.println("Which file do you want to upload?");

					// new instance of file

					// Filename
					String FileName = (new BufferedReader(new InputStreamReader(System.in))).readLine();

					File myFile = new File("./src/myP/Client/" + FileName);

					// Read file
					BufferedReader br = new BufferedReader(new FileReader(myFile));

					// try file send
					String content = new String();
					
					// Input string is 1 plus content, then we remove from server.
					String[] tot = new String[3];
					tot[0] = option;
					tot[1] = FileName;
					tot[2] = "";
					while ((content = br.readLine()) != null) {
						tot[2] += (content + " ");
						tot[2] += " \n";
					}
					// Concatenate the input to one String.
					String sendToServer = Arrays.toString(tot);
					sendToServer = sendToServer.substring(1, sendToServer.length() - 2);
					System.out.println(sendToServer);

					// Send concatenated String to server.
					socketOut.println(sendToServer);

					System.out.println(">>>> Sending the file named " + FileName + " to SecureAdditionServer");

					// SocketIn returns the out.print() from the server
					System.out.println(socketIn.readLine());
					br.close();
					socketOut.close();
					break;

				} else if (option.equals("2")) {

					System.out.println("Which file do you want to download?");

					String FileName = (new BufferedReader(new InputStreamReader(System.in))).readLine();
					File myFile = new File("./src/myP/Client/" + FileName);

					String[] tot = new String[2];
					tot[0] = option;
					tot[1] = FileName;

					String sendToServer = Arrays.toString(tot);
					sendToServer = sendToServer.substring(1, sendToServer.length() - 1);
					System.out.println(sendToServer);

					socketOut.println(sendToServer);

					System.out.println(">>>> Downloading the file named " + FileName + " from SecureAdditionServer");

					// SocketIn returns the content of the file being sent from the server

					// SocketIn returns the out.print() from the server, in this case the content of
					// the text file.
					String FileContent = "";
					String s = new String();
					int counter = 0;
					
					
					if ((FileContent += socketIn.readLine()).equals("null")) {////
						System.out.println("File does not exist");
						break;
						
					} else {
						
						while ((s = socketIn.readLine()) != null) {
							
							FileContent += "\n" + s;
						}
						
						System.out.println("File downloaded sucessfully");
						

						// Save into new file at the clients folder.
						FileWriter clientWriter = new FileWriter(myFile);
						//System.out.println("This is file path: " + myFile.getAbsolutePath());
						clientWriter.write(FileContent);
						clientWriter.close();
						socketOut.close();
					 break;
					}

				} else if (option.equals("3")) {
					
					System.out.println("Which file do you want to delete?");
					
					// Filename
					String FileName = (new BufferedReader(new InputStreamReader(System.in))).readLine();

					//Creat information to send to server
					String[] tot = new String[3];
					tot[0] = option;
					tot[1] = FileName;
					tot[2] = "";

					// Concatenate the input to one String.
					String sendToServer = Arrays.toString(tot);
					sendToServer = sendToServer.substring(1, sendToServer.length() - 2);
					System.out.println(sendToServer);

					// Send concatenated String to server.
					socketOut.println(sendToServer);

					System.out.println(">>>> Sending the file named " + FileName + " to be deleted to SecureAdditionServer");

					socketOut.close();
					
					break;
				}
			}

		} catch (Exception x) {
			System.out.println(x);
			x.printStackTrace();
		}
	}

	// The test method for the class @param args Optional port number and host name
	public static void main(String[] args) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if (args.length > 0) {
				port = Integer.parseInt(args[0]);
			}
			if (args.length > 1) {
				host = InetAddress.getByName(args[1]);
			}
			SecureAdditionClient addClient = new SecureAdditionClient(host, port);
			addClient.run();
		} catch (UnknownHostException uhx) {
			System.out.println(uhx);
			uhx.printStackTrace();
		}
	}
}
