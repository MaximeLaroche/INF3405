import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;


public class serveur {
	
	private static ServerSocket listener;
	
	
	
	private static boolean isPort(int port) {
		if(5000<=port && port <= 5050)
			return true;
		return false;
	}
	
	private static Integer[] IP(Scanner sc) {
		
		sc.useDelimiter("[:\\.|\\r]");
		System.out.println("Entrer l'addresse IP du Client");
		
		Integer[] IPbytes = new Integer[4];
		for(int i=0;i<4;i++) {
			try {
				String temp = sc.next();
				IPbytes[i] = Integer.parseInt(temp);
				if(IPbytes[i]>255 || IPbytes[i]<0) {
					System.out.println("Entrer un nombre entre 0 et 255");
					i--;
				}
			}
			catch(Exception e) {
				System.out.println("Entrer un nombre");
				i--;
			}
			
			
		}
		return IPbytes;
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		
		//Demander les info demande à l'utilisateur
		Scanner sc = new Scanner(System.in);
		
		Integer IPbytes[]= IP(sc);
		
		String serverAddress = IPbytes[0] + "." +
							   IPbytes[1] + "." +
							   IPbytes[2] + "." + 
							   IPbytes[3]; 
		
		System.out.println("L'addresse IP est: " + serverAddress);
		/*System.out.println("Veuillez entrer le no de client");
		int clientNumber = sc.nextInt();
		
		
		int serverPort = 0;
		boolean isPort = false;
		while(!isPort) {
			System.out.println("Veuillez entrer le port du serveur");
			serverPort = sc.nextInt();
			isPort = isPort(serverPort);
		}*/
		
		int clientNumber=0;
		int serverPort=0;
		
		//Création de la connexion pour communiquer avec
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
	


	

