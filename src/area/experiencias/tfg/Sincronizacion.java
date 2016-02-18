package area.experiencias.tfg;
//
//import java.lang.reflect.Method;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//import area.LocalStorage.services.LocalStorageServices;
//import area.communication.implementation.CompruebaSincronizacion;
//
//public class Sincronizacion extends CompruebaSincronizacion{
//	int count = 0;
//	private Context _context;
//	private MenuItem _synchonize,_downloading;
//	private TextView _avisoModoLocal;
//	
//	
//	
//	public void setSincronizacion(Context context, MenuItem synchonize, MenuItem downloading, TextView avisoModoLocal) {
//		_context = context;
//		_synchonize = synchonize;
//		_downloading = downloading;
//		_avisoModoLocal = avisoModoLocal;
//	}
//	
//	
//	@Override
//	public void avisar(final boolean desincronizado) {
//		if (_synchonize == null) return;
//		((Activity)_context).runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(count != LocalStorageServices.DatabaseService().getCountSinc()){
//					count = LocalStorageServices.DatabaseService().getCountSinc();
//					 //if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.refeshList();
//					 //if(observacionesFragment != null && observacionesFragment.isAdded()) observacionesFragment.refeshList();
//					 //updateUI();
//					 
//				}
//				if (desincronizado)
//					_synchonize.setVisible(true);
//				else
//					_synchonize.setVisible(false);
//				
//				
//			}
//		});
//		
//	}
//
//	@Override
//	public void descargando(final boolean descargando) {
//		if (_downloading == null) return;
//		((Activity)_context).runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				if (descargando)
//					_downloading.setVisible(!_downloading.isVisible());
//				else
//					_downloading.setVisible(false);
//			}
//		});
//	}
//	
//	@Override
//	public void modoLocal(final boolean modoLocal) {
//		if (_avisoModoLocal == null) return;
//		((Activity)_context).runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				if (modoLocal)
//					_avisoModoLocal.setVisibility(View.GONE);
//				else
//					_avisoModoLocal.setVisibility(View.VISIBLE);
//			}
//		});
//	}
//}
