package area.experiencias.tfg;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.services.WebServiceServices;
import area.domain.modelo.PerfilModel;
import area.experiencias.tfg.R;

public class ActivityViewDrawerActivity extends FragmentActivity implements
NavDrawerFragmentActivities.NavigationDrawerCallbacks {

	
	private class Sincronizacion extends CompruebaSincronizacion{

		boolean isUp = true;
		
		@Override
		public void avisar(boolean desincronizado) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void descargando(final boolean descargando) {
			//if (downloading == null) return;
			if (enLocal == null) return;
			if (ActivityViewDrawerActivity.this == null) return;
			ActivityViewDrawerActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (descargando){
						enLocal.setVisible(false);
						//downloading.setVisible(!downloading.isVisible());
					}
					else{
						int id = LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId);
						if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(id) ){
							 enLocal.setVisible(true);
						 }else{
							 enLocal.setVisible(false);
						 }
							 
						//downloading.setVisible(false);
					}
				}
			});
		}

		@Override
		public void modoLocal(boolean modoLocal) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void updated(final boolean isUpdated){
			if (enLocal == null) return;
			if (ActivityViewDrawerActivity.this == null) return;
			ActivityViewDrawerActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {					
//					if(isUpdated != isUp){
//						isUp = isUpdated;
					
					if (isUpdated){
						int id = LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId);
												
						if(LocalStorageServices.DatabaseService().getExperienceIsUpdated(id) && LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(id)){
							 enLocal.setVisible(true);
							 //listaActividades.setVisible(true);
						 }else{
							enLocal.setVisible(false);
							if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(id) ){
								 WebServiceServices.Download().downloadActivity(id);
							 }
						 }
						
						
					}
				}
			});
			
		}
		
		
	}
	
	
	private Sincronizacion sinc;
	
	private MenuItem enLocal,downloading = null;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavDrawerFragmentActivities mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	
	
	
	/**
     * Activity Sections
     */
    private static final int DESCRIPTION 		= 0;
    //private static final int DETALLES			= 1;
    private static final int RECURSOS			= 1;
    private static final int SITIOS				= 2;
    private static final int ENTREGAS			= 3;

    private static final int OBSERVACIONES		= 5;
    private static final int SALIR				= 6;
	
    
    private static final int DESCRIPTION_STUDENT			= 0;
    private static final int RECURSOS_STUDENT				= 1;
    private static final int SITIOS_STUDENT					= 2;
    private static final int ENTREGAS_STUDENT				= 3;
    
    private static final int OBSERVACIONES_STUDENT			= 5;
    private static final int SALIR_STUDENT					= 6;
    
	
    private int _actiId;
	
    MiniActivity activity;
    
    protected static ObservacionesFragment observacionesFragment;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_drawer);
		Intent intent = getIntent();
		_actiId = intent.getExtras().getInt("id");
		mNavigationDrawerFragment = (NavDrawerFragmentActivities) getFragmentManager().findFragmentById(R.id.navigation_drawer_activity);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer_activity,(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
			activity = LocalStorageServices.DatabaseService().getMiniActivity(_actiId);
		
        
        ActionBar actionBar = getActionBar();
        String curso = "";
        if(activity != null && activity.getName() != null){
        	Experience exp =LocalStorageServices.DatabaseService().getExpirience(LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId));
        	
        	
        	if(exp.getName() != null) {
            	if(exp.getDefinition().equals(""))
            		curso = exp.getName();
            	else
            		curso = exp.getDefinition() + ": " + exp.getName();
            }
            else curso = getString(R.string.title);
        	
        	
//        	if(exp.getName() != null) 
//        		curso = getString(R.string.cursoTitulo) + " " + exp.getName();
//            else 
//            	curso = getString(R.string.cursoTitulo) + " " + getString(R.string.title);
        	
        	actionBar.setTitle(curso + " > " + (!activity.getDefinition().equals("") ? (activity.getDefinition() + ": ") :"")  + activity.getName());
        }
        else{
        	actionBar.setTitle(curso + " > " + getString(R.string.actividadTitulo) + " " + getString(R.string.title));
        }
		
		} catch (Exception e1) {
			Intent intent = new Intent(ActivityViewDrawerActivity.this, ExperiencesActivity.class);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
			e1.printStackTrace();
		}
		
	}
	
	@Override
	protected void onStart() {
		sinc = new Sincronizacion();
    	sinc.iniciar();
    	super.onStart();
	}
	
	@Override
	protected void onStop() {
		sinc.parar();
    	sinc = null;
    	super.onStop();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		
		
		Intent i = getIntent();
		_actiId = i.getExtras().getInt("id");
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
			
			switch (position){
				case DESCRIPTION_STUDENT:
		            fragmentManager
					.beginTransaction()
					.replace(R.id.container,DescriptionFragmentActivity.newInstance(_actiId),"PORTADA").commit();
		            break;
		        case RECURSOS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_actiId,"activities", false)).commit();
		        	break;
		        case SITIOS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_actiId,"activities", true)).commit();
		        	break;
		        case ENTREGAS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,EntregasFragment.newInstance(_actiId,"activities")).commit();
		        	break;
		        	
		        case OBSERVACIONES_STUDENT:
		        	observacionesFragment = ObservacionesFragment.newInstance(_actiId, "activities");
		        	fragmentManager.beginTransaction().replace(R.id.container, observacionesFragment).commit();
		        	break;
		        	
		        case SALIR_STUDENT:
		        	compruebaSalida();
		        	break;
		    }
		}
		else{
			switch (position){
		        case DESCRIPTION:
		            fragmentManager
					.beginTransaction()
					.replace(R.id.container,DescriptionFragmentActivity.newInstance(_actiId),"PORTADA").commit();
		            break;
		        
//		        case DETALLES:
//		        	fragmentManager
//					.beginTransaction()
//					.replace(R.id.container,DetallesFragment.newInstance(_actiId,"activities")).commit();
//		        	break;
		        case OBSERVACIONES:
		        	observacionesFragment = ObservacionesFragment.newInstance(_actiId, "activities");
		        	fragmentManager.beginTransaction().replace(R.id.container, observacionesFragment).commit();
		        	break;
		        case RECURSOS:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_actiId, "activities", false)).commit();
		        	break;
		        case SITIOS:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_actiId,"activities", true)).commit();
		        	break;
		        case ENTREGAS:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,EntregasFragment.newInstance(_actiId,"activities")).commit();
		        	break;
		        case SALIR:
		        	compruebaSalida();
		        	break;
			}
		}
	}

