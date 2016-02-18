package area.experiencias.tfg;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExperienceViewActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Activity Sections
     */
    private static final int DESCRIPTION = 0;
    private static final int TECHNICAL_CONTEXT = 1;
    private static final int EDUCATIONAL_CONTEXT = 2;
    private static final int ACTIVITY_SEQUENCE = 3;

    /**
     * Edition Modes
     */
    private static final int VIEW = 0;
    private static final int EDITION = 1;
    private static final int DOCUMENTATION = 2;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private static Experience experience;
    private static SharedPreferences preferences;

    // Refresh menu item
    private MenuItem refreshMenuItem;

    // Activity Sequence Variables
    private static MiniActivityListAdapter adapter;
    private List<MiniActivity> activityList;
    public static MiniActivityListInterface store = new MiniActivityList<MiniActivity>();

    private DescriptionFragment descriptionFragment;
    private TechnicalContextFragment technicalContextFragment;
    private EducationalContextFragment educationalContextFragment;
    private ActivitySequenceFragment activitySequenceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_view);

        // Se recoge el objeto Experiencia y su imagen provenientes de la actividad previa
        Intent intent = getIntent();
        experience = intent.getParcelableExtra("experience");

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if(experience.getName() != null) actionBar.setTitle(experience.getName());
        else actionBar.setTitle(getString(R.string.title));
        actionBar.setSubtitle(getString(R.string.experience));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        adapter = new MiniActivityListAdapter(this,ExperienceViewActivity.store.getList());
        // Hilo de petición de actividades
        new GetActivities(this).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experience_view, menu);
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
            case R.id.action_view_mode:
                if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(VIEW);
                return true;
            case R.id.action_edition_mode:
                if(descriptionFragment != null && descriptionFragment.isAdded()) descriptionFragment.changeMode(EDITION);
                return true;
            case R.id.action_documentation_mode:
                return true;
            case R.id.action_refresh:
                ImageLoader.getInstance().clearMemoryCache();
                new GetActivities(this).execute();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_logout:
                Intent intent = new Intent(ExperienceViewActivity.this, LoginActivity.class);
                // Se finalizan todas las actividades
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case DESCRIPTION:
                    return descriptionFragment = DescriptionFragment.newInstance();
                case TECHNICAL_CONTEXT:
                    return technicalContextFragment = TechnicalContextFragment.newInstance();
                case EDUCATIONAL_CONTEXT:
                    return educationalContextFragment = EducationalContextFragment.newInstance();
                case ACTIVITY_SEQUENCE:
                    return activitySequenceFragment = ActivitySequenceFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase();
            }
            return null;
        }
    }

    /**
     * Fragment para sección de descripción
     */
    public static class DescriptionFragment extends Fragment {

        String picturePath;

        private static final int SELECT_PICTURE = 0;
        private static final int TAKE_PICTURE = 1;

        TextView title, description;
        ImageView image;
        EditText titleEditor, descriptionEditor;
        Button changeImage;
        ImageButton save, cancel;
        LinearLayout buttonsLayout;

        public static DescriptionFragment newInstance() {
            return new DescriptionFragment();
        }

        public DescriptionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_description, container, false);
            title = (TextView) rootView.findViewById(R.id.title);
            title.setText(experience.getName());
            titleEditor = (EditText) rootView.findViewById(R.id.editTitle);
            titleEditor.setText(experience.getName());
            description = (TextView) rootView.findViewById(R.id.description);
            description.setText(experience.getDescription());
            descriptionEditor = (EditText) rootView.findViewById(R.id.editDescription);
            descriptionEditor.setText(experience.getDescription());
            image = (ImageView) rootView.findViewById(R.id.image);
            if(experience.getImage() != null) image.setImageBitmap(experience.getImage());
            if(!experience.getElement_image_file_name().equals("none")) {
                ImageLoader.getInstance().loadImage("http://" + getString(R.string.server_address) +
                        experience.getElement_image_file_name().replace("original", "medium"), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
                    }
                });
            }else{
                image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(getActivity().getResources()
                        , R.drawable.imagen), 8, getActivity()));
            }
            changeImage = (Button) rootView.findViewById(R.id.changeImage);
            changeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.add_image_dialog);
                    dialog.setTitle(getString(R.string.add_image));

                    ImageButton gallery = (ImageButton) dialog.findViewById(R.id.gallery);

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent selectPicture = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            selectPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(selectPicture, SELECT_PICTURE);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            buttonsLayout = (LinearLayout) rootView.findViewById(R.id.buttonsLayout);
            save = (ImageButton) rootView.findViewById(R.id.saveButton);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean changes = false;
                    if(!title.getText().toString().equals(titleEditor.getText().toString()) ||
                            !description.getText().toString().equals(descriptionEditor.getText().toString()) ||
                            (picturePath != null && !picturePath.equals(experience.getElement_image_file_name())))
                        changes = true;
                    if(changes) {
                        Editable titleText = titleEditor.getText();
                        Editable descriptionText = descriptionEditor.getText();
                        title.setText(titleText.toString());
                        description.setText(descriptionText.toString());
                        experience.setName(titleText.toString());
                        experience.setDescription(descriptionText.toString());
                        experience.setElement_image_file_name(picturePath);
                        new SaveExperienceTask(getActivity()).execute(experience);
                    }
                    changeMode(VIEW);
                }
            });
            cancel = (ImageButton) rootView.findViewById(R.id.cancelButton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(experience.getImage() != null) image.setImageBitmap(experience.getImage());
                    changeMode(VIEW);
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                // Resultado de seleccionar imagen
                case SELECT_PICTURE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        System.out.println("Imagen seleccionada: " + picturePath);
                        cursor.close();

                        Bitmap bitmap = ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeFile(picturePath), 8, getActivity());
                        experience.setImage(bitmap);
                        image.setImageBitmap(bitmap);
                    }
                    break;
                // Resultado de tomar fotografía
                case TAKE_PICTURE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri pictureUri = data.getData();
                        picturePath = ImageHelper.getRealPathFromURI(pictureUri, getActivity());
                        System.out.println("Imagen seleccionada: " + picturePath);

                        Bitmap bitmap = ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeFile(picturePath), 8, getActivity());
                        experience.setImage(bitmap);
                        image.setImageBitmap(bitmap);
                    }
                    break;
            }
        }

        public void changeMode(int mode){
            switch (mode){
                case VIEW:
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.GONE);
                    titleEditor.setVisibility(View.GONE);
                    descriptionEditor.setVisibility(View.GONE);
                    changeImage.setVisibility(View.GONE);
                    break;
                case EDITION:
                    title.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    buttonsLayout.setVisibility(View.VISIBLE);
                    titleEditor.setVisibility(View.VISIBLE);
                    descriptionEditor.setVisibility(View.VISIBLE);
                    changeImage.setVisibility(View.VISIBLE);
                    break;
            }
        }

        // Hilo para guardar los cambios en una experiencia
        public class SaveExperienceTask extends AsyncTask<Experience, Boolean, Boolean> {

            private ProgressDialog dialog;

            public SaveExperienceTask(Context context) {
                dialog = new ProgressDialog(context);
            }

            protected void onPreExecute() {
                this.dialog.setMessage("Guardando experiencia...");
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            @Override
            protected Boolean doInBackground(Experience... experiences) {
                preferences = getActivity().getSharedPreferences("data", MODE_PRIVATE);
                try {
                    HttpClient client = new DefaultHttpClient();
                    String UrlId = "http://" + getString(R.string.server_address) + "/gl/experiences/" + experience.getId() + ".json";
                    HttpPut request = new HttpPut(UrlId);
                    request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entity.addPart("title", new StringBody(experiences[0].getName()));
                    entity.addPart("description", new StringBody(experiences[0].getDescription()));
                    request.setEntity(entity);
                    HttpResponse response = client.execute(request);
                    if(experiences[0].getElement_image_file_name() != null) {
                        request = new HttpPut(UrlId);
                        request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                        entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(experiences[0].getElement_image_file_name());
                        String mimeType
                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        entity.addPart("element_image", new FileBody(new File(experiences[0].getElement_image_file_name()),mimeType));
                        request.setEntity(entity);
                        response = client.execute(request);
                    }
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
                    Toast.makeText(getActivity(), getString(R.string.experience_saved), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getActivity(), getString(R.string.experience_not_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Fragment para sección de Contexto Técnico
     */
    public static class TechnicalContextFragment extends Fragment {

        public static TechnicalContextFragment newInstance() {
            return new TechnicalContextFragment();
        }

        public TechnicalContextFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_technical_context, container, false);
            return rootView;
        }
    }

    /**
     * Fragment para sección de Contexto Educativo
     */
    public static class EducationalContextFragment extends Fragment {

        public static EducationalContextFragment newInstance() {
            return new EducationalContextFragment();
        }

        public EducationalContextFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_educational_context, container, false);
            return rootView;
        }
    }

    /**
     * Fragment para sección de Secuencia de Actividades
     */
    public static class ActivitySequenceFragment extends Fragment {

        GridView gridView;

        public static ActivitySequenceFragment newInstance() {
            return new ActivitySequenceFragment();
        }

        public ActivitySequenceFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_sequence, container, false);
            gridView = (GridView) rootView.findViewById(R.id.gridView);
            gridView.setAdapter(adapter);

            return rootView;
        }
    }

    public void updateUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private class GetActivities extends AsyncTask<Boolean, Integer, List<MiniActivity>>{
        ProgressDialog progress;
        Context context;

        private GetActivities(Context context) {
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
            progress.setMessage(context.getString(R.string.loading_experiences));
            progress.setCancelable(false);
            progress.setMax(100);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected List<MiniActivity> doInBackground(Boolean... params) {
            publishProgress(20);
            try {
                HttpClient client = new DefaultHttpClient();
                String urlActivities = "http://" + getString(R.string.server_address) +
                        "/gl/activitySequence/getWholeView.json?id=" + experience.getActivity_sequence_id();
                HttpGet request = new HttpGet(urlActivities);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    experience.setActivities_ids(readActivitiesIds(entity));
                    System.out.println(experience.activitiesIdsToString());
                }
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/activities/getMiniView.json?id=";
                for (int i=0; i<experience.getActivities_ids().size(); i++)
                    if (i==experience.getActivities_ids().size()-1) UrlId = UrlId+experience.getActivities_ids().get(i);
                    else UrlId = UrlId+experience.getActivities_ids().get(i)+",";
                request = new HttpGet(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                publishProgress(40);
                response = client.execute(request);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity = response.getEntity();
                    publishProgress(80);
                    store.deleteList();
                    activityList = readJsonStream(entity);
                    // LISTA DE Activities
                    for (MiniActivity mi : activityList) {
                        System.out.println(mi.toString());
                        store.saveActivity(mi);
                        publishProgress(90);
                    }
                }
                publishProgress(100);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return activityList;
        }

        @Override
        protected void onProgressUpdate(Integer... num) {
            if (num[0] < 60) progress.setMessage(getString(R.string.preparing_data));
            if (num[0] == 60) progress.setMessage(getString(R.string.waiting_server_response));
            if (num[0] > 60) progress.setMessage(getString(R.string.starting));
            progress.setProgress(num[0]);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPostExecute(List<MiniActivity> list) {
            if (refreshMenuItem != null) {
                refreshMenuItem.collapseActionView();
                // remove the progress bar view
                refreshMenuItem.setActionView(null);
            }
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }

    public List<MiniActivity> readJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readActivityList(reader);
        }
        finally {
            reader.close();
        }
    }

    public List<MiniActivity> readActivityList(JsonReader reader) throws IOException {
        List<MiniActivity> activities = new ArrayList<MiniActivity>();
        reader.beginArray();
        while (reader.hasNext()) {
            activities.add(readActivity(reader));
        }
        reader.endArray();
        return activities;
    }

    public MiniActivity readActivity(JsonReader reader) throws IOException {
        MiniActivity activity = new MiniActivity();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                activity.setId(reader.nextInt());
            } else if (name.equals("name")){
                activity.setName(reader.nextString());
            } else if (name.equals("image")) {
                activity.setElement_image_file_name(reader.nextString());
            }else if (name.equals("start")) {
                activity.setStart(reader.nextString());
            }else if (name.equals("end")) {
                activity.setEnd(reader.nextString());
            }else if (name.equals("progress")){
                activity.setProgress(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return activity;
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
