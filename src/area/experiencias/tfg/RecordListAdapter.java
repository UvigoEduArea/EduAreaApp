package area.experiencias.tfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import area.experiencias.tfg.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class RecordListAdapter extends BaseAdapter {
	private  boolean mostrar = false;
	
    private  Context _context;
    private ArrayList<RecordModel> _list;
    
    public RecordListAdapter(Context context, ArrayList<RecordModel> list) {
        _context = context;
        this._list = list;
    }

   
     class RecordHolder {
        TextView modificado, titulo, descripcion;
        ImageButton icono;
        RelativeLayout rl;
        WebView wv;
        ImageView aviso,videoDescargado;
        
        public RecordHolder(View rowView) {
        	titulo = (TextView) rowView.findViewById(R.id.titulo);
        	descripcion = (TextView) rowView.findViewById(R.id.descripcion);
        	modificado = (TextView) rowView.findViewById(R.id.modificado);
        	icono = (ImageButton) rowView.findViewById(R.id.icono);
        	rl = (RelativeLayout) rowView.findViewById(R.id.recordItem);
        	wv = (WebView) rowView.findViewById(R.id.webView);
        	aviso = (ImageView) rowView.findViewById(R.id.aviso);
        	videoDescargado = (ImageView) rowView.findViewById(R.id.videoDescargado);
        }

        public void build(final RecordModel record, final int position) {
        	
        	if(record.isSeparador()){
        		titulo.setText(record.getTitle());
        		icono.setVisibility(View.GONE);
        		modificado.setVisibility(View.GONE);
        		descripcion.setVisibility(View.GONE);
        		rl.setBackgroundColor(_context.getResources().getColor(R.color.gris_claro));  
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
        		rl.setBackgroundColor(0x00000000);
        		icono.setVisibility(View.VISIBLE);
        		modificado.setVisibility(View.VISIBLE);
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

					icono.setImageResource(R.drawable.image_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadUrl(WebService._BASE_URL + record.getData());
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_VIDEO:
					if (record != null && record.getData() != null){
						if(record.getLocalData() != null)
	    					videoDescargado.setVisibility(View.VISIBLE);
	    			}
					icono.setImageResource(R.drawable.film_blue);
					icono.setScaleType(ScaleType.CENTER_CROP);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadUrl(WebService._BASE_URL + record.getData());
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_DOCUMENT:
					icono.setImageResource(R.drawable.file_text2_blue);
					icono.setScaleType(ScaleType.CENTER_CROP);
					icono.setBackgroundResource(android.R.color.transparent);
					if(mostrar){
					wv.loadDataWithBaseURL(WebService._BASE_URL, record.getData(), "text/html; charset=UTF-8", null, null);
					wv.setVisibility(View.GONE);
					}
					break;
				case RecordModel.RECORD_SNIPPET:
					icono.setImageResource(R.drawable.embed2_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
					if(mostrar){
					wv.getSettings().setJavaScriptEnabled(true);
					wv.loadDataWithBaseURL(WebService._BASE_URL, record.getData(), "text/html; charset=UTF-8", null, null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_POSITIVE_COMMENT:
					icono.setImageResource(R.drawable.like_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_NEGATIVE_COMMENT:
					icono.setImageResource(R.drawable.like2_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_TEXT:
					icono.setImageResource(R.drawable.paragraph_justify_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
					if(mostrar){
					wv.loadData(record.getData(), "text/html; charset=UTF-8", null);
					wv.setVisibility(View.VISIBLE);
					}
					break;
				case RecordModel.RECORD_IDEA:
					icono.setImageResource(R.drawable.bulb_blue);
					icono.setBackgroundResource(android.R.color.transparent);
					icono.setScaleType(ScaleType.CENTER_CROP);
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
			convertView = inflater.inflate(R.layout.record_item, parent,false);
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