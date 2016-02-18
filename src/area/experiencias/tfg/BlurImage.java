package area.experiencias.tfg;

import org.opencv.core.Rect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import eduarea.facedetector.FaceDetector;

public class BlurImage extends FaceDetector {

	@Override
	protected void extractData(Bundle data) {
		//RECOGER EL ARRAYLIST EN STRING Y PASARLO A RECTS
		System.gc();
		if(data.getString("image_path_original") != null && !data.getString("image_path_original").equals("")){
			try{
				original = BitmapFactory.decodeFile(data.getString("image_path_original"));
				blur = BitmapFactory.decodeFile(data.getString("image_path"));
			
				String faces_array = data.getString("faces_array").replace("[", "").replace("]", "").trim();
				String[] value = TextUtils.split(faces_array, ",");
				for(int i = 0; i< value.length; i += 3){
					allFaces.add(new Rect(Integer.parseInt(value[i].replace("{","").trim()), Integer.parseInt(value[i+1].trim()), Integer.parseInt(value[i+2].split("x")[0].trim()) ,Integer.parseInt(value[i+2].split("x")[1].replace("}", "").replace(",", "").trim())));
				}
			}catch(OutOfMemoryError e){
				e.printStackTrace();
				try {
					
				
				original = BitmapFactory.decodeFile(data.getString("image_path_original"));
				blur = BitmapFactory.decodeFile(data.getString("image_path"));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			original = BitmapFactory.decodeFile(data.getString("image_path"));
			blur = original.copy(Bitmap.Config.ARGB_8888, false);
		}
		
		original_image.setImageBitmap(original);
		data_from_app = data;
		processImage();
	}

}
