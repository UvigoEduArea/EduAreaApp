package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 01/03/14.
 */
public class ReflectionList<T> extends ArrayList<T> implements ReflectionListInterface {

    private ArrayList<Reflection> list;

    public ReflectionList() {
        this.list = new ArrayList<Reflection>();
    }

    @Override
    public void saveReflection(Reflection reflection) {
        this.list.add(reflection);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<Reflection> getList() {
        return this.list;
    }
}
