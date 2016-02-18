package area.experiencias.tfg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.WebService;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.modelo.PerfilModel;
import area.domain.services.Services;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import area.experiencias.tfg.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by adrianbouza on 23/02/14.
 */
public class ExperienceListAdapter extends BaseAdapter {

    private  Context context;
    private final ArrayList<Experience> list;

     class ExperienceHolder{
        TextView title,definition, autor, numComentarios;
        WebView description;
        ImageView image, aviso, updated, eliminar;
        ImageButton commentButton;

        public ExperienceHolder(View rowView){
            title = (TextView) rowView.findViewById(R.id.title);
            description = (WebView) rowView.findViewById(R.id.description);
            image = (ImageView) rowView.findViewById(R.id.image);
            commentButton = (ImageButton) rowView.findViewById(R.id.commentButton);
            aviso = (ImageView) rowView.findViewById(R.id.aviso);
            updated = (ImageView) rowView.findViewById(R.id.atDate);
            definition = (TextView) rowView.findViewById(R.id.definition);
            eliminar = (ImageView) rowView.findViewById(R.id.eliminar);
            autor = (TextView) rowView.findViewById(R.id.autor);
            numComentarios = (TextView) rowView.findViewById(R.id.numComentarios);
            
        }

        public void build(final Experience experience, final int position){
        	try{
        		if(!experience.getDefinition().equals("")){ 
        			definition.setVisibility(View.VISIBLE);
        			definition.setText(experience.getDefinition());
        		}
        		else{
        			definition.setText("");
        			definition.setVisibility(View.GONE);
        		}
        		
	            if(experience.getName().length() != 0) title.setText(experience.getName());
	            else {
	                title.setHint(context.getString(R.string.title));
	                title.setText("");
	            }
	            if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT ){ 
	            	description.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	            	description.loadData(experience.getDescriptionStudent().length() != 0 ? experience.getDescriptionStudent() : "Descripción", "text/html; charset=UTF-8", null);
	            }
	            else {
	            	description.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	            	description.loadData(experience.getDescription().length() != 0 ? experience.getDescription() : "Descripción", "text/html; charset=UTF-8", null);
	            	
	            }
	            if(ExperiencesActivity.editionMode && experience.getPermiso().equals("owner"))
            		eliminar.setVisibility(View.VISIBLE);
            	else
            		eliminar.setVisibility(View.GONE);
	            
	            
	            String fecha = Services.Utilidades().viewDateToString(experience.getUpdatedAt());
	            autor.setText(fecha.equals("") ? experience.getAutor() : experience.getAutor() + "\n" + fecha);
	            
	            if(!LocalStorageServices.DatabaseService().isExperienceSinc(experience.getId()))
	        		aviso.setVisibility(View.VISIBLE);
	        	else
	        		aviso.setVisibility(View.INVISIBLE);
	            
	            
	            if(!LocalStorageServices.DatabaseService().getExperienceIsUpdated(experience.getId()) && LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(experience.getId())){
	            	updated.setVisibility(View.VISIBLE);
	            	updated.setImageResource(R.drawable.descargado_naranja);
	            	WebServiceServices.Download().downloadActivity(experience.getId());
	            }else{
	            	if(LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(experience.getId()) && !ApplicationService.isDowloading() ){
		            	updated.setVisibility(View.VISIBLE);
		            	updated.setImageResource(R.drawable.descargado_blue);
	            	}
	            	else{
	            		if(LocalStorageServices.DatabaseService().getExperienceMantenerActualizado(experience.getId()) && ApplicationService.isDowloading()){
	            			updated.setVisibility(View.VISIBLE);
	    	            	updated.setImageResource(R.drawable.descargado_naranja);
	            		}
		            	else{
		            		updated.setVisibility(View.INVISIBLE);
		            	}
	            	}
	            }
	            		
	            
	            		
	            		
	            if(!experience.getElement_image_file_name().contains("/system") && !experience.getElement_image_file_name().contains("none"))
	            	ImageLoader.getInstance().displayImage(Uri.fromFile(new File(experience.getElement_image_file_name())).toString(), image);
	            else{
		            if(!experience.getElement_image_file_name().contains("none")) {
		                ImageLoader.getInstance().loadImage(WebService._BASE_URL +
		                        experience.getElement_image_file_name().replace("original", "medium"), new SimpleImageLoadingListener(){
		                    @Override
		                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		                    	try {						
		                        	image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage,8,context));
			                    } catch (OutOfMemoryError E){
	    							System.gc();
	    							Toast.makeText(context,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
	    							
								} catch (Exception e) {
									// TODO: handle exception
								}
		                    }
		                });
		            }else{
		                image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources()
		                        , R.drawable.imagen), 8, context));
		            }
	            }
	            commentButton.setFocusable(false);
	            commentButton.setFocusableInTouchMode(false);
	            int n = LocalStorageServices.DatabaseService().getNumComentarios(experience.getLessonPlanId());
	            if(n > 0){
	            	numComentarios.setText("" + n + "");
	            }else{
	            	numComentarios.setText("");
	            }
	            
	            final int id = experience.getLessonPlanId();
	            commentButton.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    Intent intent = new Intent(context,CommentActivity.class);
	                    intent.putExtra("model","Experience");
	                    intent.putExtra("id",id);
	                    intent.putExtra("title", experience.getName());
	                    intent.putExtra("type", R.string.experience);
	                    context.startActivity(intent);
	                }
	            });
	            description.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
	                    switch (event.getAction()) 
	                    {
	                     // ACTION_UP and ACTION_DOWN together make up a click
	                     // We're handling both to make sure we grab it
	                        //case MotionEvent.ACTION_DOWN:
	                        case MotionEvent.ACTION_UP:
	                        {
	                        	 Intent intent = new Intent(context, ExperienceViewDrawerActivity.class);
	                             intent.putExtra("id",experience.getId());
	                             context.startActivity(intent);
	                            break;
	                        }
	                    }
	                    return false;
					}
				});
	            
	            eliminar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						eliminarExperience(position);
						
					}

					
				});
	            
        }catch(Exception e){
        	e.printStackTrace();
        }
            
            
        }

    }
     
     
     private void eliminarExperience(final int posicion) {
    	 AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Aviso");
			alert.setMessage("¿Estás seguro que deseas eliminar esta curso y todo su contenido?");
			alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogConfirm, int which) {


					final ProgressDialog pd = new ProgressDialog(context);
					pd.setMessage("Borrando...");
					pd.setCancelable(false);
					pd.show();		    	
					WebServiceServices.Web().deleteAsync(list.get(posicion).getId(), "experiences", new IAction<Boolean>() {

						@Override
						public void Do(Boolean param) {
							if (pd.isShowing()) {
								pd.dismiss();
							}
							list.remove(posicion);
							ExperienceListAdapter.this.notifyDataSetInvalidated();
							ExperienceListAdapter.this.notifyDataSetChanged();
						}
					});

					dialogConfirm.dismiss();
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
     

    public ExperienceListAdapter(Context context, ArrayList<Experience> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExperienceHolder experienceHolder;
        try{
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.experience_list_item, parent, false);
	            experienceHolder = new ExperienceHolder(convertView);
	            convertView.setTag(experienceHolder);
	        } else {
	            experienceHolder = (ExperienceHolder) convertView.getTag();
	        }
	        experienceHolder.build(list.get(position), position);
        return convertView;
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
}
