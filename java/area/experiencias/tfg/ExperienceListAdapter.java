package area.experiencias.tfg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 23/02/14.
 */
public class ExperienceListAdapter extends BaseAdapter {

    private static Context context;
    private final ArrayList<Experience> list;

    static class ExperienceHolder{
        TextView title, description;
        ImageView image;
        ImageButton commentButton;

        public ExperienceHolder(View rowView){
            title = (TextView) rowView.findViewById(R.id.title);
            description = (TextView) rowView.findViewById(R.id.description);
            image = (ImageView) rowView.findViewById(R.id.image);
            commentButton = (ImageButton) rowView.findViewById(R.id.commentButton);
        }

        public void build(final Experience experience){
            if(experience.getName().length() != 0) title.setText(experience.getName());
            else {
                title.setHint(context.getString(R.string.title));
                title.setText("");
            }
            if(experience.getShortDescription().length() != 0) description.setText(experience.getShortDescription());
            else {
                description.setHint(context.getString(R.string.description));
                description.setText("");
            }
            if(!experience.getElement_image_file_name().equals("none")) {
                ImageLoader.getInstance().loadImage("http://" + context.getString(R.string.server_address) +
                        experience.getElement_image_file_name().replace("original", "medium"), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage,8,context));
                    }
                });
            }else{
                image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources()
                        , R.drawable.imagen), 8, context));
            }
            commentButton.setFocusable(false);
            commentButton.setFocusableInTouchMode(false);
            final int id = experience.getId();
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,CommentActivity.class);
                    intent.putExtra("model","Experience");
                    intent.putExtra("id",id);
                    intent.putExtra("title", experience.getName());
                    intent.putExtra("type", R.string.experience);
                    context.startActivity(intent);
                }
            });
        }


    }

    public ExperienceListAdapter(Context context, ArrayList<Experience> list) {
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
        ExperienceHolder experienceHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.experience_list_item, parent, false);
            experienceHolder = new ExperienceHolder(convertView);
            convertView.setTag(experienceHolder);
        } else {
            experienceHolder = (ExperienceHolder) convertView.getTag();
        }
        experienceHolder.build(list.get(position));
        return convertView;
    }
}
