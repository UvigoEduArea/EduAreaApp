package area.experiencias.tfg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.services.Services;
import area.experiencias.tfg.R;


public class CommentActivity extends ActionBarActivity {

	
	
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
						cargaComentarios(false); 
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
						cancelarSinc.setVisible(true);
						
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
	
	
	
    String model,title;
    int id,type;

    private static SharedPreferences preferences;
    private static CommentListAdapter adapter;
    private List<Comment> commentList;
    public static CommentListInterface store = new CommentList<Comment>();

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault());

    // Refresh menu item
    private MenuItem refreshMenuItem,downloading,synchonize,cancelarSinc;

    private Sincronizacion sinc;
    EditText commentText;

    TextView avisoModoLocal;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
			
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        
        model = intent.getExtras().getString("model");
        id = intent.getExtras().getInt("id");
        title = intent.getExtras().getString("title");
        //type = intent.getExtras().getInt("type");

        ActionBar actionBar = getSupportActionBar();
        if (title != null) actionBar.setTitle(title);
        else actionBar.setTitle(getString(R.string.comments));
        //actionBar.setSubtitle(type);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preferences = getSharedPreferences("data",MODE_PRIVATE);

        avisoModoLocal = (TextView) findViewById(R.id.modoLocal);
        
        ListView listView = (ListView) findViewById(R.id.commentList);
        adapter = new CommentListAdapter(this,CommentActivity.store.getList());
        listView.setAdapter(adapter);

        commentText = (EditText) findViewById(R.id.editComment);
        Button sendComment = (Button) findViewById(R.id.sendComment);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentText.getText().toString();
                commentText.setText("");
                if(comment.length() > 0) {
                    //new SetCommentTask(CommentActivity.this, comment).execute();
                	
                	Comment comentario = new Comment();
                	comentario.setModel(model);
                	comentario.setModelId(id);
                	comentario.setComment(comment);
                	comentario.setDate(new Date());
                	WebServiceServices.Web().setCommentAsync(comentario, new IAction<Boolean>() {

						@Override
						public void Do(Boolean param) {
							// TODO Auto-generated method stub
							//if(param)
								cargaComentarios(true);
						}
					});
                }
            }
        });
        
        
    	} catch (Exception e) {
			Intent intent = new Intent(CommentActivity.this, ExperiencesActivity.class);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
		}
        
    }

    @Override
    protected void onStart() {
    	sinc = new Sincronizacion();
        sinc.iniciar();
        super.onStart();        
        if(commentList == null || commentList.isEmpty()){
        	cargaComentarios(true);
        }
    }

    
    
    @Override
    protected void onStop() {
    	sinc.parar();
    	sinc = null;
    	super.onStop();
    }
    
	private void cargaComentarios(boolean prog) {
		if(prog)
			Services.Utilidades().preparaProgress(CommentActivity.this);
		Services.Utilidades().actualizaProgress(20);
		
		store.deleteList();
		
		Services.Utilidades().actualizaProgress(60);
      
		WebServiceServices.Web().getComents(id, 20,new IAction<List<Comment>>() {
					@Override
					public void Do(List<Comment> comentarios) {
						Services.Utilidades().actualizaProgress(80);
							commentList = comentarios;
							for(Comment comentario : comentarios){
								store.saveComment(comentario);
							}
						if (refreshMenuItem != null) {
							refreshMenuItem.collapseActionView();
				            // remove the progress bar view
				            refreshMenuItem.setActionView(null);
				        }	
						Services.Utilidades().actualizaProgress(100);
						adapter.notifyDataSetChanged();
						Services.Utilidades().finalizaProgress();
					}
				});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        downloading = menu.findItem(R.id.downloading);
        synchonize = menu.findItem(R.id.synchronize);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
            	cargaComentarios(true);
                return true;
            case android.R.id.home:
                onBackPressed();
            	return true;
            case R.id.action_logout:
            	ApplicationService.setPerfil(null);
            	Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
            	    Toast.makeText(CommentActivity.this, "No hay ningún cliente de correco configurado.", Toast.LENGTH_SHORT).show();
            	}
            	return true;
            case R.id.acercaDe:
            	Intent acercaDe = new Intent(CommentActivity.this, AcercaDe.class);
            	startActivity(acercaDe);
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    
    
    private void forzarSinc() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(CommentActivity.this);
		alert.setTitle(getResources().getString(R.string.aviso));
		alert.setMessage(getResources().getString(R.string.avisoForzarSinc));
		alert.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialogConfirm, int which) {


				final ProgressDialog pd = new ProgressDialog(CommentActivity.this);
				pd.setMessage(getResources().getString(R.string.forzandoSinc));
				pd.setCancelable(false);
				pd.show();		    	
				try {
					WebServiceServices.Sincronizador().parar();
            		LocalStorageServices.DatabaseService().deleteSincronizacion();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	cargaComentarios(true);

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
    
    
    

}
