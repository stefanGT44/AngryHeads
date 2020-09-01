package com.gdx.client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.utils.Queue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.gdx.game.GdxGame;

/**
 * Connector sluzi za pokretanje i odrzavanje KryoNet konekcije.
 * Povezuje korisnike preko glavnog servera ili lokalno u zavisnosti od toga dali se nalaze na istoj lokalnoj mrezi.
 * Ako su povezani preko glavnog servera oba korisnika su client, Ako su povezani lokalno jedan korisnik je server dok je drugi klient.
 * Glavni server vrsi dodeljivanje uloga ukoliko se lokalno povezuju. Prilikom povezivanja prvo pokusavaju da se povezu preko UDP protokola 
 * ukoliko to ne uspe zbog mogucnosti gubljenja paketa pokrecu sigurno ali malo sporije povezivanje preko TCP protokola
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Connector implements Runnable {

	private ReentrantLock lock;
	private Queue<Packet> queue = new Queue<Packet>();
	private Packet oponent;
	private boolean connected = false;
	private boolean isServer = false;
	private boolean isClient = false;
	private String localServerIp;
	private int localTCPPort;
	private int localUDPPort;

	private String serverIp = "hadziserver.ddns.net";
	private int serverTCPPort = 7070;
	private int serverUDPPort = 7071;
	private int backupTCPPort = 9090;
	private String oponentName;
	private String username;
	private boolean useBackup = false;
	public boolean oponentDisconnected = false;

	public Connector(String username, String oponentName, ReentrantLock lock) {
		this.username = username;
		this.oponentName = oponentName;
		GdxGame.get().gameScreen.oponentName = oponentName;
		GdxGame.get().gameScreen.myName = username;
		this.lock = lock;
	}

	@Override
	public void run() {

		try {
			Client client = new Client();
			new Thread(client).start();
			if (connectToServer(client) == false)
				return;

			Kryo kryo = client.getKryo();
			kryo.register(String.class);
			kryo.register(Packet.class);

			ClientListener listener = new ClientListener(this);
			client.addListener(listener);
			ServerSocket sock = new ServerSocket(0);
			int TCPport = sock.getLocalPort();
			sock.setReuseAddress(true);
			sock.close();

			DatagramSocket dSock = new DatagramSocket();
			int UDPport = dSock.getLocalPort();
			dSock.setReuseAddress(true);
			dSock.close();

			String adresses = getIPs();

			client.sendTCP(username + ";" + oponentName + ";" + adresses + ";" + TCPport + ";" + UDPport + ";");

			while (true) {
				if (isConnected()) {
					if (isOponentDisconnected()) {
						return;
					}
					if (queueSize() > 0) {
						if (useBackup) {
							client.sendTCP(getPlayer());
						} else {
							client.sendUDP(getPlayer());
						}
					}
				} else {
					if (isServer || isClient) {
						break;
					}
				}
			}

			if (isServer) {
				System.out.println("SERVER");
				client.close();
				Server server = new Server();
				server.start();
				server.bind(TCPport, UDPport);

				Kryo skryo = server.getKryo();
				skryo.register(Packet.class);

				server.addListener(new ServerListener(this, lock));

				while (true) {
					if (isOponentDisconnected()) {
						return;
					}
					if (queueSize() > 0) {
						server.sendToAllUDP(getPlayer());
					}
				}
			}

			if (isClient) {
				System.out.println("CLIENT");
				if (connectToLocalServer(client) == false) {
					return;
				}
				setConnected(true);

				while (true) {
					if (!client.isConnected()) {
						return;
					}
					if (queueSize() > 0) {
						client.sendUDP(getPlayer());
					}
				}

			}

		} catch (Exception e) {
			System.out.println("Server communication Error");
			e.printStackTrace();
		}
	}

	/**
	 * Povezuje clienta sa glavnim serverom
	 * @param client
	 * 			KryoNet klient
	 * @return vraca true ili false u zavisnosti od toga dali je konekcija uspesno postignuta
	 */
	private boolean connectToServer(Client client) {
		try {
			client.connect(5000, serverIp, serverTCPPort, serverUDPPort);
			useBackup = false;
		} catch (Exception e) {
			try {
				client.connect(6000, serverIp, backupTCPPort);
				useBackup = true;
			} catch (Exception ex) {
				System.out.println("Could not connect to server");
				return false;
			}
		}
		return true;
	}

	/**
	 * Povezuje klienta sa lokalnim serverom tj sa protivnikom
	 * @param client
	 * 			KryoNet klient
	 * @return vraca true ili false u zavisnosti od toga dali je konekcija uspesno postignuta
	 */
	private boolean connectToLocalServer(Client client) {
		boolean connected = false;
		String[] localAdresses = getLocalServerIp().split("\\|");
		for (int i = 0; i < localAdresses.length; i++) {
			try {
				client.connect(5000, localAdresses[i], getLocalTCPPort(), getLocalUDPPort());
				connected = true;
				break;
			} catch (Exception ipEx) {
			}
		}
		return connected;
	}

	/**
	 * Preuzimanje prvog paketa u redu koj je primljen preko konekcije
	 * @return Vraca Player objekat koj u sebi ima ucitano sve podatke iz paketa
	 */
	public synchronized Packet getPlayer() {
		Packet player = queue.first();
		queue.removeFirst();
		return player;
	}

	/**
	 * Stavlja paket koj je primljen preko konekcije u red (queue) kako bi se kasnije iz njega iscitavali
	 * @param player
	 * 			Paket koj je dobijen preko konekcije koj sadrzi podatke o protivniku
	 */
	public synchronized void queuePlayer(Packet player) {
		queue.addLast(player);
	}

	public synchronized int queueSize() {
		return queue.size;
	}

	public synchronized Packet getOponent() {
		return oponent;
	}

	public synchronized void setOponent(Packet oponent) {
		this.oponent = oponent;
	}

	public synchronized boolean isConnected() {
		return this.connected;

	}

	public synchronized void setConnected(boolean connected) {
		this.connected = connected;
	}

	public synchronized boolean isServer() {
		return isServer;
	}

	public synchronized void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public synchronized boolean isClient() {
		return isClient;
	}

	public synchronized void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public synchronized String getLocalServerIp() {
		return localServerIp;
	}

	public synchronized void setLocalServerIp(String localServerIp) {
		this.localServerIp = localServerIp;
	}

	public synchronized int getLocalTCPPort() {
		return localTCPPort;
	}

	public synchronized void setLocalTCPPort(int localTCPPort) {
		this.localTCPPort = localTCPPort;
	}

	public synchronized int getLocalUDPPort() {
		return localUDPPort;
	}

	public synchronized void setLocalUDPPort(int localUDPPort) {
		this.localUDPPort = localUDPPort;
	}

	public synchronized boolean isOponentDisconnected() {
		return oponentDisconnected;
	}

	public synchronized void setOponentDisconnected(boolean oponentDisconnected) {
		this.oponentDisconnected = oponentDisconnected;
	}

	/**
	 * Dohvata sve lokalne ip adrese sa date masine od korisnika kako bi ih prosledio protivniku preko glavnog servera
	 * ukoliko se konektuju lokalno. Protivnik pokusava kasnije da se konektuje redom na date adrese.
	 * @return niz lokalnih ip adresa
	 * @throws Exception ukoliko je nastala greska prilikom dohvatanja adresa
	 */
	private String getIPs() throws Exception {
		String adresses = "|";
		try {
			String str = "";
			Pattern patern = Pattern
					.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
			Matcher matcher = null;
			Enumeration<NetworkInterface> enumNI = NetworkInterface.getNetworkInterfaces();
			while (enumNI.hasMoreElements()) {
				NetworkInterface ifc = enumNI.nextElement();
				if (ifc.isUp()) {
					Enumeration<InetAddress> enumAdds = ifc.getInetAddresses();
					while (enumAdds.hasMoreElements()) {
						InetAddress addr = enumAdds.nextElement();
						str = addr.getHostAddress() + "";
						matcher = patern.matcher(str);
						if (matcher.matches())
							adresses += str + "|";
					}
				}
			}

		} catch (Exception e) {
			try {
				adresses = "|" + InetAddress.getLocalHost().getHostAddress() + "|";
			} catch (Exception ex) {
				throw new Exception();
			}
		}

		return adresses;
	}

}
