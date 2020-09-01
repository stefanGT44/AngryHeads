package com.gdx.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.gdx.game.GdxGame;

/**
 * Klasa koja kontrolise health svog playera. Vrsi prikaz bloka koj predstavlja indikator koliko je zivota ostalo datom igracu.
 * racuna odbijanje zivota i sve kalkulacije vezane za zivot datog igraca
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class HealthBar {

	Color green1 = Color.valueOf("317410"), green2 = Color.valueOf("91fe41"), red1 = Color.valueOf("ff0000"),
			red2 = Color.valueOf("ffa640"), color1, color2;

	public int HP;
	public float maxHP;
	private Player player;
	private float x, y;
	private float width = 58, height = 6, currentWidth = 58;
	private int offsetX = 35, offsetY = 90;

	public Color color = new Color(0, 0, 0, 1);

	private float extraHeight = 2;
	private float extraWidth = currentWidth;
	private float extraOffsetY = 1 + extraHeight;
	public float extraResource = 100, maxResource = 100;
	
	
	BitmapFont font;
	public String username;
	private float textOffsetX = 0, textOffsetY = 18;

	public HealthBar(boolean flag, int HP, Player player, String username) {
		this.player = player;
		this.HP = HP;
		this.maxHP = HP;
		this.username = username;

		if (flag) {
			color1 = green1;
			color2 = green2;
			font = new BitmapFont(Gdx.files.internal("fonts/greenFont.fnt"));
		} else {
			color1 = red1;
			color2 = red2;
			font = new BitmapFont(Gdx.files.internal("fonts/redFont.fnt"));
		}

		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, username);
		textOffsetX = (width - layout.width) / 2;

		update();
	}

	public void render(ShapeRenderer shapeRenderer) {
		if (player == GdxGame.get().gameScreen.player2 && player.invisible)
			return;
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x - 1, y - 1, width + 2, height + 2);
		if (HP > 0)
			shapeRenderer.rect(x, y, currentWidth, height, color1, color2, color2, color1);
		if (player.extraBar) {
			shapeRenderer.setColor(Color.SKY);
			shapeRenderer.rect(x, y - extraOffsetY, extraWidth, extraHeight);
		}
		GdxGame.get().batch.begin();
		font.draw(GdxGame.get().batch, username, x + textOffsetX, y + textOffsetY);
		GdxGame.get().batch.end();
	}

	public void update() {
		if (player.image.getScaleX() == -1)
			x = player.image.getX() + offsetX - player.image.getWidth();
		else
			x = player.image.getX() + offsetX;
		y = player.image.getY() + offsetY;
	}
	
	public void useResource(float amount) {
		extraResource -= amount;
		if (extraResource <= 0) {
			extraResource = 0;
			return;
		}
		extraWidth = (int) ((width * extraResource) / maxResource);
	}
	
	public float getResource() {
		return this.extraResource;
	}
	
	public float getMaxResource() {
		return maxResource;
	}
	
	public void regenerateResource(float amount) {
		extraResource += amount;
		if (extraResource >= maxResource) {
			extraWidth = width;
			extraResource = maxResource;
			return;
		}
		extraWidth = (int)((width * extraResource) / maxResource);
	}

	public void dealDamage(int dmg) {
		HP -= dmg;
		if (HP <= 0)
			return;
		currentWidth = (int) ((width * HP) / maxHP);
		GdxGame.get().gameScreen.packetChanged = true;
	}

}
