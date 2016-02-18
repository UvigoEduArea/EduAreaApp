package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public class EntryList<T> extends ArrayList<T> implements EntryListInterface {

    private ArrayList<Entry> list;

    public EntryList(){
        this.list = new ArrayList<Entry>();
    }

    @Override
    public void saveEntry(Entry entry) {
        this.list.add(entry);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<Entry> getList() {
        return this.list;
    }
}
