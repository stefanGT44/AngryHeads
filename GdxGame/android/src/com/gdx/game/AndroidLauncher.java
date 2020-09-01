package com.gdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Klasa koja se pokrece prilikom pokretanja aplikacije. Implementira Adhandler kako bi se vrsilo skrivanje 
 * i prikazivanje reklama na vrhu ekrana preko libGDX threada koj se pokrece tokom igrice. On create metoda konkretno ucitava igricu 
 * postavlja je na view i stavlja na vrh adMob reklame.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class AndroidLauncher extends AndroidApplication implements AdHandler{
	private static final String TAG = "AndroidLauncher";
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	protected AdView adView;


	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case SHOW_ADS:
					adView.setVisibility(View.VISIBLE);
					break;
				case HIDE_ADS:
					adView.setVisibility(View.GONE);
					break;
			}
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView = initializeForView(new GdxGame(this), config);
		layout.addView(gameView);

		adView = new AdView(this);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				int visiblity = adView.getVisibility();
				adView.setVisibility(AdView.GONE);
				adView.setVisibility(visiblity);
				Log.i(TAG, "Ad Loaded...");
			}
		});
		adView.setAdSize(AdSize.SMART_BANNER);


		adView.setAdUnitId("ca-app-pub-1059537898516792/1099507466");

		AdRequest.Builder builder = new AdRequest.Builder();
		//run once before uncommenting the following line. Get TEST device ID from the logcat logs.
		//builder.addTestDevice("79B448DFF4281063F89E9425DF2A3530");
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);

		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layout.addView(adView, adParams);
		adView.loadAd(builder.build());
		setContentView(layout);
	}

	@Override
	public void showAds(boolean show) {
		if(show)
			handler.sendEmptyMessage(SHOW_ADS);
		else
			handler.sendEmptyMessage(HIDE_ADS);
	}
}
