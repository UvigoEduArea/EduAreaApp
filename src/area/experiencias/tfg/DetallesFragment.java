package area.experiencias.tfg;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.domain.modelo.DetallesModel;
import area.domain.modelo.PerfilModel;
import area.domain.services.Services;
import area.experiencias.tfg.R;

public class DetallesFragment extends Fragment {
	
	
	
	private class Sincronizacion extends CompruebaSincronizacion{
		int count = 0;
		
		@Override
		public void avisar(final boolean desincronizado) {
			if (synchonize == null) return;
			if (getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(count != LocalStorageServices.DatabaseService().getCountSinc()){
						count = LocalStorageServices.DatabaseService().getCountSinc();
						 
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
			if (getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (descargando)
						downloading.setVisible(!downloading.isVisible());
					else
						downloading.setVisible(false);
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
	
	
	
	
	
	LinearLayout listaDetalles;
	
	
	
	private MenuItem refreshMenuItem;
    private MenuItem synchonize;
    private MenuItem downloading;
    private MenuItem addActivity;
    private MenuItem observaciones;
    private MenuItem addEntrega;
    private MenuItem editionMode, viewMode, save, cancel;
	
    private TextView avisoModoLocal,sinElementos;
	
	private Sincronizacion sinc;
	
	int _id;
	String _tipo;



	private MenuItem cancelarSinc;
	
	public static DetallesFragment newInstance(int id, String tipo){
		return new DetallesFragment(id, tipo);
	}
	public DetallesFragment(int id, String tipo){
		_id = id;
		_tipo = tipo;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lista_detalles, container, false);
		listaDetalles = (LinearLayout) rootView.findViewById(R.id.listaDetalles);
		avisoModoLocal = (TextView) rootView.findViewById(R.id.modoLocal);
		sinElementos = (TextView) rootView.findViewById(R.id.sinElementos);
		List<DetallesModel> detalles;
		try {
			detalles = LocalStorageServices.DatabaseService().getDetalles(_id);
			if(detalles.size()==0)
				sinElementos.setVisibility(View.VISIBLE);
			else
				sinElementos.setVisibility(View.GONE);
				
			
			for(DetallesModel det : detalles){
				JSONObject box = new JSONObject(det.getDetalles());
				TextView titulo = new TextView( getActivity() );
				titulo.setTextSize(16);
				titulo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				titulo.setText(box.isNull("title") == false ? box.getString("title") : "");
				listaDetalles.addView(titulo);
				
				JSONArray dBoxes;
				if(!box.isNull("data_boxes")){
					dBoxes = box.getJSONArray("data_boxes");
					for(int i = 0; i<dBoxes.length(); i++){
						JSONObject dBox = dBoxes.getJSONObject(i);						
						TextView value = new TextView( getActivity() );
						value.setTextSize(16);
						value.setText(Html.fromHtml(dBox.isNull("value") == false ? dBox.getString("value") : ""));
						value.setBackgroundColor(getResources().getColor(R.color.translucent));
						listaDetalles.addView(value); 
						
					}
				}
			}
			ActionBar actionBar = getActivity().getActionBar();
	        actionBar.setSubtitle(getString(R.string.detalles));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
    	inflater.inflate(R.menu.experience_view, menu);
    	refreshMenuItem = menu.findItem(R.id.action_refresh);
        synchonize = menu.findItem(R.id.synchronize);
        downloading = menu.findItem(R.id.downloading);
        addActivity = menu.findItem(R.id.add_activity);
        observaciones = menu.findItem(R.id.action_documentation_mode);
        addEntrega = menu.findItem(R.id.add_entrega);
        editionMode = menu.findItem(R.id.action_edition_mode);
        viewMode = menu.findItem(R.id.action_view_mode);
        save = menu.findItem(R.id.action_save);
        cancel = menu.findItem(R.id.action_cancel);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        addActivity.setVisible(false);
    	addEntrega.setVisible(false);
    	editionMode.setVisible(false);
    	viewMode.setVisible(false);
    	save.setVisible(false);
    	cancel.setVisible(false);
    	refreshMenuItem.setVisible(false);
    	super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	 int id = item.getItemId();
         switch (id){
             case R.id.action_view_mode:
                 return true;
             case R.id.action_edition_mode:
                 save.setVisible(true);
                 cancel.setVisible(true);
                 editionMode.setVisible(false);
                 return true;
             case R.id.action_documentation_mode:
            	 ExperienceViewDrawerActivity.observacionesFragment = _tipo.equals("lessons_plans") ?
            			 ObservacionesFragment.newInstance(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_id), _tipo) :
            				 ObservacionesFragment.newInstance(_id, _tipo);
		        	getFragmentManager().beginTransaction().replace(R.id.container, ExperienceViewDrawerActivity.observacionesFragment).addToBackStack(null).commit();
                 return true;
             case R.id.action_refresh:
                 return true;
             case android.R.id.home:
            	 return super.onOptionsItemSelected(item);
             case R.id.add_activity:
             	return true;
             case R.id.add_entrega:
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
             case R.id.action_save:
             	return true;
             case R.id.action_cancel:
             	save.setVisible(false);
            	cancel.setVisible(false);
            	editionMode.setVisible(true);
             	return true;
             	
             case R.id.sugerencia:
              	Intent i = new Intent(Intent.ACTION_SEND);
              	i.setType("message/rfc822");
              	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"eduareauvigo@gmail.com"});
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
            
         }
         return super.onOptionsItemSelected(item);
    }	
	
	
	
}
