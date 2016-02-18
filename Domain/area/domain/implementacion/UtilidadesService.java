package area.domain.implementacion;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.interfaces.IUtilidadesService;
import area.experiencias.tfg.ExperienceViewDrawerActivity;
import area.experiencias.tfg.ExperiencesActivity;
import area.experiencias.tfg.LoginActivity;
import area.experiencias.tfg.R;

public class UtilidadesService implements IUtilidadesService{

	private Context _contexto;
	private ProgressDialog progress;
	private ProgressBar progressBar;
	
	
	@Override
	public String md5(String s) throws NoSuchAlgorithmException {
	    try {
	    	MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 0x10)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	        throw e;
	    }
	}

	@Override
	public void preparaProgress(Context contexto) {
		_contexto = contexto;
		 progress = new ProgressDialog(_contexto);
         progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         progress.setMessage(ApplicationService.getContext().getString(R.string.loading_experiences));
         progress.setCancelable(false);
         progress.setMax(100);
         progress.setProgress(0);
         progress.show();
	}

	@Override
	public void actualizaProgress(int i) {
		 if (i < 60) progress.setMessage(ApplicationService.getContext().getString(R.string.preparing_data));
         if (i == 60) progress.setMessage(ApplicationService.getContext().getString(R.string.waiting_server_response));
         if (i > 60) progress.setMessage(ApplicationService.getContext().getString(R.string.starting));
         progress.setProgress(i);
	}

	@Override
	public void finalizaProgress() {
		progress.dismiss();
	}

	@Override
	public Date StringToDate(String fecha) {
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
			try {
				return df1.parse(fecha);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
	}

	@Override
	public String DateToString(Date date) {
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
		return df1.format(date);
	}

	@Override
	public String viewDateToString(Date date) {
		DateFormat df1 = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault());
		return df1.format(date);
	}
	
	@Override
	public String viewDateToString(String date) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
		DateFormat df1 = new SimpleDateFormat("dd-MM-y HH:mm", Locale.getDefault());
		
		try {
			return df1.format(df.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public boolean isLocalFile(String file) {
	    try {
	        new URL(file);
	        return false;
	    } catch (MalformedURLException e) {
	        return true;
	    }
	}

	@Override
	public void showProgress(Context contexto) {
		_contexto = contexto;
		 progressBar = new ProgressBar(_contexto);
		 progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void finishProgress() {
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public boolean fileExists(String path) {
		File file = new File(path);
		if(file.exists())
			return true;
		
		return false;
	}

	@Override
	public void cancelarSinc(final Context contexto) {

    	AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
		alert.setTitle(contexto.getResources().getString(R.string.aviso));
		alert.setMessage(contexto.getResources().getString(R.string.avisoForzarSinc));
		alert.setPositiveButton(contexto.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialogConfirm, int which) {

				final ProgressDialog pd = new ProgressDialog(contexto);
				pd.setMessage(contexto.getResources().getString(R.string.forzandoSinc));
				pd.setCancelable(false);
				pd.show();		    	

				try {
					WebServiceServices.Sincronizador().parar();
            		LocalStorageServices.DatabaseService().deleteSincronizacion();
            		
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
				dialogConfirm.dismiss();
				
				pd.dismiss();
			}
		});

		alert.setNegativeButton(contexto.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}

	@Override
	public void volverMenuPrincipal(Context contexto) {
		Intent intent = new Intent(contexto, ExperiencesActivity.class);
     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        contexto.startActivity(intent);
	}
	
	

	@Override
	public void descargar(Context contexto,  final int expId) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
		alert.setTitle(contexto.getResources().getString(R.string.aviso));
		alert.setMessage(contexto.getResources().getString(R.string.avisoDescargarCurso));
		alert.setPositiveButton(contexto.getResources().getString(R.string.descargar_y_actualizar),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogConfirm,
							int which) {
						LocalStorageServices.DatabaseService().setExperienceMantenerActualizado(1, expId);
						WebServiceServices.Download().downloadActivity(expId);
						dialogConfirm.dismiss();
						
					}
				});

		alert.setNegativeButton(contexto.getResources().getString(R.string.no_actualizar),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						LocalStorageServices.DatabaseService().setExperienceMantenerActualizado(0, expId);
						dialog.dismiss();
						
						
					}
				});
		alert.setNeutralButton(contexto.getResources().getString(R.string.descargar_solo_ahora), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialogConfirm, int which) {
				LocalStorageServices.DatabaseService().setExperienceMantenerActualizado(0, expId);
				WebServiceServices.Download().downloadActivity(expId);
				dialogConfirm.dismiss();
				
			}
		});
		alert.show();		
	}

	@Override
	public void getCode(final Context contexto, int expId) {
		final ProgressDialog pd = new ProgressDialog(contexto);
		pd.setMessage(contexto.getResources().getString(R.string.obteniendo_codigo));
		pd.setCancelable(false);
		pd.show();
		WebServiceServices.Web().getExperienceCode(expId, new IAction<String>() {

			@Override
			public void Do(String param) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
				if (param != null && !param.equals(""))
					muestraCodigo(contexto,param);
				else
					muestraCodigo(contexto, contexto.getResources().getString(R.string.codigo_no_disponible));
			}
		});
	}
	
	private void muestraCodigo(Context contexto, String codigo){
		AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
		alert.setTitle(contexto.getResources().getString(R.string.codigo_a_compartir));
		alert.setMessage(codigo);
		alert.setNeutralButton(contexto.getResources().getString(R.string.cerrar), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}

	
	

}
