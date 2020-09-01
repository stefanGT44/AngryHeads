package com.gdx.screens;

import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.client.ClientThread;
import com.gdx.game.GdxGame;

public class MatchMakingScreen implements Screen {

	private ClientThread clientThread;
	private ReentrantLock lock;
	private Stage stage;
	private Skin skin;

	private int counter = 0;
	private float t = 0;
	private float speed = 0.9f;

	private Image background;
	private SpriteDrawable[] bgr = new SpriteDrawable[4];

	private TextField oponentField;
	private ImageButton friendsUsername;
	private ImageButton random;
	private ImageButton friend;
	private ImageButton connect;
	private ImageButton logOut;

	/**
	 * inicailizacija svih parametara koj se ne menjaju, pozadina, zvuk, i sve texture
	 */
	public MatchMakingScreen() {
		speed = 1f - speed;
		skin = new Skin(Gdx.files.internal("Skins/default/skin/uiskin.json"));

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

		friendsUsername = initFriendsEmail();
		friend = initWithFriendButton();
		connect = initConnectButton();
		random = initPlyRandomButton();
		logOut = initLogOutButton();

		setOponentTxtField();
	}

	/**
	 * inicializaciaja svih parametara za dati view koji se postavljaju na pocetnu vrednost.
	 * ova metoda dati view postavlja na ekran i postavlja reklame na gornji deo ekrana ukoliko se igra na androidu
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

		lock = GdxGame.get().loginScreen.lock;
		clientThread = GdxGame.get().loginScreen.clientThread;

		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);
		if (stage != null)
			stage.dispose();
		stage = new Stage(new StretchViewport(GdxGame.get().width, GdxGame.get().height, GdxGame.get().camera));

		background.remove();
		friend.remove();
		random.remove();
		random.addAction(Actions.fadeIn(0f));
		friend.addAction(Actions.fadeIn(0f));

		stage.addActor(background);
		stage.addActor(friend);
		stage.addActor(random);
		stage.addActor(logOut);

		Gdx.input.setInputProcessor(stage);
		GdxGame.get().setScreen(this);

		if (GdxGame.get().loginScreen.sound.isPlaying() == false) {
			GdxGame.get().loginScreen.sound.play();
		}
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

		if (oponentField != null) {
			if (!oponentField.isVisible()) {
				oponentField.setVisible(true);
				connect.setVisible(true);
				friendsUsername.setVisible(true);
			}
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
	 * kreira i vraca dugme with Friend sa akciom da se pojavi polje za unos korisnickog imena protivnika sa kojim 
	 * korisnik zeli da se poveze i igra
	 * @return dato dugme
	 */
	private ImageButton initWithFriendButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/withFriend1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/withFriend2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));

		ImageButton button = new ImageButton(up, down);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				random.addAction(Actions.fadeOut(1f));
				oponentField.addAction(Actions.parallel(Actions.fadeOut(0f), Actions.fadeIn(1f)));
				oponentField.setVisible(false);

				connect.addAction(Actions.parallel(Actions.fadeOut(0f), Actions.fadeIn(1f)));
				connect.setVisible(false);
				friend.addAction(Actions.fadeOut(1f));

				friendsUsername.addAction(Actions.parallel(Actions.fadeOut(0f), Actions.fadeIn(1f)));
				friendsUsername.setVisible(false);

				stage.addActor(oponentField);
				stage.addActor(connect);
				stage.addActor(friendsUsername);

			}

		});

		return button;
	}

	/**
	 * kreira i vraca dugme connect bez akcija na njemu. sluzi cisto kao vizuelni prikaz
	 * @return dato dugme
	 */
	private ImageButton initConnectButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/connect1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/connect2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));

		ImageButton button = new ImageButton(up, down);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (oponentField.getText().equals(""))
					return;

				clientThread.command = "connect";
				clientThread.oponent = oponentField.getText();
				lock.unlock();

				logOut.remove();
				GdxGame.get().connectingScreen.initScreen();
			}

		});

		return button;
	}

	/**
	 * kreira i vraca dugme Random koje kao akciju ima slanje preko konektora zahtev da se upari sa random korisnikom tj
	 * sa prvim slobodnim korisnikom koj se nalazi u redu cekanja za random povezivanje
	 * @return dato dugme
	 */
	private ImageButton initPlyRandomButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/random1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/random2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));

		ImageButton button = new ImageButton(up, down);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2 + 80);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// connect RANDOM

				clientThread.command = "connect";
				clientThread.oponent = "random";
				lock.unlock();

				GdxGame.get().connectingScreen.initScreen();
			}

		});

		return button;
	}

	/**
	 * kreira i vraca dugme friends Username bez akcija na njemu. sluzi cisto kao vizuelni prikaz
	 * @return dato dugme
	 */
	private ImageButton initFriendsEmail() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/friendsemail.png", Texture.class);
		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));

		ImageButton button = new ImageButton(up);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2 + 130);

		return button;
	}

	/**
	 * Text polje gde korisnik unosi ime protivnika sa kojim zeli da se poveze i igra
	 */
	private void setOponentTxtField() {
		oponentField = new TextField("", skin);
		oponentField.setSize(300, 30);
		oponentField.setPosition(GdxGame.get().width / 2 - oponentField.getWidth() / 2,
				GdxGame.get().height / 2 - oponentField.getHeight() / 2 + 80);

	}

	/**
	 * Inicializacija LogOut dugmeta koje korisnika odjavljuje i vraca na LogIn Screen/View
	 * @return dato dugme
	 */
	private ImageButton initLogOutButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/logout1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/logout2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));

		ImageButton button = new ImageButton(up, down);
		button.setSize(280, 80);
		button.setPosition(GdxGame.get().width / 2 - button.getWidth() / 2,
				GdxGame.get().height / 2 - button.getHeight() / 2 - 90);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// back
				clientThread.command = "kill";
				lock.unlock();

				GdxGame.get().loginScreen.initScreen(false);
			}

		});

		return button;
	}

}
