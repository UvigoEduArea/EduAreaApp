package area.experiencias.tfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import area.experiencias.tfg.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.WebService;
import area.communication.services.WebServiceServices;
import area.domain.modelo.RecordModel;
import area.domain.services.Services;
//import area.experiencias.tfg.ExperienceViewActivity.DescriptionFragment;

/**
 * Adapter para la lista de Records
 * @author luis
 *
 */

public class RecordGridAdapter extends BaseAdapter {
	private  boolean mostrar = false;
	
    private  Context _context;
    private ArrayList<RecordModel> _list;    //private LayoutInflater inflater;

    public RecordGridAdapter(Context context, ArrayList<RecordModel> list) {
        _context = context;
        this._list = list;
    }

   
      class RecordHolder {
        TextView modificado, titulo, descripcion;
        ImageButton icono;
        RelativeLayout rl;
        WebView wv;
        ImageView aviso,tipoRecord,videoDescargado;
        
        public RecordHolder(View rowView) {
        	titulo = (TextView) rowView.findViewById(R.id.titulo);
        	descripcion = (TextView) rowView.findViewById(R.id.descripcion);
        	modificado = (TextView) rowView.findViewById(R.id.modificado);
        	icono = (ImageButton) rowView.findViewById(R.id.icono);
        	rl = (RelativeLayout) rowView.findViewById(R.id.recordItem);
        	wv = (WebView) rowView.findViewById(R.id.webView);
        	aviso = (ImageView) rowView.findViewById(R.id.aviso);
        	tipoRecord = (ImageView) rowView.findViewById(R.id.tipoRecord);
        	videoDescargado = (ImageView) rowView.findViewById(R.id.videoDescargado);
        	
        }

