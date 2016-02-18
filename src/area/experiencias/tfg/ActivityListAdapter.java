package area.experiencias.tfg;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import area.experiencias.tfg.R;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class ActivityListAdapter extends BaseAdapter {

    private  Context context;
    private  ArrayList<MiniActivity> list;

     class ActivityHolder{

        TextView title, description, start, end, desc, autor, numComentarios;
        ImageButton image,comentar;
        RelativeLayout lay;
        ImageView aviso, eliminar;

        public ActivityHolder(View rowView){
            title = (TextView) rowView.findViewById(R.id.activity_title);
            desc = (TextView) rowView.findViewById(R.id.activity_desc);
            image = (ImageButton) rowView.findViewById(R.id.imageActivity);
            comentar = (ImageButton) rowView.findViewById(R.id.commentButton);
            start = (TextView) rowView.findViewById(R.id.inicio);
            end = (TextView) rowView.findViewById(R.id.fin);
            lay = (RelativeLayout) rowView.findViewById(R.id.lay);
            aviso = (ImageView) rowView.findViewById(R.id.aviso);
            eliminar = (ImageView) rowView.findViewById(R.id.eliminar);
            autor = (TextView) rowView.findViewById(R.id.autor);
            numComentarios = (TextView) rowView.findViewById(R.id.numComentarios);
        }

        public void build(final MiniActivity miniActivity, final int position){
        	if (miniActivity == null) return;
//            if(miniActivity.getName() != null && miniActivity.getName().length() != 0) 
//            	title.setText(miniActivity.getName());
//            else 
//            	title.setText(context.getString(R.string.title));
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.imagen));
            if(miniActivity.getName() != null && miniActivity.getName().length() != 0){
            	if(miniActivity.getDefinition().equals(""))
            		title.setText(miniActivity.getName());
            	else
            		title.setText(miniActivity.getDefinition() + ": " + miniActivity.getName());
            }
            else 
            	title.setText(context.getString(R.string.title));
           
            String fecha = Services.Utilidades().viewDateToString(miniActivity.getUpdatedAt());
            autor.setText(fecha.equals("") ? miniActivity.getAutor() : miniActivity.getAutor() + "\n" + fecha);
            
            if(!LocalStorageServices.DatabaseService().isExperienceSinc(miniActivity.getId()) || miniActivity.getId() < 0)
        		aviso.setVisibility(View.VISIBLE);
        	else
        		aviso.setVisibility(View.INVISIBLE);
            if(ApplicationService.getPerfil().getType() == PerfilModel.STUDENT){
            	if(miniActivity.getDescriptionStudent() != null)
            		desc.setText(Html.fromHtml(miniActivity.getDescriptionStudent().length() != 0 ? miniActivity.getDescriptionStudent() : "Descripción"));
            }
            else{
            	
            	if(miniActivity.getDescription() != null)
            		desc.setText(Html.fromHtml(miniActivity.getDescription().length() != 0 ? miniActivity.getDescription() : "Descripción"));
            }
            
            if(ActivitySequenceFragment.editionMode && miniActivity.getPermiso() != null && !miniActivity.getPermiso().equals("viewer"))
        		eliminar.setVisibility(View.VISIBLE);
        	else
        		eliminar.setVisibility(View.GONE);
            
            if(miniActivity.getStart() != null && miniActivity.getStart().length() != 0) start.setText(miniActivity.getStart());
            else start.setHint(context.getString(R.string.activity_start));
            if(miniActivity.getEnd() != null && miniActivity.getEnd().length() != 0) end.setText(miniActivity.getEnd());
            else end.setHint(context.getString(R.string.activity_end));
            
            if(!miniActivity.getElement_image_file_name().contains("/system") && !miniActivity.getElement_image_file_name().contains("none"))
            	ImageLoader.getInstance().displayImage(Uri.fromFile(new File(miniActivity.getElement_image_file_name())).toString(), image);
            else{
	            if(miniActivity.getElement_image_file_name() != null && !miniActivity.getElement_image_file_name().contains("none")) {
	            	ImageLoader.getInstance().displayImage((WebService._BASE_URL + miniActivity.getElement_image_file_name().replace("/original/", "/medium/")), image);
	               
	            }else{
	            	try {
						
					
	                	image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources()
	                    , R.drawable.imagen), 8, context));
	                
		            } catch (OutOfMemoryError E){
						System.gc();
						Toast.makeText(context,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
						
					} catch (Exception e) {
						// TODO: handle exception
					}
	            }
            }
            lay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					lanzaActivityRecords(position);
				}
			});
            lay.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(v==image){
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            v.setAlpha(.5f);
                        }
                        else
                        {
                            v.setAlpha(1f);
                        }
                    }
                    return false;
                }
            });
            desc.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// Switching on what type of touch it was
                    switch (event.getAction()) 
                    {
                     // ACTION_UP and ACTION_DOWN together make up a click
                     // We're handling both to make sure we grab it
                        //case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                        {
                        	lanzaActivityRecords(position);
                            break;
                        }
                    }
                     
                    // Returning false means that we won't be handling any other input
                    // Any un-handled gestures are tossed out
                    return false;
				}
			});
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lanzaActivityRecords(position);
                }
            });
            image.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(v==image){
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            v.setAlpha(.5f);
                        }
                        else
                        {
                            v.setAlpha(1f);
                        }
                    }
                    return false;
                }
            });
            
            eliminar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					eliminarActividad(position);
					
				}
			});
            int n = LocalStorageServices.DatabaseService().getNumComentarios(miniActivity.getId());
            if(n > 0){
            	numComentarios.setText("" + n + "");
            }else{
            	numComentarios.setText("");
            }
            comentar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,CommentActivity.class);
                    intent.putExtra("model","activities");
                    intent.putExtra("id",miniActivity.getId());
                    intent.putExtra("title", miniActivity.getName());
                    //intent.putExtra("type", "");
                    context.startActivity(intent);
				}
			});
            
           
        }
    }
     
     private void eliminarActividad(final int posicion) {
    	 AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Aviso");
			alert.setMessage("¿Estás seguro que deseas eliminar esta actividad?");
			alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogConfirm, int which) {


					final ProgressDialog pd = new ProgressDialog(context);
					pd.setMessage("Borrando...");
					pd.setCancelable(false);
					pd.show();		    	
					WebServiceServices.Web().deleteAsync(list.get(posicion).getId(), "activities", new IAction<Boolean>() {

						@Override
						public void Do(Boolean param) {
							if (pd.isShowing()) {
								pd.dismiss();
							}
							list.remove(posicion);
							ActivityListAdapter.this.notifyDataSetInvalidated();
							ActivityListAdapter.this.notifyDataSetChanged();
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

    private  void lanzaActivityRecords(final int position) {
    	Intent intent = new Intent(context,ActivityViewDrawerActivity.class);
    	intent.putExtra("activity", list.get(position));
		intent.putExtra("position", position);
		intent.putExtra("id",list.get(position).getId());
		context.startActivity(intent);
		
	}

	public ActivityListAdapter(Context context, ArrayList<MiniActivity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ActivityHolder miniActivityHolder;
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_list_item, parent, false);
            miniActivityHolder = new ActivityHolder(convertView);
            convertView.setTag(miniActivityHolder);
        } else {
            miniActivityHolder = (ActivityHolder) convertView.getTag();
        }
        miniActivityHolder.build(list.get(position), position);
        return convertView;
    }

    
}
