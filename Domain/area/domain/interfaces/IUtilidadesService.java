package area.domain.interfaces;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

public interface IUtilidadesService {

	/**
	 * Devuelve el MD5 de una cadena de caracteres
	 * @param s								String de cual se devolverá su MD5
	 * @return								MD5 de la cadena s
	 * @throws NoSuchAlgorithmException		Ocurre cuando el algoritmo de java.security no está disponible
	 */
	public String md5(String s) throws NoSuchAlgorithmException;
	
	/**
	 * Prepara el cuadro de diálogo de progreso mientras se cargan datos en segundo plano
	 * @param contexto		Context del activity que lanza la barra de progreso
	 */
	public void preparaProgress(Context contexto);
	
	/**
	 * Actualiza la barra de progreso al porcentage indicado
	 * @param i		Porcentage al que se actualiza la barra de progreso
	 */
	public void actualizaProgress(int i);
	
	/**
	 * Cierra la barra de progreso que esta activa
	 */
	public void finalizaProgress();
	
	/**
	 * Pasa un string con la fecha y hora a un objeto tipo Date
	 * @param fecha		String con la fecha
	 * @return			Objeto Date con la fecha indicada en el string
	 */
	public Date StringToDate(String fecha);
	
	/**
	 * Pasa una fecha de un objeto Date a string
	 * @param date		Fecha a convertir a string
	 * @return			String con la fecha dada
	 */
	public String DateToString(Date date);
	
	/**
	 * Pasa una fecha de un objeto Date a string para poder enseñarlo por pantalla
	 * @param date 	Fecha a convertir a string
	 * @return		String con la fecha con formato para mostrar por pantalla
	 */
	public String viewDateToString(Date date);
	
	public String viewDateToString(String date);
	
	/**
	 * Comprueba si se trata de un archivo local
	 * @param url		Uri a comprobar
	 * @return			True si es local, false en otro caso
	 */
	public boolean isLocalFile(String url);
	
	/**
	 * Muestra una rueda de progress
	 * @param contexto
	 */
	public void showProgress(Context contexto);
	
	/**
	 * Finaliza la rueda de progreso
	 */
	public void finishProgress();
	
	/**
	 * Comprueba si existe el archivo especificado
	 * @param path		ruta del archivo
	 * @return	true si existe false en otro caso
	 */
	public boolean fileExists(String path);
	
	/**
	 * Cancela la sincroinización de los elementos que estén por sincronizar en la tabla de soncronización
	 * @param contexto 
	 */
	public void cancelarSinc(Context contexto);
	
	/**
	 * Vuelve a la pantalla de la lista de cursos
	 * @param contexto
	 */
	public void volverMenuPrincipal(Context contexto);
	
	/**
	 * Descarga completa de una experiencia
	 * @param contexto		Contexto para mostrar el diálogo
	 * @param expId			Id de la experiencia a descargar
	 */
	public void descargar(Context contexto, int expId);
	
	/**
	 * Obtiene el codigo de la experiencia para poder compartirla
	 * @param contexto		Contexto para mostrar el diálogo
	 * @param expId			Id de la experiencia de la que se quiere el código
	 */
	public void getCode(Context contexto, int expId);
	
	
}
