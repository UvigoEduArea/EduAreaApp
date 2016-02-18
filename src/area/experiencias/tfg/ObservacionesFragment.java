package area.experiencias.tfg;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
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
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.CompruebaSincronizacion;
import area.communication.implementation.WebService;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.RecordModel;
import area.domain.services.Services;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import area.experiencias.tfg.R;
import eduarea.facedetector.FaceDetector;

public  class ObservacionesFragment extends Fragment {

	
	
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
//						enLocal.setVisible(false);
						downloading.setVisible(!downloading.isVisible());
					}
					else{
//						if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizadoFromLessonPlanId(_id) ){
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
		
		
		
//		@Override
//		public void updated(final boolean isUpdated){
//			if (enLocal == null) return;
//			if (getActivity() == null) return;
//			getActivity().runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					if(isUpdated != isUp){
//						isUp = isUpdated;	
//						if(LocalStorageServices.DatabaseService().getExperienceIsUpdatedFromLessonPlanId(_id) && LocalStorageServices.DatabaseService().getExperienceMantenerActualizadoFromLessonPlanId(_id)){
//							 enLocal.setVisible(true);
//						 }else{
//							 enLocal.setVisible(false);
//								if( LocalStorageServices.DatabaseService().getExperienceMantenerActualizadoFromLessonPlanId(_id) ){
//									 try {
//										WebServiceServices.Download().downloadActivity(LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(_id));
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								 }
//						 }
//												
//					}
//				}
//			});
//			
//		}	
		
	}
	
	
	
	
	
	

        String picturePath;
        private String rutaArchivo;
        private String rutaArchivoOriginal;
        private Uri imageFilePath;
        
        
        private static final int SELECT_PICTURE_RECORD = 2;
        private static final int TAKE_PICTURE_RECORD = 3;
        private static final int SELECT_VIDEO_RECORD = 4;
        private static final int RECORD_VIDEO_RECORD = 5;
        private static final int PICK_FILE_RECORD = 6;
        
        TextView title, description;
        WebView desWeb;
        ImageView image;
        EditText titleEditor, descriptionEditor;
        Button changeImage;
        private ImageButton addDocument, addImage, addVideo;
        private ImageButton addPositiveComment, addNegativeComment, addFreeText, addIdea, addSnippet;
		private ListView listaRecords;
		private GridView gridRecords;
		private RecordListAdapter adapterRecords;
		private RecordGridAdapter gridAdapterRecords;
		private ArrayList<RecordModel> recordList = new ArrayList<RecordModel>();
		private ProgressBar progress;
		private boolean typeList = true;
		
		
		private MenuItem refreshMenuItem;
	    private MenuItem synchonize, enLocal;
	    private MenuItem downloading;
	    private MenuItem addActivity;
	    private MenuItem observaciones;
	    private MenuItem addEntrega;
	    private MenuItem editionMode, viewMode, save, cancel, mas, menos,listaActividades;
		
	    private TextView avisoModoLocal,sinElementos;
		
		private Sincronizacion sinc;
		
		private int _id;
		private String _tipoSolicitado;
		private MenuItem cancelarSinc;
		
		private boolean all = true;
        
        public static ObservacionesFragment newInstance(int elementId, String tipo) {
            return new ObservacionesFragment(elementId, tipo);
        }
        public ObservacionesFragment(int elementId, String tipo) {
        	_id = elementId;
        	_tipoSolicitado = tipo;
        }
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.observaciones_fragment, container, false);
           
            addPositiveComment = (ImageButton) rootView.findViewById(R.id.addPositiveComment);
            addNegativeComment = (ImageButton) rootView.findViewById(R.id.addNegativeComment);
            addFreeText = (ImageButton) rootView.findViewById(R.id.addFreeText);
            addIdea = (ImageButton) rootView.findViewById(R.id.addIdea);
            addSnippet = (ImageButton) rootView.findViewById(R.id.addSnippet);
            addImage = (ImageButton) rootView.findViewById(R.id.addImage);
            addVideo = (ImageButton) rootView.findViewById(R.id.addVideo);
            addDocument = (ImageButton) rootView.findViewById(R.id.addDocument);
            progress = (ProgressBar) rootView.findViewById(R.id.progressBar);
            
            avisoModoLocal = (TextView) rootView.findViewById(R.id.modoLocal);
            sinElementos = (TextView) rootView.findViewById(R.id.sinElementos);
            
            recordButtonListeners();
            final Button cabeceraButton = (Button) rootView.findViewById(R.id.cabeceraRecords);
            cabeceraButton.setSelected(true);
           
            listaRecords = (ListView) rootView.findViewById(R.id.listaRecs);
            
            gridRecords = (GridView) rootView.findViewById(R.id.gridRecs);
            
            ActionBar actionBar = getActivity().getActionBar();
            if(_tipoSolicitado.equals("submissions")){
            	 actionBar.setSubtitle(getString(R.string.entregas));
            	 cabeceraButton.setText(getString(R.string.entregasRealizadas));
            }
            else
            	actionBar.setSubtitle(getString(R.string.observaciones));
          
            cabeceraButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(typeList){
						cabeceraButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.list_white, 0);
						gridRecords.setVisibility(View.VISIBLE);
						listaRecords.setVisibility(View.GONE);
					}
					else{
						cabeceraButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grid_white, 0);
						gridRecords.setVisibility(View.GONE);
						listaRecords.setVisibility(View.VISIBLE);
					}
					typeList = ! typeList;	
				}
			});
            
            
            listaRecords.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
    				creaDialogo(recordList.get(posicion).getRecordTypeId(), recordList.get(posicion));
    			}
    		});
            gridRecords.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
    				creaDialogo(recordList.get(posicion).getRecordTypeId(), recordList.get(posicion));
    			}
    		});
            if(!(getActivity() instanceof EntregaViewActivityNew))
            	setHasOptionsMenu(true);
            
            return rootView;
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
            listaActividades = menu.findItem(R.id.lista_actividades);
            save = menu.findItem(R.id.action_save);
            cancel = menu.findItem(R.id.action_cancel);
            cancelarSinc = menu.findItem(R.id.forzar_sinc);
            addActivity.setVisible(false);
            enLocal = menu.findItem(-1);
            mas = menu.findItem(R.id.expandir);
            menos = menu.findItem(R.id.contraer);
            menos.setVisible(true);
            
            if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
            	observaciones.setVisible(true);
            else
            	observaciones.setVisible(false);
        	
        	int id = !_tipoSolicitado.equals("activities") ? LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(_id) : LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(_id);
            
        	if(LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(id)){
            	listaActividades.setVisible(true);
            }
            else{
            	listaActividades.setVisible(false);
            }
        	
        	addEntrega.setVisible(false);
        	editionMode.setVisible(false);
        	viewMode.setVisible(false);
        	save.setVisible(false);
        	cancel.setVisible(false);
        	
        
        	
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
                	 getActivity().getSupportFragmentManager().popBackStackImmediate();
                     return true;
                 case R.id.action_refresh:
                	 updateRecords();
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
                	 
                	 
                	 
                 case R.id.expandir:
               	  all = true;
               	  menos.setVisible(true);
               	  mas.setVisible(false);
               	  obtenerRecords();
               	  return true;
                 case R.id.contraer:
               	  all = false;
               	  menos.setVisible(false);
               	  mas.setVisible(true);
               	  obtenerRecords();
               	  return true;
               	  
             }
             return super.onOptionsItemSelected(item);
        }
        
        
        
        
        
        
        @Override
        public void onResume() {
        	super.onResume();
        	recordList = new ArrayList<RecordModel>();
        	adapterRecords = new RecordListAdapter(getActivity(), recordList);
        	gridAdapterRecords = new RecordGridAdapter(getActivity(), recordList);
            listaRecords.setAdapter(adapterRecords);
            gridRecords.setAdapter(gridAdapterRecords);
            obtenerRecords();
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
        
        private void obtenerRecords() {
    		WebServiceServices.Web().getRecords(_id, _tipoSolicitado, all, new IAction<List<RecordModel>>() {
    			
    			@Override
    			public void Do(List<RecordModel> param) {
    				recordList.clear();
    				if(param.size()==0)
    					sinElementos.setVisibility(View.VISIBLE);
    				else
    					sinElementos.setVisibility(View.GONE);
    				for( RecordModel rec : param){
    					recordList.add(rec);
    				}
    				
    	            adapterRecords.notifyDataSetChanged();
    	            gridAdapterRecords.notifyDataSetChanged();
    	             progress.setVisibility(View.GONE);
    			}
    		});
    	}
        
        protected void updateRecords(){
        	progress.setVisibility(View.VISIBLE);
        	obtenerRecords();
        }
        
        protected void refeshList(){
        	adapterRecords.notifyDataSetChanged();
        	gridAdapterRecords.notifyDataSetChanged();
        }
        
        private void recordButtonListeners() {
    		
            addPositiveComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    creaDialogo(RecordModel.RECORD_POSITIVE_COMMENT, null);
                }
            });
            
            
            addNegativeComment.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				creaDialogo(RecordModel.RECORD_NEGATIVE_COMMENT, null);
    			}
    		});
            
            
            addFreeText.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				creaDialogo(RecordModel.RECORD_TEXT, null);
    			}
    		});
            
            
            addIdea.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				creaDialogo(RecordModel.RECORD_IDEA, null);
    			}
    		});
            
            
            addSnippet.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				creaDialogo(RecordModel.RECORD_SNIPPET, null);
    			}
    		});

            
            addImage.setOnClickListener(new View.OnClickListener() {
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
                            startActivityForResult(selectPicture, SELECT_PICTURE_RECORD);
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
                                imageFilePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);

                                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(takePicture, TAKE_PICTURE_RECORD);
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

            
            addVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.add_video_dialog);
                    dialog.setTitle(getString(R.string.add_video));

                    ImageButton gallery = (ImageButton) dialog.findViewById(R.id.gallery);

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent selectPicture = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            selectPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
                            startActivityForResult(selectPicture, SELECT_VIDEO_RECORD);
                            dialog.dismiss();
                        }
                    });

                    ImageButton camera = (ImageButton) dialog.findViewById(R.id.camera);

                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent takeVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                if (takeVideo.resolveActivity(getActivity().getPackageManager()) != null) {
                                	
                                	//takeVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                	//takeVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
                                	//takeVideo.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15*1024*1024L);
                                    startActivityForResult(takeVideo, RECORD_VIDEO_RECORD);
                                }
                                dialog.dismiss();
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

            
            addDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("file/*");
                        startActivityForResult(intent, PICK_FILE_RECORD);
                    } catch (ActivityNotFoundException e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.file_explorer_not_found))
                                .setCancelable(true)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
    	}
        
        

		@Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
               
                case SELECT_PICTURE_RECORD:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String picturePath = cursor.getString(columnIndex);
                        System.out.println("Imagen seleccionada: " + picturePath);
                        cursor.close();
                        rutaArchivo = picturePath;
                        creaDialogo(RecordModel.RECORD_IMAGE, null);


                    }
                    break;
                case TAKE_PICTURE_RECORD:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri pictureUri = imageFilePath;
                        final String picturePath = ImageHelper.getRealPathFromURI(pictureUri,getActivity());
                        System.out.println("Imagen seleccionada: " + picturePath);
                        rutaArchivo = picturePath;
                        creaDialogo(RecordModel.RECORD_IMAGE, null);

                    }
                    break;
                case SELECT_VIDEO_RECORD:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedVideo = data.getData();
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedVideo,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String videoPath = cursor.getString(columnIndex);
                        System.out.println("Video seleccionado: " + videoPath);
                        cursor.close();
                        rutaArchivo = videoPath;
                        creaDialogo(RecordModel.RECORD_VIDEO, null);

                    }
                    break;
                case RECORD_VIDEO_RECORD:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri recordedVideo = data.getData();
                        final String videoPath = ImageHelper.getRealPathFromURI(recordedVideo, getActivity());
                        System.out.println("Video seleccionado: " + videoPath);
                        rutaArchivo = LocalStorageServices.ExternalStorage().guardarVideo(videoPath, String.valueOf(new Date().getTime()));
                        creaDialogo(RecordModel.RECORD_VIDEO, null);

                    }
                    break;
                case PICK_FILE_RECORD:
                    if (resultCode == Activity.RESULT_OK) {
                        //final String documentPath = data.getData().getPath();
                        rutaArchivo = ImageHelper.getRealPathFromURI(data.getData(), getActivity());
                        creaDialogo(RecordModel.RECORD_DOCUMENT, null);

                    }
                    break;
                case FaceDetector.BLUR_IMAGE:
                	RecordModel rec = null;
                	System.gc();
                	try {
						rec = LocalStorageServices.DatabaseService().getRecord(data.getExtras().getInt("record_id"), data.getExtras().getString("record_creation_date"));
						if(data.getExtras().getString("blurred_path") != null){
							rec.setBlurredImage(data.getExtras().getString("blurred_path"));
							rec.setFacesArray(data.getExtras().getString("faces_array"));
							rutaArchivoOriginal = rutaArchivo;
							rutaArchivo = data.getExtras().getString("blurred_path");
                		}else{
                			rutaArchivo = data.getExtras().getString("image_path");
                			rec.setData(rutaArchivo);
                		}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
                	
                	creaDialogo(RecordModel.RECORD_IMAGE, rec);
                	break;
            }
        }

    
        
		protected void creaDialogo(final int tipoRecord, final RecordModel rec) {
			try {
				// Se crea un diálogo de añadir reflexión
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
				dialog.setContentView(R.layout.add_record_dialog);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setCancelable(false);

				ImageButton save = (ImageButton) dialog.findViewById(R.id.save);
				ImageButton cancel = (ImageButton) dialog.findViewById(R.id.cancel);
				ImageButton borrar = (ImageButton) dialog.findViewById(R.id.papelera);
				ImageButton descargar = (ImageButton) dialog.findViewById(R.id.download);
				ImageButton fullScreen = (ImageButton) dialog.findViewById(R.id.full_screen);
				final ImageButton playPause = (ImageButton) dialog.findViewById(R.id.play_pause);
				final ImageButton blur = (ImageButton) dialog.findViewById(R.id.borrar_caras);
				
				final WebView snippet = (WebView) dialog.findViewById(R.id.webViewSnippet);
				final VideoView video = (VideoView) dialog.findViewById(R.id.videoView);
				final ProgressBar progressVideo = (ProgressBar) dialog.findViewById(R.id.progressBarVideo);
				final ImageView imagen = (ImageView) dialog.findViewById(R.id.imagen);
				final ImageView image_original = (ImageView) dialog.findViewById(R.id.image_blurred);
				final EditText titulo = (EditText) dialog.findViewById(R.id.tituloRecord);
				final EditText descripcion = (EditText) dialog.findViewById(R.id.descripcionRecord);
				final EditText texto = (EditText) dialog.findViewById(R.id.textoRecord);

				final boolean videoIniciado = false;

				
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				
				
				
				if(rec != null){
					titulo.setText(rec.getTitle());
					descripcion.setText(rec.getDescription());
					borrar.setVisibility(View.VISIBLE);
					
					if(rec.getPermiso().equals("viewer")){
						save.setEnabled(false);
						borrar.setEnabled(false);
						titulo.setFocusable(false);
						descripcion.setFocusable(false);
						texto.setFocusable(false);
						borrar.setVisibility(View.GONE);
						save.setVisibility(View.GONE);
					}
					else{
						borrar.setVisibility(View.VISIBLE);
						save.setVisibility(View.VISIBLE);
						save.setEnabled(true);
						borrar.setEnabled(true);
						titulo.setFocusable(true);
						descripcion.setFocusable(true);
						texto.setFocusable(true);
						
					}
					

				}
				else{
					borrar.setVisibility(View.GONE);					
				}
				
				switch (tipoRecord) {
				case RecordModel.RECORD_IMAGE:
					dialog.setTitle(getString(R.string.image));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.image_blue);
					imagen.setVisibility(View.VISIBLE);
					texto.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					fullScreen.setVisibility(View.VISIBLE);

					if(rec != null){
						if(rec.getFacesArray() != null && !rec.getFacesArray().equals("")){
							if(!rec.getBlurredImage().contains("/system")){
								try {
									ImageLoader.getInstance().displayImage(Uri.fromFile(new File(rec.getBlurredImage())).toString(), imagen);
									
									
									rutaArchivo = rec.getBlurredImage();
									blur.setVisibility(View.VISIBLE);
									if(!rec.getData().contains("/system"))
										rutaArchivoOriginal = rec.getData();
									else{
										if(!rec.getData().equals("none")){
											ImageLoader.getInstance().loadImage(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
												@Override
												public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
													try {
														
													} catch (OutOfMemoryError E){
														System.gc();
														Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
														dialog.dismiss();
													} catch (Exception e) {
														// TODO: handle exception
													}
													rutaArchivoOriginal = ImageLoader.getInstance().getDiskCache().get(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/")).getAbsolutePath();
													blur.setClickable(true);
												}
												
											});	
										}
									}
								} catch (OutOfMemoryError E){
									System.gc();
									Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();

								} catch (Exception e) {

								}
								if(!(getActivity() instanceof EntregaViewActivityNew))
									blur.setVisibility(View.VISIBLE);
								blur.setClickable(true);
							}else{
								if(!rec.getBlurredImage().equals("none")) {
									ImageLoader.getInstance().loadImage(WebService._BASE_URL + rec.getBlurredImage().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
										@Override
										public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											try {
												imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
											} catch (OutOfMemoryError E){
												System.gc();
												Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
												dialog.dismiss();		
											} catch (Exception e) {
												
											}
											rutaArchivo = ImageLoader.getInstance().getDiskCache().get(WebService._BASE_URL + rec.getBlurredImage().replace("/original/", "/medium/")).getAbsolutePath();
											if(!(getActivity() instanceof EntregaViewActivityNew))
												blur.setVisibility(View.VISIBLE);
										}
									});
									
									
									ImageLoader.getInstance().loadImage(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
										@Override
										public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											try {
											} catch (OutOfMemoryError E){
												System.gc();
												Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
												dialog.dismiss();
											} catch (Exception e) {
												// TODO: handle exception
											}
											rutaArchivoOriginal = ImageLoader.getInstance().getDiskCache().get(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/")).getAbsolutePath();
											blur.setClickable(true);
										}
									});
								}
								else{
									try {
										imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources()
												, R.drawable.imagen), 8, getActivity()));
									} catch (OutOfMemoryError E){
										System.gc();
										Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
										dialog.dismiss();
									} catch (Exception e) {
									}
								}
							}

						}
						
						
						//if(rec.getFacesArray() != null && !rec.getFacesArray().equals("")){
						else{
							if(!rec.getData().contains("/system")){
								
								try {
									ImageLoader.getInstance().displayImage(Uri.fromFile(new File(rec.getData())).toString(), imagen);
									rutaArchivo = rec.getData();
								} catch (OutOfMemoryError E){
									System.gc();
									Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
									dialog.dismiss();

								} catch (Exception e) {
									e.printStackTrace();
								}
								if(!(getActivity() instanceof EntregaViewActivityNew))
									blur.setVisibility(View.VISIBLE);
								blur.setClickable(true);
							}else{
								if(!rec.getData().equals("none")) {

									ImageLoader.getInstance().loadImage(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
										@Override
										public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											try {
												imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
											} catch (OutOfMemoryError E){
												System.gc();
												Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
												dialog.dismiss();

											} catch (Exception e) {
												e.printStackTrace();
											}
											rutaArchivo = ImageLoader.getInstance().getDiskCache().get(WebService._BASE_URL + rec.getData().replace("/original/", "/medium/")).getAbsolutePath();

											if(!(getActivity() instanceof EntregaViewActivityNew))
												blur.setVisibility(View.VISIBLE);
											blur.setClickable(true);
										}
									});
								}
								else{
									try {
										imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources()
												, R.drawable.imagen), 8, getActivity()));
									} catch (OutOfMemoryError E){
										System.gc();
										Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
										dialog.dismiss();
									} catch (Exception e) {
										// TODO: handle exception
									}

								}
							}
						}
					}
					
					
					else{
						try {
							ImageLoader.getInstance().displayImage(Uri.fromFile(new File(rutaArchivo)).toString(), imagen);
						} catch (OutOfMemoryError E){
							System.gc();
							Toast.makeText(getActivity(),"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					
					
					fullScreen.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Dialog dFull = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
							dFull.setContentView(R.layout.imagen_pantalla_completa);
							 final ImageView imagenFull = (ImageView) dFull.findViewById(R.id.imagenFull);
							 imagenFull.setImageDrawable(imagen.getDrawable());
							 dFull.show();
						}
					});
					
					break;
				case RecordModel.RECORD_VIDEO:
					dialog.setTitle(getString(R.string.video));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.film_blue);
					texto.setVisibility(View.GONE);
					imagen.setVisibility(View.GONE);
					video.setVisibility(View.VISIBLE);
					progressVideo.setVisibility(View.VISIBLE);
					descargar.setVisibility(View.GONE);
					playPause.setVisibility(View.VISIBLE);
					fullScreen.setVisibility(View.GONE);
					if (rec != null && rec.getData() != null){
						if(rec.getLocalData() == null)
							descargar.setVisibility(View.VISIBLE);
					}
					final MediaController mediacontroller = new MediaController(getActivity());
					try {
						video.setMediaController(mediacontroller);
						mediacontroller.setAnchorView(dialog.getCurrentFocus());
						Uri uri = rec != null ? Uri.parse(WebService._BASE_URL + rec.getData()) : Uri.fromFile(new File(rutaArchivo));
						if(rec != null && rec.getLocalData() != null)
							uri = Uri.fromFile(new File(rec.getLocalData()));
						video.setVideoURI(uri);
					} catch (Exception e) {
						e.printStackTrace();
					}
					video.requestFocus();
					
					video.setOnPreparedListener(new OnPreparedListener() {
						public void onPrepared(MediaPlayer mp) {
							progressVideo.setVisibility(View.GONE);
							mediacontroller.show(99);
							mp.seekTo(99);
							
							video.seekTo(100);

						}
					});
					
					video.setOnErrorListener(new OnErrorListener() {

						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							progressVideo.setVisibility(View.GONE);
							return false;
						}
					});
					playPause.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(video.isPlaying()){
								playPause.setImageResource(R.drawable.play3);
								video.pause();
							}
							else{
								playPause.setImageResource(R.drawable.pause2);
								video.start();
							}
							
						}
					});
					video.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							playPause.setImageResource(R.drawable.play3);
						}
					});
					
					fullScreen.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Uri uri = rec != null ? Uri.parse(WebService._BASE_URL + rec.getData()) : Uri.fromFile(new File(rutaArchivo));
							if(rec != null && rec.getLocalData() != null)
								uri = Uri.fromFile(new File(rec.getLocalData()));
							Intent i = new Intent(Intent.ACTION_VIEW);
							
							i.setDataAndType(uri, "video/*");
							getActivity().startActivity(i);
						}
					});
					break;
				case RecordModel.RECORD_DOCUMENT:
					dialog.setTitle(getString(R.string.document));

					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.file_text2_blue);
					texto.setVisibility(View.GONE);
					imagen.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					break;
				case RecordModel.RECORD_SNIPPET:
					dialog.setTitle(getString(R.string.snippet));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.embed2_blue);
					imagen.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					if(rec != null){
						texto.setVisibility(View.GONE);
						snippet.setVisibility(View.VISIBLE);
						progressVideo.setVisibility(View.VISIBLE);
						snippet.setWebChromeClient(new WebChromeClient());
						snippet.setWebViewClient(new WebViewClient(){
							@Override
							public void onPageFinished(WebView view, String url) {
								progressVideo.setVisibility(View.GONE);
								super.onPageFinished(view, url);
							}
						});
						snippet.getSettings().setJavaScriptEnabled(true);
						snippet.loadData(rec.getData(), "text/html; charset=UTF-8", null);
					}
					else{
						snippet.setVisibility(View.GONE);
						texto.setHint(getString(R.string.hintSnippet));
						texto.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_NEGATIVE_COMMENT:
					dialog.setTitle(getString(R.string.negative_comment));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.like2_blue);
					imagen.setVisibility(View.GONE);
					texto.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					if(rec != null)
						texto.setText(rec.getData());
					break;
				case RecordModel.RECORD_POSITIVE_COMMENT:
					dialog.setTitle(getString(R.string.positive_comment));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.like_blue);
					imagen.setVisibility(View.GONE);
					texto.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					if(rec != null)
						texto.setText(rec.getData());
					break;
				case RecordModel.RECORD_TEXT:
					dialog.setTitle(getString(R.string.free_text));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.paragraph_justify_blue);
					imagen.setVisibility(View.GONE);
					texto.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					if(rec != null)
						texto.setText(rec.getData());
					break;
				case RecordModel.RECORD_IDEA:
					dialog.setTitle(getString(R.string.idea));
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.bulb_blue);
					imagen.setVisibility(View.GONE);
					texto.setVisibility(View.GONE);
					descargar.setVisibility(View.GONE);
					if(rec != null)
						texto.setText(rec.getData());
					break;

				}

				save.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						snippet.onPause();
						final ProgressDialog pd = new ProgressDialog(getActivity());
						pd.setMessage("Enviando...");
						pd.setCancelable(false);
						pd.show();		    	
						RecordModel record = new RecordModel();
						record.setId(rec == null ? -1 : rec.getId());
						if(rec != null && rec.getId() == -1){
							record.setId(LocalStorageServices.DatabaseService().getRecordId(Services.Utilidades().DateToString(rec.getCreationDate())));
						}
						record.setTitle((titulo.getText()!=null) ? titulo.getText().toString() : "");
						record.setDescription((descripcion.getText()!=null) ? descripcion.getText().toString() : "");

						if(tipoRecord == RecordModel.RECORD_IMAGE || tipoRecord == RecordModel.RECORD_VIDEO || tipoRecord == RecordModel.RECORD_DOCUMENT)
							if(rec != null && rec.getFacesArray() != null && !rec.getFacesArray().equals("")){
								if(tipoRecord == RecordModel.RECORD_IMAGE)
									record.setData(LocalStorageServices.ExternalStorage().escalarImagen(rutaArchivoOriginal));
								else
									record.setData(rutaArchivoOriginal);
							}
							else{
								if(rec == null)
									if(tipoRecord == RecordModel.RECORD_IMAGE)
										record.setData(LocalStorageServices.ExternalStorage().escalarImagen(rutaArchivo));
									else
										record.setData(rutaArchivo);
								else{
									//record.setData("");
									record.setData(rec.getData());
								}
							}
						else
							record.setData((texto.getText()!=null) ? texto.getText().toString() : "");
						if(tipoRecord == RecordModel.RECORD_VIDEO){
							record.setLocalData(rec == null ?  rutaArchivo : rec.getLocalData());
							if(rec == null && MimeTypeMap.getFileExtensionFromUrl(rutaArchivo).equals("")){
								//record.setData(record.getData() + ".mp4");
							}
						}

						record.setRecordTypeId(tipoRecord);
						record.setRecordType(tipoRecord);
						record.setBlurredImage(rec == null ? null : rec.getBlurredImage());
						record.setFacesArray(rec == null ? null : rec.getFacesArray());
						record.setCreationDate(rec == null ? new Date() : rec.getCreationDate());
						record.setUpdateDate(new Date());

						WebServiceServices.Web().createRecordAsync(record, _id, _tipoSolicitado, new IAction<Boolean>() {

							@Override
							public void Do(Boolean result) {
								if (pd.isShowing()) {
									pd.dismiss();
								}
								progress.setVisibility(View.VISIBLE);
								obtenerRecords();
							}
						});
						dialog.dismiss();
						
						
						
						
						
					}
				});
				

				borrar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						snippet.onPause();

						AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
						alert.setTitle("Aviso");
						alert.setMessage("¿Estás seguro que deseas eliminar esta observación?");
						alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogConfirm, int which) {


								final ProgressDialog pd = new ProgressDialog(getActivity());
								pd.setMessage("Borrando...");
								pd.setCancelable(false);
								pd.show();		    	
								WebServiceServices.Web().deleteRecordAsync(rec.getId(), _id, rec.getCreationDate(), _tipoSolicitado, new IAction<Boolean>() {

									@Override
									public void Do(Boolean result) {
										if (pd.isShowing()) {
											pd.dismiss();
										}
										progress.setVisibility(View.VISIBLE);
										obtenerRecords();
									}
								});


								dialogConfirm.dismiss();
								dialog.dismiss();
							}
						});

						alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});



						alert.show();

					}
				});

				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						snippet.onPause();
						dialog.dismiss();

					}
				});

				descargar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						WebServiceServices.Web().descargaArchivoAsync(rec, _id);
						dialog.dismiss();
					}
				});

				blur.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {

						RecordModel record = new RecordModel();
						record.setId(rec == null ? -1 : rec.getId());
						if(rec != null && rec.getId() == -1){
							record.setId(LocalStorageServices.DatabaseService().getRecordId(Services.Utilidades().DateToString(rec.getCreationDate())));
						}
						record.setTitle((titulo.getText()!=null) ? titulo.getText().toString() : "");
						record.setDescription((descripcion.getText()!=null) ? descripcion.getText().toString() : "");

						if(tipoRecord == RecordModel.RECORD_IMAGE || tipoRecord == RecordModel.RECORD_VIDEO || tipoRecord == RecordModel.RECORD_DOCUMENT){
							if(rec.getFacesArray() != null && !rec.getFacesArray().equals("")){
								record.setData(rutaArchivoOriginal);
								record.setBlurredImage(rutaArchivo);
								record.setFacesArray(rec.getFacesArray());
							}else
								record.setData(rutaArchivo == null ? rec.getData() : rutaArchivo);
						}else
							record.setData((texto.getText()!=null) ? texto.getText().toString() : "");

						record.setRecordTypeId(tipoRecord);
						record.setRecordType(tipoRecord);
						record.setCreationDate(rec == null ? new Date() : rec.getCreationDate());
						record.setUpdateDate(new Date());
						try {
							LocalStorageServices.DatabaseService().setRecord(_id, record);
						} catch (Exception e) {
							e.printStackTrace();
						}

						boolean ok = true;
						Intent i = new Intent(getActivity(), BlurImage.class);
						Bundle bundle = new Bundle();
						if(rutaArchivo != null){
							bundle.putString("image_path", rutaArchivo);
							bundle.putString("image_path_original", record.getData());
							bundle.putString("faces_array", record.getFacesArray());
							bundle.putInt("record_id", record.getId());
							bundle.putString("record_creation_date", Services.Utilidades().DateToString(rec.getCreationDate()));
						}else{
							ok = false;
						}

						if(ok){
							i.putExtra("bundle_imagen", bundle);
							startActivityForResult(i, FaceDetector.BLUR_IMAGE);
							dialog.dismiss();
						}else{
							Toast.makeText(getActivity(), "Se ha producido un error. ", Toast.LENGTH_SHORT).show();
						}
					}

				});

				dialog.show();

			} catch (Exception e) {
				e.printStackTrace();
			}


		}
	
}
