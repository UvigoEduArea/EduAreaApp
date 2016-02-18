package area.experiencias.tfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.app.ActionBar;

//import android.app.Fragment;
//import android.app.FragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.support.v4.widget.DrawerLayout;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.PerfilModel;
import area.domain.services.Services;
//import area.experiencias.tfg.ExperienceViewActivity.DescriptionFragment;
import area.experiencias.tfg.R;

public class ExperienceViewDrawerActivity extends FragmentActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	
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
			if (ExperienceViewDrawerActivity.this == null) return;
			ExperienceViewDrawerActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (descargando){
						enLocal.setVisible(false);
						//downloading.setVisible(!downloading.isVisible());
					}
					else{
						if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(_expId) ){
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
			if (ExperienceViewDrawerActivity.this == null) return;
			ExperienceViewDrawerActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					
//					if(isUpdated != isUp){
//						isUp = isUpdated;
					if(isUpdated){
						//int id = LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_actiId);
												
						if(LocalStorageServices.DatabaseService().getExperienceIsUpdated(_expId) && LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(_expId)){
							 enLocal.setVisible(true);
							 //listaActividades.setVisible(true);
						 }else{
							enLocal.setVisible(false);
							if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(_expId) ){
								 WebServiceServices.Download().downloadActivity(_expId);
							 }
						 }
						
						
					}
				}
			});
			
		}
		
	}
	
	
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	
	private MenuItem refreshMenuItem;
    private MenuItem synchonize;
    private MenuItem downloading;
    private MenuItem addActivity;
    private MenuItem observaciones;
    private MenuItem addEntrega;
    private MenuItem editionMode, viewMode, save, cancel;
	
	/**
     * Activity Sections
     */
    private static final int DESCRIPTION 					= 0;
    //private static final int DETALLES						= 1;
    private static final int ACTIVITY_SEQUENCE				= 1;
    private static final int RECURSOS						= 2;
    private static final int SITIOS							= 3;
    private static final int ENTREGAS						= 4;
    

    private static final int OBSERVACIONES					= 6;
//    private static final int CODE							= 8;
//    private static final int DESCARGAR_A_LOCAL			= 9;
    private static final int SALIR							= 7;
    
    
    
    private static final int DESCRIPTION_STUDENT			= 0;
    private static final int ACTIVITY_SEQUENCE_STUDENT		= 1;
    private static final int RECURSOS_STUDENT				= 2;
    private static final int SITIOS_STUDENT					= 3;
    private static final int ENTREGAS_STUDENT				= 4;
   
    
    private static final int OBSERVACIONES_STUDENT			= 6;
    //private static final int DESCARGAR_A_LOCAL_STUDENT		= 7;
    private static final int SALIR_STUDENT					= 7;
    
    
    private ActivitySequenceFragment activitySequenceFragment;
    private DetallesFragment detallesFragment;
    protected static ObservacionesFragment observacionesFragment;
    private RecursosFragment recursosFragment;
    private EntregasFragment entregasFragment;
	
	private int _expId;
	
	private MenuItem enLocal;
	
	private Sincronizacion sinc;
	
	private Experience experience;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.experience_view_drawer);
		Intent intent = getIntent();
		_expId = intent.getExtras().getInt("id");
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
			experience = LocalStorageServices.DatabaseService().getExpirience(_expId);
		
        
        ActionBar actionBar = getActionBar();
        
