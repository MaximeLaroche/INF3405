import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	private static int port = 0;

	public static void main(String[] args) throws Exception {
		// askAddress();
		ip = "127.0.0.1";
		port = 5000;

		socket = new Socket(ip, port);

		while (true) {
			System.out.format("Le serveur fonctionne sur :", ip, port, "\n");

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			String helloMessageFromServer = in.readUTF();
			System.out.println(helloMessageFromServer);

			String command = sendCommand();
			out.writeUTF(command);
			if (command.contains("download")) {
				String fileName = command.split(" ", 2)[1];
				
				download(fileName, socket);
			} else if (command.contains("upload")) {
				
				String fileName = command.split(" ", 2)[1];
				upload(fileName, socket);

			}

			try {
				String response = in.readUTF();
				System.out.println(response);
			} catch (Exception e) {
				e.getStackTrace();
			}

		}
		// TODO uniquement close le socket quand on veut exit

	}

	private static void upload(String fileName, Socket socket) {

		File file = new File(fileName);
		DataOutputStream out = null;
		if (file.exists()) {

			long fileSize = file.length();

			try {
				FileInputStream fileIn = new FileInputStream(file);
				out = new DataOutputStream(socket.getOutputStream());
				out.writeLong(fileSize);
				int paquetSize;
				byte[] paquet = new byte[1024];
				while (fileSize > 0) {
					paquetSize = fileIn.read(paquet);
					out.write(paquet, 0, paquetSize);
					fileSize -= paquetSize;
				}
				fileIn.close();
			} catch (Exception e) {
				e.getStackTrace();
			}

		}
	}
	private static void download(String fileName, Socket socket) {
		FileOutputStream fileOut = null;
		 File currentDirectory = new File(System.getProperty("user.dir"));;
		try {
			fileName = currentDirectory.getPath() + "/" + fileName;
			fileOut = new FileOutputStream(fileName);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			long fileSize = in.readLong();
			byte[] paquet = new byte[1024];
			int paquetLenght;
			while (fileSize > 0) {
				paquetLenght = in.read(paquet);
				fileOut.write(paquet, 0, paquetLenght);
				fileSize -= paquetLenght;
			}
			fileOut.close();

		} catch (Exception e) {
			e.getStackTrace();
		}


	
	}
	
	private static String sendCommand() {
		System.out.println("Envoyer une commande au serveur");
		return keyboard.nextLine();
	}

	// Verifier bonne adresse IP
	private static void askAddress() {

		boolean hasValidAddress = false;

		String badIP = "Veuillez entrer des nombres entre 0 et 255 separ�s par un .\n Par exemple, 152.0.54.254";
		String badPort = "Veuillez entrer uniquement un nombre entre 5000 et 5050 pour le port";

		do {

			hasValidAddress = true;
			String ligne;
			// Separer les octets par "." et le port par ":"
			String entrees[] = {};
			do {
				System.out.println(
						"Veuiller entrer votre addresse IP, compose de quatre entier entre 0 et 255,\n suivi du port, entre 5000 et 5050.\n Par exemple, 127.0.50.254:5043 est une addresse valide.");
				ligne = keyboard.nextLine();
				entrees = ligne.split("[:\\.]");

			} while (entrees.length != 5);

			// Valider l'addresse IP

			for (int i = 0; i < 4; i++) {
				int temp;
				try {
					temp = Integer.parseInt(entrees[i]);
					if (temp < 0 || temp > 255) {
						System.out.println(badIP);
						hasValidAddress = false;
					}
				} catch (Exception e) {
					System.out.println(badIP);
					hasValidAddress = false;
				}
			}

			// Valider le port
			try {
				int temp = Integer.parseInt(entrees[4]);
				if (temp < 5000 || temp > 5050) {
					System.out.println(badPort);
					hasValidAddress = false;
				}
			} catch (Exception e) {
				System.out.println(badPort);
				hasValidAddress = false;
			}
			ip = entrees[0] + "." + entrees[1] + "." + entrees[2] + "." + entrees[3];
			port = Integer.parseInt(entrees[4]);

		} while (!hasValidAddress);
	}

}