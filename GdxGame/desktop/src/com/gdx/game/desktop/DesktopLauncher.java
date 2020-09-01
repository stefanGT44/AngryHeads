package com.gdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.game.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Game";
		config.foregroundFPS = 60;
		config.width = (int) GdxGame.get().width;
		config.height = (int) GdxGame.get().height;
		config.resizable = false;
		
		//config.addIcon("data/icon.png", Files.FileType.Internal);
		new LwjglApplication(GdxGame.get(), config);
	}
}
