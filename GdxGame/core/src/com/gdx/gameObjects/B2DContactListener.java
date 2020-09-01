package com.gdx.gameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gdx.game.GdxGame;
import com.gdx.player.Player;
import com.gdx.player.RocketMan;
import com.gdx.screens.GameScreen;
/**
 * Listener za kontakt izmedju Box2D objekata. 
 * Sluzi prvenstveno za brisanje projektila prilokom kontakta sa Box2D svetom.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class B2DContactListener implements ContactListener {
	private Body projectile;
	private Player player1;
	public Circle impactCircle = new Circle(0, 0, 50);
	private float x, y;
	// private long currentTime, timeSinceLastImpact, impactTimeDelay = 300;
	private Object tmp;
	private float impactForceX = 220, impactForceY = 350;

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		if ((contact.getFixtureA().getBody().getUserData() instanceof Projectile)
				&& (contact.getFixtureB().getBody().getUserData() instanceof gameBody)) {
			if (GdxGame.get().gameScreen.player1 instanceof RocketMan) {
				((RocketMan) GdxGame.get().gameScreen.player1).destroy(contact.getFixtureA().getBody());
			} else {
				if (GdxGame.get().gameScreen.player2 instanceof RocketMan) {
					((RocketMan) GdxGame.get().gameScreen.player2).destroy(contact.getFixtureA().getBody());
				}
			}
			projectile = contact.getFixtureA().getBody();
			player1 = GdxGame.get().gameScreen.player1;
			if (tmp == projectile.getUserData())
				return;
			tmp = projectile.getUserData();

			x = projectile.getPosition().x * GameScreen.PPM;
			y = projectile.getPosition().y * GameScreen.PPM;

			impactCircle.setPosition(x, y);
			player1.bodyRectangle.setPosition(player1.body.getPosition().x * GameScreen.PPM - player1.width / 2,
					player1.body.getPosition().y * GameScreen.PPM - player1.height / 2);

			if (Intersector.overlaps(impactCircle, player1.bodyRectangle)) {
				impact();
			}

		} else {
			if ((contact.getFixtureA().getBody().getUserData() instanceof gameBody)
					&& (contact.getFixtureB().getBody().getUserData() instanceof Projectile)) {
				if (GdxGame.get().gameScreen.player1 instanceof RocketMan) {
					Projectile projectile = (Projectile)contact.getFixtureB().getBody().getUserData();
					GdxGame.get().gameScreen.projectiles.remove(projectile);
					projectile.image.remove();
					RocketMan rocketMan = (RocketMan)GdxGame.get().gameScreen.player1;
					rocketMan.destroy(contact.getFixtureB().getBody());
				} else {
					if (GdxGame.get().gameScreen.player2 instanceof RocketMan) {
						((RocketMan) GdxGame.get().gameScreen.player2).destroy(contact.getFixtureB().getBody());
					}
				}

				projectile = contact.getFixtureB().getBody();
				player1 = GdxGame.get().gameScreen.player1;
				if (tmp == projectile.getUserData())
					return;
				tmp = projectile.getUserData();

				x = projectile.getPosition().x * GameScreen.PPM;
				y = projectile.getPosition().y * GameScreen.PPM;

				impactCircle.setPosition(x, y);
				player1.bodyRectangle.setPosition(player1.body.getPosition().x * GameScreen.PPM - player1.width / 2,
						player1.body.getPosition().y * GameScreen.PPM - player1.height / 2);

				if (Intersector.overlaps(impactCircle, player1.bodyRectangle)) {
					impact();
				}
			}
		}
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	/**
	 * poziva se ukoliko je projektil u kontaktu sa nekim delom Box2D sveta i pokrece akciju explozije i oduzimanje health-a 
	 * od igraca ukoliko ga je pogodio. Takodje pookrece akciju odbijanja objekta od centra na kom se izvrsio impact.
	 */
	private void impact() {

		if (impactCircle.x <= (player1.bodyRectangle.x + player1.width / 2)) {
			x = impactForceX;
		} else {
			x = -impactForceX;
		}
		if (impactCircle.y <= (player1.bodyRectangle.y + player1.height / 2)) {
			y = impactForceY;
		} else {
			y = -impactForceY;
		}

		GdxGame.get().gameScreen.player1.projectileHitVector.set(x, y);
		GdxGame.get().gameScreen.player1.projectileHit = true;

		if (((Projectile) projectile.getUserData()).player == GdxGame.get().gameScreen.player1) {
			GdxGame.get().gameScreen.player1.projectileDemage = false;
		} else {
			GdxGame.get().gameScreen.player1.projectileDemage = true;
			GdxGame.get().gameScreen.player1.hitByProjectile = true;
		}
	}

}
