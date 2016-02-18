package area.communication.services;

import area.communication.implementation.DownloadService;
import area.communication.implementation.SincronizadorService;
import area.communication.implementation.WebService;
import area.communication.interfaces.IDownloadService;
import area.communication.interfaces.ISincronizador;
import area.communication.interfaces.IWebService;

public class WebServiceServices {

	private static IWebService _webService = null;
	private static ISincronizador _sincronizador = null;
	private static IDownloadService _download = null;
	
	public static IWebService Web(){
		if(_webService == null) _webService = new WebService();
		return _webService;
	}
	
	public static ISincronizador Sincronizador(){
		if(_sincronizador == null) _sincronizador = new SincronizadorService();
		return _sincronizador;
	}
	
	
	public static IDownloadService Download(){
		if(_download == null) _download = new DownloadService();
		return _download;
	}
	
}
