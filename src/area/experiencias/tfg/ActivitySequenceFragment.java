package area.experiencias.tfg;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.PerfilModel;
import area.domain.services.Services;
import area.experiencias.tfg.R;

public class ActivitySequenceFragment extends Fragment {
	
	
	
	private class Sincronizacion extends CompruebaSincronizacion{
		int count = 0;
		boolean isUp = true;
		
		@Override
		public void avisar(final boolean desincronizado) {
			if (synchonize == null) return;
			if (getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(count != LocalStorageServices.DatabaseService().getCountSinc()){
						count = LocalStorageServices.DatabaseService().getCountSinc();
						 refeshList();
						 if(count == 0){
							 actualizaExperienciaCompleta();
						 }
						 
					}
					if (desincronizado){
						synchonize.setVisible(true);
					}
					else{
						synchonize.setVisible(false);
						//actualizaExperienciaCompleta();
					}
					
					
				}
			});
			
		}

		@Override
		public void descargando(final boolean descargando) {
			if (downloading == null || enLocal == null) return;
			if (getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (descargando){
						//enLocal.setVisible(false);
						downloading.setVisible(!downloading.isVisible());
					}
					else{
//						if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(_id) ){
//							 enLocal.setVisible(true);
//						 }else{
//							 enLocal.setVisible(false);
//						 }
							 
						downloading.setVisible(false);
					}
				}
			});
		}
		
		@Override
		public void modoLocal(final boolean modoLocal) {
			if (avisoModoLocal == null) return;
			if (getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (modoLocal){
						avisoModoLocal.setVisibility(View.GONE);
						if(LocalStorageServices.DatabaseService().getCountSinc() != 0){
							cancelarSinc.setVisible(true);	
						}
						else{
							cancelarSinc.setVisible(false);
						}
					}
					else{
						cancelarSinc.setVisible(false);
						avisoModoLocal.setVisibility(View.VISIBLE);
					}
				}
			});
		}
				
	}
	
	
	
    ListView lista;
    private ActivityListAdapter adapterList;
    private  MiniActivityListInterface store = new MiniActivityList<MiniActivity>();
    private ProgressBar progress;
    
    private MenuItem synchonize, enLocal, cancelarSinc, editar, cancelar,listaActividades;
    private MenuItem downloading;
    private MenuItem addActivity;
    private MenuItem observaciones;
    
    
    private TextView avisoModoLocal,sinElementos;
	
	private Sincronizacion sinc;
    
	public static boolean editionMode = false;
    
    int _id;
    String _tipo;
    
    public static ActivitySequenceFragment newInstance(int id, String tipo) {
        return new ActivitySequenceFragment(id, tipo);
    }

    public ActivitySequenceFragment(int id, String tipo) {
    	_id = id;
    	_tipo = tipo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_list_activities, container, false);
    	lista = (ListView) rootView.findViewById(R.id.listaActivities);
    	avisoModoLocal = (TextView) rootView.findViewById(R.id.modoLocal);
    	sinElementos = (TextView) rootView.findViewById(R.id.sinElementos);
    	
    	progress = (ProgressBar) rootView.findViewById(R.id.progress);
    	adapterList = new ActivityListAdapter(getActivity(), store.getList());
    	lista.setAdapter(adapterList);    	
    	ActionBar actionBar = getActivity().getActionBar();
        actionBar.setSubtitle(getString(R.string.title_section4));
    	
    	
    	Services.Utilidades().showProgress(getActivity());
    	setHasOptionsMenu(true);
        return rootView;
    }
    
        
    @Override
    public void onResume() {
    	sinc = new Sincronizacion();
    	sinc.iniciar();
    	//actualizaExperienciaCompleta();
    	progress.setVisibility(View.VISIBLE);
    	pideActividades();
    	super.onResume();
    }
    
    @Override
    public void onStop() {
    	sinc.parar();
    	sinc = null;        	
    	super.onStop();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.activity_fragment, menu);
        synchonize = menu.findItem(R.id.synchronize);
        downloading = menu.findItem(R.id.downloading);
        addActivity = menu.findItem(R.id.add_activity);
        observaciones = menu.findItem(R.id.action_documentation_mode);
        listaActividades = menu.findItem(R.id.lista_actividades);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        enLocal = menu.findItem(-1);
        editar = menu.findItem(R.id.action_edition_mode);
        cancelar = menu.findItem(R.id.action_cancel);
        if(LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(_id)){
        	listaActividades.setVisible(true);
        }
        else{
        	listaActividades.setVisible(false);
        }
        if(LocalStorageServices.DatabaseService().getExpirience(_id).getPermiso().equals("viewer")){
        	addActivity.setVisible(false);
        	editar.setVisible(false);
        }else{
        	addActivity.setVisible(true);
        	editar.setVisible(true);
        }
        
