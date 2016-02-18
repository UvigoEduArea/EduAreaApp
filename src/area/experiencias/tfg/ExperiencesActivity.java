package area.experiencias.tfg;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.PerfilModel;
import area.domain.services.Services;

import com.nostra13.universalimageloader.core.ImageLoader;
import area.experiencias.tfg.R;

public class ExperiencesActivity extends ActionBarActivity {

	
	
	private class Sincronizacion extends CompruebaSincronizacion{
		int count = 0;
		boolean isUp = true;
		
		@Override
		public void avisar(final boolean desincronizado) {
			if (synchonize == null || adapter == null) return;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(count != LocalStorageServices.DatabaseService().getCountSinc()){
						count = LocalStorageServices.DatabaseService().getCountSinc();
						 adapter.notifyDataSetChanged();
						 
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
			runOnUiThread(new Runnable() {
				
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
			runOnUiThread(new Runnable() {
				
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
		
		@Override
		public void updated(final boolean isUpdated){
			if (adapter == null) return;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(isUpdated != isUp){
						isUp = isUpdated;
						adapter.notifyDataSetChanged();
					}
				}
			});
			
		}
	}
	
	
	
	private ExperienceListAdapter adapter;
    private List<Experience> experienceList = new ArrayList<Experience>();
    public  ExperienceListInterface store = new ExperienceList<Experience>();
    
    private ArrayList<Experience> lista = new ArrayList<Experience>();
    
    ListView lvExperiencias;
	private MenuItem synchonize;
	private MenuItem downloading,cancelarSinc, editar, cancelar;
	private Sincronizacion sinc;
	boolean dialogoIdioma;
	private int state = 0;
	TextView avisoModoLocal;
	
	public static boolean editionMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences_list);
        setTitle(getString(R.string.app_name));
        avisoModoLocal = (TextView) findViewById(R.id.modoLocal);
        adapter = new ExperienceListAdapter(ExperiencesActivity.this,lista);
        lvExperiencias = (ListView) findViewById(R.id.list);
        lvExperiencias.setAdapter(adapter);    
        onListItemClick();
        WebServiceServices.Sincronizador().iniciar();
    }

    @Override
    protected void onResume() {
    	 sinc = new Sincronizacion();
         sinc.iniciar();
         super.onResume();
         
         if (experienceList.isEmpty()) {
        	 obtenerExperiencias(state != 0 ? state : Experience.ALL, true);
 		}else{
 			obtenerExperiencias(state != 0 ? state : Experience.ALL, false);
 		}
         
    }
    
    @Override
    protected void onStop() {
    	sinc.parar();
    	sinc = null;
    	super.onStop();
    }