//        if(experience.getName() != null) actionBar.setTitle(getString(R.string.cursoTitulo) + " " + experience.getName());
//        else actionBar.setTitle(getString(R.string.cursoTitulo) + " " + getString(R.string.title));
        
        if(experience.getName() != null) {
        	if(experience.getDefinition().equals(""))
        		actionBar.setTitle(experience.getName());
        	else
        		actionBar.setTitle(experience.getDefinition() + ": " + experience.getName());
        }
        else actionBar.setTitle(getString(R.string.title));
        	
        
		
		} catch (Exception e1) {
			Intent intent = new Intent(ExperienceViewDrawerActivity.this, ExperiencesActivity.class);
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
		_expId = i.getExtras().getInt("id");
		FragmentManager fragmentManager = getSupportFragmentManager();

		
		try {
			if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
				switch (position){
		        case DESCRIPTION_STUDENT:
		            fragmentManager
					.beginTransaction()
					.replace(R.id.container,DescriptionFragmentExperience.newInstance(_expId),"PORTADA").commit();
		            break;
		        case ACTIVITY_SEQUENCE_STUDENT:
		            fragmentManager
					.beginTransaction()
					.replace(R.id.container,ActivitySequenceFragment.newInstance(_expId,"lessons_plans")).commit();
		            break;
		        case RECURSOS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_expId, "lessons_plans", false)).commit();
		        	break;
		        case SITIOS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,RecursosFragment.newInstance(_expId,"lessons_plans", true)).commit();
		        	break;
		        case ENTREGAS_STUDENT:
		        	fragmentManager
					.beginTransaction()
					.replace(R.id.container,EntregasFragment.newInstance(_expId,"lesson_plans")).commit();
		        	break;
		        case OBSERVACIONES_STUDENT:
		        	observacionesFragment = ObservacionesFragment.newInstance(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_expId), "lesson_plans");
		        	fragmentManager.beginTransaction().replace(R.id.container,observacionesFragment).commit();
		        	
		        	break;
		        case SALIR_STUDENT:
		        	compruebaSalida();
		        	break;
		    }
			}else{
				switch (position){
			        case DESCRIPTION:
			            fragmentManager
						.beginTransaction()
						.replace(R.id.container,DescriptionFragmentExperience.newInstance(_expId),"PORTADA").commit();
			            break;
			        case ACTIVITY_SEQUENCE:
			            fragmentManager
						.beginTransaction()
						.replace(R.id.container,ActivitySequenceFragment.newInstance(_expId, "lessons_plans")).commit();
			            break;
			        case OBSERVACIONES:
			        	observacionesFragment = ObservacionesFragment.newInstance(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_expId), "lesson_plans");
			        	fragmentManager.beginTransaction().replace(R.id.container,observacionesFragment).commit();
			        	
			        	break;
			        case RECURSOS:
			        	fragmentManager
						.beginTransaction()
						.replace(R.id.container,RecursosFragment.newInstance(_expId,"lessons_plans", false)).commit();
			        	break;
			        case SITIOS:
			        	fragmentManager
						.beginTransaction()
						.replace(R.id.container,RecursosFragment.newInstance(_expId,"lessons_plans", true)).commit();
			        	break;
			        case ENTREGAS:
			        	fragmentManager
						.beginTransaction()
						.replace(R.id.container,EntregasFragment.newInstance(_expId,"lesson_plans")).commit();
			        	break;
			        case SALIR:
			        	compruebaSalida();
			        	break;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		
	}



	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	

	
	@Override
	public void onBackPressed() {
		try {
			compruebaSalida();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		enLocal = menu.add(0, -1, 100, "En local");
		enLocal.setIcon(R.drawable.cloud_download2);
		enLocal.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		enLocal.setVisible(false);
		
		MenuItem descargar = menu.add(0,-2,100,getResources().getString(R.string.descarga_a_local));
		descargar.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		descargar.setVisible(true);
		
		MenuItem codigo = menu.add(0,-3,100,getResources().getString(R.string.codigo_a_compartir));
		codigo.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		codigo.setVisible(true);
		
		//downloading = menu.findItem(R.id.downloading);
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
        	 
         case -2:
        	 Services.Utilidades().descargar(ExperienceViewDrawerActivity.this, _expId);
        	 return true;
         case -3:
        	 Services.Utilidades().getCode(ExperienceViewDrawerActivity.this, _expId);
        	 return true;
        	 
         }

		return super.onOptionsItemSelected(item);
	}
	

	
	private void listaAvtividades() {
		final Dialog dialog = new Dialog(ExperienceViewDrawerActivity.this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceViewDrawerActivity.this);
		ListView lista = new ListView(ExperienceViewDrawerActivity.this);
		List<String> nombresActividades = new ArrayList<String>();
		try {
			final List<MiniActivity> listaActividades = LocalStorageServices.DatabaseService().getMiniActivities(LocalStorageServices.DatabaseService().getActivityIds(_expId));
			
			for (int i=0; i<listaActividades.size(); i++){
				if(i == 0){
					nombresActividades.add(experience.getDefinition() + ": " + experience.getName());
				}
				nombresActividades.add(i+1 + ". " +listaActividades.get(i).getDefinition() +": " +listaActividades.get(i).getName());
			}
						
			lista.setAdapter(new ArrayAdapter<>(ExperienceViewDrawerActivity.this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, nombresActividades));
			
			
			builder.setView(lista);
			//builder.setTitle(getResources().getString(R.string.cursoTitulo) + " " + experience.getName());
			builder.setTitle(getResources().getString(R.string.listaActividades));
			//dialog = builder.create();
			
			//dialog.show();
			
			builder.show();
			
			lista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(position == 0){
						//dialog.dismiss();
						Intent intent = new Intent(ExperienceViewDrawerActivity.this, ExperienceViewDrawerActivity.class);
                        intent.putExtra("id",experience.getId());
                        ExperienceViewDrawerActivity.this.startActivity(intent);
					}else{
						Intent intent = new Intent(ExperienceViewDrawerActivity.this,ActivityViewDrawerActivity.class);
						intent.putExtra("id",listaActividades.get(position - 1).getId());
						ExperienceViewDrawerActivity.this.startActivity(intent);
					}
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}
	
	
	private void compruebaSalida() {
		final DescriptionFragmentExperience fragment = (DescriptionFragmentExperience)getSupportFragmentManager().findFragmentByTag("PORTADA");
		 if (fragment != null && fragment.isVisible()) {
			 
			 if(!fragment.hayCambios())
				finish();
			 else {
				 AlertDialog.Builder alert = new AlertDialog.Builder(ExperienceViewDrawerActivity.this);
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
									// TODO Auto-generated method stub
									dialog.dismiss();
									finish();
									
								}
							});

					alert.show();
			}
			}
		 else{
			 getSupportFragmentManager().beginTransaction()
				.replace(R.id.container,DescriptionFragmentExperience.newInstance(_expId),"PORTADA").commit(); 
		 }
	}


}
