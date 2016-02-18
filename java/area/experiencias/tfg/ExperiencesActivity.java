package area.experiencias.tfg;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.stream.JsonReader;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExperiencesActivity extends ListActivity {

    private static SharedPreferences preferences;
    private static ExperienceListAdapter adapter;
    private List<Experience> experienceList = new ArrayList<Experience>();
    public static ExperienceListInterface store = new ExperienceList<Experience>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences);
        adapter = new ExperienceListAdapter(this,ExperiencesActivity.store.getList());
        setListAdapter(adapter);
        preferences = getSharedPreferences("data",MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(experienceList.isEmpty()) {
            new GetExperiencesTask().execute(false);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        // Recoger información de la experiencia seleccionada

        // CAMBIAR!!
        Intent intent = new Intent(this, ExperienceViewActivity.class);
        intent.putExtra("experience",experienceList.get(position));
        //intent.putExtra("image",experienceList.get(position).getImage());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_all_experiences:
                ImageLoader.getInstance().clearMemoryCache();
                new GetExperiencesTask().execute(false);
                return true;
            case R.id.action_my_experiences:
                ImageLoader.getInstance().clearMemoryCache();
                new GetExperiencesTask().execute(true);
                return true;
            case R.id.action_logout:
                Intent intent = new Intent(ExperiencesActivity.this, LoginActivity.class);
                // Se finalizan todas las actividades
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public class GetExperiencesTask extends AsyncTask<Boolean, Integer, List<Experience>> {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(ExperiencesActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setMessage(getString(R.string.loading_experiences));
            progress.setCancelable(false);
            progress.setMax(100);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected List<Experience> doInBackground(Boolean... owner) {
            List<Experience> IdList;
            String BaseUrlPage;
            publishProgress(20);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/experiences/getWholeView.json?id=";
                // Recoger experiencias propias o de todos los usuarios
                if (!owner[0]) BaseUrlPage = "http://" + getString(R.string.server_address) + "/gl/experiences.json?page=";
                else BaseUrlPage = "http://" + getString(R.string.server_address) + "/gl/experiences/search.json?owner=true&page=";
                int numPage = 1;
                String UrlPage = BaseUrlPage+numPage;
                HttpGet request = new HttpGet(UrlPage);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                store.deleteList();
                publishProgress(40);
                do{
                    HttpResponse response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    if (entity == null) break;
                    IdList = readJsonStream(entity);
                    if(IdList.size() > 0) {
                        for (int i = 0; i < IdList.size(); i++)
                            if (i == IdList.size() - 1) UrlId = UrlId + IdList.get(i).getId();
                            else UrlId = UrlId + IdList.get(i).getId() + ",";
                        numPage++;
                        UrlPage = BaseUrlPage + numPage;
                        request = new HttpGet(UrlPage);
                        if (IdList.size() % 15 == 0) UrlId = UrlId + ",";
                    }
                }while(IdList.size()==15);
                // LISTA DE IDs
                System.out.println(UrlId);
                request = new HttpGet(UrlId);
                publishProgress(60);
                HttpResponse response = client.execute(request);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity = response.getEntity();
                    experienceList = readJsonStream(entity);
                    publishProgress(80);
                }else return experienceList;
                for(Experience experience : experienceList){
                    store.saveExperience(experience);
                }
                publishProgress(100);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return experienceList;
        }

        @Override
        protected void onProgressUpdate(Integer... num) {
            if (num[0] < 60) progress.setMessage(getString(R.string.preparing_data));
            if (num[0] == 60) progress.setMessage(getString(R.string.waiting_server_response));
            if (num[0] > 60) progress.setMessage(getString(R.string.starting));
            progress.setProgress(num[0]);
        }

        @Override
        protected void onPostExecute(List<Experience> experiences) {
            adapter.notifyDataSetChanged();
            progress.dismiss();
            getActivities();
        }
    }

    public void getActivities(){
        for(final Experience experience : experienceList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlActivities = "http://" + getString(R.string.server_address) +
                            "/gl/activitySequence/getWholeView.json?id=" + experience.getActivity_sequence_id();
                    try {
                        HttpClient client = new DefaultHttpClient();
                        HttpGet request = new HttpGet(urlActivities);
                        HttpResponse response = client.execute(request);
                        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            HttpEntity entity = response.getEntity();
                            experience.setActivities_ids(readActivitiesIds(entity));
                            System.out.println(experience.toString());
                            System.out.println(experience.activitiesIdsToString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public List<Experience> readJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readExperienceList(reader);
        }
        finally {
            reader.close();
        }
    }

    public List<Experience> readExperienceList(JsonReader reader) throws IOException {
        List<Experience> experiences = new ArrayList<Experience>();
        reader.beginArray();
        while (reader.hasNext()) {
            experiences.add(readExperience(reader));
        }
        reader.endArray();
        return experiences;
    }

    public Experience readExperience(JsonReader reader) throws IOException {
        Experience experience = new Experience();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                experience.setId(reader.nextInt());
            } else if (name.equals("name")){
                experience.setName(reader.nextString());
            } else if (name.equals("image")){
                experience.setElement_image_file_name(reader.nextString());
            } else if (name.equals("description")){
                experience.setDescription(reader.nextString());
                if (experience.getDescription().length() >= 200)
                    experience.setShortDescription(experience.getDescription().substring(0,199)+"...");
                else experience.setShortDescription(experience.getDescription());
            } else if (name.equals("guide")){
                experience.setActivity_sequence_id(readActivitySequence(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return experience;
    }

    public int readActivitySequence(JsonReader reader) throws IOException {
        int activitySequence = -1;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("components")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name2 = reader.nextName();
                    if (name2.equals("activity_sequence_id")) {
                        activitySequence = reader.nextInt();
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else reader.skipValue();
        }
        reader.endObject();
        return activitySequence;
    }

    public ArrayList<Integer> readActivitiesIds(HttpEntity in) throws IOException {
        ArrayList<Integer> idList = new ArrayList<Integer>();
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("activities")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name2 = reader.nextName();
                                if (name2.equals("id")) {
                                    idList.add(reader.nextInt());
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
            reader.endArray();
            return idList;
        }
        finally {
            reader.close();
        }
    }

}