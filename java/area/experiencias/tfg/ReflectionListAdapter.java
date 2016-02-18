package area.experiencias.tfg;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by adrianbouza on 01/03/14.
 */
public class ReflectionListAdapter extends BaseAdapter {

    public static final String RESPONSE_NEGATIVE = "Negative comment";
    public static final String RESPONSE_POSITIVE = "Positive comment";

    private static Context context;
    private ArrayList<Reflection> list;
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault());

    public ReflectionListAdapter(Context context, ArrayList<Reflection> list) {
        this.context = context;
        this.list = list;
    }

    static class ReflectionHolder{
        TextView update, text;
        ImageButton commentButton;
        ImageView icon;

        public ReflectionHolder(View rowView){
            update = (TextView) rowView.findViewById(R.id.update);
            text = (TextView) rowView.findViewById(R.id.text);
            commentButton = (ImageButton) rowView.findViewById(R.id.comment);
            icon = (ImageView) rowView.findViewById(R.id.icon);
        }

        public void build(final Reflection reflection, final int position){
            text.setText(reflection.getText());
            update.setText(format.format(reflection.getLastUpdate()));
            if(reflection.getText() != null) text.setText(reflection.getText());
            else reflection.setText("");
            setIcon(icon,reflection.getType());
            final int id = reflection.getId();
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,CommentActivity.class);
                    intent.putExtra("model","ExperienceActivityEntry");
                    intent.putExtra("id",id);
                    intent.putExtra("title", reflection.getText());
                    intent.putExtra("type", R.string.reflection);
                    context.startActivity(intent);
                }
            });
        }
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
        ReflectionHolder reflectionHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reflection_item, parent, false);
            reflectionHolder = new ReflectionHolder(convertView);
            convertView.setTag(reflectionHolder);
        } else {
            reflectionHolder = (ReflectionHolder) convertView.getTag();
        }
        reflectionHolder.build(list.get(position),position);

        return convertView;
    }

    private static void setIcon(ImageView icon, String type) {
        if(type.equals(RESPONSE_POSITIVE)){
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.positive));
        }else if(type.equals(RESPONSE_NEGATIVE)){
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.negative));
        }
    }
}
