package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 23/02/14.
 */
public class ExperienceList<T> extends ArrayList<T> implements ExperienceListInterface {

    private ArrayList<Experience> list;

    public ExperienceList() {
        this.list = new ArrayList<Experience>() ;
    }

    @Override
    public void saveExperience(Experience experience) {
        this.list.add(experience);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<Experience> getList() {
        return this.list;
    }

    @Override
    public void setImage(int id, Bitmap image) {
        for(Experience e : this.list){
            if(e.getId() == id){
                e.setImage(image);
                break;
            }
        }
    }
}
