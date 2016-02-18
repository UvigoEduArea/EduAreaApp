package area.communication.implementation;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;

import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.exception.WebServiceException;
import area.communication.interfaces.IDownloadService;
import area.communication.services.WebServiceServices;
import area.domain.services.Services;

public class DownloadService implements IDownloadService{

	@Override
	public void downloadActivity(int id) {
		
		class DescargarElemento implements Runnable{
			
			private int _id;
			
			public DescargarElemento(int id) {
				_id = id;
			}
			@Override
			public void run() {
				try {
					ApplicationService.setDownload(true);
					WebServiceServices.Web().getFullExperience(_id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ApplicationService.setDownload(false);
			}
			
		}
		DescargarElemento tarea = new DescargarElemento(id);
		new Thread(tarea).start();
		
	}

	

}
