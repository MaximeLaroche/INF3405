import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class serveur {
	
	private static ServerSocket listener;

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		int clientNumber = 0;
		
		String serverAddress = "127.0.0.1";
		int serverPort = 5000;
		
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
	
}

	

