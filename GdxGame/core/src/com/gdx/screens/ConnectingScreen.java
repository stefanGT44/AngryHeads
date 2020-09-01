package com.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;

/**
 * View koj se prikazuje tokon stvaranja konekcije
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class ConnectingScreen implements Screen {

	private Stage stage;
	private Image background;
	private SpriteDrawable[] bgr = new SpriteDrawable[4];

	private int counter = 0;
	private float t = 0;
	private float speed = 0.9f;

	private ImageButton connectingImg;
	private ImageButton cancel;
	private boolean nextScreen, backScreen;
	public char position;

	/**
	 * inicailizacija svih parametara koj se ne menjaju
	 */
	public ConnectingScreen() {

		speed = 1f - speed;

		Texture backgroundTexture1 = GdxGame.get().assets.get("LogInScreen/bg1.png", Texture.class);
		Texture backgroundTexture2 = GdxGame.get().assets.get("LogInScreen/bg2.png", Texture.class);
		Texture backgroundTexture3 = GdxGame.get().assets.get("LogInScreen/bg3.png", Texture.class);
		Texture backgroundTexture4 = GdxGame.get().assets.get("LogInScreen/bg4.png", Texture.class);

		bgr[0] = new SpriteDrawable(new Sprite(backgroundTexture1));
		bgr[1] = new SpriteDrawable(new Sprite(backgroundTexture2));
		bgr[2] = new SpriteDrawable(new Sprite(backgroundTexture3));
		bgr[3] = new SpriteDrawable(new Sprite(backgroundTexture4));

		background = new Image(backgroundTexture1);
		background.setSize(GdxGame.get().width, GdxGame.get().height);

		connectingImg = getConnectingImg();
		cancel = initCancelButton();
		connectingImg.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(1f), Actions.fadeIn(1f))));

	}

	/**
	 * inicializaciaja svih parametara za dati view koji se postavljaju na pocetnu vrednost.
	 * ova metoda dati view postavlja na ekran.
	 */
	public void initScreen() {
		try {
			GdxGame.get().handler.showAds(true);
		} catch (Exception e) {
		}
		try {
			GdxGame.get().gameScreen.sound.stop();
		} catch (Exception e) {
		}

		nextScreen = false;
		backScreen = false;

		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);
		if (stage != null)
			stage.dispose();
		stage = new Stage(new StretchViewport(GdxGame.get().width, GdxGame.get().height, GdxGame.get().camera));

		stage.addActor(background);
		stage.addActor(connectingImg);
		stage.addActor(cancel);

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

	/**
	  * {@inheritDoc}
	  * render metoda koja racuna svaki frejm prikazan
	  */
	@Override
	public void render(float delta) {
		t += delta;
		if (t > speed) {
			cycleBakground();
			t = 0;
		}

		stage.draw();
		stage.act();

		if (nextScreen(false)) {
			cancel.remove();
			GdxGame.get().gameScreen.initScreen(position);
		}

		if (backScreen(false)) {
			cancel.remove();
			GdxGame.get().loginScreen.initScreen(true);
		}
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

	/**
	 * vrsi izmenu pozadine u zavisnosti od vremena koj ej isteklo. pozadina se ponasa kao gif
	 */
	private void cycleBakground() {

		if (counter < bgr.length - 1) {
			background.setDrawable(bgr[++counter]);
		} else
			background.setDrawable(bgr[counter = 0]);
	}

	/**
	 * dohvata sliku za konekciju
	 * @return vraca imagebutton date slike
	 */
	private ImageButton getConnectingImg() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/connecting.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));

		ImageButton Button = new ImageButton(up);
		Button.setSize(370, 180);
		Button.setPosition(GdxGame.get().width / 2 - Button.getWidth() / 2,
				GdxGame.get().height / 2 - Button.getHeight() / 2 + 30);

		return Button;
	}

	/**
	 * prelazak na sledeci ekran
	 * @param b 
	 * 		boolean koj se postavlja za prelazak na sledeci ekran
	 */
	public synchronized boolean nextScreen(boolean b) {
		boolean tmp = nextScreen;
		nextScreen = b;
		return tmp;
	}

	/**
	 * prelazak na prethodni ekran
	 * @param b 
	 * 		boolean koj se postavlja za prelazak na prethodni ekran
	 */
	public synchronized boolean backScreen(boolean b) {
		boolean tmp = backScreen;
		backScreen = b;
		return tmp;
	}

	/**
	 * inicializacija dugmeta 'cancel'
	 * @return Dato dugme
	 */
	private ImageButton initCancelButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/cancel1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/cancel2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));

		ImageButton button = new ImageButton(up, down);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2 - 90);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// back
				cancel.remove();

				GdxGame.get().loginScreen.clientThread.client.exit(true);
				GdxGame.get().loginScreen.initScreen(true);
			}

		});

		return button;
	}

}