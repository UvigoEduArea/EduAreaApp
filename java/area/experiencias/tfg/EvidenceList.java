package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public class EvidenceList<T> extends ArrayList<T> implements EvidenceListInterface {

    private ArrayList<Evidence> list;

    public EvidenceList(){
        this.list = new ArrayList<Evidence>();
    }

    @Override
    public void saveEvidence(Evidence evidence) {
        this.list.add(evidence);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<Evidence> getList() {
        return this.list;
    }

    @Override
    public void setImage(int id, Bitmap bitmap) {
        for(Evidence e : list){
            if(e.getId() == id){
                e.setBitmap(bitmap);
            }
        }
    }
}
