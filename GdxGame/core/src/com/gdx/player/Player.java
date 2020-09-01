package com.gdx.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.gdx.client.Packet;
import com.gdx.game.GdxGame;
import com.gdx.screens.GameScreen;

/**
 * Klasa koja kontrolise kretanje i prikaz pleyera u zavisnosti od kontrola koje koristi korisnik.
 * postavlja sebe kao izmenjeni parametar ukoliko je doslo do promene nekog parametra od playera da bi se njegovi 
 * parametri preuzeli i spakovali u paket i poslali preko mreze. 
 * Ceo prikaz i pozicija igraca su regulisani uspomoc ove klase kao i provere dali je pogodjen sa strane protivnika
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public abstract class Player {

	public static final float MIN_FRAME_LENGTH = 1f / 60f;
	protected float timeSinceLastRender;

	public Image image, bloodImage;
	public GameScreen gameScreen;
	private int velCounter = 0;

	public boolean attacking = false;
	public boolean hit = false, struck;
	public boolean jump = false;
	public boolean running = false;
	public boolean left, right;
	protected float moveForce = 3f;
	private float jumpForce = 250f;

	public Vector3 mousePos = new Vector3(0, 0, 0);
	public Body body;
	public float x, y;
	public float offsetX, offsetY, width, height;

	public Rectangle bodyRectangle;
	private Player player2;
	protected float hitForceX = 80, hitForceY = 150;
	public boolean projectileHit = false, projectileDemage = false;
	public Vector2 projectileHitVector = new Vector2();
	private int projectileDemageAmount = 22;
	public boolean hitByProjectile = false;
	public boolean extraBar;

	public boolean invisible = false;
	public boolean shield = false;
	
	public long timeSinceLastThirdPower;
	public int thirdPowerCD, thirdPowerDuration;

	public HealthBar healthBar;
	public int attackDamage = 0;

	public Player(float x, float y, float width, float height, boolean isStatic) {
		gameScreen = GdxGame.get().gameScreen;
		body = gameScreen.createBox(x, y, width, height, isStatic, true);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bodyRectangle = new Rectangle(x, y, width, height);
	}

	public void render(float delta) {
	}

	public void update(float delta) {
		hitCheck();
		if (projectileHit) {
			if (projectileDemage)
				healthBar.dealDamage(projectileDemageAmount);
			body.applyForceToCenter(projectileHitVector, false);
			projectileHit = false;
		}
		move();
		healthBar.update();
		if (x != body.getPosition().x || y != body.getPosition().y) {

			x = body.getPosition().x;
			y = body.getPosition().y;
			gameScreen.packetChanged = true;
		} else {
			hitByProjectile = false;
		}
		updateSpritePosition();
	}

	public void move() {
		if (hitByProjectile)
			return;

		if (left) {
			gameScreen.packetChanged = true;
			running = true;
			setScaleX(-1);
			updateSpritePosition();
			body.setLinearVelocity(-moveForce, body.getLinearVelocity().y);
		}
		if (right) {
			gameScreen.packetChanged = true;
			running = true;
			setScaleX(1);
			updateSpritePosition();
			body.setLinearVelocity(moveForce, body.getLinearVelocity().y);
		}
		if (!right && !left && running) {
			running = false;
			if (!jump) {
				body.setLinearVelocity(0, body.getLinearVelocity().y);
			}
			gameScreen.packetChanged = true;
		}

		if (jump && body.getLinearVelocity().y == 0) {
			velCounter++;
			if (velCounter == 2) {
				velCounter = 0;
				jump = false;
				gameScreen.packetChanged = true;
			}
		}
	}

	public void updateSpritePosition() {
		updateStaticSpritePosition();
		this.setY(body.getPosition().y * GameScreen.PPM - offsetY - height / 2);
	}

	public void updateStaticSpritePosition() {
		if (getScaleX() == -1) {
			setX(body.getPosition().x * GameScreen.PPM + offsetX + width / 2);
		} else {
			setX(body.getPosition().x * GameScreen.PPM - offsetX - width / 2);
		}
	}

	public void startJump() {
		if (jump == false) {
			jump = true;
			body.applyForceToCenter(0, jumpForce, false);
		}
	}

	public void updatePacketBeforeSending(Packet packet) {
		packet.x = body.getPosition().x;
		packet.y = body.getPosition().y;
		packet.scaleX = this.getScaleX();
		packet.running = this.running;
		packet.jump = this.jump;
		packet.HP = healthBar.HP;
		packet.attacking = this.attacking;
		packet.hit = this.hit;
	}

	protected SpriteDrawable[] loadAnimation(int frameNum, String type) {
		SpriteDrawable[] animation = new SpriteDrawable[frameNum];
		for (int i = 0; i < frameNum; i++) {
			animation[i] = new SpriteDrawable(
					new Sprite(GdxGame.get().assets.get("Characters/" + type + (i + 1) + ".png", Texture.class)));
		}
		return animation;
	}

	private void hitCheck() {
		if (shield)
			return;
		if (!GdxGame.get().gameScreen.player2.hit)
			return;

		this.bodyRectangle.setPosition(body.getPosition().x * GameScreen.PPM - width / 2,
				body.getPosition().y * GameScreen.PPM - height / 2);

		player2 = GdxGame.get().gameScreen.player2;
		if (player2 instanceof Ninja) {
			Ninja ninja = (Ninja) player2;
			if (ninja.getScaleX() == 1) {
				ninja.attackRectangle.setPosition(ninja.body.getPosition().x * GameScreen.PPM - ninja.width / 2,
						ninja.body.getPosition().y * GameScreen.PPM - ninja.height / 2 - 5);

				if (ninja.attackRectangle.overlaps(this.bodyRectangle) && !struck) {
					body.applyForceToCenter(hitForceX, hitForceY, false);
					struck = true;
					healthBar.dealDamage(gameScreen.getOppositeCharacter(this).attackDamage);
				}
			} else {
				ninja.attackRectangle.setPosition(
						ninja.body.getPosition().x * GameScreen.PPM - ninja.width / 2
								- (ninja.attackRectangle.width - ninja.width),
						ninja.body.getPosition().y * GameScreen.PPM - ninja.height / 2 - 5);

				if (ninja.attackRectangle.overlaps(this.bodyRectangle) && !struck) {
					body.applyForceToCenter(-hitForceX, hitForceY, false);
					struck = true;
					healthBar.dealDamage(gameScreen.getOppositeCharacter(this).attackDamage);
				}
			}
		}

	}
	
	public void renderParticleEffects() {
	}

	public void thirdPower() {
	}

	public void visualControlUpdates(float delta) {
	}

	public void setHealthBar(HealthBar healthBar) {
		this.healthBar = healthBar;
	}

	public void attack() {
	}

	public float getY() {
		return image.getY();
	}

	public float getX() {
		return image.getX();
	}

	public float getScaleX() {
		return image.getScaleX();
	}

	public float getWidth() {
		return image.getWidth();
	}

	public float getHeight() {
		return image.getHeight();
	}

	public void setX(float x) {
		image.setX(x);
	}

	public void setY(float y) {
		image.setY(y);
	}

	public void setScaleX(float scaleX) {
		image.setScaleX(scaleX);
	}

}
