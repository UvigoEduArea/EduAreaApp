package area.LocalStorage.interfaces;

import java.io.InputStream;

import android.graphics.Bitmap;

/**
 * Gestiona las peticiones para acceder al almacenamiento externo.
 * @author luis
 *
 */
public interface IExternalStorageService {
	
	/**
	 * Guarda un video en la memoria externa
	 * @param in			InputStream con los bytes a guardar
	 * @param nombre		Nombre del archivo
	 * @return				Ruta al video guardado.
	 */
	public String guardarVideo(InputStream in, String nombre);
	
	/**
	 * Devuelve la ruta local donde se almacenan los videos.
	 * @return
	 */
	public String getDirVideos();
	
	/**
	 * Devuelve la ruta local donde se almacenan las imagenes
	 * @return
	 */
	public String getDirImages();
	
	/**
	 * Guarda una imagen en la memoria externa
	 * @param in			InputStream con los bytes a guardar
	 * @param nombre		Nombre del archivo
	 * @return				Ruta a la imagen guardada.
	 */
	public String guardarImage(InputStream in, String nombre);
	
	/**
	 * Guarda una imagen en la memoria externa
	 * @param bm		Bitmap a guardar
	 * @param nombre	Nombre del archivo
	 * @return			Ruta a la imagen guardada.
	 */
	public String guardarImagen(Bitmap bm, String nombre);
	
	/**
	 * Guarda un vídeo en la carpeta de videos.
	 * @param rutaOrigen	Ruta original donde esta el video
	 * @param nombre		Nombre del archivo con que se va a guardar en la carpeta vídeos
	 * @return				Ruta al video guardado.
	 */
	public String guardarVideo(String rutaOrigen, String nombre);
	
	/**
	 * Obtiene una copia de la imagen de la ruta escalada
	 * @param ruta		Ruta en donde se encuentra la imagen
	 * @return			Ruta en donde se almacena la imagen escalada
	 */
	public String escalarImagen(String ruta);
	
	/**
	 * Comprueba si un video ya existe en local
	 * @param nombre 	Nombre del video a comprobar
	 * @return			True si existe, false en otro caso.
	 */
	public boolean existeVideo(String nombre);
}
