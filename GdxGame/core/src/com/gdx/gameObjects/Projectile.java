package com.gdx.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.game.GdxGame;
import com.gdx.player.Player;

/**
 * Projectile koj kontronilse i sadrzi sve informacije o Box2D body-u koj predstavlja projektil od playera 'rocketMan'
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Projectile {

	public Body body;
	private float width = 10, height = 10;
	public Player player;
	public Image image;
	float PPM = GdxGame.get().gameScreen.PPM;
	private boolean start = true;

	/**
	 * Konstruktor za projektil
	 * @param x
	 * 			x koordinata
	 * @param y
	 * 			y koordinata
	 * @param bulletForceX
	 * 			Sila koja deluje na body po x koordinati
	 * @param bulletForceY
	 * 			Sila koja deluje na body po y koordinati
	 * @param player
	 * 			Player kome pripada taj projektil tj player koj ga je ispalio.
	 */
	public Projectile(float x, float y, float bulletForceX, float bulletForceY, Player player) {
		this.player = player;

		Body body;
		BodyDef def = new BodyDef();

		def.type = BodyDef.BodyType.DynamicBody;
		// def.bullet = true;
		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = true;
		body = GdxGame.get().gameScreen.world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

		body.createFixture(shape, 1f);
		shape.dispose();
		body.applyForceToCenter(bulletForceX, bulletForceY, false);
		body.setUserData(this);
		GdxGame.get().gameScreen.activeBodys.add(body);

		this.body = body;

		image = new Image((Texture) GdxGame.get().assets.get("Characters/rocketMan/bullet/bullet1.png"));
		image.setSize(32, 32);

		GdxGame.get().gameScreen.projectiles.add(this);
	}

	/**
	 * Sluzi za updateovanje slike animacije datog projektila.
	 * @param delta
	 * 			deleta vrednosti iz render metode koja nosi vrednost kasnjenja izmedju render cycle-a.
	 */
	public void update(float delta) {
		image.setPosition(body.getPosition().x * PPM - 16, body.getPosition().y * PPM - 16);
		if (start) {
			GdxGame.get().gameScreen.stage.addActor(image);
			start = false;
		}
	}

}
