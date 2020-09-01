package com.gdx.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.gdx.client.Packet;
import com.gdx.game.GdxGame;
import com.gdx.gameObjects.Projectile;
import com.gdx.screens.GameScreen;

/**
 * Klasa koja kontrolise kretanje i prikaz rocketman-a u zavisnosti od kontrola koje koristi korisnik.
 * postavlja sebe kao izmenjeni parametar ukoliko je doslo do promene nekog parametra od playera da bi se njegovi 
 * parametri preuzeli i spakovali u paket i poslali preko mreze. 
 * Ceo prikaz i pozicija igraca su regulisani uspomoc ove klase kao i provere dali je pogodjen sa strane protivnika.
 * 
 * Dodati su jos detalji koji se iskljucivo odnose na rocketman-a kao sto je letenje u vis i aktiviranje
 * stita zajedno sa svim promenama animacija u odgovarajucim trenutcima
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class RocketMan extends Player {

	private SpriteDrawable[] idleAnimation, walkAnimation, bloodAnimation, shieldAnimation, shootAnimation, explosionAnimation;

	private int anim = 0;
	private int idlePos = 0, runPos = 0, bloodPos = 0, shieldPos = 0, shootPos = 0, explosionPos = 0;
	private float bulletForceX = 25, bulletForceY = 10;
	private long currentTime, timeSinceLastShoot = 0, timeSinceFlight = 0;
	private int shootTimer = 700;
	public boolean shoot = false, flightRequest = false, shieldAdded = false, shooting = false, explosion = false;
	public Circle shieldCircle = new Circle(0, 0, 100);
	public Image shieldImage, explosionImage;

	private ParticleEffect pe;
	private boolean flying;

	public Array<Body> destroyBodys = new Array<Body>();

	public RocketMan(float x, float y, float width, float height, boolean isStatic, int scaleX) {
		super(x, y, width, height, isStatic);
		idleAnimation = loadAnimation(4, "rocketMan/idle/idle");
		walkAnimation = loadAnimation(6, "rocketMan/walk/walk");
		bloodAnimation = loadAnimation(5, "blood/blood");
		shieldAnimation = loadAnimation(5, "rocketMan/shield/shield");
		shootAnimation = loadAnimation(6, "rocketMan/shoot/shoot");
		explosionAnimation = loadAnimation(13, "rocketMan/explosion/explosion");

		pe = new ParticleEffect();
		pe.load(Gdx.files.internal("Characters/rocketMan/particleEffects/jetpack1.part"),
				Gdx.files.internal("Characters/rocketMan/particleEffects/"));
		pe.scaleEffect(0.8f);
		pe.flipY();

		image = new Image();
		image.setSize(128, 128);
		image.setDrawable(idleAnimation[0]);
		image.setScaleX(scaleX);
		
		explosionImage = new Image();
		explosionImage.setSize(128, 128);

		bloodImage = new Image();
		bloodImage.setSize(128, 128);

		shieldImage = new Image();
		shieldImage.setSize(256, 256);

		GdxGame.get().gameScreen.stage.addActor(image);

		offsetX = 50;
		offsetY = 28;

		attackDamage = 20;

		hitForceX = 80;
		hitForceY = 150;

		extraBar = true;

		thirdPowerCD = 10000;
		thirdPowerDuration = 3000;

		updateStaticSpritePosition();
	}

	public void render(float delta) {
		if (shield) {
			if (GdxGame.get().gameScreen.player1 == this) {
				currentTime = System.currentTimeMillis();
				if (currentTime - timeSinceLastThirdPower > thirdPowerDuration) {
					shield = false;
					gameScreen.packetChanged = true;
					shieldImage.remove();
					shieldAdded = false;
				}
			}
			shield();
		}

		if (shoot) {
			shootProjectile();
			shoot = false;
			shooting = true;
			gameScreen.packetChanged = true;
		}
		for (Body body : destroyBodys) {
			try {
				GdxGame.get().gameScreen.world.destroyBody(body);
				destroyBodys.removeValue(body, true);
			} catch (Exception e) {
			}
		}

		timeSinceLastRender += delta;
		if (timeSinceLastRender >= MIN_FRAME_LENGTH) {
			super.render(delta);

			anim++;
			
			if (shield) {
				if (shieldPos == shieldAnimation.length)
					shieldPos = 0;
				// if (anim % 2 == 0) {
				shieldImage.setDrawable(shieldAnimation[shieldPos++]);
				// }
			}
			if (struck) {
				if (bloodPos == bloodAnimation.length) {
					struck = false;
					bloodPos = 0;
					bloodImage.remove();
				} else {
					if (anim % 3 == 0) {
						if (bloodPos == 0) {
							bloodImage.setScaleX(gameScreen.getOppositeCharacter(this).image.getScaleX());
							visualControlUpdates(delta);
							GdxGame.get().gameScreen.stage.addActor(bloodImage);
						}
						bloodImage.setDrawable(bloodAnimation[bloodPos++]);
					}
				}
			}
			
			if (explosion) {
				if (explosionPos == explosionAnimation.length) {
					explosion = false;
					explosionPos = 0;
					explosionImage.remove();
				} else {
					if (explosionPos == 0) gameScreen.stage.addActor(explosionImage);
					explosionImage.setDrawable(explosionAnimation[explosionPos++]);
				}
			}

			if (attacking) {
				attacking = false;

				// shoot i packetChanged mora da se odradi SAMO JEDNOM kada je odgovarajuci
				// frejm pucanja
				// prikazan
				shoot = true;
				gameScreen.packetChanged = true;
			}
			if (shooting) {
				if (shootPos == shootAnimation.length) {
					shootPos = 0;
					shooting = false;
				} else
					if (anim % 2 == 0)
					image.setDrawable(shootAnimation[shootPos++]);
			} else
			if (running) {
				if (runPos == walkAnimation.length)
					runPos = 0;
				if (anim % 3 == 0)
					image.setDrawable(walkAnimation[runPos++]);
			} else {
				runPos = 0;
				if (idlePos == idleAnimation.length)
					idlePos = 0;
				if (anim % 3 == 0)
					image.setDrawable(idleAnimation[idlePos++]);
			}
			timeSinceLastRender = 0f;
		}

	}

	@Override
	public void thirdPower() {
		currentTime = System.currentTimeMillis();
		if ((currentTime - timeSinceLastThirdPower) < thirdPowerCD || shield) {
			return;
		}

		shield = true;
		gameScreen.packetChanged = true;
		timeSinceLastThirdPower = System.currentTimeMillis();
	}

	int counter = 0;

	@Override
	public void attack() {
		super.attack();

		currentTime = System.currentTimeMillis();
		if ((currentTime - timeSinceLastShoot) < shootTimer) {
			return;
		}

		attacking = true;
		gameScreen.packetChanged = true;
		timeSinceLastShoot = System.currentTimeMillis();
	}

	public void update(float delta) {
		super.update(delta);
		if (setFly) {
			flightRequest = true;
			setFly = false;
		} else {
			flightRequest = false;
		}
		visualControlUpdates(delta);
	}

	public void shootProjectile() {

		float y = body.getPosition().y * GdxGame.get().gameScreen.PPM + 10;

		if (getScaleX() == 1) {
			float x = body.getPosition().x * GdxGame.get().gameScreen.PPM + 40;
			if (GdxGame.get().gameScreen.player1 == this) {
				new Projectile(x, y, bulletForceX, bulletForceY, this);
			} else {
				new Projectile(x, y, bulletForceX, bulletForceY, this);
			}
		} else {
			float x = body.getPosition().x * GdxGame.get().gameScreen.PPM - 40;
			if (GdxGame.get().gameScreen.player1 == this) {
				new Projectile(x, y, -bulletForceX, bulletForceY, this);
			} else {
				new Projectile(x, y, -bulletForceX, bulletForceY, this);
			}
		}
	}

	public void renderParticleEffects() {
		if (System.currentTimeMillis() - timeSinceFlight < 1000) {
			GdxGame.get().batch.begin();
			pe.draw(GdxGame.get().batch);
			GdxGame.get().batch.end();
		}
	}

	public void updatePacketBeforeSending(Packet packet) {
		super.updatePacketBeforeSending(packet);
		packet.shoot = this.shoot;
		packet.shield = this.shield;
		packet.flightRequest = this.flightRequest;
	}

	public void fly(float delta) {
		pe.update(delta);
		if (flightRequest) {
			if (healthBar.getResource() != 0) {
				if (!flying)
					pe.start();
				pe.getEmitters().first().setPosition(body.getPosition().x * gameScreen.PPM,
						body.getPosition().y * gameScreen.PPM);
				flying = true;
				healthBar.useResource(0.8f);
				body.applyForceToCenter(body.getLinearVelocity().x, 20f, false);
				timeSinceFlight = System.currentTimeMillis();
			} else {
				pe.getEmitters().first().setPosition(-200, -200);
			}
		} else {
			if (healthBar.getResource() != healthBar.getMaxResource()) {
				healthBar.regenerateResource(0.4f);
				gameScreen.packetChanged = true;
			}
			if (flying) {
				pe.getEmitters().first().setPosition(-200, -200);
			}
			flying = false;
		}
	}

	public void visualControlUpdates(float delta) {
		if (shield) {
			shieldImage.setX(body.getPosition().x * gameScreen.PPM - shieldImage.getWidth() / 2);
			shieldImage.setY(body.getPosition().y * gameScreen.PPM - shieldImage.getHeight() / 2 + 10);
			if (!shieldAdded) {
				gameScreen.stage.addActor(shieldImage);
				shieldAdded = true;
			}
		}
		if (struck) {
			if (gameScreen.getOppositeCharacter(this).image.getScaleX() == 1) {
				bloodImage.setX(body.getPosition().x * GdxGame.get().gameScreen.PPM - offsetX - width / 2);
			} else {
				bloodImage.setX(body.getPosition().x * GdxGame.get().gameScreen.PPM + offsetX + width / 2);
			}
			bloodImage.setY(body.getPosition().y * GdxGame.get().gameScreen.PPM - height / 2 - offsetY);
		}
		fly(delta);
	}

	boolean setFly = false;

	@Override
	public void startJump() {
		setFly = true;
	}

	public void destroy(Body body) {
		if (!destroyBodys.contains(body, true)) {
			destroyBodys.add(body);
			GdxGame.get().gameScreen.activeBodys.removeValue(body, true);
			Projectile p = (Projectile)body.getUserData();
			GdxGame.get().gameScreen.projectiles.remove(p);
			p.image.remove();
		}
		explosion = true;
		explosionImage.setPosition(body.getPosition().x * GameScreen.PPM - explosionImage.getWidth()/2,
				body.getPosition().y * GameScreen.PPM - explosionImage.getHeight()/2);
	}

	public void shield() {
		shieldCircle.setPosition(body.getPosition().x * GameScreen.PPM, body.getPosition().y * GameScreen.PPM);
		for (Body b : GdxGame.get().gameScreen.activeBodys) {
			if (b.getUserData() instanceof Projectile) {
				if (((Projectile) b.getUserData()).player != this)
					if (shieldCircle.contains(b.getPosition().x * GameScreen.PPM, b.getPosition().y * GameScreen.PPM)) {
						destroy(b);
					}
			}
		}

		Body pl2body = GdxGame.get().gameScreen.getOppositeCharacter(this).body;
		float x = pl2body.getPosition().x * GameScreen.PPM, y = pl2body.getPosition().y * GameScreen.PPM;
		if (shieldCircle.contains(x, y)) {
			if (shieldCircle.x <= x) {
				x = 50;
			} else {
				x = -50;
			}
			if (shieldCircle.y <= y) {
				y = 50;
			} else {
				y = -50;
			}

			pl2body.applyForceToCenter(x, y, false);
		}
	}

}
