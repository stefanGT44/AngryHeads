package com.gdx.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Listener od KryoNet biblioteke koj nasluskuje dali je dobio novi paket preko konekcije i reaguje na odgovarajuci nacin u zavisnosti od toga 
 * kojoj instanci pripada paket i sta je njegov sadrzaj <br>
 * object predstavlja paket koj se dobije preko konekcije. Ukoliko je paket instance String izvrsava dodelu rezima rada connectora
 * u zavisnosti od toga sta je sadrzaj paketa ('connected', 'start server', 'start client', 'oponent disconected'). 
 * 'start server' i 'start client' se pokrece ukoliko je detektovao Server da se cilienti nalaze na istoj lokalnoj mrezi i pokrece 
 * direktnu server klient komunikaciju izmedju njih, jedan se pokrece kao server drugi kao klient.<br>
 * Ukoliko je instanca paketa 'Packet' onda sadrzaj paketa predstavlja podatke o protivniku  i biva dodeljena konektoru<br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ClientListener extends Listener {

	private Connector connector;

	public ClientListener(Connector connector) {
		this.connector = connector;
	}
	
	/**
	  * {@inheritDoc}
	  * object predstavlja paket koj se dobije preko konekcije. Ukoliko je paket instance String izvrsava dodelu rezima rada connectora
	  * u zavisnosti od toga sta je sadrzaj paketa ('connected', 'start server', 'start client', 'oponent disconected'). 
	  * 'start server' i 'start client' se pokrece ukoliko je detektovao Server da se cilienti nalaze na istoj lokalnoj mrezi i pokrece 
	  * direktnu server klient komunikaciju izmedju njih, jedan se pokrece kao server drugi kao klient.<br>
	  * Ukoliko je instanca paketa 'Packet' onda sadrzaj paketa predstavlja podatke o protivniku  i biva dodeljena konektoru
	  */
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof String) {
			String msg = ((String) object);
			System.out.println(msg);
			if (msg.equals("connected")) {
				connector.setConnected(true);
			}

			if (msg.equals("start server")) {
				connector.setServer(true);
			}

			if (msg.contains("start client")) {
				connector.setLocalServerIp(msg.split(";")[1]);
				connector.setLocalTCPPort(Integer.parseInt(msg.split(";")[2]));
				connector.setLocalUDPPort(Integer.parseInt(msg.split(";")[3]));
				connector.setClient(true);
			}

			if (msg.equals("oponent disconnected")) {
				connector.setOponentDisconnected(true);
			}

		}

		if (object instanceof Packet) {
			connector.setOponent((Packet) object);
		}
	}

}
