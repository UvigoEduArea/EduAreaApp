package area.experiencias.tfg;

import java.util.ArrayList;
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
import area.domain.modelo.RecursoModel;
import area.domain.services.Services;
import area.experiencias.tfg.R;

public  class RecursosFragment extends Fragment {
	
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
					}
					if (desincronizado)
						synchonize.setVisible(true);
					else
						synchonize.setVisible(false);
				}
			});
		}

		@Override
		public void descargando(final boolean descargando) {
			if (downloading == null) return;
			if (enLocal == null) return;
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
			if (avisoModoLocal == null || cancelarSinc == null) return;
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
						avisoModoLocal.setVisibility(View.VISIBLE);
						cancelarSinc.setVisible(false);
					}
				}
			});
		}
	}
	
	
    ListView lista;
   
    
    
    private MenuItem synchonize, enLocal, editar, cancelar, mas, menos, mapa;
    private MenuItem downloading;
    private MenuItem observaciones;
    
    private TextView avisoModoLocal,sinElementos;
	
	private Sincronizacion sinc;
    
    
    private RecursosListAdapter adapterList;
    private ArrayList<RecursoModel> recursosList = new ArrayList<RecursoModel>();
    private ProgressBar progress;
    private int _id;
    private String _tipo;
    private boolean _sitios = false;

    public static boolean editionMode = false;
    private boolean all = true;

	private MenuItem cancelarSinc,listaActividades;
    
    public static RecursosFragment newInstance(int id, String tipo, boolean sitios) {
        return new RecursosFragment(id, tipo, sitios);
    }

    public RecursosFragment(int id, String tipo, boolean sitios) {
    	_id = id;
    	_tipo = tipo;
    	_sitios = sitios;
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
    	progress = (ProgressBar) rootView.findViewById(R.id.progress);
    	avisoModoLocal = (TextView) rootView.findViewById(R.id.modoLocal);
    	sinElementos = (TextView) rootView.findViewById(R.id.sinElementos);
    	adapterList = new RecursosListAdapter(getActivity(), recursosList);
    	lista.setAdapter(adapterList);
    	
    	ActionBar actionBar = getActivity().getActionBar();
    	if(_sitios)
    		actionBar.setSubtitle(getString(R.string.sitios));
    	else
    		actionBar.setSubtitle(getString(R.string.recursos));
    	
    	//actualizaExperienciaCompleta();
    	pideRecursos();
    	setHasOptionsMenu(true);
        return rootView;
    }
    
    @Override
    public void onStart() {
    	sinc = new Sincronizacion();
    	sinc.iniciar();
    	super.onStart();
    }
    
    @Override
    public void onStop() {
    	sinc.parar();
    	sinc = null;        	
    	super.onStop();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.recursos_fragment, menu);
        synchonize = menu.findItem(R.id.synchronize);
        downloading = menu.findItem(R.id.downloading);
        observaciones = menu.findItem(R.id.action_documentation_mode);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        enLocal = menu.findItem(-1);
        editar = menu.findItem(R.id.action_edition_mode);
        cancelar = menu.findItem(R.id.action_cancel);
        listaActividades = menu.findItem(R.id.lista_actividades);
        mas = menu.findItem(R.id.expandir);
        menos = menu.findItem(R.id.contraer);
        menos.setVisible(true);
        mapa = menu.findItem(R.id.mapa);
        
        int id = !_tipo.equals("activities") ? _id : LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_id);
        if(LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(id)){
        	listaActividades.setVisible(true);
        }
        else{
        	listaActividades.setVisible(false);
        }
        if(!_sitios)
        	mapa.setVisible(false);
        
//        if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
//        	editar.setVisible(false);
//        }
        
        
        Experience exp = LocalStorageServices.DatabaseService().getExpirience(_id);
        MiniActivity act = LocalStorageServices.DatabaseService().getMiniActivity(_id);
        if(exp != null){
        	if(exp.getPermiso().equals("viewer")){
        		editar.setVisible(false);
        	}else{
        		editar.setVisible(true);
        		}
        }
        if(act != null){
        	if(act.getPermiso() != null && act.getPermiso().equals("viewer")){
        		editar.setVisible(false);
        	}else{
        		editar.setVisible(true);
        		}
        }
        
        
        
        
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	 int id = item.getItemId();
         switch (id){
         
             case R.id.action_documentation_mode:
            	 ExperienceViewDrawerActivity.observacionesFragment = _tipo.equals("lessons_plans") ?
            			 ObservacionesFragment.newInstance(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_id), _tipo) :
            				 ObservacionesFragment.newInstance(_id, _tipo);
            	getFragmentManager().beginTransaction().replace(R.id.container, ExperienceViewDrawerActivity.observacionesFragment).addToBackStack(null).commit();
                 return true;
             case R.id.action_refresh:
            	 updateUI();
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
             	 
             	 
              case R.id.mapa:
  	            Intent mapa = new Intent(getActivity(), MapActivity.class);
  	            mapa.putExtra("id",getRecursosIds());
  	            //mapa.putExtra("id",LocalStorageServices.DatabaseService().getRecursosIds(_id));
  	            startActivity(mapa);
  	            return true;
  	        
              case R.id.action_edition_mode:
           	   editar.setVisible(false);
           	   cancelar.setVisible(true);
           	   editionMode = true;
           	   adapterList.notifyDataSetInvalidated();
           	   adapterList.notifyDataSetChanged();
           	   
           	   return true;
              case R.id.action_cancel:
           	   cancelar.setVisible(false);
           	   editar.setVisible(true);
           	   editionMode = false;
           	   adapterList.notifyDataSetInvalidated();
           	   adapterList.notifyDataSetChanged();
           	   return true;
           	   
           	   
              case R.id.expandir:
            	  all = true;
            	  menos.setVisible(true);
            	  mas.setVisible(false);
            	  pideRecursos();
            	  return true;
              case R.id.contraer:
            	  all = false;
            	  menos.setVisible(false);
            	  mas.setVisible(true);
            	  pideRecursos();
            	  return true;
           	   
         }
         return super.onOptionsItemSelected(item);
    }
    
    private void pideRecursos() {
    	WebServiceServices.Web().getRecursosView(_id, all, _sitios, new IAction<List<RecursoModel>>() {
    		@Override
    		public void Do(List<RecursoModel> lista) {
    			recursosList.clear();
    			
    			for (RecursoModel rec : lista){
//    				if(_sitios && rec.getTipo().equals("Event")){
//    					recursosList.add(rec);
//    				}
//    				if(!_sitios && !rec.getTipo().equals("Event")){
//    					recursosList.add(rec);
//    				}
    				recursosList.add(rec);
    			}
    			if(recursosList.size()==0)
    				sinElementos.setVisibility(View.VISIBLE);
    			else
    				sinElementos.setVisibility(View.GONE);
    			
    			adapterList.notifyDataSetInvalidated();
    			adapterList.notifyDataSetChanged();
    			progress.setVisibility(View.GONE);
    		}
    	});
	}
    private void updateUI(){
    	progress.setVisibility(View.VISIBLE);
    	actualizaExperienciaCompleta();
    }
    
    private void actualizaExperienciaCompleta(){
    	WebServiceServices.Web().getWholeViewExperiences(_id+"", false, new IAction<List<Experience>>() {
			@Override
			public void Do(List<Experience> param) {
				pideRecursos();
			}
		});
    }
    private String getRecursosIds(){
    	String ids = "";
    	for (RecursoModel r : recursosList){
    		ids = ids + "," + r.getId();
    	}
    	return ids;
    }
    private void refeshList(){
    	adapterList.notifyDataSetChanged();
    }
    
}
