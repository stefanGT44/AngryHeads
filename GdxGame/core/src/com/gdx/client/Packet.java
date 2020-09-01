package com.gdx.client;

/**
 * Paket koj se prosledjuje protivniku preko KryoNet konekcije koj sadrzi sve podatke o datom korisnikovom igracu
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Packet {

	public float x = 0, y = 0, scaleX = 1;
	public boolean running = false, direction = false, jump = false, hit = false, attacking = false, shoot = false,
			shield = false, invisible = false, flightRequest = false;
	public int attackId = 0, attackPosMax = 0, HP = 0;

}
