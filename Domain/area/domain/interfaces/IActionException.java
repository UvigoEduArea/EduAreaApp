package area.domain.interfaces;

/**
 * 
 * @author luis
 *
 * @param <T>
 */
public interface IActionException<T> {

	/**
	 * Método a ejecutar tras una resultado correcto.
	 * @param param
	 */
	void Do(T param);
	
	/**
	 * Método a ejecutar tras producirse un error.
	 * @param e	Excepción que devuelve el error
	 */
	void Error(Exception e);
	
}
