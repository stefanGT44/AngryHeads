package com.gdx.screens;

import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

public class LogInScreen implements Screen {

	private Stage stage;
	private Skin skin;
	public TextField usernameField;
	public TextField passwordField;
	private Image background;
	private SpriteDrawable[] bgr = new SpriteDrawable[4];

	private int counter = 0;
	private float t = 0;
	private float speed = 0.9f;

	private ImageButton registerButton;
	private ImageButton logInButton;
	private ImageButton regFailed;
	private ImageButton regSuccess;
	private ImageButton usernameImage;
	private ImageButton passwordImage;

	public ReentrantLock lock;
	public boolean changeScreen;
	public boolean registrationFailed;
	public ClientThread clientThread;

	private String username;
	private String password;

	public Music sound;

	/**
	 * inicailizacija svih parametara koj se ne menjaju, pozadina, zvuk, i sve texture
	 */
	public LogInScreen() {
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
		initRegButtons();
		initLogInButton();
		initRegisterButton();
		setUsernameTxtField();
		setPasswordTxtField();

		usernameImage = getUsernameImg();
		passwordImage = getPasswordImg();

		sound = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu sound.mp3"));
	}

	/**
	 * inicializaciaja svih parametara za dati view koji se postavljaju na pocetnu vrednost.
	 * ova metoda dati view postavlja na ekran i postavlja reklame na gornji deo ekrana ukoliko se igra na androidu
	 */
	public void initScreen(boolean enter) {
		try {
			GdxGame.get().handler.showAds(true);
		} catch (Exception e) {
		}
		try {
			GdxGame.get().gameScreen.sound.stop();
		} catch (Exception e) {
		}

		changeScreen = false;
		registrationFailed = true;
		lock = new ReentrantLock(true);

		lock.lock();
		clientThread = new ClientThread(this, lock, GdxGame.get().connectingScreen, GdxGame.get().gameScreen);
		Thread cT = new Thread(clientThread);
		cT.start();

		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);

		if (stage != null)
			stage.dispose();
		stage = new Stage(new StretchViewport(GdxGame.get().width, GdxGame.get().height, GdxGame.get().camera));

		stage.addActor(background);
		stage.addActor(logInButton);
		stage.addActor(registerButton);
		stage.addActor(usernameImage);
		stage.addActor(passwordImage);
		stage.addActor(usernameField);
		stage.addActor(passwordField);

		Gdx.input.setInputProcessor(stage);
		GdxGame.get().setScreen(this);

		if (enter) {
			clientThread.command = "logIn";
			clientThread.username = username;
			clientThread.password = password;

			lock.unlock();
			lock.lock();

			if (changeScreen) {
				GdxGame.get().matchMakingScreen.initScreen();
			}
		}

