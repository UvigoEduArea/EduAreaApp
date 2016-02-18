package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public class CommentList<T> extends ArrayList<T> implements CommentListInterface {

    private ArrayList<Comment> list;

    public CommentList(){
        this.list = new ArrayList<Comment>();
    }

    @Override
    public void saveComment(Comment comment) {
        this.list.add(comment);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<Comment> getList() {
        return this.list;
    }
}
