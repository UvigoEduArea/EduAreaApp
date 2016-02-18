package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class MiniActivityList<T> extends ArrayList<T> implements MiniActivityListInterface {

    private ArrayList<MiniActivity> list;

    public MiniActivityList() {
        this.list = new ArrayList<MiniActivity>();
    }

    @Override
    public void saveActivity(MiniActivity activity) {
        this.list.add(activity);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<MiniActivity> getList() {
        return this.list;
    }

    @Override
    public void setImage(int id, Bitmap image) {
        for(MiniActivity mi : this.list){
            if(mi.getId() == id){
                mi.setImage(image);
                break;
            }
        }
    }
}
