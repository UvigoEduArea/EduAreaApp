package area.experiencias.tfg;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.implementation.WebService;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.DetallesModel;
import area.domain.modelo.EntregaModel;
import area.domain.modelo.PerfilModel;
import area.domain.modelo.RecordModel;
import area.domain.services.Services;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import area.experiencias.tfg.R;
import eduarea.facedetector.FaceDetector;

public class EntregaViewActivityNew extends ActionBarActivity  {

	
	private class Sincronizacion extends CompruebaSincronizacion{
		int count = 0;
		
		@Override
		public void avisar(final boolean desincronizado) {
			if (synchonize == null) return;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(count != LocalStorageServices.DatabaseService().getCountSinc()){
						count = LocalStorageServices.DatabaseService().getCountSinc();
						 //if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.refeshList();
						 if(observacionesFragment != null && observacionesFragment.isAdded()) observacionesFragment.refeshList();
						 //updateUI();
						 
					}
					if (desincronizado)
						synchonize.setVisible(true);
					else{
						synchonize.setVisible(false);
						if(observacionesFragment != null && observacionesFragment.isAdded()) observacionesFragment.refeshList();
					}
					
					
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
						//if(LocalStorageServices.DatabaseService().getCountSinc() > 0)
						cancelarSinc.setVisible(true);
						
					}
					else{
						avisoModoLocal.setVisibility(View.VISIBLE);
						cancelarSinc.setVisible(false);
					}
				}
			});
		}
	}
	
	
	
    //SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Activity Sections
     */
    private static final int DESCRIPTION 		= 0;
    private static final int ACTIVITY_SEQUENCE	= 1;
    private static final int DETALLES			= 2;
    private static final int OBSERVACIONES		= 1;

    private static final int ANIMATION_DURATION = 400;
    
    /**
     * Edition Modes
     */
    private static final int VIEW = 0;
    private static final int EDITION = 1;
    private static final int DOCUMENTATION = 2;

    private int _mode = 0;
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    //private static Experience experience;
    
    //private MiniActivity activity;
    
    private EntregaModel entrega;

    private MenuItem refreshMenuItem;
    private MenuItem synchonize;
    private MenuItem downloading;
    private MenuItem addActivity;
    private MenuItem observaciones;
    private MenuItem editionMode, viewMode, save, cancel;
    
    private TextView avisoModoLocal;

    private  ActivityListAdapter adapterList;
    //public  MiniActivityListInterface store = new MiniActivityList<MiniActivity>();

    protected DescriptionFragment descriptionFragment;
    private ActivitySequenceFragment activitySequenceFragment;
    private DetallesFragment detallesFragment;
    protected ObservacionesFragment observacionesFragment;
    
    private static Context _context;

	private Sincronizacion sinc;
	private int id;
	
	DescriptionFragment DF = null;

	private MenuItem cancelarSinc;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
			
		
    	super.onCreate(null);
        setContentView(R.layout.new_entrega_activity);
        avisoModoLocal = (TextView) findViewById(R.id.modoLocal);
        _context = this;
        
     // Se recoge el objeto Experiencia y su imagen provenientes de la actividad previa
        Intent intent = getIntent();
        //experience = intent.getParcelableExtra("experience");
        //activity = intent.getParcelableExtra("activity");
        if (savedInstanceState != null)
        	id = savedInstanceState.getInt("id");
        else
        	id = intent.getIntExtra("id", 0);
        if(id < 0){
        	entrega = LocalStorageServices.DatabaseService().getEntrega(LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(id));
        	 //if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(EDITION);
        	}
        else
        	entrega = LocalStorageServices.DatabaseService().getEntrega(id);
        
        
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if(entrega.getTitulo() != null) actionBar.setTitle(getString(R.string.entregaTitulo) + " " + entrega.getTitulo());
        else actionBar.setTitle(getString(R.string.entregaTitulo) + " " + getString(R.string.title));
        //actionBar.setSubtitle(getString(R.string.entrega));
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        
        
        descriptionFragment = new DescriptionFragment().newInstance();
        observacionesFragment = ObservacionesFragment.newInstance(entrega.getId(), "submissions");
        
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.descripcion_fragment, descriptionFragment);
        transaction.add(R.id.observaciones_fragment, observacionesFragment);
        transaction.commit();
        
       
    	} catch (Exception e) {
    		e.printStackTrace();
    		Intent intent = new Intent(EntregaViewActivityNew.this, ExperiencesActivity.class);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
		} 
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putInt("id", id);
    	super.onSaveInstanceState(outState);
    	
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
    public void onBackPressed() {
		if(descriptionFragment != null && descriptionFragment.isAdded()){
			//descriptionFragment.cancelar();	
			descriptionFragment.compruebaCambios();
		}
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entregas, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        synchonize = menu.findItem(R.id.synchronize);
        downloading = menu.findItem(R.id.downloading);
        addActivity = menu.findItem(R.id.add_activity);
        observaciones = menu.findItem(R.id.action_documentation_mode);
        editionMode = menu.findItem(R.id.action_edition_mode);
        viewMode = menu.findItem(R.id.action_view_mode);
        save = menu.findItem(R.id.action_save);
        cancel = menu.findItem(R.id.action_cancel);
        cancelarSinc = menu.findItem(R.id.forzar_sinc);
        
        
//        if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT)
//        	editionMode.setVisible(false);
        
        if(entrega.getPermiso() != null && entrega.getPermiso().equals("viewer")){
        	editionMode.setVisible(false);
        }
        
       if(_mode == EDITION){
    	   save.setVisible(true);
    	   cancel.setVisible(true);
    	   editionMode.setVisible(false);
       }
    	   
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_view_mode:
                if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(VIEW);
                return true;
            case R.id.action_edition_mode:
            	if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(EDITION);
                save.setVisible(true);
                cancel.setVisible(true);
                editionMode.setVisible(false);
                return true;
            case R.id.action_documentation_mode:
            	
            	//if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(DOCUMENTATION);
                return true;
            case R.id.action_refresh:
                if(observacionesFragment != null && observacionesFragment.isAdded()) observacionesFragment.updateRecords();
                return true;
            case android.R.id.home:
                //this.finish();
            	if(descriptionFragment != null && descriptionFragment.isAdded()){
        			//descriptionFragment.cancelar();	
        			descriptionFragment.compruebaCambios();
        		}
                return true;
            case R.id.add_activity:
            	return true;
            case R.id.action_logout:
            	try {
            		//LocalStorageServices.DatabaseService().deletePerfil();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	ApplicationService.setPerfil(null);
            	Intent intent = new Intent(EntregaViewActivityNew.this, LoginActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.action_save:
            	if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.botonSalvar();
            	
            	return true;
            case R.id.action_cancel:
            	if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.cancelar();
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
              	    //startActivity(Intent.createChooser(i, "Send mail..."));
              		startActivity(i);
              	} catch (android.content.ActivityNotFoundException ex) {
              	    Toast.makeText(EntregaViewActivityNew.this, "No hay ningún cliente de correco configurado.", Toast.LENGTH_SHORT).show();
              	}
              	return true;
              	
             case R.id.forzar_sinc:
             	Services.Utilidades().cancelarSinc(EntregaViewActivityNew.this);
             	return true;
             	
             case R.id.volver:
            	 Services.Utilidades().volverMenuPrincipal(EntregaViewActivityNew.this);
            	 return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Fragment para sección de descripción
     */
    public class DescriptionFragment extends Fragment {

        String picturePath;
        private String rutaArchivo;
        private String rutaArchivoOriginal;
        private Uri imageFilePath;
        private static final int SELECT_PICTURE = 0;
        private static final int TAKE_PICTURE = 1;
        private static final int SELECT_PICTURE_RECORD = 2;
        private static final int TAKE_PICTURE_RECORD = 3;
        private static final int SELECT_VIDEO_RECORD = 4;
        private static final int RECORD_VIDEO_RECORD = 5;
        private static final int PICK_FILE_RECORD = 6;
        
        TextView title, description, definition;
        WebView desWeb;
        ImageView image;
        EditText titleEditor, descriptionEditor,definitionEditor;
        Button changeImage;
        //ImageButton save, cancel;
        ProgressBar prog;
        LinearLayout buttonsLayout,buttonsRecords;
        private ImageButton addDocument, addImage, addVideo;
        private ImageButton addPositiveComment, addNegativeComment, addFreeText, addIdea, addSnippet;
        private Uri imageFileUri;
		private ListView listaRecords;
		private GridView gridRecords;
		private RelativeLayout entriesLayout;
		private RecordListAdapter adapterRecords;
		private RecordGridAdapter gridAdapterRecords;
		private ArrayList<RecordModel> recordList = new ArrayList<RecordModel>();
		private ProgressBar progress;
		private boolean isShowing = false;
		private boolean typeList = true;
		private boolean _hayCambios;
        
        public  DescriptionFragment newInstance() {
            return new DescriptionFragment();
        }
        
       public DescriptionFragment() {
		// TODO Auto-generated constructor stub
	}
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_description, container, false);
            prog = (ProgressBar) rootView.findViewById(R.id.cargando);
            prog.setVisibility(View.VISIBLE);
            title = (TextView) rootView.findViewById(R.id.title);
            title.setText(entrega.getTitulo());
            titleEditor = (EditText) rootView.findViewById(R.id.editTitle);
            titleEditor.setText(entrega.getTitulo());
            definition = (TextView) rootView.findViewById(R.id.definition);
            definitionEditor = (EditText) rootView.findViewById(R.id.editDefinition);
           
            if(!entrega.getDefinition().equals("")){
            	definition.setVisibility(View.VISIBLE);
            	definition.setText(entrega.getDefinition());
            	definitionEditor.setText(entrega.getDefinition());
            }else{
            	definition.setVisibility(View.GONE);
            }
            
            desWeb = (WebView) rootView.findViewById(R.id.description);
            desWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			
            desWeb.loadDataWithBaseURL("file:///android_asset/", entrega.getDescripcion(), "text/html", "utf-8", null);
            //desWeb.loadData(experience.getDescription(), "text/html; charset=UTF-8", null);
            desWeb.setWebViewClient(new WebViewClient() {

         	   public void onPageFinished(WebView view, String url) {
         		   prog.setVisibility(View.GONE);
         	    }
         	});
            
            descriptionEditor = (EditText) rootView.findViewById(R.id.editDescription);
            descriptionEditor.setText(entrega.getDescripcion());
            image = (ImageView) rootView.findViewById(R.id.image);
            buttonsLayout = (LinearLayout) rootView.findViewById(R.id.buttonsLayout);
            
            buttonsRecords = (LinearLayout) rootView.findViewById(R.id.buttons_layout);
            addPositiveComment = (ImageButton) rootView.findViewById(R.id.addPositiveComment);
            addNegativeComment = (ImageButton) rootView.findViewById(R.id.addNegativeComment);
            addFreeText = (ImageButton) rootView.findViewById(R.id.addFreeText);
            addIdea = (ImageButton) rootView.findViewById(R.id.addIdea);
            addSnippet = (ImageButton) rootView.findViewById(R.id.addSnippet);
            addImage = (ImageButton) rootView.findViewById(R.id.addImage);
            addVideo = (ImageButton) rootView.findViewById(R.id.addVideo);
            addDocument = (ImageButton) rootView.findViewById(R.id.addDocument);
            progress = (ProgressBar) rootView.findViewById(R.id.progressBar);
            //recordButtonListeners();
            final Button cabeceraButton = (Button) rootView.findViewById(R.id.cabeceraRecords);
            //cabeceraButton.setSelected(true);
           
            
              //if(entrega.getImage() != null) image.setImageBitmap(activity.getImage());
            if(entrega.getImagen() != null && !entrega.getImagen().contains("/system") && !entrega.getImagen().contains("none"))
            	ImageLoader.getInstance().displayImage(Uri.fromFile(new File(entrega.getImagen())).toString(), image);
            else{
            	if(!entrega.getImagen().contains("none")) {
            		ImageLoader.getInstance().loadImage(WebService._BASE_URL +
            				entrega.getImagen().replace("original", "medium"), new SimpleImageLoadingListener() {
            			@Override
            			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            				try {
								
							
            					image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
	            			} catch (OutOfMemoryError E){
								System.gc();
								Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
								
							} catch (Exception e) {
								// TODO: handle exception
							}
            			}
            		});
            	}
            	else{
            		try {
						
					
            			image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(getActivity().getResources()
            				, R.drawable.imagen), 8, getActivity()));
	            	} catch (OutOfMemoryError E){
						System.gc();
						Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
						
					} catch (Exception e) {
						// TODO: handle exception
					}
            	}
            }
            changeImage = (Button) rootView.findViewById(R.id.changeImage);
            changeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.add_image_dialog);
                    dialog.setTitle(getString(R.string.add_image));

                    ImageButton gallery = (ImageButton) dialog.findViewById(R.id.gallery);

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent selectPicture = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            selectPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(selectPicture, SELECT_PICTURE);
                            dialog.dismiss();
                        }
                    });
                    ImageButton camera = (ImageButton) dialog.findViewById(R.id.camera);
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                ContentValues values = new ContentValues(2);
                                values.put(MediaStore.Images.Media.DESCRIPTION, "Edu-AREA image");
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                imageFileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

                                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(takePicture, TAKE_PICTURE);
                                    dialog.dismiss();
                                }
                            }catch (ActivityNotFoundException exception){
                                Dialog dialog = new Dialog(getActivity());
                                dialog.setTitle(getString(R.string.could_not_open_camera));
                                dialog.show();
                            }
                        }
                    });
                    dialog.show();
                }
            });
          
            
            if(entrega.getTitulo().equals("") && ApplicationService.getPerfil().getType() != PerfilModel.STUDENT){
            	changeMode(EDITION);
            }
            
            return rootView;
            
           
            
        }
        @Override
        public void onResume() {
        	super.onResume();
        }
 
        
        protected void updateRecords(){
        	progress.setVisibility(View.VISIBLE);
        }
        
        protected void refeshList(){
        }
        
     
        

        protected void enviaEntrega(EntregaModel entrega) {
			final ProgressDialog dialog ;
			
			dialog = new ProgressDialog(_context);
			dialog.setMessage("Guardando entrega...");
            dialog.setCancelable(false);
            dialog.show();
            
            int expId = 0;
			try {
				expId = LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(entrega.getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            WebServiceServices.Web().putEntregaAsync(entrega, expId, new IAction<Boolean>() {
				@Override
				public void Do(Boolean param) {
					 if (dialog.isShowing()) {
		                    dialog.dismiss();
		                }
		                picturePath = null;
				}
			});
		}

		@Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                // Resultado de seleccionar imagen
                case SELECT_PICTURE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        System.out.println("Imagen seleccionada: " + picturePath);
                        cursor.close();
                        entrega.setImagen(picturePath);   
                        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(entrega.getImagen())).toString(), image);
                    }
                    break;
                // Resultado de tomar fotografía
                case TAKE_PICTURE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri pictureUri = data.getData();
                        picturePath = ImageHelper.getRealPathFromURI(pictureUri, getActivity());
                        System.out.println("Imagen seleccionada: " + picturePath);
                        entrega.setImagen(picturePath);
                        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(entrega.getImagen())).toString(), image);
                    }
                    else{
                    	picturePath = ImageHelper.getRealPathFromURI(imageFileUri, getActivity());
                    	entrega.setImagen(picturePath);
                    	ImageLoader.getInstance().displayImage(imageFileUri.toString(), image);
                    }
                    break;
            
            }
        }

		
		
        public void changeMode(int mode){
            switch (mode){
                case VIEW:
                	_mode = VIEW;
                    title.setVisibility(View.VISIBLE);
                    desWeb.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.GONE);
                    titleEditor.setVisibility(View.GONE);
                    descriptionEditor.setVisibility(View.GONE);
                    changeImage.setVisibility(View.GONE);
                    if(!entrega.getDefinition().equals(""))
                    	definition.setVisibility(View.VISIBLE);
                    definitionEditor.setVisibility(View.GONE);
                    break;
                case EDITION:
                	_mode = EDITION;
                    title.setVisibility(View.GONE);
                    desWeb.setVisibility(View.GONE);
                    //buttonsLayout.setVisibility(View.VISIBLE);
                    titleEditor.setVisibility(View.VISIBLE);
                    descriptionEditor.setVisibility(View.VISIBLE);
                    changeImage.setVisibility(View.VISIBLE);
                    definition.setVisibility(View.GONE);
                    definitionEditor.setVisibility(View.VISIBLE);
                    break;
                case DOCUMENTATION:
                	title.setVisibility(View.VISIBLE);
                    desWeb.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.GONE);
                    titleEditor.setVisibility(View.GONE);
                    descriptionEditor.setVisibility(View.GONE);
                    changeImage.setVisibility(View.GONE);
                	break;
                    
            }
        }
        

        
        protected boolean hayCambios(){			
    		if(!title.getText().toString().equals(titleEditor.getText().toString()) 
    			||(entrega.getDescripcion() != null && !entrega.getDescripcion().equals(descriptionEditor.getText().toString()) )
    		   || picturePath != null
    		   || !entrega.getDefinition().equals(definitionEditor.getText().toString())){
    			_hayCambios = true;
    		}else{
    			_hayCambios = false;
    		}
    		
    		return _hayCambios;
    	}
        
        
        
        
        
		private void botonSalvar() {
			boolean changes = false;
			InputMethodManager imm = (InputMethodManager)_context.getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
			((Activity) _context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			if(!title.getText().toString().equals(titleEditor.getText().toString()) ||
			        !entrega.getDescripcion().equals(descriptionEditor.getText().toString()) 
			        || picturePath != null
			        || !entrega.getDefinition().equals(definitionEditor.getText().toString())
			        )
			    changes = true;
			if(changes) {
			    Editable titleText = titleEditor.getText();
			    Editable descriptionText = descriptionEditor.getText();
			    title.setText(titleText.toString());
			    desWeb.loadData(descriptionText.toString(), "text/html; charset=UTF-8", null);
			    definition.setText(definitionEditor.getText().toString());
			    entrega.setTitulo(titleText.toString());
			    entrega.setDescripcion(descriptionText.toString());
			    entrega.setDefinition(definitionEditor.getText().toString());
			    if(picturePath != null && !picturePath.equals(entrega.getImagen()))
			    	entrega.setImagen(picturePath);
			    enviaEntrega(entrega);
			    picturePath = null;
			}
			//cancelar();
		}
		private void cancelar() {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			if(titleEditor != null)
				titleEditor.setText(entrega.getTitulo());
			if(descriptionEditor != null)
				descriptionEditor.setText(entrega.getDescripcion());
			if(definitionEditor != null)
				definitionEditor.setText(entrega.getDefinition());
			//if(entrega.getImage() != null) image.setImageBitmap(activity.getImage());
			picturePath = null;
			changeMode(VIEW);
		}
        
		
		
		protected void compruebaCambios(){
			if (titleEditor != null && descriptionEditor != null && hayCambios()) {
				InputMethodManager imm = (InputMethodManager)_context.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
				((Activity) _context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				AlertDialog.Builder alert = new AlertDialog.Builder(_context);
				alert.setTitle(getResources().getString(R.string.aviso));
				alert.setMessage(getResources().getString(R.string.hayCambios));
				alert.setPositiveButton(getResources().getString(R.string.save),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogConfirm,
									int which) {
								botonSalvar();
								_hayCambios = false;
								changeMode(VIEW);
								finish();
								//((FragmentActivity) _context).getSupportFragmentManager().popBackStack();
							}
						});

				alert.setNegativeButton(getResources().getString(R.string.descartar),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								cancelar();
								dialog.dismiss();
								_hayCambios = false;
								finish();
								//((FragmentActivity) _context).getSupportFragmentManager().popBackStack();
							}
						});

				alert.show();
			}
			else{
				finish();
			}
		}
		
		
		
		

    }
    
    
    
    
    public void updateUI(){
        runOnUiThread(new Runnable() {
            public void run() {
            	adapterList.notifyDataSetChanged();
            }
        });
    }
}
