import java.io.DataInputStream;
import java.io.DatainputStream;
import java.net.Socket;

public class Client {
	
	private static Socket socket;
	
	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		int port= 5000;
		socket = new Socket(severAddress,port);
		System.out.format("Le serveur fonctionne sur :", serverAddress, port);
		
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServer= in.readUTF();
		Systeme.out.println("HelloFromServer");
		
		socket.close();
		
	}
	
}