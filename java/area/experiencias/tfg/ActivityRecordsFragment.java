package area.experiencias.tfg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.stream.JsonReader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityRecordsFragment extends Fragment {

    private static final int ENTRIES = 2;
    private static final int REFLECTIONS = 1;
    private static final int EVIDENCES = 0;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault());

    private OnModeChangeListener mCallback;

    private MiniActivity miniActivity;
    private int currentPosition;
    private Activity activity;
    private SharedPreferences preferences;

    private ImageView image;

    // Progress
    private ProgressBar progress;

    // Entries
    private EntriesFragment entriesFragment;
    private ArrayList<Entry> entries = new ArrayList<Entry>();
    private EntryListInterface entryStore = new EntryList<Entry>();

    private EntryListAdapter adapterEntries;

    // Reflections
    private ReflectionsFragment reflectionFragment;
    private List<Reflection> reflectionList = new ArrayList<Reflection>();
    public ReflectionListInterface reflectionStore = new ReflectionList<Reflection>();

    private ReflectionListAdapter adapterReflections;

    // Evidences
    private EvidencesFragment evidencesFragment;
    private List<Evidence> evidenceList = new ArrayList<Evidence>();
    private EvidenceListInterface evidenceStore = new EvidenceList<Evidence>();

    private EvidenceListAdapter adapterEvidences;

    private ViewPager pager;
    private TextView title;
    private TextView description;
    public RelativeLayout entriesLayout;

    private WeakReference<GetActivityInfoTask> activityInfoTaskWeakRef;
    private WeakReference<GetReflectionsTask> reflectionTaskWeakRef;
    private WeakReference<GetEvidencesTask> evidenceTaskWeakRef;
    private GetEvidencesTask evidenceTask;
    private GetReflectionsTask reflectionTask;
    private GetActivityInfoTask activityInfoTask;

    public static ActivityRecordsFragment newInstance(MiniActivity miniActivity, int position) {
        ActivityRecordsFragment f = new ActivityRecordsFragment();
        Bundle bdl = new Bundle();
        bdl.putParcelable("activity", miniActivity);
        bdl.putInt("position", position);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activity_records,
                container, false);
        root.setId(currentPosition);
        progress = (ProgressBar) root.findViewById(R.id.progressBar);
        title = (TextView) root.findViewById(R.id.title);
        description = (TextView) root.findViewById(R.id.description);
        image = (ImageView) root.findViewById(R.id.image);
        pager = (ViewPager) root.findViewById(R.id.pager);
        entriesLayout = (RelativeLayout) root.findViewById(R.id.entriesLayout);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        currentPosition = getArguments().getInt("position");
        miniActivity = getArguments().getParcelable("activity");

        // Se muestra la información de la que ya disponemos acerca de la actividad
        if (miniActivity.getName() != null) title.setText(miniActivity.getName());

        if (!miniActivity.getElement_image_file_name().equals("none")) {
            ImageLoader.getInstance().loadImage("http://" + getString(R.string.server_address) +
                    miniActivity.getElement_image_file_name(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
                }
            });
        } else {
            image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeResource(getActivity().getResources()
                    , R.drawable.imagen), 8, getActivity()));
        }


        // Buttons actuando como Tabs
        final Button reflectionsButton = (Button) getView().findViewById(R.id.toggleReflections);
        final Button evidencesButton = (Button) getView().findViewById(R.id.toggleEvidences);
        final Button entriesButton = (Button) getView().findViewById(R.id.toggleEntries);
        evidencesButton.setSelected(true);
        reflectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reflectionsButton.isSelected()) {
                    reflectionsButton.setSelected(true);
                    evidencesButton.setSelected(false);
                    entriesButton.setSelected(false);
                    pager.setCurrentItem(1, true);
                }
            }
        });
        evidencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!evidencesButton.isSelected()) {
                    evidencesButton.setSelected(true);
                    reflectionsButton.setSelected(false);
                    entriesButton.setSelected(false);
                    pager.setCurrentItem(0, true);
                }
            }
        });
        entriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!entriesButton.isSelected()) {
                    entriesButton.setSelected(true);
                    reflectionsButton.setSelected(false);
                    evidencesButton.setSelected(false);
                    pager.setCurrentItem(2, true);
                }
            }
        });

        // Se añaden los fragments al pager
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    entriesButton.setSelected(true);
                    evidencesButton.setSelected(false);
                    reflectionsButton.setSelected(false);
                } else if (position == 1) {
                    reflectionsButton.setSelected(true);
                    evidencesButton.setSelected(false);
                    entriesButton.setSelected(false);
                } else if (position == 0) {
                    evidencesButton.setSelected(true);
                    reflectionsButton.setSelected(false);
                    entriesButton.setSelected(false);
                }
                mCallback.onModeChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        pager.setAdapter(mSectionsPagerAdapter);
        pager.setOffscreenPageLimit(5);

        adapterReflections = new ReflectionListAdapter(getActivity(), reflectionStore.getList());
        adapterEvidences = new EvidenceListAdapter(getActivity(), evidenceStore.getList());
        adapterEntries = new EntryListAdapter(getActivity(), entryStore.getList());

        startAsyncTasks();
    }

    private void startAsyncTasks() {
        activityInfoTask = new GetActivityInfoTask(this);
        this.activityInfoTaskWeakRef = new WeakReference<GetActivityInfoTask>(activityInfoTask);
        activityInfoTask.execute();
        if (!miniActivity.getElement_image_file_name().equals("none")) {
            ImageLoader.getInstance().loadImage("http://" + getString(R.string.server_address) +
                    miniActivity.getElement_image_file_name(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, 8, getActivity()));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (activityInfoTask != null && activityInfoTask.getStatus() != AsyncTask.Status.FINISHED)
            activityInfoTask.cancel(true);
        if (reflectionTask != null && reflectionTask.getStatus() != AsyncTask.Status.FINISHED)
            reflectionTask.cancel(true);
        if (evidenceTask != null && evidenceTask.getStatus() != AsyncTask.Status.FINISHED)
            evidenceTask.cancel(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            if (pager != null) mCallback.onModeChange(pager.getCurrentItem());
    }

    public void updateActivity() {
        ImageLoader.getInstance().clearMemoryCache();
        startAsyncTasks();
        entryStore.deleteList();
        updateReflections();
        updateEvidences();
    }

    public void updateEvidences() {
        GetEvidencesTask evidencesTask = new GetEvidencesTask(this);
        this.evidenceTaskWeakRef = new WeakReference<GetEvidencesTask>(evidencesTask);
        evidencesTask.execute();
    }

    public void updateReflections() {
        GetReflectionsTask reflectionsTask = new GetReflectionsTask(this);
        this.reflectionTaskWeakRef = new WeakReference<GetReflectionsTask>(reflectionsTask);
        reflectionsTask.execute();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case REFLECTIONS:
                    return reflectionFragment = new ReflectionsFragment();
                case EVIDENCES:
                    return evidencesFragment = new EvidencesFragment();
                case ENTRIES:
                    return entriesFragment = new EntriesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case REFLECTIONS:
                    return getString(R.string.reflections).toUpperCase(l);
                case EVIDENCES:
                    return getString(R.string.evidences).toUpperCase(l);
                case ENTRIES:
                    return getString(R.string.all);
            }
            return null;
        }
    }

    public class EntriesFragment extends Fragment {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ReflectionsFragment.
         */

        public EntriesFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_entries, container, false);
            // Para diferenciar la vista de la contenida en otros fragments
            rootView.setId(currentPosition);
            ListView listView = (ListView) rootView.findViewById(R.id.entries_list);
            listView.setAdapter(adapterEntries);
            return rootView;
        }

        public void updateFragment() {
            Collections.sort(entryStore.getList(), new EntryComparator());
            // UIThread para actualizar la interfaz
            // No se puede actualizar la interfaz de usuario desde un hilo que no sea de este tipo.
            if(isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notificamos al adaptador de que ha habido cambios en la lista que contiene
                        adapterEntries.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public class ReflectionsFragment extends Fragment {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ReflectionsFragment.
         */

        public ReflectionsFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_reflections, container, false);
            // Para diferenciar la vista de la contenida en otros fragments
            rootView.setId(currentPosition);
            ListView listView = (ListView) rootView.findViewById(R.id.reflection_list);
            listView.setAdapter(adapterReflections);
            updateReflections();
            return rootView;
        }

        public void updateFragment() {
            Collections.sort(reflectionStore.getList(), new EntryComparator());
            // UIThread para actualizar la interfaz
            // No se puede actualizar la interfaz de usuario desde un hilo que no sea de este tipo.
            if(isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notificamos al adaptador de que ha habido cambios en la lista que contiene
                        adapterReflections.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public class EvidencesFragment extends Fragment {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ReflectionsFragment.
         */

        public EvidencesFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_evidences, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.evidence_list);
            listView.setAdapter(adapterEvidences);
            updateEvidences();
            return rootView;
        }

        public void updateFragment() {
            Collections.sort(evidenceStore.getList(), new EntryComparator());
            // UIThread para actualizar la interfaz
            // No se puede actualizar la interfaz de usuario desde un hilo que no sea de este tipo.
            if(isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notificamos al adaptador de que ha habido cambios en la lista que contiene
                        adapterEvidences.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    // Hilo para recoger la información de la actividad
    private class GetActivityInfoTask extends AsyncTask<Boolean, Integer, Activity> {

        private WeakReference<ActivityRecordsFragment> fragmentWeakRef;

        private GetActivityInfoTask(ActivityRecordsFragment activityRecordsFragment) {
            this.fragmentWeakRef = new WeakReference<ActivityRecordsFragment>(activityRecordsFragment);
        }

        @Override
        protected Activity doInBackground(Boolean... owner) {
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/activities/getSmallView.json?id=" + miniActivity.getId();
                HttpGet request = new HttpGet(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                activity = readActivityJsonStream(entity);
                response.getEntity().consumeContent();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return activity;
        }

        @Override
        protected void onPostExecute(Activity activity) {
            if (isCancelled()) return;
            if (this.fragmentWeakRef.get() != null) {
                if (activity.getDescription() != null)
                    description.setText(activity.getDescription());
            }
        }
    }

    public Activity readActivityJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readActivity(reader);
        } finally {
            reader.close();
        }
    }

    public Activity readActivity(JsonReader reader) throws IOException {
        Activity activity = new Activity();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    activity.setId(reader.nextInt());
                } else if (name.equals("name")) {
                    activity.setName(reader.nextString());
                } else if (name.equals("element_image_file_name")) {
                    activity.setElement_image_file_name(reader.nextString());
                } else if (name.equals("description")) {
                    activity.setDescription(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return activity;
    }

    private class GetEvidencesTask extends AsyncTask<Boolean, Integer, List<Evidence>> {

        private WeakReference<ActivityRecordsFragment> fragmentWeakRef;

        private GetEvidencesTask(ActivityRecordsFragment fragment) {
            this.fragmentWeakRef = new WeakReference<ActivityRecordsFragment>(fragment);
        }

        @Override
        protected List<Evidence> doInBackground(Boolean... owner) {
            try {
                HttpClient client = new DefaultHttpClient();
                if (isAdded()) {
                    String UrlId = "http://" + getString(R.string.server_address) + "/gl/experienceActivityEntries/getEvidences.json?activity_id=" +
                            activity.getId();
                    HttpGet request = new HttpGet(UrlId);
                    request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        evidenceStore.deleteList();
                        evidenceList = readEvidenceJsonStream(entity);
                        // LISTA DE Evidencias
                        for (Evidence e : evidenceList) {
                            System.out.println(e.toString());
                            evidenceStore.saveEvidence(e);
                            entryStore.saveEntry(e);
                        }
                        response.getEntity().consumeContent();
                    }
                } else return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return evidenceList;
        }

        @Override
        protected void onPostExecute(List<Evidence> evidenceList) {
            if (isCancelled()) return;
            if (this.fragmentWeakRef.get() != null) {
                evidencesFragment.updateFragment();
                entriesFragment.updateFragment();
                progress.setVisibility(View.GONE);
                mCallback.onRefreshFinish();
            }
        }
    }

    public List<Evidence> readEvidenceJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readEvidenceList(reader);
        } finally {
            reader.close();
        }
    }

    public List<Evidence> readEvidenceList(JsonReader reader) throws IOException {
        List<Evidence> evidences = new ArrayList<Evidence>();
        reader.beginArray();
        while (reader.hasNext()) {
            evidences.add(readEvidence(reader));
        }
        reader.endArray();
        return evidences;
    }

    public Evidence readEvidence(JsonReader reader) throws IOException {
        Evidence evidence = new Evidence();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                evidence.setId(reader.nextInt());
            } else if (name.equals("type")) {
                evidence.setType(reader.nextString());
            } else if (name.equals("video")) {
                evidence.setVideo(reader.nextString());
            } else if (name.equals("video_thumbnail")){
                evidence.setVideoThumbnail(reader.nextString());
            } else if (name.equals("image")) {
                evidence.setImage(reader.nextString());
            } else if (name.equals("document")) {
                evidence.setDocument(reader.nextString());
            } else if (name.equals("text")) {
                evidence.setText(reader.nextString());
            } else if (name.equals("updated")) {
                try {
                    evidence.setLastUpdate(format.parse(reader.nextString()));
                } catch (ParseException e) {
                    evidence.setLastUpdate(new Date(0));
                    e.printStackTrace();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return evidence;
    }

    private class GetReflectionsTask extends AsyncTask<Boolean, Integer, List<Reflection>> {

        private WeakReference<ActivityRecordsFragment> fragmentWeakRef;

        private GetReflectionsTask(ActivityRecordsFragment fragment) {
            this.fragmentWeakRef = new WeakReference<ActivityRecordsFragment>(fragment);
        }

        @Override
        protected List<Reflection> doInBackground(Boolean... owner) {
            try {
                HttpClient client = new DefaultHttpClient();
                if (isAdded()) {
                    String UrlId = "http://" + getString(R.string.server_address) + "/gl/experienceActivityEntries/getReflections.json?activity_id=" +
                            activity.getId();
                    HttpGet request = new HttpGet(UrlId);
                    request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        reflectionStore.deleteList();
                        reflectionList = readReflectionJsonStream(entity);
                        // LISTA DE Reflectiones
                        for (Reflection r : reflectionList) {
                            System.out.println(r.toString());
                            reflectionStore.saveReflection(r);
                            entryStore.saveEntry(r);
                        }
                        response.getEntity().consumeContent();
                    }
                } else return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reflectionList;
        }

        @Override
        protected void onPostExecute(List<Reflection> ReflectionList) {
            if (isCancelled()) return;
            if (this.fragmentWeakRef.get() != null) {
                reflectionFragment.updateFragment();
                entriesFragment.updateFragment();
            }
        }
    }

    public List<Reflection> readReflectionJsonStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readReflectionList(reader);
        } finally {
            reader.close();
        }
    }

    public List<Reflection> readReflectionList(JsonReader reader) throws IOException {
        List<Reflection> reflections = new ArrayList<Reflection>();
        reader.beginArray();
        while (reader.hasNext()) {
            reflections.add(readReflection(reader));
        }
        reader.endArray();
        return reflections;
    }

    public Reflection readReflection(JsonReader reader) throws IOException {
        Reflection reflection = new Reflection();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("type")) {
                reflection.setType(reader.nextString());
            } else if (name.equals("id")) {
                reflection.setId(reader.nextInt());
            } else if (name.equals("text")) {
                reflection.setText(reader.nextString());
            } else if (name.equals("updated")) {
                try {
                    reflection.setLastUpdate(format.parse(reader.nextString()));
                } catch (ParseException e) {
                    reflection.setLastUpdate(new Date(0));
                    e.printStackTrace();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return reflection;
    }

    public class EntryComparator implements Comparator<Entry> {

        @Override
        public int compare(Entry lhs, Entry rhs) {
            long t1 = lhs.getLastUpdate().getTime();
            long t2 = rhs.getLastUpdate().getTime();
            if (t2 > t1)
                return 1;
            else if (t1 > t2)
                return -1;
            else
                return 0;
        }

    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnModeChangeListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnModeChangeListener {
        public void onModeChange(int type);

        public void onRefreshFinish();
    }
}