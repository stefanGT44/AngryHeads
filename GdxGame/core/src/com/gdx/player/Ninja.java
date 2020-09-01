package com.gdx.player;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.gdx.client.Packet;
import com.gdx.game.GdxGame;

/**
 * Klasa koja kontrolise kretanje i prikaz nijndze u zavisnosti od kontrola koje koristi korisnik.
 * postavlja sebe kao izmenjeni parametar ukoliko je doslo do promene nekog parametra od playera da bi se njegovi 
 * parametri preuzeli i spakovali u paket i poslali preko mreze. 
 * Ceo prikaz i pozicija igraca su regulisani uspomoc ove klase kao i provere dali je pogodjen sa strane protivnika.
 * 
 * Dodati su jos detalji koji se iskljucivo odnose na nindzu kao sto je postajanje nevidljiv ili udarac sa macem prilikom cega 
 * se izvrsava odgovarajuca promena animacije
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Ninja extends Player {

	private SpriteDrawable[] idleAnimation, walkAnimation, jumpAnimation;

	private SpriteDrawable[] attack1, attack2, attack3, attack4, attack5;
	private ArrayList<SpriteDrawable[]> attackList;
	private SpriteDrawable[][] attackMatrix;

	private int anim = 0;
	private int animationSpeed = 75;
	public int idlePos = 0, runPos = 0, jumpPos = 0, attackPos = 0, attackPosMax;

	public Rectangle attackRectangle;
	public int attackId = 0, comboTimer = 1000;
	public long timeSinceLastAttack = 0;
	private float attackJumpForce = 200;
	public long currentTime;

	public Ninja(float x, float y, float width, float height, boolean isStatic, int scaleX) {
		super(x, y, width, height, isStatic);
		idleAnimation = loadAnimation(4, "ninja/idle/idle");
		walkAnimation = loadAnimation(6, "ninja/walk/walk");
		jumpAnimation = loadAnimation(8, "ninja/jump/jump");

		attack1 = loadAnimation(4, "ninja/attack1/attack1");
		attack2 = loadAnimation(4, "ninja/attack2/attack2");
		attack3 = loadAnimation(4, "ninja/attack3/attack3");
		attack4 = loadAnimation(8, "ninja/attack4/attack4");
		attack5 = loadAnimation(9, "ninja/attack5/attack5");

		attackMatrix = new SpriteDrawable[5][9];
		attackMatrix[0] = attack1;
		attackMatrix[1] = attack2;
		attackMatrix[2] = attack3;
		attackMatrix[3] = attack4;
		attackMatrix[4] = attack5;
		
		attackList = new ArrayList();
		attackList.add(attack1);
		attackList.add(attack2);
		attackList.add(attack3);
		attackList.add(attack4);
		attackList.add(attack5);

		image = new Image();
		image.setSize(128, 128);
		image.setDrawable(idleAnimation[0]);
		image.setScaleX(scaleX);

		GdxGame.get().gameScreen.stage.addActor(image);

		offsetX = 48;
		offsetY = 28;

		attackDamage = 10;
		moveForce = 4f;
		
		thirdPowerCD = 5000;
		thirdPowerDuration = 2000;

		updateStaticSpritePosition();

		attackRectangle = new Rectangle(x, y, width * 2, height * 2);
	}

	boolean skipFrame = false;

	public void render(float delta) {
		struck = false;
		if (invisible) {
			if (GdxGame.get().gameScreen.player1 == this) {// smanji alpha
				currentTime = System.currentTimeMillis();
				if (currentTime - timeSinceLastThirdPower > thirdPowerDuration) {
					invisible = false;
					gameScreen.packetChanged = true;
				}
				image.getColor().a = 0.4f;
				healthBar.color1.a = 0.4f;
				healthBar.color2.a = 0.4f;
				healthBar.color.a = 0.4f;
				healthBar.font.getColor().a = 0.4f;

			} else { // potpuno da nestane
				image.getColor().a = 0f;
				healthBar.color1.a = 0f;
				healthBar.color2.a = 0f;
				healthBar.color.a = 0f;
				healthBar.font.getColor().a = 0f;
			}
		} else {
			image.getColor().a = 1f;
			healthBar.color1.a = 1f;
			healthBar.color2.a = 1f;
			healthBar.color.a = 1f;
			healthBar.font.getColor().a = 1f;
		}

		timeSinceLastRender += delta;
		if (timeSinceLastRender >= MIN_FRAME_LENGTH) {
			super.render(delta);

			anim++;
			if (attacking) {
				if (attackPos == attackPosMax) {
					attackPos = 0;
					attacking = false;
					gameScreen.packetChanged = true;
				} else {
					if (anim % 3 == 0) {
						if (attackId == 4 && !jump && attackPos == 2) {
							body.applyForceToCenter(0, attackJumpForce, false);
							jump = true;
							gameScreen.packetChanged = true;
						}
						if (!skipFrame) {
							//image.setDrawable(attackList.get(attackId)[attackPos++]);
							image.setDrawable(attackMatrix[attackId][attackPos++]);
						}
					}
				}
			} else if (jump) {
				if (jumpPos == jumpAnimation.length)
					jumpPos = 0;
				if (anim % 3 == 0)
					image.setDrawable(jumpAnimation[jumpPos++]);
			} else if (running) {
				if (runPos == walkAnimation.length)
					runPos = 0;
				if (anim % 3 == 0)
					image.setDrawable(walkAnimation[runPos++]);
			} else {
				if (idlePos == idleAnimation.length)
					idlePos = 0;
				if (anim % 3 == 0)
					image.setDrawable(idleAnimation[idlePos++]);
			}
			if (!jump)
				jumpPos = 0;

			timeSinceLastRender = 0f;
		}
	}

	boolean hitSent = false;

	public void update(float delta) {
		super.update(delta);
		/*if (hit) {
			hit = false;
			gameScreen.packetChanged = true;
		}*/

		if (attacking) {
			switch (attackId) {
			case 0:
				if (attackPos == 1 || attackPos == 2) {
					if (!hitSent)
						hit = true;
					hitSent = true;
				} else {
					hit = false;
					gameScreen.packetChanged = true;
					hitSent = false;
				}
				break;
			case 1:
				if (attackPos == 1 || attackPos == 2) {
					if (!hitSent)
						hit = true;
					hitSent = true;
				} else {
					hitSent = false;
					hit = false;
					gameScreen.packetChanged = true;
				}
				break;
			case 2:
				if (attackPos == 2 || attackPos == 3) {
					if (!hitSent)
						hit = true;
					hitSent = true;
				} else {
					hit = false;
					gameScreen.packetChanged = true;
					hitSent = false;
				}
				break;
			case 3:
				if (attackPos == 2 || attackPos == 3 || attackPos == 4 || attackPos == 5) {
					if (!hitSent)
						hit = true;
					hitSent = true;
				} else {
					hit = false;
					gameScreen.packetChanged = true;
					hitSent = false;
				}
				break;
			case 4:
				if (attackPos == 6 && body.getLinearVelocity().y != 0)
					skipFrame = true;
				else
					skipFrame = false;
				if (attackPos == 7) {
					if (!hitSent)
						hit = true;
					hitSent = true;
				} else {
					hit = false;
					gameScreen.packetChanged = true;
					hitSent = false;
				}
				break;
			}
			gameScreen.packetChanged = true;
		}
	}

	@Override
	public void visualControlUpdates(float delta) {
		if (attacking) {
			if (attackId == 4 && attackPos == 6 && jump)
				skipFrame = true;
			else
				skipFrame = false;
		}
	}

	public void attack() {
		currentTime = System.currentTimeMillis();
		if (attacking) {
			return;
		}

		if ((currentTime - timeSinceLastAttack) < comboTimer) {
			if (attackId == 4)
				attackId = 0;
			else {
				attackId++;
			}
		} else {
			attackId = 0;
		}

		attackPosMax = attackList.get(attackId).length;
		attackPos = 0;
		attacking = true;
		timeSinceLastAttack = System.currentTimeMillis();
		gameScreen.packetChanged = true;
	}

	@Override
	public void thirdPower() {
		currentTime = System.currentTimeMillis();
		if ((currentTime - timeSinceLastThirdPower) < thirdPowerCD || invisible) {
			return;
		}

		invisible = true;
		gameScreen.packetChanged = true;
		timeSinceLastThirdPower = System.currentTimeMillis();
	}

	public void updatePacketBeforeSending(Packet packet) {
		super.updatePacketBeforeSending(packet);
		packet.attackId = attackId;
		packet.attackPosMax = attackPosMax;
		packet.invisible = invisible;
	}

}
