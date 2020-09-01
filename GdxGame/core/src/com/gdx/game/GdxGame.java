package com.gdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.screens.ConnectingScreen;
import com.gdx.screens.FinishScreen;
import com.gdx.screens.GameScreen;
import com.gdx.screens.LogInScreen;
import com.gdx.screens.MatchMakingScreen;
import com.gdx.screens.SplashScreen;
/**
 * Predstavlja glavnu kaslu prilikom pokretanja igre. U sebi sadrzi sve View-ove igre koji se kreiraju samo jednom.
 * View-ovi se dobijaju iz ove klase
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class GdxGame extends Game {
	private static GdxGame instance;

	public float width;
	public float height;
	public float screenWidth;
	public float screenHeight;

	public OrthographicCamera camera;
	public AssetManager assets;
	public SpriteBatch batch;

	public LogInScreen loginScreen;
	public MatchMakingScreen matchMakingScreen;
	public ConnectingScreen connectingScreen;
	public GameScreen gameScreen;
	public FinishScreen finishScreen;
	public static AdHandler handler = null;

	/**
	 * Metoda za dobijanje ovog singletona
	 * @return vrace samog sebe
	 */
	public static GdxGame get() {
		if (instance == null)
			new GdxGame(handler);

		return instance;
	}

	/**
	 * Konstruktor klase koja inicializuje sebe i kameru koja aprikazuje sadrzaj igre
	 * @param handler
	 * 			Handler koj sluzi za regulaciju prikaza reklama
	 */
	public GdxGame(AdHandler handler) {
		this.handler = handler;
		instance = this;

		width = 900;
		height = 500;
		camera = new OrthographicCamera();
	}

	@Override
	public void create() {
		assets = new AssetManager();
		batch = new SpriteBatch();

		setScreen(new SplashScreen());
	}

	@Override
	public void render() {
		super.render();

	}

	@Override
	public void dispose() {
		batch.dispose();
		assets.dispose();

		super.dispose();
	}

	/**
	 * inicializacija svih View-ova (game screeno-va)
	 */
	public void initScreens() {
		loginScreen = new LogInScreen();
		matchMakingScreen = new MatchMakingScreen();
		connectingScreen = new ConnectingScreen();
		gameScreen = new GameScreen();
		finishScreen = new FinishScreen();
	}
}