		if (sound.isPlaying() == false) {
			sound.setVolume(1);
			sound.setLooping(true);
			sound.play();
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
	 * kreira i vraca dugme username bez akcija na njemu. sluzi cisto kao vizuelni prikaz
	 * @return dato dugme
	 */
	private ImageButton getUsernameImg() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/email.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));

		ImageButton Button = new ImageButton(up);
		Button.setSize(190, 60);
		Button.setPosition(GdxGame.get().width / 2 - Button.getWidth() / 2,
				GdxGame.get().height / 2 - Button.getHeight() / 2 + 130);

		return Button;
	}

	/**
	 * kreira i vraca dugme password bez akcija na njemu. sluzi cisto kao vizuelni prikaz
	 * @return dato dugme
	 */
	private ImageButton getPasswordImg() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/password1.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));

		ImageButton Button = new ImageButton(up);
		Button.setSize(190, 60);
		Button.setPosition(GdxGame.get().width / 2 - Button.getWidth() / 2,
				GdxGame.get().height / 2 - Button.getHeight() / 2 + 30);

		return Button;
	}

	/**
	 * Inicializuje log in dugme zajedno sa akcijama na njemu. 
	 * Kada korisnik pritisne dato dugem salje se preko client Threada username i password serveru koj vrsi proveru
	 * @return dato dugme
	 */
	private Actor initLogInButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/login1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/login2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));
		logInButton = new ImageButton(up, down);
		logInButton.setSize(280, 80);
		logInButton.setPosition(GdxGame.get().width / 2 - logInButton.getWidth() / 2 + 300,
				GdxGame.get().height / 2 - logInButton.getHeight() / 2 - 190);

		logInButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// login
				if (usernameField.getText().equals("") || passwordField.getText().equals(""))
					return;

				clientThread.command = "logIn";
				clientThread.username = usernameField.getText();
				clientThread.password = passwordField.getText();
				username = usernameField.getText();
				password = passwordField.getText();
				lock.unlock();
				lock.lock();

				if (changeScreen) {
					GdxGame.get().matchMakingScreen.initScreen();
				}
			}

		});

		return logInButton;
	}

	/**
	 * Inicializuje log in dugme zajedno sa akcijama na njemu. 
	 * Kada korisnik pritisne dato dugem salje se preko client Threada username i password serveru koj vrsi registraciju
	 * @return dato dugme
	 */
	private ImageButton initRegisterButton() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/register1.png", Texture.class);
		Texture downTexture = GdxGame.get().assets.get("Buttons/register2.png", Texture.class);

		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		Drawable down = new TextureRegionDrawable(new TextureRegion(downTexture));
		registerButton = new ImageButton(up, down);
		registerButton.setSize(280, 80);
		registerButton.setPosition(GdxGame.get().width / 2 - registerButton.getWidth() / 2 - 280,
				GdxGame.get().height / 2 - registerButton.getHeight() / 2 - 190);

		registerButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// register
				if ((usernameField.getText().length() < 5) || (passwordField.getText().length() < 5)) {
					regSuccess.remove();
					stage.addActor(regFailed);
					return;
				}

				clientThread.command = "register";
				clientThread.username = usernameField.getText();
				clientThread.password = passwordField.getText();
				lock.unlock();
				lock.lock();

				if (registrationFailed) {
					regSuccess.remove();
					regFailed.remove();
					stage.addActor(regFailed);
				} else {
					regSuccess.remove();
					regFailed.remove();
					stage.addActor(regSuccess);
				}
			}

		});

		return registerButton;
	}

	/**
	 * Ucitava i postavlja parametre texture i slika za registraciono dugme
	 */
	private void initRegButtons() {
		Texture upTexture = GdxGame.get().assets.get("Buttons/registrationFailed.png", Texture.class);
		Drawable up = new TextureRegionDrawable(new TextureRegion(upTexture));
		regFailed = new ImageButton(up);
		regFailed.setSize(500, 100);
		regFailed.setPosition(GdxGame.get().width / 2 - regFailed.getWidth() / 2,
				GdxGame.get().height / 2 - regFailed.getHeight() / 2 - 60);

		upTexture = GdxGame.get().assets.get("Buttons/registrationSuccessfull.png", Texture.class);
		up = new TextureRegionDrawable(new TextureRegion(upTexture));
		regSuccess = new ImageButton(up);
		regSuccess.setSize(500, 100);
		regSuccess.setPosition(GdxGame.get().width / 2 - regSuccess.getWidth() / 2,
				GdxGame.get().height / 2 - regSuccess.getHeight() / 2 - 60);

	}

	/**
	 * Postavljanje textualnog polja za username gde korisnik unosi svoj username
	 * @return
	 */
	private Actor setUsernameTxtField() {
		usernameField = new TextField("", skin);
		usernameField.setSize(300, 30);
		usernameField.setPosition(GdxGame.get().width / 2 - usernameField.getWidth() / 2,
				GdxGame.get().height / 2 - usernameField.getHeight() / 2 + 100);

		return usernameField;
	}

	/**
	 * Postavljanje textualnog polja za password gde korisnik unosi svoju sifru
	 * @return
	 */
	private Actor setPasswordTxtField() {
		passwordField = new TextField("", skin);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('*');
		passwordField.setSize(300, 30);
		passwordField.setPosition(GdxGame.get().width / 2 - passwordField.getWidth() / 2,
				GdxGame.get().height / 2 - passwordField.getHeight() / 2);

		return passwordField;
	}

}
