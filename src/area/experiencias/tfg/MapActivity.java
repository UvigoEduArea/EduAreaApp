package area.experiencias.tfg;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import area.LocalStorage.services.LocalStorageServices;
import area.domain.modelo.AnexosRecursoModel;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.MarkerManager.Collection;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;






public class MapActivity extends FragmentActivity {

	
	
	public class Posicion implements ClusterItem {
	    private final LatLng mPosition;
	    private final String direccion;
	    private final int _id;

	    public Posicion(double lat, double lng, String dir, int id) {
	        mPosition = new LatLng(lat, lng);
	        direccion = dir;
	        _id = id;
	    }

	    @Override
	    public LatLng getPosition() {
	        return mPosition;
	    }
	    
	    public String getDireccion(){
	    	return direccion;
	    }
	    
	    public int getId(){
	    	return _id;
	    }
	    
	}
	
	
	class MyClusterRender extends DefaultClusterRenderer<Posicion> {

		public MyClusterRender(Context context, GoogleMap map,
				ClusterManager<Posicion> clusterManager) {
			super(context, map, clusterManager);
		}
		
		@Override
		protected void onBeforeClusterItemRendered(Posicion item, MarkerOptions markerOptions) {
			super.onBeforeClusterItemRendered(item, markerOptions);
			markerOptions.title(item.getDireccion());
			
			
			
		}
		
	}
	
	
	 private GoogleMap mMap;
	 
	 private String id;
	
	 //ClusterManager<Posicion> cluster;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		try {
		setUpMapIfNeeded();
		getMap().setMyLocationEnabled(true);
		//getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.2329134,-8.7203053), 10));
		Intent intent = getIntent();
		
		id = intent.getStringExtra("id");
		
		
		
		
		
		ClusterManager<Posicion> cluster =  new ClusterManager<>(this, getMap());
		
		cluster.setRenderer(new MyClusterRender(this, getMap(), cluster));
		
		
			List<AnexosRecursoModel> anexos = LocalStorageServices.DatabaseService().getAnexosRecurso(id);
		
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			
			
			for (AnexosRecursoModel anexo : anexos){
				cluster.addItem(new Posicion(anexo.getLatitude(), anexo.getLongitude(), anexo.getDireccion(), anexo.getRecursoId()));
				builder.include(new LatLng(anexo.getLatitude(), anexo.getLongitude()));
				
				
			}
		
			getMap().setOnInfoWindowClickListener(cluster);
			
			cluster.setOnClusterItemInfoWindowClickListener(new OnClusterItemInfoWindowClickListener<MapActivity.Posicion>() {

				@Override
				public void onClusterItemInfoWindowClick(Posicion item) {
					Intent i = new Intent(MapActivity.this, RecursoViewAvtivity.class);
					i.putExtra("id", item.getId());
					startActivity(i);
					
				}
			});
			
			final LatLngBounds bounds = builder.build();
			
		
			
			
			

		
		getMap().setOnCameraChangeListener(cluster);
		
		//getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10.0f));
		
		
		getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() { 
			@Override 
			public void onMapLoaded() { 
			    getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
			 } 
			});
		
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	 @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		 int id = item.getItemId();
         switch (id){
         
	         case R.id.MapaNormal:
	        	getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
	             return true;
	         case R.id.MapaIbrido:
	        	 getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
	             return true;
	         case R.id.MapaSatelite:
	        	 getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	             return true;
	         case R.id.MapaTerreno:
	        	 getMap().setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	             return true;
          
         
         }
		
		
		return super.onOptionsItemSelected(item);
	}
	
	
	private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {
            //startDemo();
        }
    }
	
	protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

	
	
	
}
