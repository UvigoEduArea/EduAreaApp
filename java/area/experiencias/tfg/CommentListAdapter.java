package area.experiencias.tfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by adrianbouza on 01/03/14.
 */
public class CommentListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Comment> list;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public CommentListAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.comment_item, parent, false);
        TextView comment, user, update;
        comment = (TextView) rowView.findViewById(R.id.comment);
        comment.setText(list.get(position).getComment());
        user = (TextView) rowView.findViewById(R.id.user);
        user.setText(list.get(position).getUser());
        update = (TextView) rowView.findViewById(R.id.update);
        update.setText(format.format(list.get(position).getDate()));
        return rowView;
    }
}
