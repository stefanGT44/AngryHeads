package com.gdx.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.game.GdxGame;
import com.gdx.player.Player;

/**
 * Dugme za skok
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class JumpButton {

	public Image image;
	public Rectangle rec;
	public Player player;

	public Vector3 mousePos = new Vector3(0, 0, 0);

	/**
	 * Konstruktor za dugme skoka
	 * @param x
	 * 		x koordinata
	 * @param y
	 * 		y koordinata
	 * @param stage
	 * 		stage kome pripada dato dugme
	 * @param player
	 * 		player kome pripada dato dugme, tj na kog utice
	 */
	public JumpButton(float x, float y, Stage stage, Player player) {
		this.player = player;
		image = new Image(GdxGame.get().assets.get("Buttons/jump.png", Texture.class));
		image.setX(x);
		image.setY(y);
		image.setScale(0.9f);
		stage.addActor(image);
		rec = new Rectangle(x, y, image.getWidth(), image.getHeight());
	}

	public void update() {

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
			player.startJump();
		}
	}

}
