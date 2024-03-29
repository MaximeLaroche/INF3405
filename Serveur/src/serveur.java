import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class serveur {

	private static ServerSocket listener;
	private static String ip = "";
	private static int port = 1;
	private static Scanner keyboard = new Scanner(System.in);

	private static void askAddress() {
		/*
		 * Cette fonction reste dans une boucle infinie jusqu'à ce que les variables
		 * globales ip et port ait un bon format
		 */

		boolean hasValidAddress = false;

		String badIP = "Veuillez entrer des nombres entre 0 et 255 separ?s par un .\n Par exemple, 127.0.0.1";
		String badPort = "Veuillez entrer uniquement un nombre entre 5000 et 5050 pour le port";

		do {

			hasValidAddress = true;
			String ligne;
			// Separer les octets par "." et le port par ":"
			String entrees[] = {};
			do {
				System.out.println(
						"Veuiller entrer votre addresse IP, compose de quatre entier entre 0 et 255,\n suivi du port, entre 5000 et 5050.\n Par exemple, 127.0.0.1:5043 est une addresse valide.");
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
					e.getStackTrace();
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
				e.getStackTrace();
			}
			ip = entrees[0] + "." + entrees[1] + "." + entrees[2] + "." + entrees[3];
			port = Integer.parseInt(entrees[4]);

		} while (!hasValidAddress);
	}

	public static void main(String[] args) throws Exception {

		askAddress();

		int clientNumber = 0;

		// Création de la connexion pour communiquer avec
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(ip);

		listener.bind(new InetSocketAddress(serverIP, port));
		System.out.format("The server is running on %s:%d%n", serverIP, port, "\n");

		try {

			while (true) {

				new ClientHandler(listener.accept(), clientNumber++).start();
			}

		} finally {
			listener.close();
		}
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;
		private boolean exit = false;
		private File currentDirectory;

		public ClientHandler(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with client#" + clientNumber + " at " + socket);

			if (currentDirectory == null) {
				currentDirectory = new File(System.getProperty("user.dir"));
				//currentDirectory = new File("entrepotServeur");
			}
		}

		public void run() {

			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				while (exit == false) {

					// Envoyer un premier message au client
					out.writeUTF("Hello from server - you are client# " + clientNumber);

					// Écouter la demande du client
					String query = in.readUTF();
					printInfo(query);

					// Gérer la commande du client et retourner la réponse dans "message"
					String message = commands(query, socket);
					out.writeUTF(message);

				}
			} catch (IOException e) {
				System.out.println("Error handling client#" + clientNumber + ": " + e);
				e.getStackTrace();
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					System.out.print("Could not close the socket\n");
					e.getStackTrace();
				}
			}

		}

		private void printInfo(String command) {
			System.out.println("[" + ip + ":" + port + " - " + getDate() + "]: " + command);
		}

		private String getDate() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss");
			return formatter.format(new Date());
		}

		private String commands(String entrees, Socket socket) {
			/*
			 * Commandes[0] est la commande le reste des index sont les argument
			 */

			String[] commandes = entrees.split(" ", 2);
			String output = "";
			switch (commandes[0]) {
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
					exit = true;
					break;
				default:
					System.out.println("Commande invalide");
			}
			return output;

		}

		private String cd(String whereTo) {
			String message;
			
			
			File temp = new File(whereTo);
			if (temp.exists()) {
				currentDirectory = new File(currentDirectory,whereTo);
				message = "Moved to " + whereTo + "\n";
			} else {
				message = "Impossible to move to " + whereTo + ". The directory does not exist\n";
			}
			
			

			System.out.println(message);
			return message;
		}

		private String ls() {

			String files[] = currentDirectory.list();
			String message = "";
			for (String file : files) {
				System.out.println(file);
				message += file + "\n";
			}
			if (files.length == 0) {
				message = "Directory is empty.\n";
			}
			return message;
		}

		private String mkdir(String directoryName) {
			System.out.println("On est dans mkdir");
			String message = "";
			File folder = new File(currentDirectory.getPath() + "/" + directoryName);
			if (folder.mkdir()) {
				message = "votre dossier " + directoryName + " a ete cree";
				System.out.println(message);
				// out.writeUTF(message);
			}
			return message;

		}

		private String upload(String fileName) {

			FileOutputStream fileOut = null;
			String message = "Could not transfer file";
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

				message = "Succesfully tranfered file";

			} catch (Exception e) {
				System.out.println(message);
				e.getStackTrace();
			}

			return message;
		}

		private String download(String fileName) {
			String message = "downloading 99%... aka(failed)";
			File file = new File(/*"entrepotServeur/" + */ fileName);
			DataOutputStream out = null;
			if (file.exists() && file.length() > 0) {

				long fileSize = file.length();

				try {
					FileInputStream fileIn = new FileInputStream(file);
					out = new DataOutputStream(socket.getOutputStream());
					out.writeBoolean(file.exists());
					out.writeLong(fileSize);
					int paquetSize;
					byte[] paquet = new byte[1024];
					while (fileSize > 0) {
						paquetSize = fileIn.read(paquet);
						out.write(paquet, 0, paquetSize);
						fileSize -= paquetSize;
					}
					fileIn.close();
					message = "File download successfully";
				} catch (Exception e) {
					e.getStackTrace();
				}

			} else {
				boolean exists = false;
				message = "Could not download " + fileName + ". It does not exist";
				try {
					out = new DataOutputStream(socket.getOutputStream());
					out.writeBoolean(exists);
				} catch (Exception e) {
					e.getStackTrace();
					System.out.println("Error sending response");
				}

			}
			return message;
		}
	}

}