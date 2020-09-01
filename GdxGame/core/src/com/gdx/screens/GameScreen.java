package com.gdx.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.client.Connector;
import com.gdx.client.Packet;
import com.gdx.controls.AttackButton;
import com.gdx.controls.JumpButton;
import com.gdx.controls.ThirdPowerButton;
import com.gdx.controls.TouchPad;
import com.gdx.game.GdxGame;
import com.gdx.gameObjects.B2DContactListener;
import com.gdx.gameObjects.Projectile;
import com.gdx.gameObjects.gameBody;
import com.gdx.player.HealthBar;
import com.gdx.player.Ninja;
import com.gdx.player.Player;
import com.gdx.player.RocketMan;

/**
 * View koj se prikazuje tokom igre izmedju 2 korisnika. 
 * Komunikacija se vrsi uz pomoc connectora na odvojenom threadu. 
 * Koristi interface od libGDX biblioteke za renderovanje i prikaz na ekrana
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class GameScreen implements Screen {

	public static final float PPM = 56;
	public static final float SCALE = 56;
	public static final float RATIO = SCALE / PPM;

	public String oponentName;
	public String myName;
	private Connector connector;
	private Thread connectorThread;
	public Packet packet;
	public boolean packetChanged;
	public Stage stage;

	private float speed = 0.9f;
	private int counter = 0;
	private float t = 0;
	private Image background;
	private SpriteDrawable[] bgr = new SpriteDrawable[4];

	public TouchPad touchPad;
	public JumpButton jumpButton;
	public AttackButton attackButton;
	public ThirdPowerButton thirdPowerButton;

	public B2DContactListener contactListener;
	public World world;
	public Box2DDebugRenderer renderer;
	public Player player1, player2;
	public int bottomY = 86;

	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	public HealthBar hp1, hp2;

	public float width = GdxGame.get().width;
	public float height = GdxGame.get().height;

	public Vector3 mousePos = new Vector3(0, 0, 0);
	public Array<Body> activeBodys = new Array<Body>();

	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	BitmapFont font;
	String time;
	int fontX, fontY;
	long tempTime = -1, startTime;
	boolean matchEnd = false;

	// u sekundama
	int matchTime = 60;

	public Music sound;
	public long soundID = 0;

	/**
	 * inicailizacija svih parametara koj se ne menjaju, pozadina, zvuk, i sve texture
	 */
	public GameScreen() {
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

		sound = Gdx.audio.newMusic(Gdx.files.internal("sounds/fight sound.mp3"));
	}

	/**
	 * inicializaciaja svih parametara za dati view koji se postavljaju na pocetnu vrednost.
	 * ova metoda dati view postavlja na ekran i skida reklame sa gornjeg dela ekrana ukoliko se igra na androidu
	 */
	public void initScreen(char position) {
		try {
			GdxGame.get().handler.showAds(false);
		} catch (Exception e) {
		}
		try {
			GdxGame.get().loginScreen.sound.stop();
		} catch (Exception e) {
		}
		GdxGame.get().screenWidth = Gdx.graphics.getWidth();
		GdxGame.get().screenHeight = Gdx.graphics.getHeight();
		GdxGame.get().camera.setToOrtho(false, GdxGame.get().width, GdxGame.get().height);
		GdxGame.get().batch.setProjectionMatrix(GdxGame.get().camera.combined);
		shapeRenderer.setProjectionMatrix(GdxGame.get().camera.combined);

		world = new World(new Vector2(0, -50f), false);
		contactListener = new B2DContactListener();
		world.setContactListener(contactListener);
		renderer = new Box2DDebugRenderer();
		// main frame
		createBox(width / 2 - 50, bottomY - 25, width + 100, 50, true, true);
		createBox(width / 2 - 50, height + 25, width + 100, 50, true, true);
		createBox(-25, height / 2, 50, height, true, true);
		createBox(width + 25, height / 2, 50, height, true, true);

		// platforms
		createBox(210, 230, 420, 14, true, true);

		if (stage != null)
			stage.dispose();
		stage = new Stage(new StretchViewport(width, height, GdxGame.get().camera));

		stage.addActor(background);

		if (position == 'L') {
			player1 = new Ninja(50, bottomY + 36, 28, 36, false, -1);
			player2 = new RocketMan(width - 50, bottomY + 36, 28, 36, true, 1);
		} else {
			player1 = new RocketMan(width - 50, bottomY + 36, 28, 36, false, -1);
			player2 = new Ninja(50, bottomY + 36, 28, 36, true, 1);
		}

		hp1 = new HealthBar(true, 100, player1, myName);
		player1.setHealthBar(hp1);

		hp2 = new HealthBar(false, 100, player2, oponentName);
		player2.setHealthBar(hp2);

		touchPad = new TouchPad(100, 20, stage, player1);
		jumpButton = new JumpButton(800, 20, stage, player1);
		attackButton = new AttackButton(730, 20, stage, player1);
		thirdPowerButton = new ThirdPowerButton(660, 20, stage, player1);

		packet = new Packet();

		font = new BitmapFont(Gdx.files.internal("fonts/gameTimer.fnt"));
		fontX = (int) (width / 2);
		fontY = (int) (height - 20);
		startTime = System.currentTimeMillis();
		matchEnd = false;

		GdxGame.get().setScreen(this);

		sound.stop();
		sound.setVolume(1);
		sound.setLooping(true);
		sound.play();
	}

	/**
	  * {@inheritDoc}
	  */
	@Override
	public void show() {
	}

	/**
	  * {@inheritDoc}
	  * render metoda koja racuna svaki frejm prikazan
	  */
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		if (player1.healthBar.HP <= 0) {
			GdxGame.get().loginScreen.clientThread.client.exit(true);
			GdxGame.get().finishScreen.initScreen(false);

		}
		if (player2.healthBar.HP <= 0) {
			GdxGame.get().loginScreen.clientThread.client.exit(true);
			GdxGame.get().finishScreen.initScreen(true);
		}

		if (!connectorThread.isAlive()) {
			GdxGame.get().loginScreen.clientThread.client.exit(true);
			GdxGame.get().loginScreen.initScreen(true);
		}

		cycleBakground(delta);
		world.step(1 / 45f, 6, 2);

		update(delta);
		updateOponent(connector.getOponent());

		player1.render(delta);
		player2.render(delta);

		stage.act(delta);
		stage.draw();

		player1.renderParticleEffects();
		player2.renderParticleEffects();

		thirdPowerButton.render();

		GdxGame.get().batch.begin();
		font.draw(GdxGame.get().batch, time, fontX, fontY);
		GdxGame.get().batch.end();

		// renderer.render(world, GdxGame.get().camera.combined.scl(SCALE));

		if (packetChanged) {
			player1.updatePacketBeforeSending(packet);
			connector.queuePlayer(packet);
			packetChanged = false;
		}

		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		try {
			shapeRenderer.begin(ShapeType.Filled);
		} catch (Exception e) {
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
		}

		player1.healthBar.render(shapeRenderer);
		player2.healthBar.render(shapeRenderer);

		if (!player1.invisible)
			shapeRenderer.end();

		Gdx.gl.glDisable(GL30.GL_BLEND);
	}

	public boolean tuched = false;

	/**
	 * izvrsava update komponenti koju su prikazani na ekranu. U to spada 
	 * pomeranje playera regulacija akcije dodira na ekran...
	 * @param delta
	 * 			delta vrednost iz rendera koja predstavlja razliku vremena izmedju frejmova
	 */
	public void update(float delta) {
		if (!matchEnd) {
			tempTime = System.currentTimeMillis() - startTime;
			tempTime = matchTime - tempTime / 1000;
			time = "" + tempTime;
			if (tempTime == 0) {
				matchEnd = true;
				startTime = System.currentTimeMillis();
			}
		} else {
			if (System.currentTimeMillis() - startTime > 100) {
				startTime = System.currentTimeMillis();
				player1.healthBar.dealDamage(1);
			}
		}

		tuched = false;
		for (int i = 0; i < 3; i++) {
			if (Gdx.input.isTouched(i)) {
				touchPad.screenTouched(Gdx.input.getX(i), Gdx.input.getY(i));
				jumpButton.screenTouched(Gdx.input.getX(i), Gdx.input.getY(i));
				attackButton.screenTouched(Gdx.input.getX(i), Gdx.input.getY(i));
				thirdPowerButton.screenTouched(Gdx.input.getX(i), Gdx.input.getY(i));
				// tuched = true;
			}
		}

		if (!tuched) {
			touchPad.update();
		}
		keyBoard();

		for (Projectile p : projectiles)
			p.update(delta);

		player1.update(delta);

		player2.healthBar.update();
		player2.visualControlUpdates(delta);
		thirdPowerButton.update();
	}

	public void resize(int width, int height) {
	}

	/**
	 * vrsi proveru inputa kroz tastaturu prilikom igranja na racunaru
	 */
	public void keyBoard() {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player1.left = true;
			player1.right = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			player1.left = false;
			player1.right = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			player1.startJump();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			player1.attack();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			player1.thirdPower();
		}
	}

	/**
	 * Uzima parametre iz paketa koj je dobijen preko mreze i setuje ih protivniku 
	 * @param opponent
	 * 			Paket koj je dobijen od konektora
	 */
	public void updateOponent(Packet opponent) {
		try {
			player2.body.setTransform(opponent.x, opponent.y, 0);
			player2.running = opponent.running;
			player2.jump = opponent.jump;
			player2.hit = opponent.hit;
			player2.setY(player2.body.getPosition().y * PPM - player2.offsetY - player2.height / 2);
			player2.setScaleX(opponent.scaleX);
			player2.updateStaticSpritePosition();
			if (player2 instanceof Ninja) {
				Ninja ninja = (Ninja) player2;
				ninja.attacking = opponent.attacking;
				if (ninja.attackId != opponent.attackId)
					ninja.attackPos = 0;
				ninja.attackId = opponent.attackId;
				ninja.attackPosMax = opponent.attackPosMax;
				ninja.invisible = opponent.invisible;
			}
			if (player2 instanceof RocketMan) {
				RocketMan rocketman = (RocketMan) player2;
				if (opponent.shoot) {
					rocketman.shoot = true;
					opponent.shoot = false;
					connector.setOponent(opponent);
				}
				if (!opponent.shield && rocketman.shield) {
					rocketman.shieldImage.remove();
					rocketman.shieldAdded = false;
				}
				rocketman.shield = opponent.shield;
				rocketman.flightRequest = opponent.flightRequest;
			}

			if (player2.healthBar.HP != opponent.HP) {
				player2.healthBar.dealDamage(player2.healthBar.HP - opponent.HP);
				player2.struck = true;
			}
		} catch (Exception e) {

		}
	}

	/**
	 * vrsi izmenu pozadine u zavisnosti od vremena koj ej isteklo. pozadina se ponasa kao gif
	 */
	private void cycleBakground(float delta) {

		t += delta;
		if (t > speed) {
			if (counter < bgr.length - 1) {
				background.setDrawable(bgr[++counter]);
			} else
				background.setDrawable(bgr[counter = 0]);

			t = 0;
		}
	}

	/**
	 * Vraca playera koj nije prosledjeni player
	 * @param player
	 * @return
	 */
	public Player getOppositeCharacter(Player player) {
		if (player2 == player)
			return player1;
		else
			return player2;
	}

	/**
	 * Kreira pravugaonik koja pripada Box2D svetu.
	 * @param x
	 * 		x koordinata
	 * @param y
	 * 		y koordinata
	 * @param width
	 * 		sirina pravugaonika
	 * @param height
	 * 		visina pravouganika
	 * @param isStatic
	 * 		boolean koj govori dal je dati box2D objekat statican
	 * @param fixedRotation
	 * 		boolean koj govori dali box2D svet moze da rotira dati pravugaonik
	 * @return Vraca kreirani body koj je postavljen u box2D svet
	 */
	public Body createBox(float x, float y, float width, float height, boolean isStatic, boolean fixedRotation) {
		Body body;
		BodyDef def = new BodyDef();
		if (isStatic)
			def.type = BodyDef.BodyType.StaticBody;
		else
			def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = fixedRotation;
		body = world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

		if (isStatic)
			body.createFixture(shape, 0f);
		else
			body.createFixture(shape, 1f);
		shape.dispose();

		body.setUserData(new gameBody());
		return body;
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
	 * setuje konektor za mreznu komunikaciju uz pomoc kog ce se dobijati informacije o protivniku.
	 * @param connector
	 * 			Konektor koj radi na drugom threadu i sluzi za komunikaciju
	 * @param connectorThread
	 * 			thread na kom radi connector
	 */
	public void setConnector(Connector connector, Thread connectorThread) {
		this.connector = connector;
		this.connectorThread = connectorThread;
	}

}
