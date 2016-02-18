package area.domain.services;

import area.domain.implementacion.UtilidadesService;
import area.domain.interfaces.IUtilidadesService;

/**
 * Clase donde se instancian los servicios
 * @author luis
 *
 */
public class Services {

	private static IUtilidadesService _utilidades;
	
	
	public static IUtilidadesService Utilidades(){
		if(_utilidades == null) _utilidades = new UtilidadesService();
		return _utilidades;
	}
}
