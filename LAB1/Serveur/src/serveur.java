import java.io.DataOutputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;


public class serveur {
	
	private static ServerSocket listener;
	private static String ip = "";
	private static int serverPort =1;
	private static Scanner keyboard = new Scanner(System.in);

	private static File currentDirectory;
	
	private static void askAddress() {
		
		boolean hasValidAddress = false;
		
		
		String badIP = "Veuillez entrer des nombres entre 0 et 255 separ�s par un .\n Par exemple, 152.0.54.254";
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
	

	
	
	private static String commands(String entrees, Socket socket) {
		/*
		 * Commandes[0] est la commande
		 * le reste des index sont les argument*/
		boolean exit = false;
		String[] commandes = entrees.split(" ",2);
		String output = "";
		switch(commandes[0]) {
		case "cd":
			output = cd(commandes[1]);
			break;
		case "ls":
			output = ls();
			break;
		case "mkdir":
			output = mkdir(commandes[1]);
			break;
		case "upload":
			output = upload(commandes[1]);
			break;
		case "download":
			output = download(commandes[1]);
			break;
		case "exit":
			output = "Client exited the server. Socket closed";
			break;
		default:
			System.out.println("Commande invalide");
		}
		return output;
		
	}
	private static String cd(String whereTo) {
		currentDirectory = new File(whereTo);
		String message = "Moved to " + whereTo;
		System.out.println(message);
		return message;
	}
	private static String ls() {
		String files[] = currentDirectory.list();
		String message = "";
		for(String file : files){
			System.out.println(file);
			message+=file + "\n";
		}
		return message;
	}
	private static String mkdir(String directoryName) {
		System.out.println("On est dans mkdir");
		String message="";
		File folder = new File(directoryName);
		if(folder.mkdir()){
			message = "votre dossier " + directoryName + " a ete cree";
			System.out.println(message);
			//out.writeUTF(message);
		}
		return message;
		
	}
	private static String upload(String fileName) {
		String message = "";
		return message;
	}
	private static String download(String fileName) {
		String message = "";
		return message;
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		
		//askAddress();
		ip="127.0.0.1";
		serverPort = 5000;
		
	
		String serverAddress =  "" ;
		
		
		
		System.out.println("Veuillez entrer le no de client");
		int clientNumber = 0;
		
		
		
		//Cr�ation de la connexion pour communiquer avec
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(ip);
		
		
		//Association de l'addresse et du port `ala connexion

		listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverIP, serverPort);
		
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


					DataInputStream in = new DataInputStream(socket.getInputStream());

					String query =in.readUTF();
					System.out.println(query);
					String message = commands(query,socket);

					out.writeUTF(message);

					socket.close();
					
				}catch (IOException e)
				{
					System.out.println("Error handling client#" + clientNumber + ": " + e);
				}
				
				
				
			}
		}
		
	}
	


	

