package area.experiencias.tfg;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import area.LocalStorage.services.ApplicationService;
import area.experiencias.tfg.R;

/**
 * Created by adrianbouza on 19/06/14.
 */
public class TFGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationService.setContext(getApplicationContext());
        // Aquí se establece la configuración de la caché para imágenes de toda la aplicación

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.imagen)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
