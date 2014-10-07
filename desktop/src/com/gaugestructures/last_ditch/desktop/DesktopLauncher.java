package com.gaugestructures.last_ditch.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.LastDitch;

public class DesktopLauncher {
	public static void main(String[] arg) {
//        TexturePacker.process("gfx", "gfx", "graphics");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = C.TITLE;
        config.width = C.WIDTH;
        config.height = C.HEIGHT;

		new LwjglApplication(new LastDitch(), config);
	}
}
