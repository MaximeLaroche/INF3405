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
	private static Scanner keyboard = new Scanner(System.in);
	private static String ip = "";
	private static int serverPort =0;
	public static void main(String[] args) throws Exception
	{
		
		boolean isIp= false;
		
		
		askAddress();
		socket = new Socket(ip,serverPort);
		System.out.format("Le serveur fonctionne sur :", ip, serverPort);
		
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServer= in.readUTF();
		System.out.println("HelloFromServer");
		//File f = new File("");
		socket.close();
		
	}
	
	//Verifier bonne adresse IP
private static void askAddress() {
		
		boolean hasValidAddress = false;
		
		
		String badIP = "Veuillez entrer des nombres entre 0 et 255 separés par un .\n Par exemple, 152.0.54.254";
		String badPort = "Veuillez entrer uniquement un nombre entre 5000 et 5050 pour le port";
		
		
		
		do {
			
			hasValidAddress = true;
			String ligne;
			//Separer les octets par "." et le port par ":"
			String entrees[] = {};		
			do{
				System.out.println("Veuiller entrer votre addresse IP, compose de quatre entier entre 0 et 255,\n suivi du port, entre 5000 et 5050.\n Par exemple, 127.0.50.254:5043 est une addresse valide.");
			    ligne = keyboard.nextLine();
				entrees = ligne.split("[:\\.]");
				
			}while(entrees.length != 5);
			
			//Valider l'addresse IP
			
			for(int i=0;i<4;i++) {
				int temp;
				try {
					temp = Integer.parseInt(entrees[i]);
					if(temp<0 || temp >255) {
						System.out.println(badIP);
						hasValidAddress = false;
					}
				}catch(Exception e) {
					System.out.println(badIP);
					hasValidAddress = false;
				}
			}
			
			//Valider le port
			try {
				int temp = Integer.parseInt(entrees[4]);
				if(temp < 5000 || temp > 5050) {
					System.out.println(badPort);
					hasValidAddress = false;
				}
			}catch(Exception e) {
			System.out.println(badPort);
			hasValidAddress = false;
			}
			ip = entrees[0] + "." + entrees[1] + "." + entrees[2] + "." + entrees[3];
			serverPort = Integer.parseInt(entrees[4]);
						
				
				
				}while(!hasValidAddress);
			}
	
}