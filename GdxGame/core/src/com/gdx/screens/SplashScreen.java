package com.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.game.GdxGame;

/**
 * View koj se prikazuje kao prvi na pocetku prilikomstartovanje igre.
 * Prikazuje naslov igre i na ovom View-u se zadrzavo onoliko dugo koliko je potrebno da ucita u memoriju sve fajlove
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class SplashScreen implements Screen {

	private Stage stage;
	private Image img;
	private Texture texture = new Texture(Gdx.files.internal("SplashScreen/logo.png"));

	private float time = 0;

	/**
	 * konstruktor splash screen-a sakriva reklame tokom njegovog prikaza, postavlja i ucitava parametre za kameru koja vrsi prikaz
	 * view-a na ekran. takodje pokrece metodu queueAssets koja ucitava u pozadini sve potrebne fajlove
	 */
	public SplashScreen() {
		try {
			GdxGame.get().handler.showAds(false);
		} catch (Exception e) {
		}

		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);

		stage = new Stage(new StretchViewport(GdxGame.get().width, GdxGame.get().height, GdxGame.get().camera));

		img = new Image(texture);

		img.setPosition(GdxGame.get().width / 2 - img.getWidth() / 2, GdxGame.get().height / 2 - img.getHeight() / 2);
		img.addAction(Actions.parallel(Actions.fadeOut(0f), Actions.fadeIn(1f)));
		stage.addActor(img);

		queueAssets();
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	/**
	 * metoda koja vrsi logicki update komponenata koji se prikazuju na ekran. Nakon ucitavanja svih fajlova prebacuje 
	 * prikaz na LogIn view.
	 * @param delta
	 */
	public void update(float delta) {
		if (GdxGame.get().assets.update() /* && time > 1 */) {
			GdxGame.get().initScreens();
			GdxGame.get().loginScreen.initScreen(false);
		}
	}

	/**
	  * {@inheritDoc}
	  * render metoda koja racuna svaki frejm prikazan
	  */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		time += delta;

		stage.act(delta);
		stage.draw();

		update(delta);

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
		texture.dispose();
		stage.dispose();

	}

	/**
	 * metoda koja ucitava sve potrebne fajlove u memoriju radi brzeg pristupanja njima tokom igre.
	 */
	public void queueAssets() {
		GdxGame.get().assets.load("Characters/ninja/idle/idle1.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/idle/idle2.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/idle/idle3.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/idle/idle4.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/walk/walk1.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/walk/walk2.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/walk/walk3.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/walk/walk4.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/walk/walk5.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/walk/walk6.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/jump/jump1.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump2.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump3.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump4.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump5.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump6.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump7.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/jump/jump8.png", Texture.class);

		GdxGame.get().assets.load("Characters/blood/blood1.png", Texture.class);
		GdxGame.get().assets.load("Characters/blood/blood2.png", Texture.class);
		GdxGame.get().assets.load("Characters/blood/blood3.png", Texture.class);
		GdxGame.get().assets.load("Characters/blood/blood4.png", Texture.class);
		GdxGame.get().assets.load("Characters/blood/blood5.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/bullet/bullet.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/bullet/bullet1.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/attack1/attack11.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack1/attack12.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack1/attack13.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack1/attack14.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/attack2/attack21.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack2/attack22.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack2/attack23.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack2/attack24.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/attack3/attack31.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack3/attack32.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack3/attack33.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack3/attack34.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/attack4/attack41.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack42.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack43.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack44.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack45.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack46.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack47.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack4/attack48.png", Texture.class);

		GdxGame.get().assets.load("Characters/ninja/attack5/attack51.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack52.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack53.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack54.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack55.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack56.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack57.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack58.png", Texture.class);
		GdxGame.get().assets.load("Characters/ninja/attack5/attack59.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/idle/idle1.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/idle/idle2.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/idle/idle3.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/idle/idle4.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/walk/walk1.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/walk/walk2.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/walk/walk3.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/walk/walk4.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/walk/walk5.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/walk/walk6.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/shield/shield1.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shield/shield2.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shield/shield3.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shield/shield4.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shield/shield5.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion1.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion2.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion3.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion4.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion5.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion6.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion7.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion8.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion9.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion10.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion11.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion12.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/explosion/explosion13.png", Texture.class);

		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot1.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot2.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot3.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot4.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot5.png", Texture.class);
		GdxGame.get().assets.load("Characters/rocketMan/shoot/shoot6.png", Texture.class);

		GdxGame.get().assets.load("Buttons/login1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/login2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/register1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/register2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/email.png", Texture.class);
		GdxGame.get().assets.load("Buttons/password1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/connect1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/connect2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/random1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/random2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/withFriend1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/withFriend2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/back1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/back2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/cancel1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/cancel2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/logout1.png", Texture.class);
		GdxGame.get().assets.load("Buttons/logout2.png", Texture.class);
		GdxGame.get().assets.load("Buttons/friendsemail.png", Texture.class);
		GdxGame.get().assets.load("Buttons/connecting.png", Texture.class);
		GdxGame.get().assets.load("Buttons/registrationFailed.png", Texture.class);
		GdxGame.get().assets.load("Buttons/registrationSuccessfull.png", Texture.class);

		GdxGame.get().assets.load("LogInScreen/background1.png", Texture.class);
		GdxGame.get().assets.load("LogInScreen/bg1.png", Texture.class);
		GdxGame.get().assets.load("LogInScreen/bg2.png", Texture.class);
		GdxGame.get().assets.load("LogInScreen/bg3.png", Texture.class);
		GdxGame.get().assets.load("LogInScreen/bg4.png", Texture.class);

		GdxGame.get().assets.load("GameScreen/bg1.png", Texture.class);
		GdxGame.get().assets.load("GameScreen/bg2.png", Texture.class);
		GdxGame.get().assets.load("GameScreen/bg3.png", Texture.class);
		GdxGame.get().assets.load("GameScreen/bg4.png", Texture.class);

		GdxGame.get().assets.load("Buttons/circle.png", Texture.class);
		GdxGame.get().assets.load("Buttons/arrows.png", Texture.class);
		GdxGame.get().assets.load("Buttons/jump.png", Texture.class);
		GdxGame.get().assets.load("Buttons/attackIcon.png", Texture.class);
		GdxGame.get().assets.load("Buttons/stealth.png", Texture.class);
		GdxGame.get().assets.load("Buttons/shieldIcon.png", Texture.class);

		GdxGame.get().assets.load("Buttons/victory.png", Texture.class);
		GdxGame.get().assets.load("Buttons/defeat.png", Texture.class);
	}

}
