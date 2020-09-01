package com.gdx.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import com.gdx.screens.ConnectingScreen;
import com.gdx.screens.GameScreen;

/**
 * Clietn klasa sluzi za povezivanje sa serverom preko TCP protokola, vrsenje registracije, logovanje,
 * povezivanje sa protivnikom i diskonekcija sa serverom. Ova konekcija je samo validna dok je korisnik u Meniu<br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */

public class Client {
	private ConnectingScreen connectingScreen;
	private GameScreen gameScreen;  
	private boolean exit = false;

	private Socket sock;
	private PrintWriter out;
	private BufferedReader in;
	private String msg;

	private String username;
	private String password;
	private String oponent;
	private String host;

	
	/**
	 * Kontruktor Clienta koj vrsi stvaranje TCP konekcije sa serverom
	 * 
	 * 
	 * @param host
	 * 			adresa servera
	 * @param port
	 * 			port na koj se povezuje
	 * @param connectingScreen
	 * 			view za povezivanje iz libGdx koda
	 * @param gameScreen
	 * 			view gde se izvrsava igra 	
	 */
	public Client(String host, int port, ConnectingScreen connectingScreen, GameScreen gameScreen) {
		this.host = host;
		this.connectingScreen = connectingScreen;
		this.gameScreen = gameScreen;

		try {
			sock = new Socket(host, port);

			out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			System.out.println("Connected to server");
		} catch (Exception e) {
			System.out.println("Connection to server failed");
		}
	}

	/**
	 * Registracija korisniika
	 * @param username
	 * 			korisnicko ime
	 * @param password
	 * 			Sifra
	 * @return Vraca poruku od servera ili ukoliko je doslo do greske ili prekida u komunikaciji vraca: registration failed
	 */
	public String register(String username, String password) {

		try {
			msg = "register;" + username + ";" + password;
			out.println(msg);

			msg = in.readLine();
			msg = msg.split(";")[0];

			return msg;
		} catch (Exception e) {
			return "registration failed";
		}
	}

	/**
	 * Logovanje korisnika
	 * @param username
	 * 			korisnicko ime
	 * @param password
	 * 			lozinka korisnika
	 * @return	Vraca poruku od servera ili ukoliko je doslo do greske ili prekida u komunikaciji vraca: Log In failed
	 */		
	public String logIn(String username, String password) {

		try {
			this.username = username;
			this.password = password;

			msg = "log in;" + username + ";" + password;
			out.println(msg);

			msg = in.readLine();
			msg = msg.split(";")[0];

			return msg;

		} catch (Exception e) {

			return "Log In failed";
		}
	}

	/**
	 * Vrsi povezivanje sa datim protivnikom i prelazi na kryoNet konekciju tj pokrece novi thread za Connector koj koristi kryoNet biblioteku
	 * @param oponent
	 * 			Korisnicko ime protivnika
	 * @param lock
	 * 			Reentrant lock koj sluzi za sigurnu promenu View-a u tredu od igrice
	 */
	public void connectToPlayer(String oponent, ReentrantLock lock) {

		try {

			msg = "connect to player;" + username + ";" + oponent;
			out.println(msg);

			while (true) {
				if (exit(false)) {
					return;
				}

				if (in.ready())
					msg = in.readLine();
				else
					continue;

				if (msg.charAt(0) == 'R' || msg.charAt(0) == 'L') {
					connectingScreen.position = msg.charAt(0);
					continue;
				}

				if (msg.contains("wait for player")) {
					continue;
				} else {
					oponent = msg.split(";")[1];
					break;
				}
			}

			sock.close();

			Connector connector = new Connector(username, oponent, lock);

			Thread connectorThread = new Thread(connector);
			gameScreen.setConnector(connector, connectorThread);
			connectorThread.start();

			while (true) {
				if (connector.isConnected()) {
					connectingScreen.nextScreen(true);
					break;
				}
				if (!connectorThread.isAlive()) {
					connectingScreen.backScreen(true);
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("connecting failed");
		}
	}

	/**
	 * Vrsi diskonektovanje klienta od servera
	 */
	public void disconnect() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sluzi za izlazak iz while petlje ukoliko korisnik izadje iz menia
	 * @param b
	 * @return
	 */
	public synchronized boolean exit(boolean b) {
		boolean tmp = exit;
		exit = b;
		return tmp;
	}

}
