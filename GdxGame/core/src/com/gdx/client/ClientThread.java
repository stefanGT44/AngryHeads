package com.gdx.client;

import java.util.concurrent.locks.ReentrantLock;

import com.gdx.game.GdxGame;
import com.gdx.screens.ConnectingScreen;
import com.gdx.screens.GameScreen;
import com.gdx.screens.LogInScreen;

/**
 * ClientThread predstavlja thread koj pokrece client server komunikaciju izmedju korisnika kao clienta i glavnog servera. 
 * Ova komunukacija se odrzava sve dok je korisnik u meniu i nije preso na kryoNet komunikaciju
 * Vrsi izemnu View-a u libgdx threadu u zavisnosti od toga dali se korisnik logovao, registrovao ili pokrenuo konektovanje 
 * sa protivnikom prilikom cega se client kladi salje data komanda i pokrece KryoNet konekcija <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ClientThread implements Runnable {

	private ConnectingScreen connectingScreen;
	private GameScreen gameScreen;
	private ReentrantLock lock;
	public String command;

	public String username;
	public String password;
	public String oponent;

	private LogInScreen logInScreen;
	public Client client;

	public ClientThread(LogInScreen logInScreen, ReentrantLock lock, ConnectingScreen connectingScreen,
			GameScreen gameScreen) {
		this.logInScreen = logInScreen;
		this.lock = lock;
		this.connectingScreen = connectingScreen;
		this.gameScreen = gameScreen;

	}

	private void connectToServer() {
		if (client == null)
			client = new Client("hadziserver.ddns.net", 6000, connectingScreen, gameScreen);
	}

	@Override
	public void run() {

		try {
			while (true) {
				lock.lock();

				if (command.equals("kill")) {
					client.disconnect();
					return;
				}

				if (command.equals("logIn")) { // LOGIN
					connectToServer();

					String msg = client.logIn(username, password);
					System.out.println(msg);

					if (msg.equals("log in successfull"))
						logInScreen.changeScreen = true;
				} else if (command.equals("register")) { // REGISTER
					connectToServer();

					String msg = client.register(username, password);
					System.out.println(msg);

					if (msg.equals("registration failed"))
						logInScreen.registrationFailed = true;
					else
						logInScreen.registrationFailed = false;
				} else if (command.equals("connect")) { // CONNECT
					client.connectToPlayer(oponent, lock);
					client.disconnect();
					return;
				}

				lock.unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
