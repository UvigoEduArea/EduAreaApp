package area.experiencias.tfg;


import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.implementation.WebService;
import area.domain.modelo.AnexosRecursoModel;
import area.domain.modelo.RecursoModel;
import area.domain.services.Services;

import com.nostra13.universalimageloader.core.ImageLoader;
import area.experiencias.tfg.R;


public class AnexoListAdapter extends BaseAdapter {

    private  Context context;
    private  ArrayList<AnexosRecursoModel> list;

     class AnexoHolder{

        TextView nombre,titulo,fecha;
        Button botonDescargar;
        ImageButton icono;

        public AnexoHolder(View rowView){
            nombre = (TextView) rowView.findViewById(R.id.descripcion);
            botonDescargar = (Button) rowView.findViewById(R.id.botonDescargar);
            titulo = (TextView) rowView.findViewById(R.id.titulo);
            fecha = (TextView) rowView.findViewById(R.id.modificado);
            icono = (ImageButton) rowView.findViewById(R.id.icono);
        }

        public void build(final AnexosRecursoModel anexo, final int position){
        	if (anexo == null) return;
        	fecha.setVisibility(View.INVISIBLE);
        	titulo.setVisibility(View.INVISIBLE);
        	botonDescargar.setVisibility(View.GONE);
        	if (anexo.getSnippetUrl() != null && !anexo.getSnippetUrl().equals("")){
        		botonDescargar.setVisibility(View.GONE);
        		try{
	        		Matcher m = Patterns.WEB_URL.matcher(anexo.getSnippetUrl());
	        		m.find();
	        		nombre.setText(m.group());
	        		icono.setImageResource(R.drawable.embed2_white);
        		}catch (Exception e) {
					nombre.setText("fileName");
				}
        	}
        	if(anexo.getFileName() != null && !anexo.getFileName().equals("")){
        		nombre.setText(anexo.getFileName());
        		
        		if(Services.Utilidades().fileExists(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
        											+ "/" + anexo.getFileName()))
        			botonDescargar.setText(context.getString(R.string.abrir));
        		else
        			botonDescargar.setText(context.getString(R.string.save));
        		botonDescargar.setVisibility(View.VISIBLE);
        		icono.setImageResource(R.drawable.file_text2_white);
        	}
        	if(anexo.getDireccion()!=null && !anexo.getDireccion().equals("")){
        		nombre.setText(anexo.getDireccion());
        		icono.setImageResource(R.drawable.location2);
        		
        	}
        		
          botonDescargar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Services.Utilidades().fileExists(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
													+ "/"  + anexo.getFileName())){
					DescargaArchivo(WebService._BASE_URL + anexo.getDocument(), anexo.getFileName());
					Toast.makeText(context,context.getString(R.string.descargandoArchivo), Toast.LENGTH_SHORT).show();
				}
				else{
					AbreArchivo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"  + anexo.getFileName());
				}
				
			}

			
		});
            
            
            
            
           
        }
        
        
        private void DescargaArchivo(String url, final String fileName){
       	 
       	 DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
       	 request.setDescription(context.getString(R.string.descargando)+ " " + fileName);
       	 request.setTitle("Edu-AREA");
       	 // in order for this if to run, you must use the android 3.2 to compile your app
       	 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
       	     request.allowScanningByMediaScanner();
       	     request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
       	 }
       	 request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

       	 // get download service and enqueue file
       	 DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
       	 manager.enqueue(request);
       	 
       	 context.registerReceiver(new BroadcastReceiver(){

   			@Override
   			public void onReceive(Context context, Intent intent) {
   				//AbreArchivo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"  + fileName);
   				botonDescargar.setText(context.getString(R.string.abrir));
   				
   			}
       		 
       	 }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
       	 
        }



    }
     private void AbreArchivo(String ruta) {
    	 Intent myIntent = new Intent(Intent.ACTION_VIEW);
    	 File file = new File(ruta);
    	 myIntent.setData(Uri.fromFile(file));
    	 Intent j = Intent.createChooser(myIntent, context.getString(R.string.noHayApp));
    	 context.startActivity(j);
			
		}
 
     private void lanzaRecusrosViewActivity(int posicion){
    	 Intent intent = new Intent(context,RecursoViewAvtivity.class);
     	 intent.putExtra("id", list.get(posicion).getId());
     	 context.startActivity(intent);
     }
     
    
	public AnexoListAdapter(Context context, ArrayList<AnexosRecursoModel> list) {
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
        AnexoHolder anexoHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.record_item, parent, false);
            anexoHolder = new AnexoHolder(convertView);
            convertView.setTag(anexoHolder);
        } else {
        	anexoHolder = (AnexoHolder) convertView.getTag();
        }
        anexoHolder.build(list.get(position), position);
        return convertView;
    }

    
}
