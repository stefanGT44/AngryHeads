package com.gdx.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.game.GdxGame;
import com.gdx.player.Player;
import com.gdx.screens.GameScreen;

/**
 * Touchpad koj kontrolise kretanje playera levo ili desno.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class TouchPad {

	public Image circle, arrows;
	public Vector3 mousePos = new Vector3(0, 0, 0);
	public Rectangle rec;
	public float centerX;
	public Player player;

	/**
	 * Konstruktor za touchpad
	 * @param x
	 * 		x koordinata
	 * @param y
	 * 		y koordinata
	 * @param stage
	 * 		stage kome pripada dato dugme
	 * @param player
	 * 		player kome pripada dato dugme, tj na kog utice
	 */
	public TouchPad(float x, float y, Stage stage, Player player) {
		this.player = player;
		this.centerX = x;
		circle = new Image(GdxGame.get().assets.get("Buttons/circle.png", Texture.class));
		arrows = new Image(GdxGame.get().assets.get("Buttons/arrows.png", Texture.class));
		circle.setX(x);
		circle.setY(y);
		arrows.setX(x - arrows.getWidth() / 2 + circle.getWidth() / 2);
		arrows.setY(y + (circle.getHeight() / 2 - arrows.getHeight() / 2));
		stage.addActor(arrows);
		stage.addActor(circle);
		rec = new Rectangle(arrows.getX(), arrows.getY(), arrows.getWidth(), arrows.getHeight());
	}

	/**
	 * updateuje izgled.
	 */
	public void update() {
		circle.setX(centerX);
		player.left = false;
		player.right = false;

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
			if (mousePos.x > centerX && mousePos.x < arrows.getX() + arrows.getWidth()) {
				float x = mousePos.x - circle.getWidth() / 2;
				if (x > arrows.getX() + arrows.getWidth() - circle.getWidth())
					circle.setX(arrows.getX() + arrows.getWidth() - circle.getWidth());
				else
					circle.setX(x);
			}
			if (mousePos.x < centerX + circle.getWidth() / 2 && mousePos.x > arrows.getX()) {
				float x = mousePos.x - circle.getWidth() / 2;
				if (x < arrows.getX())
					circle.setX(arrows.getX());
				else
					circle.setX(x);
			}
			if (mousePos.x < centerX + circle.getWidth() / 2) {
				player.left = true;
				player.right = false;
			} else if (mousePos.x > centerX) {
				player.right = true;
				player.left = false;
			}

			GdxGame.get().gameScreen.tuched = true;
		}
	}

}
