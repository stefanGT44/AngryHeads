package gameServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ova klasa predstavalj rad threada koj komunicira samo sa jednim korisnikom preko socket-a koj mu je 
 * dodeljen. Vrsi registrovanje, loginovanje i pokretanje povezivanje sa protivnikom prilikom cega se konekcija 
 * prebacuje na brzu kryoNet konekciju. Takodje registruje ukoliko se korisnik diskonektovao.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ServerThread implements Runnable {

	private Socket sock;
	private Server server;

	private String msg;
	private String[] request;

	private String username = null;
	private String password = null;
	private String oponent = null;
	private User player = null;

	private boolean logedIn = false;

	public ServerThread(Socket sock, Server server) {
		this.sock = sock;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			System.out.println("[" + sock.getInetAddress() + " : " + sock.getPort() + "] Connected");

			PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			while (true) {
				try {
					msg = in.readLine();
				} catch (Exception e) {
					break;
				}
				if (msg == null)
					break;

				request = msg.split(";");
				if (request.length < 3)
					continue;

				if (request[0].equals("connect to player") && logedIn) {
					username = request[1];
					oponent = request[2];
					player = new User(username, oponent, out);
					server.matchmaking("connect", player);
				} else { // LOG IN
					if (request[0].equals("log in")) {
						username = request[1];
						password = request[2];

						if (server.logIn(username, password)) {
							logedIn = true;
							out.println("log in successfull;");
						} else {
							logedIn = false;
							out.println("log in failed;");
						}
					} else { // REGISTRATION
						if (request[0].equals("register")) {
							username = request[1];
							password = request[2];

							if (server.register(username, password))
								out.println("registration successfull;");
							else {
								out.println("registration failed;");
							}
						}
					}
				}
			}
			sock.close();
			System.out.println("[" + sock.getInetAddress() + " : " + sock.getPort() + "] Disconnected");

			// kada se diskonektuje da se proveri i izbaci iz playerPool
			if (player != null) {
				server.matchmaking("remove", player);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
