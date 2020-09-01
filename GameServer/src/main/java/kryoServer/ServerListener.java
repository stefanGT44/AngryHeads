package kryoServer;

import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
/**
 * Server listener koj osluskuje dali je doso neki paket preko mreze na odgovarajuce portove. 
 * Predstavlja samo jednu vrstu prosledjivaca paketa (client1->server->client2).
 * Ima u hash mapi zabelezeno tacno kom klientu prosledjuje paket u zavisnosti od toga od kog klienta je doso paket.
 * Takodje javlja clientu da ceka na protivnika ukoliko se taj jos nije javio serveru tj konektovao. 
 * Ukoliko su klienti na istoj lokalnoj mrezi tj javili su se sa istom public ipAdresom salje clientu1 lokalnu ip adresu od clienta2 koju 
 * je dobio od njega i obrnuto jednom klientu dodeljuje ulogu servera, 
 * a drugom ulogu clienta. Ukoliko ta konekcija ne uspe klienti se ponovo javljaju ovom serveru i povezuju se preko njega.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ServerListener extends Listener {

	private HashMap<Connection, Connection> activePlayers = new HashMap<>();
	private HashMap<String, Connection> notConnectedPalyers = new HashMap<>();
	private String ip1;
	private String ip2;

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void connected(Connection connection) {
		super.connected(connection);
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void received(Connection connection, Object object) {
		try {
			if (object instanceof String) {
				String[] str = ((String) object).split(";");
				if (notConnectedPalyers.containsKey(str[1])) {
					activePlayers.put(connection, notConnectedPalyers.get(str[1]));
					activePlayers.put(notConnectedPalyers.get(str[1]), connection);

					ip1 = connection.getRemoteAddressTCP().toString();
					ip1 = ip1.substring(ip1.indexOf("/") + 1, ip1.indexOf(":"));

					ip2 = notConnectedPalyers.get(str[1]).getRemoteAddressTCP().toString();
					ip2 = ip2.substring(ip2.indexOf("/") + 1, ip2.indexOf(":"));

					if (ip1.equals(ip2)) {

						connection.sendTCP("start server");
						notConnectedPalyers.get(str[1])
								.sendTCP("start client;" + str[2] + ";" + str[3] + ";" + str[4] + ";");

					} else {
						connection.sendTCP("connected");
						notConnectedPalyers.get(str[1]).sendTCP("connected");
					}

					notConnectedPalyers.remove(str[1]);
				} else {
					notConnectedPalyers.put(str[0], connection);
					connection.sendTCP("wait for oponent");
				}

			}

			if (object instanceof Packet) {
				activePlayers.get(connection).sendTCP(object);
			}
		} catch (Exception e) {

		}
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void disconnected(Connection connection) {

		if (activePlayers.containsKey(connection)) {
			try {
				activePlayers.get(connection).sendTCP("oponent disconnected");
			} catch (Exception e) {

			}

			activePlayers.remove(connection);
		} else {
			try {
				notConnectedPalyers.values().remove(connection);
			} catch (Exception e) {

			}
		}

	}

}
