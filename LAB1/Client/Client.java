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

		while (true) {
			socket = new Socket(ip, port);
			System.out.format("Le serveur fonctionne sur :", ip, port, "\n");

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			String helloMessageFromServer = in.readUTF();

			String command = sendCommand();
			out.writeUTF(command);
			if (command.contains("download")) {
				String fileName = command.split(" ", 2)[1];
				FileOutputStream fileOut = new FileOutputStream(fileName);
				socket.getInputStream().transferTo(fileOut);
			} else if (command.contains("upload")) {
				String fileName = command.split(" ", 2)[1];
				FileInputStream fileIn = new FileInputStream("entrepotLocal/" + fileName);

				long amount = fileIn.transferTo(socket.getOutputStream());
				// socket.close();
				// socket = new Socket(ip,port);
				fileIn.close();
			}

			try {
				System.out.println(in.readUTF());
			} catch (Exception e) {
				e.getStackTrace();
			}

			System.out.println(helloMessageFromServer);

		}
		// TODO uniquement close le socket quand on veut exit

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