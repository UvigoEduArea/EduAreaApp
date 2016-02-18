package area.experiencias.tfg;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by adrianbouza on 18/03/14.
 */
public class EvidenceListAdapter extends BaseAdapter {

    private static Context context;
    private ArrayList<Evidence> list;
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault());

    public EvidenceListAdapter(Context context, ArrayList<Evidence> list) {
        this.context = context;
        this.list = list;
    }

    static class EvidenceHolder {
        TextView updateText, typeText, description;
        ImageButton commentButton, download;
        ImageView icon;

        public EvidenceHolder(View rowView) {
            updateText = (TextView) rowView.findViewById(R.id.update);
            description = (TextView) rowView.findViewById(R.id.description);
            typeText = (TextView) rowView.findViewById(R.id.type);
            download = (ImageButton) rowView.findViewById(R.id.download);
            commentButton = (ImageButton) rowView.findViewById(R.id.comment);
            icon = (ImageView) rowView.findViewById(R.id.iconType);
        }

        public void build(final Evidence evidence, final int position) {
            updateText.setText(format.format(evidence.getLastUpdate()));
            description.setText(evidence.getText());
            typeText.setText(getTypeText(evidence.getType()));
            setIconType(icon, evidence.getType());
            final int id = evidence.getId();
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("model", "ExperienceActivityEntry");
                    intent.putExtra("id", id);
                    intent.putExtra("title",evidence.getText());
                    intent.putExtra("type",getTypeText(evidence.getType()));
                    context.startActivity(intent);
                }
            });
            if (evidence.getType().equals(Evidence.IMAGE)) {
                ImageLoader.getInstance().displayImage("http://" + context.getString(R.string.server_address) +
                        evidence.getImage().replace("original", "medium"), download);
                download.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                download.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                download.setBackgroundColor(Color.TRANSPARENT);
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent imageIntent = new Intent(Intent.ACTION_VIEW);
                        imageIntent.setDataAndType(Uri.parse("http://193.146.210.92:3000" + evidence.getImage()), "image/*");
                        context.startActivity(imageIntent);
                    }
                });
            } else if (evidence.getType().equals(Evidence.VIDEO)) {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                        videoIntent.setDataAndType(Uri.parse("http://" + context.getString(R.string.server_address) + evidence.getVideo()), "video/*");
                        context.startActivity(videoIntent);
                    }
                };
                download.getLayoutParams().height = 150;
                download.getLayoutParams().width = 150;
                download.setBackgroundResource(R.drawable.selector_dialog_buttons);
                download.setImageResource(android.R.drawable.ic_media_play);
                download.setOnClickListener(clickListener);
            } else if (evidence.getType().equals(Evidence.DOCUMENT)) {
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GetDocumentThread(evidence, context, position).start();
                    }
                });
                download.getLayoutParams().height = 150;
                download.getLayoutParams().width = 150;
                download.setBackgroundResource(R.drawable.selector_dialog_buttons);
                download.setImageResource(R.drawable.download);
            } else {
                download.getLayoutParams().height = 150;
                download.getLayoutParams().width = 150;
                download.setImageResource(R.drawable.download);
                download.setBackgroundResource(R.drawable.selector_dialog_buttons);
                download.setOnClickListener(null);
            }
        }

        private int getTypeText(String type) {
            if(type.equals(Evidence.IMAGE)){
                return R.string.image;
            }else if(type.equals(Evidence.VIDEO)){
                return R.string.video;
            }else{
                return R.string.document;
            }
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
        EvidenceHolder evidenceHolder;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.evidence_item, parent, false);
            evidenceHolder = new EvidenceHolder(convertView);
            convertView.setTag(evidenceHolder);

        evidenceHolder.build(list.get(position), position);

        return convertView;
    }

    private static void setIconType(ImageView icon, String type) {
        if (type.equals(Evidence.IMAGE)) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.image));
        } else if (type.equals(Evidence.VIDEO)) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.video));
        } else {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.document));
        }
    }

    static class GetDocumentThread extends Thread {

        Evidence evidence;
        Context context;
        int position;

        public GetDocumentThread(Evidence evidence, Context context, int position) {
            this.evidence = evidence;
            this.context = context;
            this.position = position;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://" + context.getString(R.string.server_address) + evidence.getDocument());
                File file = new File(Environment.getExternalStorageDirectory() + "/" + evidence.getDocument().substring(evidence.getDocument().lastIndexOf("/") + 1,
                        evidence.getDocument().lastIndexOf("?")));

                long startTime = System.currentTimeMillis();
                Log.d("ImageManager", "download begining");
                Log.d("ImageManager", "download url:" + url);
                        /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
                Log.d("ImageManager", "download ready in"
                        + ((System.currentTimeMillis() - startTime) / 1000)
                        + " sec");

                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                String type = map.getMimeTypeFromExtension(ext);

                if (type == null)
                    type = "*/*";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.fromFile(file);

                intent.setDataAndType(data, type);

                context.startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ActivityNotFoundException e) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.could_not_open_file))
                        .setMessage("No se ha encontrado ninguna aplicación para abrir el documento.")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}