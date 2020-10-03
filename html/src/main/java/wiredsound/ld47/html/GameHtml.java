package wiredsound.ld47.html;

import com.google.gwt.core.client.EntryPoint;

import playn.html.HtmlPlatform;
import wiredsound.ld47.core.Game;

public class GameHtml implements EntryPoint {
	 @Override public void onModuleLoad () {
		 HtmlPlatform.Config config = new HtmlPlatform.Config();

		 HtmlPlatform plat = new HtmlPlatform(config);
		 plat.assets().setPathPrefix("ld47/");
		 new Game(plat);
		 plat.start();
	 }
}
