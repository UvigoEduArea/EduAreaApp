package area.LocalStorage.implementacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import area.LocalStorage.interfaces.IExternalStorageService;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;

public class ExternalStorageService implements IExternalStorageService {

	
	public ExternalStorageService() {
	}
	
	/**
	 * Indica si la tarjeta de memoria externa está disponible.
	 * @return	true si lo está o false en caso contrario.
	 */
	private boolean sdDisponible(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			return true;
		return false;
	}
	
	/**
	 * Indica si la tarjeta de memoria externa es escribible.
	 * @return	true si lo es o false en caso contrario.
	 */
	private boolean sdEscribible(){
		String state = Environment.getExternalStorageState();
		if(!sdDisponible())
			return false;
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;
		return false;
	}
	

	/**
	 * Obtiene la ruta a la carpeta especificada.
	 * @param carpeta	Nombre de la carpeta.
	 * @return			Ruta completa a la carpeta.
	 */
	private String obtenerDir(String carpeta) {
		if (sdEscribible()){
			return ApplicationService.getContext().getExternalCacheDir()+File.separator+carpeta+File.separator;
		}
		else{
			return ApplicationService.getContext().getCacheDir()+File.separator+carpeta+File.separator;
		}
	}
	
	/**
	 *  Obtiene la ruta a la carpeta especificada en la memoria interna.
	 * @param carpeta 	Nombre de la carpeta.
	 * @return			Ruta completa a la carpeta.
	 */
	private String obtenerDirInterna(String carpeta){
		return ApplicationService.getContext().getCacheDir()+File.separator+carpeta+File.separator;
	}
	
	/**
	 * Obtiene la ruta base a la galería
	 * @return	Ruta completa a la galería
	 */
	private String getDirBaseGaleria(){
		if (sdEscribible()){
			return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
			
		}
		else{
			return ApplicationService.getContext().getCacheDir().toString();
		}
	}
	
	/**
	 * Escribe el contenido de un archivo en la ruta especificada.
	 * @param bytes						Contenido del archivo a guardar.
	 * @param dir						Ruta del directorio.
	 * @param ruta						Ruta completa del archivo.
	 * @throws IOException				Ocurre si hubo un problema de escritura.
	 */
	private void escribirArchivo(InputStream in, String dir,
			String ruta) throws IOException, Exception{
		FileOutputStream fOut;
		File directory = new File(dir);
		File f = new File(ruta);

		directory.mkdirs();
		if(!directory.canWrite()) throw new IOException("No se puede escribir");
		if(!f.exists())
		{
			boolean result = f.createNewFile();
			if(!result) throw new IOException("No se pudo crear el archivo");
		}
		fOut = new FileOutputStream(ruta);
		
		 byte[] buffer = new byte[1024];
         int len1 = 0;
         while ((len1 = in.read(buffer)) > 0) {
             fOut.write(buffer, 0, len1);
         }
		fOut.flush();
		fOut.close();
	}
	
	/**
	 * Escribe el contenido de un archivo en la ruta especificada.
	 * @param bm						Bitmap a guardar.
	 * @param dir						Ruta del directorio.
	 * @param ruta						Ruta completa del archivo.
	 * @throws IOException				Ocurre si hubo un problema de escritura.
	 */
	private void escribirArchivo(Bitmap bm, String dir,
			String ruta) throws IOException {
		FileOutputStream fOut;
		File directory = new File(dir);
		File f = new File(ruta);

		directory.mkdirs();
		if(!directory.canWrite()) throw new IOException("No se puede escribir");
		if(!f.exists())
		{
			boolean result = f.createNewFile();
			if(!result) throw new IOException("No se pudo crear el archivo");
		}
		fOut = new FileOutputStream(ruta);
		bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		fOut.flush();
		fOut.close();
	}
	
	/**
	 * Copia el archivo src a dst
	 * @param src			Archivo origen
	 * @param dst			Archivo destino
	 * @throws IOException	Lanza la excepción en caso de producirse
	 */
	public void copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	}
	
	
	
	@Override
	public String getDirVideos() {
		return obtenerDir("Videos");
	}
	
	@Override
	public String getDirImages() {
		return obtenerDir("Images");
	}

	@Override
	public String guardarVideo(InputStream in, String nombre) {
		String ruta = null;
		try {
			String dir = LocalStorageServices.ExternalStorage().getDirVideos();
			File directory = new File(dir);
			directory.mkdirs();
			if(!directory.canWrite())
				dir = obtenerDirInterna("Videos");
			ruta = dir + nombre;
			escribirArchivo(in, dir, ruta);
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return ruta;
	}

	@Override
	public String guardarImage(InputStream in, String nombre) {
		String ruta = null;
		try {
			String dir = LocalStorageServices.ExternalStorage().getDirImages();
			File directory = new File(dir);
			directory.mkdirs();
			if(!directory.canWrite())
				dir = obtenerDirInterna("Images");
			ruta = dir + nombre;
			escribirArchivo(in, dir, ruta);
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return ruta;
	}

	@Override
	public String guardarImagen(Bitmap bm, String nombre) {
		String ruta = null;
		try {
			String dir = LocalStorageServices.ExternalStorage().getDirImages();
			File directory = new File(dir);
			directory.mkdirs();
			if(!directory.canWrite())
				dir = obtenerDirInterna("Images");
			ruta = dir + nombre;
			escribirArchivo(bm, dir, ruta);
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return ruta;
	}

	@Override
	public String guardarVideo(String rutaOrigen, String nombre) {
		String ruta = null;
		File src,dst;
		try {
			String dir = LocalStorageServices.ExternalStorage().getDirVideos();
			File directory = new File(dir);
			directory.mkdirs();
			if(!directory.canWrite())
				dir = obtenerDirInterna("Videos");
			ruta = dir + nombre;
			src = new File(rutaOrigen);
			dst = new File(ruta);
			copy(src, dst);
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return ruta;
	}

	@Override
	public String escalarImagen(String ruta) {

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

		Bitmap bitmap = null;
		String nombre;
		
		try {
			File f = new File(ruta);
			nombre = f.getName();
			
			bmpFactoryOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(ruta, bmpFactoryOptions);
			bmpFactoryOptions.inJustDecodeBounds = false;
			if(bmpFactoryOptions.outWidth > 1200){
				bmpFactoryOptions.inJustDecodeBounds = false;
				bmpFactoryOptions.inSampleSize = 2;
			}
			if(bmpFactoryOptions.outWidth > 2048){
				bmpFactoryOptions.inJustDecodeBounds = false;
				bmpFactoryOptions.inSampleSize = 4;
			}
			bitmap = BitmapFactory.decodeFile(ruta, bmpFactoryOptions);
			
			return guardarImagen(bitmap, nombre);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean existeVideo(String nombre) {
		String ruta = null;
		try {
			String dir = LocalStorageServices.ExternalStorage().getDirVideos();
			File directory = new File(dir);
			directory.mkdirs();
			if(!directory.canWrite())
				dir = obtenerDirInterna("Videos");
			ruta = dir + nombre;
			File f = new File(ruta);
			return f.exists();
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
}
