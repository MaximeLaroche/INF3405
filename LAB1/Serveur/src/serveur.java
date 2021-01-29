import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;


public class serveur {
	
	private static ServerSocket listener;
	
	
	private static boolean isIP(String ip) {
		if(ip.contains("^[a-zA-Z]+")) {
			//reste beaucoup de contraintes à coder
			return false;
		}
		return true;
	}
	private static boolean isPort(int port) {
		if(5000<=port && port <= 5050)
			return true;
		return false;
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		
		//Demander les info de mande à l'utilisateur
		Scanner sc = new Scanner(System.in);
		
		boolean isIP = false;
		String serverAddress = "abcd";
		while(!isIP) {
			System.out.println("Veuillez entrer l'addresse IP du serveur");
			serverAddress = sc.next();
			isIP = isIP(serverAddress);
		}
		
		System.out.println("Veuillez entrer le no de client");
		int clientNumber = sc.nextInt();
		
		
		int serverPort = 0;
		boolean isPort = false;
		while(!isPort) {
			System.out.println("Veuillez entrer le port du serveur");
			serverPort = sc.nextInt();
			isPort = isPort(serverPort);
		}
		
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
	


	

