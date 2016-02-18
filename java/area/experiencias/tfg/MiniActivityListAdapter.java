package area.experiencias.tfg;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class MiniActivityListAdapter extends BaseAdapter {

    private static Context context;
    private static ArrayList<MiniActivity> list;

    static class MiniActivityHolder{

        TextView title, start, end;
        ImageButton image;
        Button state;

        public MiniActivityHolder(View rowView){
            title = (TextView) rowView.findViewById(R.id.activity_title);
            image = (ImageButton) rowView.findViewById(R.id.imageButton);
            start = (TextView) rowView.findViewById(R.id.start);
            end = (TextView) rowView.findViewById(R.id.end);
            state = (Button) rowView.findViewById(R.id.state);
        }

        public void build(final MiniActivity miniActivity, final int position){
            if(miniActivity.getName().length() != 0) title.setText(miniActivity.getName());
            else title.setHint(context.getString(R.string.title));
            if(miniActivity.getStart().length() != 0) start.setText(miniActivity.getStart());
            else start.setHint(context.getString(R.string.activity_start));
            if(miniActivity.getEnd().length() != 0) end.setText(miniActivity.getEnd());
            else end.setHint(context.getString(R.string.activity_end));
            if(!miniActivity.getElement_image_file_name().equals("none")) {
                ImageLoader.getInstance().loadImage("http://" + context.getString(R.string.server_address) +
                        miniActivity.getElement_image_file_name(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, context));
                    }
                });
            }else{
                image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources()
                    , R.drawable.imagen), 8, context));
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ActivityRecordsActivity.class);
                    intent.putParcelableArrayListExtra("activities",list);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
            image.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(v==image){
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            v.setAlpha(.5f);
                        }
                        else
                        {
                            v.setAlpha(1f);
                        }
                    }
                    return false;
                }
            });
            if(miniActivity.getProgress().length() > 0){
                state.setText(translateActivityState(miniActivity.getProgress()));
            }else{
                state.setText(context.getString(R.string.not_started));
            }
            if(miniActivity.getProgress().equals(MiniActivity.FINISHED)){
                state.setBackgroundResource(R.drawable.finished_state_button);
            }else if(miniActivity.getProgress().equals(MiniActivity.IN_PROGRESS)){
                state.setBackgroundResource(R.drawable.in_progress_state_button);
            }else{
                state.setBackgroundResource(R.drawable.not_started_state_button);
            }
            state.setOnClickListener(new View.OnClickListener() {

                int checked = 0;

                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {context.getString(R.string.not_started),
                            context.getString(R.string.in_progress),context.getString(R.string.finished)};
                    final String[] values = {MiniActivity.NOT_STARTED, MiniActivity.IN_PROGRESS, MiniActivity.FINISHED};
                    final AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.change_state));
                    builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checked = which;
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            miniActivity.setProgress(values[checked]);
                            list.get(position).setProgress(values[checked]);
                            state.setText(items[checked]);
                            state.setBackgroundResource(getBackground(checked));
                            new UpdateActivityStateTask(position, context).execute(miniActivity);
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });
        }

        private int getBackground(int checked) {
            switch (checked){
                default:
                case 0:
                    return R.drawable.not_started_state_button;
                case 1:
                    return R.drawable.in_progress_state_button;
                case 2:
                    return R.drawable.finished_state_button;
            }
        }

        private String translateActivityState(String progress) {
            if(progress.equals(MiniActivity.FINISHED)){
                return context.getString(R.string.finished);
            }else if(progress.equals(MiniActivity.IN_PROGRESS)){
                return context.getString(R.string.in_progress);
            }else return context.getString(R.string.not_started);
        }

    }

    public MiniActivityListAdapter(Context context, ArrayList<MiniActivity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MiniActivityHolder miniActivityHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_miniview_item, parent, false);
            miniActivityHolder = new MiniActivityHolder(convertView);
            convertView.setTag(miniActivityHolder);
        } else {
            miniActivityHolder = (MiniActivityHolder) convertView.getTag();
        }
        miniActivityHolder.build(list.get(position), position);
        return convertView;
    }

    // Hilo para actualizar estado de la actividad
    public static class UpdateActivityStateTask extends AsyncTask<MiniActivity, Boolean, Boolean> {

        int position;
        private ProgressDialog dialog;

        public UpdateActivityStateTask(int position, Context context) {
            this.position = position;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Cambiando estado...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(MiniActivity... miniActivities) {
            SharedPreferences preferences = context.getSharedPreferences("data", context.MODE_PRIVATE);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + context.getString(R.string.server_address) + "/gl/activities/" +
                        miniActivities[0].getId() + ".json";
                HttpPut request = new HttpPut(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("progress",list.get(position).getProgress()));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) return true;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean state) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (state) {
                Toast.makeText(context, context.getString(R.string.activity_state_changed), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, context.getString(R.string.activity_state_not_changed), Toast.LENGTH_SHORT).show();
        }
    }
}
