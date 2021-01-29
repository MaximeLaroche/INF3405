import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.*;
import java.util.Scanner;

public class Client {
	
	private static Socket socket;
	private static Scanner imput = new Scanner(System.in);
	public static void main(String[] args) throws Exception
	{
		String serverAddres= "";
		int port =0;
		boolean isIp= false;
		/*do {
			System.out.println("Entrez l'adresse IP ex :127.0.0.1  :" );
			serverAddres= imput.next();
			isIp(serverAddress);
		}while()*/
		
		System.out.println("Entrez l'adresse IP ex :127.0.0.1  :" );
		System.out.println("Entrez un numéro de port entre 5000 et 5050" );
		socket = new Socket(serverAddress,port);
		System.out.format("Le serveur fonctionne sur :", serverAddress, port);
		
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServer= in.readUTF();
		System.out.println("HelloFromServer");
		
		socket.close();
		
	}
	
	//Verifier bonne adresse IP
	
	
}