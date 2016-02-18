package area.experiencias.tfg;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import area.LocalStorage.services.ApplicationService;
import area.experiencias.tfg.R;

/**
 * 
 * @author luis
 *
 */
public class SplashScreenActivity extends Activity {

	private int _splashTime = 2000;
	private Thread thread ;
	private static boolean _iniciado = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
			_iniciado = savedInstanceState.getBoolean("estado");
		
		//ApplicationService.setContext(getApplicationContext());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);
		if(_iniciado) return;
		
	}
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		thread = new Thread(){
			@Override
			public void run() {
				try {
					
					synchronized (this) {
						
						wait(_splashTime);
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					_iniciado = true;
					Intent i = new Intent();
					i.setClass(SplashScreenActivity.this, LoginActivity.class);
					startActivity(i);
				}
			}
			
		};
		thread.start();
		
	}
	@Override
	protected void onStop() {
		
		super.onStop();
		finish();
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
