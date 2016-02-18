package area.experiencias.tfg;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.FrameLayout;
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
import area.domain.modelo.EntregaModel;
import area.domain.modelo.PerfilModel;
import area.domain.modelo.RecursoModel;
import area.domain.services.Services;

import com.nostra13.universalimageloader.core.ImageLoader;
import area.experiencias.tfg.R;


public class EntregasListAdapter extends BaseAdapter {

    private  Context context;
    private  ArrayList<EntregaModel> list;

     class EntregaHolder{

        TextView title, description, start, end, desc, autor,numComentarios;
        ImageButton image,comentar;
        RelativeLayout lay;
        ImageView aviso, eliminar;
        FrameLayout frame;
        View row;

        public EntregaHolder(View rowView){
            title = (TextView) rowView.findViewById(R.id.activity_title);
            desc = (TextView) rowView.findViewById(R.id.activity_desc);
            image = (ImageButton) rowView.findViewById(R.id.imageActivity);
            start = (TextView) rowView.findViewById(R.id.inicio);
            comentar = (ImageButton) rowView.findViewById(R.id.commentButton);
            end = (TextView) rowView.findViewById(R.id.fin);
            lay = (RelativeLayout) rowView.findViewById(R.id.lay);
            frame = (FrameLayout) rowView.findViewById(R.id.frameLayout);
            aviso = (ImageView) rowView.findViewById(R.id.aviso);
            eliminar = (ImageView) rowView.findViewById(R.id.eliminar);
            autor = (TextView) rowView.findViewById(R.id.autor);
            numComentarios = (TextView) rowView.findViewById(R.id.numComentarios);
            row = rowView;
        }

        public void build(final EntregaModel entrega, final int position){
        	if (entrega == null) return;
        	if (entrega.isSeparador()){
        		row.setBackgroundColor(context.getResources().getColor(R.color.gris_claro));        		
        		if(entrega.getDefinition().equals(""))
            		title.setText(entrega.getTitulo());
            	else
            		title.setText(entrega.getDefinition() + ": " + entrega.getTitulo());
        		image.setVisibility(View.GONE);
        		desc.setVisibility(View.GONE);
        		aviso.setVisibility(View.GONE);
        		eliminar.setVisibility(View.GONE);
        		frame.setVisibility(View.GONE);
        		autor.setVisibility(View.GONE);
        		comentar.setVisibility(View.GONE);
        		numComentarios.setVisibility(View.GONE);
        		lay.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int id = list.get(position).getId();
						if(LocalStorageServices.DatabaseService().getExpirience(id) != null){
							Intent intent = new Intent(context, ExperienceViewDrawerActivity.class);
	                        intent.putExtra("id",id);
	                        context.startActivity(intent);
						}else{
							Intent intent = new Intent(context,ActivityViewDrawerActivity.class);
							intent.putExtra("id",id);
							context.startActivity(intent);
						}
						
					}
				});
        		return;
        	}
        	else{
        		row.setBackgroundColor(0x00000000);
        		row.setBackgroundColor(Color.TRANSPARENT);
        		frame.setVisibility(View.INVISIBLE);
        		image.setVisibility(View.VISIBLE);
        		desc.setVisibility(View.VISIBLE);
        		autor.setVisibility(View.VISIBLE);
        		numComentarios.setVisibility(View.VISIBLE);
        		comentar.setVisibility(View.VISIBLE);
        	}
            if(entrega.getTitulo() != null && entrega.getTitulo().length() != 0){
            	if(entrega.getDefinition().equals(""))
            		title.setText(entrega.getTitulo());
            	else
            		title.setText(entrega.getDefinition() + ": " + entrega.getTitulo());
            	}
            else {
            	title.setText(context.getString(R.string.title));
            }
           
            String fecha = Services.Utilidades().viewDateToString(entrega.getUpdatedAt());
            autor.setText(fecha.equals("") ? entrega.getAutor() : entrega.getAutor() + "\n" + fecha);
            
            
            if(!LocalStorageServices.DatabaseService().isActivitySinc(entrega.getId()))
        		aviso.setVisibility(View.VISIBLE);
        	else
        		aviso.setVisibility(View.INVISIBLE);
           
            if(entrega.getDescripcion() != null && entrega.getDescripcion().length() != 0){
            	desc.setText(Html.fromHtml(entrega.getDescripcion()));
            }
            else{
            	desc.setText("Descripción");
            }
            //if(ApplicationService.getPerfil().getType() != PerfilModel.STUDENT){
            	if(EntregasFragment.editionMode && !entrega.getPermiso().equals("viewer"))
            		eliminar.setVisibility(View.VISIBLE);
            	else
            		eliminar.setVisibility(View.GONE);
           // }
            
            if(!entrega.getImagen().contains("/system") && !entrega.getImagen().contains("none"))
            	ImageLoader.getInstance().displayImage(Uri.fromFile(new File(entrega.getImagen())).toString(), image);
            else{
	            if(entrega.getImagen() != null && !entrega.getImagen().contains("none")) {
	            	ImageLoader.getInstance().displayImage((WebService._BASE_URL + entrega.getImagen().replace("/original/", "/medium/")), image);
	                
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
					lanzaRecusrosViewActivity(position);
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
                        	lanzaRecusrosViewActivity(position);
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
                	lanzaRecusrosViewActivity(position);
                }
            });
            
            
            eliminar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					eliminarEntrega(position);
				}
			});
            int n = LocalStorageServices.DatabaseService().getNumComentarios(entrega.getId());
            if(n > 0){
            	numComentarios.setText("" + n + "");
            }else{
            	numComentarios.setText("");
            }
            comentar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,CommentActivity.class);
                    intent.putExtra("model","resource");
                    intent.putExtra("id",entrega.getId());
                    intent.putExtra("title", entrega.getTitulo());
                    //intent.putExtra("type", "");
                    context.startActivity(intent);
				}
			});
           
        }


    }

     
     private void eliminarEntrega(final int posicion) {
    		
    	 AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Aviso");
			alert.setMessage("¿Estás seguro que deseas eliminar esta tarea?");
			alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogConfirm, int which) {


					final ProgressDialog pd = new ProgressDialog(context);
					pd.setMessage("Borrando...");
					pd.setCancelable(false);
					pd.show();		    	
					WebServiceServices.Web().deleteAsync(list.get(posicion).getId(), "submissions", new IAction<Boolean>() {

						@Override
						public void Do(Boolean param) {
							if (pd.isShowing()) {
								pd.dismiss();
							}
							list.remove(posicion);
							EntregasListAdapter.this.notifyDataSetInvalidated();
							EntregasListAdapter.this.notifyDataSetChanged();
							
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
     
     
 
     private void lanzaRecusrosViewActivity(int posicion){
    	 Intent intent = new Intent(context,EntregaViewActivityNew.class);
     	 intent.putExtra("id", list.get(posicion).getId());
     	 context.startActivity(intent);
     }

	public EntregasListAdapter(Context context, ArrayList<EntregaModel> list) {
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
        EntregaHolder entregaHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_list_item, parent, false);
            entregaHolder = new EntregaHolder(convertView);
            convertView.setTag(entregaHolder);
        } else {
        	entregaHolder = (EntregaHolder) convertView.getTag();
        }
        entregaHolder.build(list.get(position), position);
        return convertView;
    }

    
}
