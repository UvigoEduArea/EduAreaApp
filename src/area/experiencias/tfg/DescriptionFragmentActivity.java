package area.experiencias.tfg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.implementation.WebService;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.DetallesModel;
import area.domain.modelo.PerfilModel;
import area.domain.modelo.RecordModel;
import area.domain.services.Services;
//import area.experiencias.tfg.ActivityViewActivity.DescriptionFragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import area.experiencias.tfg.R;

public  class DescriptionFragmentActivity extends Fragment {

	
	
	
	
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
	
	
	
	
	
	
	/**
     * Edition Modes
     */
    private static final int VIEW = 0;
    private static final int EDITION = 1;
    
    private int _mode = 0;
	
	
        String picturePath;
      
        private static final int SELECT_PICTURE = 0;
        private static final int TAKE_PICTURE = 1;
        
        TextView title, description, textDescAlumno, textDescProfesor, definition;
        WebView desWeb;
        ImageView image;
        EditText titleEditor, descriptionEditor, descAlumno, definitionEditor;
        Button changeImage;
        ProgressBar prog;
        WebView desWebAlumno;
        LinearLayout listaDetalles;
        RelativeLayout descSup,descInf,relDetalles;
        
        LinearLayout buttonsLayout,buttonsRecords;
       
        private Uri imageFileUri;
		
		
		
        
		private int _id;
		private MiniActivity activity;
		
		
		
		private MenuItem refreshMenuItem;
	    private MenuItem synchonize;
	    private MenuItem downloading;
	    private MenuItem addActivity;
	    private MenuItem observaciones,listaActividades;
	    private MenuItem editionMode, save, cancel;
		

	    private TextView avisoModoLocal;
		
		private Sincronizacion sinc;
		
		private  static  Context _context = null;
		protected static boolean _hayCambios = false;
		