//	public void onSectionAttached(int number) {
//		switch (number) {
//		case 1:
//			mTitle = getString(R.string.title_section1);
//			break;
//		case 2:
//			mTitle = getString(R.string.title_section2);
//			break;
//		case 3:
//			mTitle = getString(R.string.title_section3);
//			break;
//		}
//	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		enLocal = menu.add(0, -1, 100, "En local");
		
		enLocal.setIcon(R.drawable.cloud_download2);
		enLocal.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		enLocal.setVisible(false);
		
		//downloading = menu.findItem(R.id.downloading);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
        switch (id){
        case R.id.lista_actividades:
       	 listaAvtividades();
       	 return true;
       	 
        }

		
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
    private void listaAvtividades() {
		Dialog dialog = new Dialog(ActivityViewDrawerActivity.this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityViewDrawerActivity.this);
		ListView lista = new ListView(ActivityViewDrawerActivity.this);
		List<String> nombresActividades = new ArrayList<String>();
		
		try {
			final Experience exp = LocalStorageServices.DatabaseService().getExpirience(LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId));
			final List<MiniActivity> listaActividades = LocalStorageServices.DatabaseService().getMiniActivities(LocalStorageServices.DatabaseService().getActivityIds( LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId)));
			
			for (int i=0; i<listaActividades.size(); i++){
				if(i == 0){
					nombresActividades.add(exp.getDefinition() + ": " + exp.getName());
				}
				nombresActividades.add(i+1 + ". " +listaActividades.get(i).getDefinition() + ": " +listaActividades.get(i).getName());
			}
			    			
			lista.setAdapter(new ArrayAdapter<>(ActivityViewDrawerActivity.this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, nombresActividades));
			lista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(position == 0){
						Intent intent = new Intent(ActivityViewDrawerActivity.this, ExperienceViewDrawerActivity.class);
                        intent.putExtra("id",exp.getId());
                        ActivityViewDrawerActivity.this.startActivity(intent);
					}else{
						Intent intent = new Intent(ActivityViewDrawerActivity.this,ActivityViewDrawerActivity.class);
						intent.putExtra("id",listaActividades.get(position -1).getId());
						ActivityViewDrawerActivity.this.startActivity(intent);
					}
					
					
					
				}
			});
			
			builder.setView(lista);
			builder.setTitle(getResources().getString(R.string.estructuraCurso));
			dialog = builder.create();
			
			dialog.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	public void onBackPressed() {

		compruebaSalida();
		
	}

	private void compruebaSalida() {
		final DescriptionFragmentActivity fragment = (DescriptionFragmentActivity)getSupportFragmentManager().findFragmentByTag("PORTADA");
		 if (fragment != null && fragment.isVisible()) {
			

			 if(!fragment.hayCambios())
					finish();
				 else {
					 AlertDialog.Builder alert = new AlertDialog.Builder(ActivityViewDrawerActivity.this);
						alert.setTitle(getResources().getString(R.string.aviso));
						alert.setMessage(getResources().getString(R.string.hayCambios));
						alert.setPositiveButton(getResources().getString(R.string.save),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialogConfirm,
											int which) {
										fragment.botonSalvar();
										finish();
										
									}
								});

						alert.setNegativeButton(getResources().getString(R.string.descartar),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										finish();
										
									}
								});

						alert.show();
				}
			 			 
			}
		 else{
			 onNavigationDrawerItemSelected(DESCRIPTION);
		 }
	}
	

}
