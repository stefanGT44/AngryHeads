package gameServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import kryoServer.KryoServer;

/**
 * Server klasa sluzi za pokretanje KryoNet server threada i pojedinacnih server threadova prilikom 
 * konekcije clienata. Ova klasa cuva u dati fajl Korisnike sa njihovim siframa (svaki username je jedinstveen).
 * sifra i username moraju da imaju barem 5 karaktera. 
 * Takodje ova klasa vrsi matchmaking tj povezivanje 2 kleinta kako bi igrali jedan protiv drugog
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Server {

	private HashMap<String, User> playerPool = new HashMap<>(); // KEY = username ; VALUE = player
	private PrimitiveDatabase users = new PrimitiveDatabase("UserDatabase.txt"); // KEY = username; VLUE = password
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Kreira kryoNet server i osluskuje na portu 6000 dali se neki client povezao ukoliko jeste 
	 * otvara poseban thread za komunikaciju sa njim.
	 * @throws Exception ukoliko je doslo do greske prilikom podizanja servera
	 */
	public Server() throws Exception {

		KryoServer.stertKryoServer();

		int port = 6000;
		ServerSocket ss = new ServerSocket(port);
		System.out.println("Server running on port: " + port);

		while (true) {
			Socket sock = ss.accept();

			ServerThread st = new ServerThread(sock, this);
			Thread thread = new Thread(st);
			thread.start();
		}
	}

	/**
	 * Vrsi registraciju korisnik sa prosledjenim username-om i sifrom. Thredsafe situacija se postize uz pomoc Reentrant lock-a.
	 * @param username
	 * 			Korisnicko ime sa kojim korisnik zeli da se registruje
	 * @param password
	 * 			Sifra sa kojom korisnik zeli da se registrueje
	 * @return
	 * 			vraca true ili false u zavisnosti dali je korisnik uspesno ili neuspesno registrovan
	 */
	public boolean register(String username, String password) {
		lock.lock();
		if (users.containsKey(username)) {
			lock.unlock();
			return false;
		}
		users.put(username, password);

		lock.unlock();
		return true;
	}

	/**
	 * Vrsi logovanje korisnik sa prosledjenim username-om i sifrom. Thredsafe situacija se postize uz pomoc Reentrant lock-a.
	 * @param username
	 * 			Korisnicko ime sa kojim korisnik zeli da se loguje
	 * @param password
	 * 			Sifra sa kojom korisnik zeli da se loguje
	 * @return
	 * 			vraca true ili false u zavisnosti dali je korisnik uspesno ili neuspesno login-ovan.
	 */
	public boolean logIn(String username, String password) {
		lock.lock();
		if (users.containsKey(username)) {
			if (users.get(username).equals(password)) {
				lock.unlock();
				return true;
			}
		}

		lock.unlock();
		return false;
	}

	/**
	 * Vrsi povezivanje 2 klienta i prebacuje ih na kryonet komunikaciju. 
	 * Klienti imaju mogucnost da se povezu random prilikom cega se povezuju sa prvim klientom koj se nalazi u 
	 * redu cekanja za random povezivanje. Takodje mogu da se povezu direktno jedni sa drugim ukoliko znaju username od protivnika. 
	 * Takodje se ovde korisnici izbacuju iz reda cekanja ukoliko su se diskonektovali ili izlogovali.
	 * @param command
	 * 			komanda koj govori metodi sta da radi. opcije su vrisanje iz reda cekanja, random povezivanje, 
	 * ili povezivanje sa odredjenim korisnikom
	 * @param user korisnik koj zeli da se poveze sa protivnikom ili random
	 */
	public synchronized void matchmaking(String command, User user) {

		if (command.equals("remove")) {
			if (user.oponent.equals("random")) {
				playerPool.remove("random", user);
			} else {
				playerPool.remove(user.username, user);
			}

			return;
		}

		if (user.oponent.equals("random")) { // matchmaking with random oponent
			if (!playerPool.containsKey("random")) {
				playerPool.put("random", user);
				user.out.println("wait for player");
			} else {
				User user1 = playerPool.get("random");
				playerPool.remove("random");
				Binder.bind(user1, user);
			}

		} else { // matchmaking with specific user
			if (!playerPool.containsKey(user.oponent)) {
				playerPool.put(user.username, user);
				user.out.println("wait for player");
			} else {
				User user1 = playerPool.get(user.oponent);
				if (!user1.username.equals(user.oponent)) // ne slazu im se zahtevi
					return;

				playerPool.remove(user.oponent);
				Binder.bind(user1, user);
			}

		}

	}

	public static void main(String[] args) {
		try {
			new Server();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
