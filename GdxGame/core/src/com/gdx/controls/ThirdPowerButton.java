package com.gdx.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.game.GdxGame;
import com.gdx.player.Ninja;
import com.gdx.player.Player;

/**
 * Dugme za trecu moc kod nindze je nevidljivost, a kod rocketman shield
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ThirdPowerButton {

	public Image image;
	public Rectangle rec;
	public Player player;

	BitmapFont font;
	String time = "";
	long currentTime;
	
	private float x, y;
	long tempTime = 0;

	public Vector3 mousePos = new Vector3(0, 0, 0);

	/**
	 * Konstruktor za dugme
	 * @param x
	 * 		x koordinata
	 * @param y
	 * 		y koordinata
	 * @param stage
	 * 		stage kome pripada dato dugme
	 * @param player
	 * 		player kome pripada dato dugme, tj na kog utice
	 */
	public ThirdPowerButton(float x, float y, Stage stage, Player player) {
		this.player = player;
		if (player instanceof Ninja)
			image = new Image(GdxGame.get().assets.get("Buttons/stealth.png", Texture.class));
		else
			image = new Image(GdxGame.get().assets.get("Buttons/shieldIcon.png", Texture.class));
		image.setX(x);
		image.setY(y);
		image.setScale(0.9f);
		stage.addActor(image);
		rec = new Rectangle(x, y, image.getWidth(), image.getHeight());

		font = new BitmapFont(Gdx.files.internal("fonts/cd.fnt"));
		this.x = x + 10;
		this.y = y + image.getWidth() /2 + 4;
	}

	/**
	 * Poziv za render. Vrsi iscrtavanje vremena
	 */
	public void render() {
		if (image.getColor().a == 0.4f) {
			GdxGame.get().batch.begin();
			font.draw(GdxGame.get().batch, time, x , y);
			GdxGame.get().batch.end();
		}
	}

	/**
	 * updateuje izgled datog dugmeta.
	 */
	public void update() {
		currentTime = System.currentTimeMillis();
		if (currentTime - player.timeSinceLastThirdPower < player.thirdPowerCD) {
			tempTime = player.thirdPowerCD - (currentTime - player.timeSinceLastThirdPower);
			time = (tempTime/1000) + "." + ((tempTime / 100) % 10);
			image.getColor().a = 0.4f;
		} else
			image.getColor().a = 1f;
	}

	/**
	 * metoda koja se poziva kada je ekran dodirnut
	 * @param X
	 * 		x koordinata dodira
	 * @param Y
	 * 		y koordinata dodira
	 */
	public void screenTouched(float X, float Y) {
		mousePos.x = X;
		mousePos.y = Y;
		GdxGame.get().camera.unproject(mousePos);

		if (rec.contains(mousePos.x, mousePos.y)) {
			player.thirdPower();
		}
	}

}
