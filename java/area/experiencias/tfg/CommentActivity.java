package area.experiencias.tfg;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CommentActivity extends ActionBarActivity {

    String model,title;
    int id,type;

    private static SharedPreferences preferences;
    private static CommentListAdapter adapter;
    private List<Comment> commentList;
    public static CommentListInterface store = new CommentList<Comment>();

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault());

    // Refresh menu item
    private MenuItem refreshMenuItem;

    EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        model = intent.getExtras().getString("model");
        id = intent.getExtras().getInt("id");
        title = intent.getExtras().getString("title");
        type = intent.getExtras().getInt("type");

        ActionBar actionBar = getSupportActionBar();
        if (title != null) actionBar.setTitle(title);
        else actionBar.setTitle(getString(R.string.comments));
        actionBar.setSubtitle(type);

        preferences = getSharedPreferences("data",MODE_PRIVATE);

        ListView listView = (ListView) findViewById(R.id.commentList);
        adapter = new CommentListAdapter(this,CommentActivity.store.getList());
        listView.setAdapter(adapter);

        commentText = (EditText) findViewById(R.id.editComment);
        Button sendComment = (Button) findViewById(R.id.sendComment);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentText.getText().toString();
                commentText.setText("");
                if(comment.length() > 0) {
                    new SetCommentTask(CommentActivity.this, comment).execute();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetComments(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
                new GetComments(this).execute();
                return true;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Hilo para obtener los comentarios
    private class GetComments extends AsyncTask<Boolean, Integer, List<Comment>> {
        ProgressDialog progress;
        Context context;

        private GetComments(Context context) {
            this.context = context;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPreExecute() {
            if (refreshMenuItem != null) {
                refreshMenuItem.setActionView(R.layout.action_progressbar);
                refreshMenuItem.expandActionView();
            }
            progress = new ProgressDialog(context);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setMessage(context.getString(R.string.loading_comments));
            progress.setCancelable(false);
            progress.setMax(100);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected List<Comment> doInBackground(Boolean... params) {
            publishProgress(20);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/comments/getComments.json?" + "model=" + model
                        + "&object_id=" + id + "&comments_number=" + 20;
                HttpGet request = new HttpGet(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                HttpResponse response = client.execute(request);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity = response.getEntity();
                    store.deleteList();
                    publishProgress(60);
                    commentList = readJsonStream(entity);
                    // LISTA DE Commentarios
                    for (Comment comment : commentList) {
                        System.out.println(comment.toString());
                        store.saveComment(comment);
                    }
                }
                publishProgress(80);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commentList;
        }

        @Override
        protected void onProgressUpdate(Integer... num) {
            progress.setProgress(num[0]);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPostExecute(List<Comment> list) {
            progress.setProgress(100);
            if (refreshMenuItem != null) {
                refreshMenuItem.collapseActionView();
                // remove the progress bar view
                refreshMenuItem.setActionView(null);
            }
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }

    public List<Comment> readJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readCommentList(reader);
        }
        finally {
            reader.close();
        }
    }

    public List<Comment> readCommentList(JsonReader reader) throws IOException {
        List<Comment> comments = new ArrayList<Comment>();
        reader.beginArray();
        while (reader.hasNext()) {
            comments.add(readComment(reader));
        }
        reader.endArray();
        return comments;
    }

    public Comment readComment(JsonReader reader) throws IOException {
        Comment activity = new Comment();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("user")) {
                activity.setUser(reader.nextString());
            } else if (name.equals("comment")){
                activity.setComment(reader.nextString());
            } else if (name.equals("date")){
                try {
                    activity.setDate(format.parse(reader.nextString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return activity;
    }

    // Hilo para crear un comentario en el servidor
    public class SetCommentTask extends AsyncTask<Comment, Boolean, Boolean> {

        private Context context;
        private String comment;

        public SetCommentTask(Context context, String comment) {
            this.context = context;
            this.comment = comment;
        }

        @Override
        protected Boolean doInBackground(Comment... comments) {
            preferences = getSharedPreferences("data", MODE_PRIVATE);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/comments/create.json";
                HttpPost request = new HttpPost(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("model", model));
                nameValuePairs.add(new BasicNameValuePair("object_id","" + id));
                nameValuePairs.add(new BasicNameValuePair("comment", comment));
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
            if (state) {
                new GetComments(context).execute();
            } else
                Toast.makeText(CommentActivity.this, getString(R.string.comment_no_saved), Toast.LENGTH_SHORT).show();
        }
    }
}
