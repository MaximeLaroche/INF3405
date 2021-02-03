import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;


public class serveur {
	
	private static ServerSocket listener;
	private static String ip = "";
	private static int port =0;
	private static Scanner keyboard = new Scanner(System.in);
	
	private static void askAddress() {
		
		boolean hasValidAddress = false;
		
		
		String badIP = "Veuillez entrer des nombres entre 0 et 255 separés par un .\n Par exemple, 152.0.54.254";
		String badPort = "Veuillez entrer uniquement un nombre entre 5000 et 5050 pour le port";
		
		
		
		do {
			
			System.out.println("Veuiller entrer votre addresse IP, compose de quatre entier entre 0 et 255,\n suivi du port, entre 5000 et 5050.\n Par exemple, 127.0.50.254:5043 est une addresse valide.");
			hasValidAddress = true;
					String ligne = keyboard.nextLine();
			//Separer les octets par "." et le port par ":"
			String entrees[] = ligne.split("[:\\.]");
		
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
			port = Integer.parseInt(entrees[4]);
		
		
		
		
		}while(!hasValidAddress);
	}
	
	private static int askClientNo() {
		int client = 0;
		do {
			System.out.println("Veuillez entrer le numero du client");
			try {
				client = keyboard.nextInt();
			}catch(Exception e) {
				System.out.println("Veuillez entrer un nombre (pas de lettre).");
			}
		}while(client==0);
		return client;
	}
	
	
	public static void main(String[] args) throws Exception{
		
		askAddress();
		
	
		String serverAddress = ip + ":" + port;
		
		
		System.out.println("Veuillez entrer le no de client");
		int clientNumber = askClientNo();
		
		
		
		//Crï¿½ation de la connexion pour communiquer avec
		listener = new ServerSocket();
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		
		//Association de l'addresse et du port `ala connexion
		listener.bind(new InetSocketAddress(serverIP,serverPort));
		
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		
		try
		{
		
			while(true)
			{
				new ClientHandler(listener.accept(),clientNumber++).start();
			
			}
		
		}
		finally
		{
			listener.close();
		}
	}
		
		private static class ClientHandler extends Thread
		{
			private Socket socket;
			private int clientNumber;
			
			public ClientHandler(Socket socket, int clientNumber)
			{
				this.socket = socket;
				this.clientNumber = clientNumber;
				System.out.println("New connection with client#"+ clientNumber + " at "+ socket);
				
			}
			public void run()
			{
				try
				{
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					
					out.writeUTF("Hello from server - you are client# "+ clientNumber);
					
				}catch (IOException e)
				{
					System.out.println("Error handling client#" + clientNumber + ": " + e);
				}
				
				
				System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}
		
	}
	


	

