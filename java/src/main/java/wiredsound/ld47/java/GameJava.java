package wiredsound.ld47.java;

import playn.java.LWJGLPlatform;
import wiredsound.ld47.core.Game;

public class GameJava {
	public static void main (String[] args) {
		LWJGLPlatform.Config config = new LWJGLPlatform.Config();

		config.appName = "Ludum Dare 47 - WiredSound";

		config.width = 1024;
		config.height = 764;

		LWJGLPlatform plat = new LWJGLPlatform(config);
		new Game(plat);
		plat.start();
	}
}