        public void build(final RecordModel record, final int position) {
        	if(record.isSeparador()){
        		titulo.setText(record.getTitle());
        		titulo.setMaxLines(5);
        		titulo.setBackgroundColor(_context.getResources().getColor(R.color.gris_claro));
        		descripcion.setVisibility(View.GONE);
        		modificado.setVisibility(View.INVISIBLE);
        		icono.setVisibility(View.INVISIBLE);
        		tipoRecord.setVisibility(View.GONE);
        		rl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int id = _list.get(position).getId();
						if(LocalStorageServices.DatabaseService().getExpirience(id) != null){
							Intent intent = new Intent(_context, ExperienceViewDrawerActivity.class);
	                        intent.putExtra("id",id);
	                        _context.startActivity(intent);
						}else{
							Intent intent = new Intent(_context,ActivityViewDrawerActivity.class);
							intent.putExtra("id",id);
							_context.startActivity(intent);
						}
						
					}
				});
        		return;
        	}else{
        		titulo.setBackgroundColor(0x00000000);
        		titulo.setMaxLines(1);
        		icono.setVisibility(View.VISIBLE);
        		modificado.setVisibility(View.VISIBLE);
        		tipoRecord.setVisibility(View.VISIBLE);
        		descripcion.setVisibility(View.VISIBLE);
        	}
        	titulo.setText(record.getTitle());
        	descripcion.setText(record.getDescription());
        	if(!LocalStorageServices.DatabaseService().isRecordSinc(record.getId()))
        		aviso.setVisibility(View.VISIBLE);
        	else
        		aviso.setVisibility(View.INVISIBLE);
        	if(mostrar)
        		wv.setVisibility(View.INVISIBLE);
        	else
        		wv.setVisibility(View.GONE);
        	modificado.setText((!record.getAutor().equals("")) ? record.getAutor() + "\n" +  Services.Utilidades().viewDateToString(record.getUpdateDate()) : Services.Utilidades().viewDateToString(record.getUpdateDate()));
        	//modificado.setText(Services.Utilidades().viewDateToString(record.getUpdateDate()));
        	icono.setClickable(false);
        	rl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(_context instanceof ActivityViewDrawerActivity)
						((ActivityViewDrawerActivity)_context).observacionesFragment.creaDialogo(record.getRecordTypeId(), record);
					if(_context instanceof ExperienceViewDrawerActivity)
						((ExperienceViewDrawerActivity)_context).observacionesFragment.creaDialogo(record.getRecordTypeId(), record);
					if(_context instanceof EntregaViewActivityNew)
						((EntregaViewActivityNew)_context).observacionesFragment.creaDialogo(record.getRecordTypeId(), record);
				}
			});
        	videoDescargado.setVisibility(View.INVISIBLE);
        	switch (record.getRecordTypeId()) {
				case RecordModel.RECORD_IMAGE:
					icono.setImageBitmap(null);
					descripcion.setVisibility(View.INVISIBLE);
					if(record.getBlurredImage()!= null && !record.getBlurredImage().equals("none") && !record.getBlurredImage().equals("") && !record.getBlurredImage().contains("/system")){

						ImageLoader.getInstance().displayImage(Uri.fromFile(new File(record.getBlurredImage())).toString(), icono);
					}else if(!record.getData().contains("/system")){
						
						ImageLoader.getInstance().displayImage(Uri.fromFile(new File(record.getData())).toString(), icono);
					}else {
						if(record.getBlurredImage()!= null && !record.getBlurredImage().equals("none") && !record.getBlurredImage().equals("")) {
		            		ImageLoader.getInstance().loadImage(WebService._BASE_URL + record.getBlurredImage().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
		            			@Override
		            			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		            				try {
										icono.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage,8,_context));
									
										icono.setBackgroundResource(android.R.color.transparent);
		            				} catch (OutOfMemoryError E){
    	    							System.gc();
    	    							Toast.makeText(_context,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
    	    							
									} catch (Exception e) {
										// TODO: handle exception
									}
		            			}
		            		});
							
						}else{
		            		ImageLoader.getInstance().loadImage(WebService._BASE_URL + record.getData().replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
		            			@Override
		            			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		            				try{
		            					icono.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage,8,_context));
		            					icono.setBackgroundResource(android.R.color.transparent);
		            				} catch (OutOfMemoryError E){
    	    							System.gc();
    	    							Toast.makeText(_context,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
    	    							
									} catch (Exception e) {
										// TODO: handle exception
									}
		            			}
		            		});

		            	}
					}
					tipoRecord.setImageResource(R.drawable.image_blue);
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadUrl(WebService._BASE_URL + record.getData());
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_VIDEO:
					descripcion.setVisibility(View.INVISIBLE);
					if (record != null && record.getData() != null){
						if(record.getLocalData() != null)
							videoDescargado.setVisibility(View.VISIBLE);
	    			}
					tipoRecord.setImageResource(R.drawable.film_blue);
					if(record != null && record.getVideoFrame() != null){
						
						ImageLoader.getInstance().loadImage(WebService._BASE_URL + record.getVideoFrame(), new SimpleImageLoadingListener() {
	            			@Override
	            			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	            				try{
	            					icono.setImageDrawable(_context.getResources().getDrawable(R.drawable.multimedia));
	            					icono.setBackground(new BitmapDrawable(_context.getResources(), ImageHelper.getRoundedCornerBitmap(loadedImage,8,_context)));
	            				} catch (OutOfMemoryError E){
	    							System.gc();
	    							Toast.makeText(_context,"Se ha producido un error al cargar la imagen. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
	    							
								} catch (Exception e) {
									// TODO: handle exception
								}
	            			}
	            			@Override
	            			public void onLoadingStarted(String imageUri,View view) {
	            				icono.setImageDrawable(_context.getResources().getDrawable(R.drawable.indicador_video));
	            				icono.setBackgroundResource(android.R.color.transparent);
	            			}
	            			@Override
	            			public void onLoadingFailed(String imageUri,
	            					View view, FailReason failReason) {
	            				icono.setImageDrawable(_context.getResources().getDrawable(R.drawable.indicador_video));
	            				icono.setBackgroundResource(android.R.color.transparent);
	            			}
	            		});
						
						
					}else{
						icono.setImageDrawable(_context.getResources().getDrawable(R.drawable.indicador_video));
						icono.setBackgroundResource(android.R.color.transparent);
					}
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadUrl(WebService._BASE_URL + record.getData());
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_DOCUMENT:
					descripcion.setVisibility(View.INVISIBLE);
					tipoRecord.setImageResource(R.drawable.file_text2_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadDataWithBaseURL(WebService._BASE_URL, record.getData(), "text/html; charset=UTF-8", null, null);
					wv.setVisibility(View.GONE);
					}
					break;
				case RecordModel.RECORD_SNIPPET:
					descripcion.setVisibility(View.INVISIBLE);
					tipoRecord.setImageResource(R.drawable.embed2_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadDataWithBaseURL(WebService._BASE_URL, record.getData(), "text/html; charset=UTF-8", null, null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_POSITIVE_COMMENT:
					descripcion.setVisibility(View.VISIBLE);
					tipoRecord.setImageResource(R.drawable.like_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_NEGATIVE_COMMENT:
					descripcion.setVisibility(View.VISIBLE);
					tipoRecord.setImageResource(R.drawable.like2_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_TEXT:
					descripcion.setVisibility(View.VISIBLE);
					tipoRecord.setImageResource(R.drawable.paragraph_justify_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_IDEA:
					descripcion.setVisibility(View.VISIBLE);
					tipoRecord.setImageResource(R.drawable.bulb_blue);
					icono.setImageResource(android.R.color.transparent);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				default:
					break;
			}
        	
        }
    }


    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
		RecordHolder holder;
		if (convertView == null || !(convertView.getTag() instanceof RecordHolder)) {
			LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_grid_item, parent,false);
			holder = new RecordHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RecordHolder) convertView.getTag();
		}
		holder.build(_list.get(position), position);

		return convertView;
	}

    public synchronized void refreshAdapter(ArrayList<RecordModel> items) {
        _list.clear();
        _list.addAll(items);
        notifyDataSetChanged();
    }
}