	private void obtenerExperiencias(int estado, final boolean progress) {
		if (progress)
			Services.Utilidades().preparaProgress(ExperiencesActivity.this);
		
		Services.Utilidades().actualizaProgress(20);

		WebServiceServices.Web().getExperiencesIds(estado,new IAction<String>() {
					@Override
					public void Do(String param) {
						Services.Utilidades().actualizaProgress(60);
						if(!param.equals("")){
							WebServiceServices.Web().getWholeViewExperiences(param, !progress, new IAction<List<Experience>>() {
										@Override
										public void Do(List<Experience> experiencias) {
												try{
													lista.clear();
													Services.Utilidades().actualizaProgress(80);
													experienceList = experiencias;
													for (Experience experience : experiencias) {
														lista.add(experience);
													}
													adapter.notifyDataSetChanged();
													Services.Utilidades().finalizaProgress();
												}catch(Exception e){
													e.printStackTrace();	
												}
	
										}
	
									});
						}else{
							lista.clear();
							adapter.notifyDataSetChanged();
							Services.Utilidades().finalizaProgress();
							Toast.makeText(ExperiencesActivity.this, getResources().getString(R.string.noHayCursos), Toast.LENGTH_LONG).show();
						}
					}
					
				});
	}

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    
    private void onListItemClick(){
    	lvExperiencias.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(ExperiencesActivity.this, ExperienceViewDrawerActivity.class);
                intent.putExtra("id",experienceList.get(position).getId());
				
		        startActivity(intent);
				
			}
    		
		});
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.experiences, menu);
        synchonize = menu.findItem(R.id.synchronize);
        downloading = menu.findItem(R.id.downloading);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        editar = menu.findItem(R.id.edit);
        cancelar = menu.findItem(R.id.action_cancel);
        //if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
        	menu.findItem(R.id.action_all_experiences).setVisible(false);
        	menu.findItem(R.id.action_beingPrepared).setVisible(false);
        	menu.findItem(R.id.action_inProgress).setVisible(false);
        	menu.findItem(R.id.action_finished).setVisible(false);
        	menu.findItem(R.id.addExperience).setVisible(true);
       // }
        	if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
        		editar.setVisible(false);
        	}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_all_experiences:
                state = Experience.ALL;
                obtenerExperiencias(Experience.ALL, true);
                return true;
            case R.id.action_beingPrepared:
                state = Experience.BEING_PREPARED;
                obtenerExperiencias(Experience.BEING_PREPARED, true);
                return true;
            case R.id.action_inProgress:
                state = Experience.IN_PROGRESS;
                obtenerExperiencias(Experience.IN_PROGRESS, true);
                return true;
            case R.id.action_finished:
                state = Experience.FINISHED;
                obtenerExperiencias(Experience.FINISHED, true);
                return true;
            case R.id.action_logout:
            	try {
            		
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	ApplicationService.setPerfil(null);
            	Intent intent = new Intent(ExperiencesActivity.this, LoginActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.sincronizar:
            	sinc.parar();
            	sinc.iniciar();
            	return true;
            case R.id.addExperience:
            	if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
            		dialogoAddExperience();
            	}else
            	//dialogoAddExperience();
            	addNewExperience();
            	return true;
            case R.id.select_language:
            	dialogoIdioma();
            	return true;
            case R.id.forzar_sinc:
            	
            	forzarSinc();
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
            	    Toast.makeText(ExperiencesActivity.this, "No hay ningún cliente de correco configurado.", Toast.LENGTH_SHORT).show();
            	}
            	return true;
            case R.id.action_refresh:
            	obtenerExperiencias(state != 0 ? state : Experience.ALL, true);
            	
            	return true;
            	
            case R.id.acercaDe:
            	Intent acercaDe = new Intent(ExperiencesActivity.this, AcercaDe.class);
            	startActivity(acercaDe);
            	
            	return true;
            case R.id.edit:
            	editar.setVisible(false);
         	   	cancelar.setVisible(true);
         	   	editionMode = true;
         	   	adapter.notifyDataSetInvalidated();
         	   	adapter.notifyDataSetChanged();
            	return true;
            	
            case R.id.action_cancel:
            	cancelar.setVisible(false);
            	editar.setVisible(true);
            	editionMode = false;
            	adapter.notifyDataSetInvalidated();
         	   	adapter.notifyDataSetChanged();
            	return true;
            	
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewExperience() {
		WebServiceServices.Web().addExperienceAsync(new IAction<Integer>() {

			@Override
			public void Do(Integer param) {
				try {
					Intent intent = new Intent(ExperiencesActivity.this,ExperienceViewDrawerActivity.class);
					intent.putExtra("id",param);
                    startActivity(intent);
			    	
					//progress.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}
    
    private void dialogoAddExperience() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(ExperiencesActivity.this);
		alert.setTitle(getResources().getString(R.string.add_experience));
		
		final EditText input = new EditText(ExperiencesActivity.this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(getResources().getString(R.string.introduce_codigo));
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final ProgressDialog pd = new ProgressDialog(ExperiencesActivity.this);
				pd.setMessage(getResources().getString(R.string.enviando_codigo));
				pd.setCancelable(false);
				pd.show();
				WebServiceServices.Web().addExperience(input.getText().toString(), new IAction<Boolean>() {

					@Override
					public void Do(Boolean param) {
						if (!param)
							Toast.makeText(ExperiencesActivity.this, getResources().getString(R.string.codigo_no_valido) ,Toast.LENGTH_SHORT).show();
						if (pd.isShowing()) {
							pd.dismiss();
							obtenerExperiencias(Experience.ALL, true);
						}
					}
				});
				
			}
		});
		alert.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		alert.show();
		
	}

	private void forzarSinc() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(ExperiencesActivity.this);
		alert.setTitle(getResources().getString(R.string.aviso));
		alert.setMessage(getResources().getString(R.string.avisoForzarSinc));
		alert.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialogConfirm, int which) {


				final ProgressDialog pd = new ProgressDialog(ExperiencesActivity.this);
				pd.setMessage(getResources().getString(R.string.forzandoSinc));
				pd.setCancelable(false);
				pd.show();		    	
				try {
					WebServiceServices.Sincronizador().parar();
            		LocalStorageServices.DatabaseService().deleteSincronizacion();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	obtenerExperiencias(Experience.ALL, true);

				dialogConfirm.dismiss();
				pd.dismiss();
			}
		});

		alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert.show();
    
    }

	private void dialogoIdioma() {
    	dialogoIdioma = true;
    	final Dialog dialog = new Dialog(ExperiencesActivity.this);
    	dialog.setContentView(R.layout.select_language_dialog);
    	dialog.setTitle(getResources().getString(R.string.seleccionaIdioma));
    	
    	Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerIdioma);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.idiomas, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(adapter);
    	
    	
    	switch (ApplicationService.get_idioma()) {
		case "/gl/":
			spinner.setSelection(0);
			break;
		case "/es/":
			spinner.setSelection(1);
			break;
		case "/pt/":
			spinner.setSelection(2);
			break;
		case "/en/":
			spinner.setSelection(3);
			break;
		default:
			spinner.setSelection(0);
			break;
		}
    	
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (!dialogoIdioma){
					switch (pos) {
						case 0:
							ApplicationService.set_idioma("/gl/");
							obtenerExperiencias(Experience.ALL, true);
							break;
						case 1:
							ApplicationService.set_idioma("/es/");
							obtenerExperiencias(Experience.ALL, true);
							break;
						case 2:
							ApplicationService.set_idioma("/pt/");
							obtenerExperiencias(Experience.ALL, true);
							break;
						case 3:
							ApplicationService.set_idioma("/en/");
							obtenerExperiencias(Experience.ALL, true);
							break;
							
						default:
							ApplicationService.set_idioma("/gl/");
							obtenerExperiencias(Experience.ALL, true);
							break;
						}
					dialog.dismiss();
				}
				dialogoIdioma = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				ApplicationService.set_idioma("/gl/");
			}
		});
    	
		dialog.show();
	}

	public void updateUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}