		private int tipo;
		private MenuItem cancelarSinc;
		
		
        public static DescriptionFragmentActivity newInstance(int id) {
            return new DescriptionFragmentActivity(id);
        }
        public DescriptionFragmentActivity(int id) {
        	_id = id;
        	
        }
        
        
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_description_new, container, false);
            _context = getActivity();
            try {
            	if(_id < 0)
            		activity = LocalStorageServices.DatabaseService().getMiniActivity(LocalStorageServices.DatabaseService().getActivityIdFromLocalId(_id));
            	else
            		activity = LocalStorageServices.DatabaseService().getMiniActivity(_id);
				
				tipo = ApplicationService.getPerfil().getType();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.setSubtitle(getString(R.string.title_section1));
            
            prog = (ProgressBar) rootView.findViewById(R.id.cargando);
            prog.setVisibility(View.VISIBLE);
            
            avisoModoLocal = (TextView) rootView.findViewById(R.id.modoLocal);
            
            title = (TextView) rootView.findViewById(R.id.title);
            title.setText(activity != null ? activity.getName() : "");
            titleEditor = (EditText) rootView.findViewById(R.id.editTitle);
            titleEditor.setText(activity != null ? activity.getName() : "");
            
            definition = (TextView) rootView.findViewById(R.id.definition);
            definitionEditor = (EditText) rootView.findViewById(R.id.editDefinition);
            
            if(!activity.getDefinition().equals("")){
            	definition.setVisibility(View.VISIBLE);
            	definition.setText(activity.getDefinition());
            	definitionEditor.setText(activity.getDefinition());
            }else{
            	definition.setVisibility(View.GONE);
            }
            
            
            desWeb = (WebView) rootView.findViewById(R.id.description);
            desWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
           
			
            desWebAlumno = (WebView) rootView.findViewById(R.id.descripcionAlumno);
            desWebAlumno.getSettings().setJavaScriptEnabled(true);
            
            
            textDescAlumno = (TextView) rootView.findViewById(R.id.textDescAlum);
            textDescProfesor = (TextView) rootView.findViewById(R.id.textDescProfe);
            
            descSup = (RelativeLayout) rootView.findViewById(R.id.relDescSuper);
            descInf = (RelativeLayout) rootView.findViewById(R.id.relDescInf);
            relDetalles = (RelativeLayout) rootView.findViewById(R.id.relDetalles);
            
            if(tipo == PerfilModel.STUDENT){
            	desWeb.loadDataWithBaseURL("file:///android_asset/", activity != null ? activity.getDescriptionStudent() : "", "text/html", "utf-8", null);
            	textDescAlumno.setVisibility(View.GONE);
            	textDescProfesor.setText(R.string.descAlumno);
            	desWebAlumno.setVisibility(View.GONE);
            	descInf.setVisibility(View.GONE);
            	relDetalles.setVisibility(View.GONE);
            	
            }
            else{
            	desWeb.loadDataWithBaseURL("file:///android_asset/", activity != null ? activity.getDescription() : "", "text/html", "utf-8", null);
            	desWebAlumno.loadDataWithBaseURL("file:///android_asset/", activity.getDescriptionStudent(), "text/html", "utf-8", null);
            }
            
           
            desWeb.setWebViewClient(new WebViewClient() {

         	   public void onPageFinished(WebView view, String url) {
         		   prog.setVisibility(View.GONE);
         	    }
         	});
            
            descriptionEditor = (EditText) rootView.findViewById(R.id.editDescription);
            descAlumno = (EditText) rootView.findViewById(R.id.editDescAlumno);
            
            if(tipo == PerfilModel.STUDENT){
            	descriptionEditor.setText(activity.getDescriptionStudent());
            }
            else{
            	descriptionEditor.setText(activity.getDescription());
            	descAlumno.setText(activity.getDescriptionStudent());
            }
            image = (ImageView) rootView.findViewById(R.id.image);
            
            if(activity != null && activity.getImage() != null) image.setImageBitmap(activity.getImage());
            if(activity != null && !activity.getElement_image_file_name().contains("/system") && !activity.getElement_image_file_name().contains("none")) 
            	ImageLoader.getInstance().displayImage(Uri.fromFile(new File(activity.getElement_image_file_name())).toString(), image);
            else{
            	if(activity != null && !activity.getElement_image_file_name().contains("none")) {
            		ImageLoader.getInstance().loadImage(WebService._BASE_URL +
            				activity.getElement_image_file_name().replace("original", "medium"), new SimpleImageLoadingListener() {
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

            if(tipo != PerfilModel.STUDENT){
            
            listaDetalles = (LinearLayout) rootView.findViewById(R.id.listaDetalles);
            ScrollView scrollDetalles = (ScrollView) rootView.findViewById(R.id.ScrollViewDetalles);
            
            
            List<DetallesModel> detalles;
    		try {
    			detalles = LocalStorageServices.DatabaseService().getDetalles(_id);
    			if(detalles.size() == 0)
    				relDetalles.setVisibility(View.GONE);
    			
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
    			
    		
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            
            }
            
            setHasOptionsMenu(true);
            
            
            if((activity.getName() == null ||activity.getName().equals("")) && tipo != PerfilModel.STUDENT){
            	changeMode(EDITION);
            }
            
            return rootView;
        }
        
        
        
        @Override
        public void onStart() {
        	sinc = new Sincronizacion();
        	sinc.iniciar();
        	super.onStart();
        }
        
        @Override
        public void onPause() {
        	if(tipo != PerfilModel.STUDENT)
        		compruebaCambios();
        	super.onPause();
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
            editionMode = menu.findItem(R.id.action_edition_mode);
            save = menu.findItem(R.id.action_save);
            cancel = menu.findItem(R.id.action_cancel);
            cancelarSinc = menu.findItem(R.id.forzar_sinc);
            
            listaActividades = menu.findItem(R.id.lista_actividades);
            listaActividades.setVisible(true);
                        
//            if(tipo == PerfilModel.STUDENT){
//            	editionMode.setVisible(false);
//            	observaciones.setVisible(false);
//            }
            
            
            if(activity.getPermiso() != null && activity.getPermiso().equals("viewer")){
            	
            	editionMode.setVisible(false);
            }
            
            if(_mode == EDITION){
         	   save.setVisible(true);
         	   cancel.setVisible(true);
         	   editionMode.setVisible(false);
            }
            
        	super.onCreateOptionsMenu(menu, inflater);
        }
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        	
        	 int id = item.getItemId();
             switch (id){
                 case R.id.action_view_mode:
                    changeMode(VIEW);
                     return true;
                 case R.id.action_edition_mode:
                     changeMode(EDITION);
                     save.setVisible(true);
                     cancel.setVisible(true);
                     editionMode.setVisible(false);
                     return true;
                 case R.id.action_documentation_mode:
                	 ActivityViewDrawerActivity.observacionesFragment = ObservacionesFragment.newInstance(_id, "activities");
 		        	getFragmentManager().beginTransaction().replace(R.id.container, ExperienceViewDrawerActivity.observacionesFragment).addToBackStack(null).commit(); 
                     return true;
                 case R.id.action_refresh:
                     return true;
                 case android.R.id.home:
                     getActivity().finish();
                     return true;
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
                 	botonSalvar();
                 	
                 	return true;
                 case R.id.action_cancel:
                 	cancelar();
                 	save.setVisible(false);
                	cancel.setVisible(false);
                	editionMode.setVisible(true);
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

             }
             return super.onOptionsItemSelected(item);
        }
        
        
        
        @Override
        public void onResume() {
        	super.onResume();

        }
        
        protected void enviaActividad(MiniActivity activity) {
			final ProgressDialog dialog ;
			
			dialog = new ProgressDialog(_context);
			dialog.setMessage(_context.getResources().getString(R.string.guardandoActividad));
            dialog.setCancelable(false);
            dialog.show();
            
            WebServiceServices.Web().putActivityAsync(activity, new IAction<Boolean>() {
				@Override
				public void Do(Boolean param) {
					 if (dialog != null && dialog.isShowing()) {
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
                    if (resultCode == android.app.Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        System.out.println("Imagen seleccionada: " + picturePath);
                        cursor.close();
                        activity.setElement_image_file_name(picturePath);   
                        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(activity.getElement_image_file_name())).toString(), image);
                    }
                    break;
                // Resultado de tomar fotografía
                case TAKE_PICTURE:
                    if (resultCode == android.app.Activity.RESULT_OK && data != null) {
                        Uri pictureUri = data.getData();
                        picturePath = ImageHelper.getRealPathFromURI(pictureUri, getActivity());
                        System.out.println("Imagen seleccionada: " + picturePath);
                        activity.setElement_image_file_name(picturePath);
                        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(activity.getElement_image_file_name())).toString(), image);
                    }
                    else{
                    	picturePath = ImageHelper.getRealPathFromURI(imageFileUri, getActivity());
                    	activity.setElement_image_file_name(picturePath);
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
                    titleEditor.setVisibility(View.GONE);
                    descriptionEditor.setVisibility(View.GONE);
                    changeImage.setVisibility(View.GONE);
                    desWebAlumno.setVisibility(View.VISIBLE);
                    descAlumno.setVisibility(View.GONE);
                    if(!activity.getDefinition().equals(""))
                    	definition.setVisibility(View.VISIBLE);
                    definitionEditor.setVisibility(View.GONE);
                    break;
                case EDITION:
                	_mode = EDITION;
                    title.setVisibility(View.GONE);
                    desWeb.setVisibility(View.GONE);
                    titleEditor.setVisibility(View.VISIBLE);
                    descriptionEditor.setVisibility(View.VISIBLE);
                    changeImage.setVisibility(View.VISIBLE);
                    desWebAlumno.setVisibility(View.GONE);
                    descAlumno.setVisibility(View.VISIBLE);
                    definition.setVisibility(View.GONE);
                    definitionEditor.setVisibility(View.VISIBLE);
                    break;
                    
            }
        }
        
		protected void botonSalvar() {
			boolean changes = false;
			InputMethodManager imm = (InputMethodManager)_context.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
			((Activity) _context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			if(!title.getText().toString().equals(titleEditor.getText().toString()) ||
			        !activity.getDescription().equals(descriptionEditor.getText().toString()) 
			        || picturePath != null || !activity.getDescriptionStudent().equals(descAlumno.getText().toString())
			        || !activity.getDefinition().equals(definitionEditor.getText().toString())
			        )
			    changes = true;
			if(changes) {
			    Editable titleText = titleEditor.getText();
			    Editable descriptionText = descriptionEditor.getText();
			    title.setText(titleText.toString());
			    desWeb.loadData(descriptionText.toString(), "text/html; charset=UTF-8", null);
			    definition.setText(definitionEditor.getText().toString());
			    activity.setName(titleText.toString());
			    activity.setDescription(descriptionText.toString());
			    activity.setDescriptionStudent(descAlumno.getText().toString());
			    activity.setDefinition(definitionEditor.getText().toString());
			    if(picturePath != null && !picturePath.equals(activity.getElement_image_file_name()))
			    	activity.setElement_image_file_name(picturePath);
			    enviaActividad(activity);
			    picturePath = null;
			}
		}
		private void cancelar() {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(android.app.Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			changeMode(VIEW);
		}
        
		protected boolean hayCambios(){		
			if (tipo == PerfilModel.STUDENT) 
				return _hayCambios = false;
			if(!title.getText().toString().equals(titleEditor.getText().toString()) ||
					   !activity.getDescription().equals(descriptionEditor.getText().toString()) 
					   || picturePath != null 
					   || ((activity.getDescriptionStudent() != null) && !activity.getDescriptionStudent().equals(descAlumno.getText().toString()) ) 
					   || !activity.getDefinition().equals(definitionEditor.getText().toString())){
						_hayCambios = true;
					}else{
						_hayCambios = false;
					}
					return _hayCambios;
		}
		
		private void compruebaCambios(){
			if (hayCambios()) {
				InputMethodManager imm = (InputMethodManager)_context.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(titleEditor.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(descriptionEditor.getWindowToken(), 0);
				((Activity) _context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert.setTitle(getResources().getString(R.string.aviso));
				alert.setMessage(getResources().getString(R.string.hayCambios));
				alert.setPositiveButton(getResources().getString(R.string.save),
						new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialogConfirm,
									int which) {
								botonSalvar();
	
							}
						});
	
				alert.setNegativeButton(getResources().getString(R.string.descartar),
						new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
	
				alert.show();
			}
		}
        
    }
    