//        try {
//			if(ApplicationService.getPerfil().getType() != PerfilModel.STUDENT){
//				addActivity.setVisible(true);
//			}else{
//				editar.setVisible(false);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	 int id = item.getItemId();
         switch (id){
             case R.id.action_documentation_mode:
            	 ExperienceViewDrawerActivity.observacionesFragment = ObservacionesFragment.newInstance(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_id), _tipo);
		        	getFragmentManager().beginTransaction().replace(R.id.container, ExperienceViewDrawerActivity.observacionesFragment).addToBackStack(null).commit();
                 return true;
             case R.id.action_refresh:
                 updateUI();
                 return true;
             case R.id.add_activity:
            	 progress.setVisibility(View.VISIBLE);
             	addActivity();
             	return true;
             case R.id.action_logout:
             	try {
             		
             	} catch (Exception e) {
             		e.printStackTrace();
             	}
             	ApplicationService.setPerfil(null);
             	Intent intent = new Intent(getActivity(), LoginActivity.class);
             	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                 return true;                 
                 
             case R.id.sugerencia:
                	Intent i = new Intent(Intent.ACTION_SEND);
                	i.setType("message/rfc822");
                	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"area.telgalicia@gmail.com"});
                	i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.asuntoSugerencia));
                	i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.textoSugerencia));
                	try {
                	    //startActivity(Intent.createChooser(i, "Send mail..."));
                		startActivity(i);
                	} catch (android.content.ActivityNotFoundException ex) {
                	    Toast.makeText(getActivity(), "No hay ningún cliente de correco configurado.", Toast.LENGTH_SHORT).show();
                	}
                	return true;
                	
               case R.id.forzar_sinc:
               	Services.Utilidades().cancelarSinc(getActivity());
               	return true;
               	
               case R.id.volver:
              	 Services.Utilidades().volverMenuPrincipal(getActivity());
              	 return true;
              	 
              	 
               case R.id.action_edition_mode:
            	   editar.setVisible(false);
            	   cancelar.setVisible(true);
            	   editionMode = true;
            	   //adapterList.notifyDataSetInvalidated();
            	   adapterList.notifyDataSetChanged();
            	   
            	   return true;
               case R.id.action_cancel:
            	   cancelar.setVisible(false);
            	   editar.setVisible(true);
            	   editionMode = false;
            	   //adapterList.notifyDataSetInvalidated();
            	   adapterList.notifyDataSetChanged();
            	   
            	   return true;
              	 
         }
         return super.onOptionsItemSelected(item);
    }
    
    
    private void pideActividades() {
    	    	
    	WebServiceServices.Web().getActivitiesIds(_id, new IAction<String>() {
			@Override
			public void Do(String param) {
				WebServiceServices.Web().getMiniViewActivities(param,_id, new IAction<List<MiniActivity>>() {

					@Override
					public void Do(List<MiniActivity> lista) {
						store.deleteList();
						if(lista.size()==0)
	    					sinElementos.setVisibility(View.VISIBLE);
	    				else
	    					sinElementos.setVisibility(View.GONE);
						
						for (MiniActivity mi : lista){
							store.saveActivity(mi);
						}
						//adapterList.notifyDataSetInvalidated();
						adapterList.notifyDataSetChanged();
						progress.setVisibility(View.GONE);
					}
				});
			}
		});
	}
    
    private void actualizaExperienciaCompleta(){
    	progress.setVisibility(View.VISIBLE);
    	WebServiceServices.Web().getWholeViewExperiences(_id+"", false, new IAction<List<Experience>>() {

			@Override
			public void Do(List<Experience> param) {
				
				pideActividades();
				
			}
		});
    }
    
    
  private void addActivity() {
		WebServiceServices.Web().addActivityAsync(_id, new IAction<Integer>() {

			@Override
			public void Do(Integer param) {
				try {
					Intent intent = new Intent(getActivity(),ActivityViewDrawerActivity.class);
			    	intent.putExtra("activity", LocalStorageServices.DatabaseService().getMiniActivity(LocalStorageServices.DatabaseService().getActivityIdFromLocalId(param)));
					intent.putExtra("position", 0);
					intent.putExtra("id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(param));
					startActivity(intent);
					progress.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}
    
    
    
    protected void updateUI(){
    	progress.setVisibility(View.VISIBLE);
    	actualizaExperienciaCompleta();
    }
    
    protected void refeshList(){
    	adapterList.notifyDataSetChanged();
    }
    
    
}