package com.gdx.client;

import java.util.concurrent.locks.ReentrantLock;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * ServerListener je listener za korisnika kome je dodeljeno da bude server
 * unutar lokalne mreze. Rukuje sa dobijenim paketima od klienta.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ServerListener extends Listener {

	private Connector connector;
	private ReentrantLock lock;

	public ServerListener(Connector connector, ReentrantLock lock) {
		this.connector = connector;
		this.lock = lock;
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		connector.setConnected(true);
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void received(Connection connection, Object object) {

		if (object instanceof Packet) {
			connector.setOponent((Packet) object);
		}

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void disconnected(Connection connection) {
		connector.setOponentDisconnected(true);
	}

}
