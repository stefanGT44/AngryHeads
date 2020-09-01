package kryoServer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
/**
 * Ovo predstavlja KryoNet Server koje sluzi za komunikaciju izmedju igraca tokom igre.
 * Ovaj server otvara portove za UDP i TCP komunikaciju. TCP komunikacija sluzi kao backup ukoliko 
 * dodje do problema kod UDP komunikacije zbog gubljenja paketa.
 * Dodeljuje sebi takodje i listener koj osluskuje dali je dodo paket preko konekcije.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class KryoServer {

	/**
	 * Pokrece KryoNet Server i otvara portove 7070 za UDP 7071 za TCP i jos jedan TCP port 9090 za sigurnost 
	 * ukoliko je komunikacija iskljucivo TCP. 
	 */
	public static void stertKryoServer() {

		try {
			ServerListener listener = new ServerListener();
			// Main Server
			Server server = new Server();
			server.start();
			server.bind(7070, 7071);

			Kryo kryo = server.getKryo();
			kryo.register(String.class);
			kryo.register(Packet.class);

			server.addListener(listener);

			// Backup Server
			Server backupServer = new Server();
			backupServer.start();
			backupServer.bind(9090);

			Kryo kryo1 = backupServer.getKryo();
			kryo1.register(String.class);
			kryo1.register(Packet.class);

			backupServer.addListener(listener);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
