package com.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;

/**
 * View koj se prikazuje kad se zavrsila igra
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class FinishScreen implements Screen {

	private Stage stage;
	private Image background;
	private SpriteDrawable[] bgr = new SpriteDrawable[4];
	private Texture victory, defeat;
	private Image image;

	private int counter = 0;
	private float t = 0;
	private float speed = 0.5f;
	private float timer = 0;

	/**
	 * inicailizacija svih parametara koj se ne menjaju
	 */
	public FinishScreen() {
		speed = 1f - speed;

		Texture backgroundTexture1 = GdxGame.get().assets.get("GameScreen/bg1.png", Texture.class);
		Texture backgroundTexture2 = GdxGame.get().assets.get("GameScreen/bg2.png", Texture.class);
		Texture backgroundTexture3 = GdxGame.get().assets.get("GameScreen/bg3.png", Texture.class);
		Texture backgroundTexture4 = GdxGame.get().assets.get("GameScreen/bg4.png", Texture.class);

		bgr[0] = new SpriteDrawable(new Sprite(backgroundTexture1));
		bgr[1] = new SpriteDrawable(new Sprite(backgroundTexture2));
		bgr[2] = new SpriteDrawable(new Sprite(backgroundTexture3));
		bgr[3] = new SpriteDrawable(new Sprite(backgroundTexture4));

		background = new Image(backgroundTexture1);
		background.setSize(GdxGame.get().width, GdxGame.get().height);

		victory = GdxGame.get().assets.get("Buttons/victory.png", Texture.class);
		defeat = GdxGame.get().assets.get("Buttons/defeat.png", Texture.class);
	}

	/**
	 * inicializaciaja svih parametara za dati view koji se postavljaju na pocetnu vrednost.
	 * ova metoda dati view postavlja na ekran
	 */
	public void initScreen(boolean victory) {
		try {
			GdxGame.get().handler.showAds(true);
		} catch (Exception e) {
		}
		volume = 1f - 0.18f;
		firstRender = true;
		t = 0;
		timer = 0;
		counter = 0;
		if (victory) {
			image = new Image(this.victory);
		} else {
			image = new Image(this.defeat);
		}
		image.setPosition(GdxGame.get().width / 2 - image.getWidth() / 2,
				GdxGame.get().height / 2 - image.getHeight() / 2);

		image.addAction(Actions.fadeOut(0));

		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);

		if (stage != null)
			stage.dispose();
		stage = new Stage(new StretchViewport(GdxGame.get().width, GdxGame.get().height, GdxGame.get().camera));

		background.remove();
		image.remove();
		stage.addActor(background);
		stage.addActor(image);

		Gdx.input.setInputProcessor(stage);
		GdxGame.get().setScreen(this);
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	boolean firstRender = true;

	/**
	  * {@inheritDoc}
	  * render metoda koja racuna svaki frejm prikazan
	  */
	@Override
	public void render(float delta) {
		if (firstRender) {
			firstRender = false;
			stage.act();
			image.addAction(Actions.fadeIn(1.5f));
			return;
		}
		if (Gdx.input.isTouched() && timer > 1.8f) {
			GdxGame.get().loginScreen.initScreen(true);
		}

		timer += delta;
		t += delta;
		if (t > speed) {
			cycleBakground();
			t = 0;
		}

		stage.draw();
		stage.act();

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	float volume = 1f - 0.18f;

	/**
	 * vrsi izmenu pozadine u zavisnosti od vremena koj ej isteklo. pozadina se ponasa kao gif
	 */
	private void cycleBakground() {
		try {
			GdxGame.get().gameScreen.sound.setVolume(volume -= 0.18);
		} catch (Exception e) {
		}

		if (counter < bgr.length - 1) {
			background.setDrawable(bgr[++counter]);
		} else
			background.setDrawable(bgr[counter = 0]);
	}

}
