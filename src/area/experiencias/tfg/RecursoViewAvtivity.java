package area.experiencias.tfg;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import area.experiencias.tfg.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.WebService;
import area.domain.modelo.AnexosRecursoModel;
import area.domain.modelo.RecursoModel;

public class RecursoViewAvtivity extends ActionBarActivity {

	TextView titulo;
	WebView descripcion;
	RecursoModel recurso;
	ImageView imagen;
	ListView lista;
	AnexoListAdapter adpaterList;
	ArrayList<AnexosRecursoModel> listaAnexos = new ArrayList<AnexosRecursoModel>();
	
	private int id;
	private boolean sitios = false;
	private MenuItem mapa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recurso_view);
		ActionBar actionBar = getSupportActionBar();
        
		try {
		 titulo = (TextView) findViewById(R.id.title);
		 descripcion = (WebView) findViewById(R.id.description);
		 imagen = (ImageView) findViewById(R.id.image);
		 lista = (ListView) findViewById(R.id.ficheros);
		 Intent intent = getIntent();
		 
		 if (savedInstanceState != null){
	        	id = savedInstanceState.getInt("id");
	        	sitios = savedInstanceState.getBoolean("sitios");
		 }
	     else{
	        	id = intent.getIntExtra("id", 0);
	        	sitios = intent.getBooleanExtra("sitios", false);
	     }
		 
		 
			recurso = LocalStorageServices.DatabaseService().getRecurso(id, sitios);
			
			
			if (recurso != null){
				if(recurso.getTitulo() != null) actionBar.setTitle(recurso.getTitulo());
		        else actionBar.setTitle(getString(R.string.recursos));
			
				
				
				//titulo.setText(recurso.getTitulo());
				
				
				 if(recurso.getTitulo() != null && recurso.getTitulo().length() != 0){
		            	if(recurso.getDefinition().equals(""))
		            		titulo.setText(recurso.getTitulo());
		            	else
		            		titulo.setText(recurso.getDefinition() + ": " + recurso.getTitulo());
		            }            	
		            else{
		            	titulo.setText(getString(R.string.title));
		            }
				
				
				descripcion.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				descripcion.loadDataWithBaseURL("file:///android_asset/", recurso.getDescripcion(), "text/html", "utf-8", null);
				if(!recurso.getImagen().equals("none")){
					ImageLoader.getInstance().loadImage(WebService._BASE_URL + recurso.getImagen().replace("/original/", "/medium/"), new SimpleImageLoadingListener(){
						@Override
						public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
							try {
            					imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, RecursoViewAvtivity.this));
	            			} catch (OutOfMemoryError E){
								System.gc();
								Toast.makeText(RecursoViewAvtivity.this,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
								
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});
					
				}
			}
		List<AnexosRecursoModel> anexos = LocalStorageServices.DatabaseService().getAnexosRecurso(recurso.getId());
		if(recurso.getUrl()!= null && !recurso.getUrl().equals("")){
			AnexosRecursoModel an = new AnexosRecursoModel();
			an.setSnippetUrl(recurso.getUrl());
			listaAnexos.add(an);
			
		}
		for (AnexosRecursoModel anexo : anexos){
			listaAnexos.add(anexo);
		}
		
		adpaterList = new AnexoListAdapter(RecursoViewAvtivity.this, listaAnexos);
		lista.setAdapter(adpaterList);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("id", id);
		outState.putBoolean("sitios", sitios);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recurso_view_avtivity, menu);
		mapa = menu.findItem(R.id.mapa);
		if(!sitios)
			mapa.setVisible(false);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id){
			case android.R.id.home:
	            this.finish();
	            return true;
			case R.id.mapa:
	            Intent mapa = new Intent(RecursoViewAvtivity.this, MapActivity.class);
	            mapa.putExtra("id", recurso.getId()+"");
	            startActivity(mapa);
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
