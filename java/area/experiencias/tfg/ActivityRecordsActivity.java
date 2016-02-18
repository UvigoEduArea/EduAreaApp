package area.experiencias.tfg;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityRecordsActivity extends ActionBarActivity implements ActivityRecordsFragment.OnModeChangeListener {

    private static final int ANIMATION_DURATION = 400;

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int SELECT_VIDEO = 3;
    private static final int RECORD_VIDEO = 4;
    //private static final int PIC_CROP = 5;
    private static final int PICK_FILE = 6;

    ActivityPagerAdapter mActivityPagerAdapter;
    ViewPager pager;
    TextView indicator;

    MiniActivity miniActivity;
    ArrayList<MiniActivity> list = new ArrayList<MiniActivity>();
    int currentPosition;
    SharedPreferences preferences;

    private LinearLayout buttonsLayout;
    private boolean edition = false;
    private ImageButton addReflection, addDocument, addImage, addVideo;
    private ImageButton nextActivity, previousActivity;

    // Refresh menu item
    private MenuItem refreshMenuItem;

    private Uri imageFilePath;

    public ActivityRecordsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_records);

        // Se recoge el objeto MiniActivity y su imagen provenientes de la actividad previa
        Intent intent = getIntent();
        list = intent.getParcelableArrayListExtra("activities");
        for (MiniActivity m : list) {
            System.out.println(m.toString());
        }
        currentPosition = intent.getExtras().getInt("position");
        miniActivity = list.get(currentPosition);
        miniActivity.setImage((Bitmap) intent.getParcelableExtra("image"));

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (miniActivity.getName() != null) actionBar.setTitle(miniActivity.getName());
        else actionBar.setTitle(getString(R.string.title));
        actionBar.setSubtitle(getString(R.string.activity));

        // Se crea el viewpager y se añaden los fragments
        pager = (ViewPager) findViewById(R.id.activity_pager);
        mActivityPagerAdapter = new ActivityPagerAdapter(getSupportFragmentManager(),list);
        pager.setAdapter(mActivityPagerAdapter);
        pager.setCurrentItem(currentPosition);
        pager.setOffscreenPageLimit(3);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        indicator = (TextView) findViewById(R.id.indicator);
        indicator.setText((currentPosition+1) + " / " + list.size());

        ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                indicator.setText((currentPosition+1) + " / " + list.size());
                miniActivity = list.get(currentPosition);

                // Set up the action bar.
                final ActionBar actionBar = getSupportActionBar();
                if (miniActivity.getName() != null) actionBar.setTitle(miniActivity.getName());
                else actionBar.setTitle(getString(R.string.title));
                actionBar.setSubtitle(getString(R.string.activity));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        pager.setOnPageChangeListener(mPageChangeListener);

        buttonsLayout = (LinearLayout) findViewById(R.id.buttons_layout);

        addReflection = (ImageButton) findViewById(R.id.add_reflection);
        addReflection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Se crea un diálogo de añadir reflexión
                final Dialog dialog = new Dialog(ActivityRecordsActivity.this);
                dialog.setContentView(R.layout.add_reflection_dialog);
                dialog.setTitle(getString(R.string.new_reflection));

                final EditText reflectionText = (EditText) dialog.findViewById(R.id.editText);
                final RadioButton positive = (RadioButton) dialog.findViewById(R.id.positive);
                final RadioButton negative = (RadioButton) dialog.findViewById(R.id.negative);

                positive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        negative.setChecked(!isChecked);
                    }
                });

                negative.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        positive.setChecked(!isChecked);
                    }
                });

                ImageButton save = (ImageButton) dialog.findViewById(R.id.save);
                ImageButton cancel = (ImageButton) dialog.findViewById(R.id.cancel);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text;
                        if (reflectionText.getText() != null)
                            text = reflectionText.getText().toString();
                        else text = "";
                        String type;
                        if (positive.isChecked()) {
                            type = Reflection.POSITIVE;
                        } else if (negative.isChecked()) {
                            type = Reflection.NEGATIVE;
                        } else {
                            type = Reflection.FREE;
                        }
                        Reflection newReflection = new Reflection(type, text, new Date());
                        new SetReflectionTask(currentPosition, ActivityRecordsActivity.this).execute(newReflection);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        addImage = (ImageButton) findViewById(R.id.add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ActivityRecordsActivity.this);
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

                ImageButton camera = (ImageButton) dialog.findViewById(R.id.camera);

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values = new ContentValues(2);
                            values.put(MediaStore.Images.Media.DESCRIPTION, "Edu-AREA image");
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            imageFilePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);

                            if (takePicture.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePicture, TAKE_PICTURE);
                                dialog.dismiss();
                            }
                        }catch (ActivityNotFoundException exception){
                            Dialog dialog = new Dialog(ActivityRecordsActivity.this);
                            dialog.setTitle(getString(R.string.could_not_open_camera));
                            dialog.show();
                        }
                    }
                });

                dialog.show();
            }
        });

        addVideo = (ImageButton) findViewById(R.id.add_video);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ActivityRecordsActivity.this);
                dialog.setContentView(R.layout.add_video_dialog);
                dialog.setTitle(getString(R.string.add_video));

                ImageButton gallery = (ImageButton) dialog.findViewById(R.id.gallery);

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent selectPicture = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        selectPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
                        startActivityForResult(selectPicture, SELECT_VIDEO);
                        dialog.dismiss();
                    }
                });

                ImageButton camera = (ImageButton) dialog.findViewById(R.id.camera);

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent takeVideo = new Intent(
                                    MediaStore.ACTION_VIDEO_CAPTURE);
                            if (takeVideo.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takeVideo, RECORD_VIDEO);
                            }
                            dialog.dismiss();
                        }catch (ActivityNotFoundException exception){
                            Dialog dialog = new Dialog(ActivityRecordsActivity.this);
                            dialog.setTitle(getString(R.string.could_not_open_camera));
                            dialog.show();
                        }
                    }
                });

                dialog.show();
            }
        });

        addDocument = (ImageButton) findViewById(R.id.add_document);
        addDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, PICK_FILE);
                } catch (ActivityNotFoundException e) {
                    new AlertDialog.Builder(ActivityRecordsActivity.this)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.file_explorer_not_found))
                            .setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        nextActivity = (ImageButton) findViewById(R.id.nextActivity);
        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem()+1 < mActivityPagerAdapter.getCount()) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });

        previousActivity = (ImageButton) findViewById(R.id.previousActivity);
        previousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem()-1 >= 0) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        // Return to previous page when we press back button
        if (this.pager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);

    }

    @Override
    public void onModeChange(int type) {
        switch (type){
            case 0:
                addReflection.setVisibility(View.GONE);
                addDocument.setVisibility(View.VISIBLE);
                addImage.setVisibility(View.VISIBLE);
                addVideo.setVisibility(View.VISIBLE);
                break;
            case 1:
                addReflection.setVisibility(View.VISIBLE);
                addDocument.setVisibility(View.GONE);
                addImage.setVisibility(View.GONE);
                addVideo.setVisibility(View.GONE);
                break;
            case 2:
                addReflection.setVisibility(View.VISIBLE);
                addDocument.setVisibility(View.VISIBLE);
                addImage.setVisibility(View.VISIBLE);
                addVideo.setVisibility(View.VISIBLE);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onRefreshFinish() {
        if (refreshMenuItem != null) {
            refreshMenuItem.collapseActionView();
            // remove the progress bar view
            refreshMenuItem.setActionView(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // Resultado de seleccionar imagen
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    System.out.println("Imagen seleccionada: " + picturePath);
                    cursor.close();

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle(getString(R.string.comment_));
                    alert.setMessage(getString(R.string.write_description));

                    final EditText input = new EditText(this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // String picturePath contiene el path de la imagen
                            Evidence evidence = new Evidence(Evidence.IMAGE, picturePath, "", new Date());
                            evidence.setText(value);
                            new SetEvidenceTask(currentPosition, ActivityRecordsActivity.this).execute(evidence);
                        }
                    });
                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();

                }
                break;
            // Resultado de tomar fotografía
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri pictureUri = imageFilePath;
                    final String picturePath = ImageHelper.getRealPathFromURI(pictureUri,this);
                    System.out.println("Imagen seleccionada: " + picturePath);

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle(getString(R.string.comment_));
                    alert.setMessage(getString(R.string.write_description));

                    final EditText input = new EditText(this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // String picturePath contiene el path de la imagen
                            Evidence evidence = new Evidence(Evidence.IMAGE, picturePath, "", new Date());
                            evidence.setText(value);
                            new SetEvidenceTask(currentPosition, ActivityRecordsActivity.this).execute(evidence);
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();

                }
                break;
            // Resultado de seleccionar vídeo
            case SELECT_VIDEO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedVideo,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String videoPath = cursor.getString(columnIndex);
                    System.out.println("Video seleccionado: " + videoPath);
                    cursor.close();

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle(getString(R.string.comment_));
                    alert.setMessage(getString(R.string.write_description));

                    final EditText input = new EditText(this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // String videoPath contiene el path del video
                            Evidence evidence = new Evidence(Evidence.VIDEO, videoPath, "", new Date());
                            evidence.setText(value);
                            new SetEvidenceTask(currentPosition, ActivityRecordsActivity.this).execute(evidence);
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }
                break;
            // Resultado de capturar vídeo
            case RECORD_VIDEO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri recordedVideo = data.getData();
                    final String videoPath = ImageHelper.getRealPathFromURI(recordedVideo, this);
                    System.out.println("Video seleccionado: " + videoPath);

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle(getString(R.string.comment_));
                    alert.setMessage(getString(R.string.write_description));

                    final EditText input = new EditText(this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // String videoPath contiene el path del video
                            Evidence evidence = new Evidence(Evidence.VIDEO, videoPath, "", new Date());
                            evidence.setText(value);
                            new SetEvidenceTask(currentPosition, ActivityRecordsActivity.this).execute(evidence);
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }
                break;
            case PICK_FILE:
                if (resultCode == RESULT_OK) {
                    final String documentPath = data.getData().getPath();

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle(getString(R.string.comment_));
                    alert.setMessage(getString(R.string.write_description));

                    final EditText input = new EditText(this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // String documentPath contiene el path del video
                            Evidence evidence = new Evidence(Evidence.DOCUMENT, documentPath, "",new Date());
                            evidence.setText(value);
                            new SetEvidenceTask(currentPosition, ActivityRecordsActivity.this).execute(evidence);
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_records, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateCurrentFragment();
                return true;
            case R.id.action_edit:
                animateButtonLayout(edition, ANIMATION_DURATION);
                ((ActivityRecordsFragment)mActivityPagerAdapter.
                        getRegisteredFragment(pager.getCurrentItem())).
                        entriesLayout.setVisibility(edition ? View.GONE : View.VISIBLE);
                edition = !edition;
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void updateCurrentFragment(){
        ActivityRecordsFragment current =
                ((ActivityRecordsFragment)mActivityPagerAdapter.getRegisteredFragment(pager.getCurrentItem()));
        if(current.isAdded()) current.updateActivity();
        if (refreshMenuItem != null && android.os.Build.VERSION.SDK_INT >= 14) {
            refreshMenuItem.setActionView(R.layout.action_progressbar);
            refreshMenuItem.expandActionView();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateButtonLayout(boolean show, int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (show) {
                ObjectAnimator oa = ObjectAnimator.ofFloat(buttonsLayout, "translationY", -buttonsLayout.getHeight(), 0f);
                oa.setDuration(duration);
                oa.start();
            } else {
                ObjectAnimator oa = ObjectAnimator.ofFloat(buttonsLayout, "translationY", 0f, -buttonsLayout.getHeight());
                oa.setDuration(duration);
                oa.start();
            }
        } else {
            if (show) {
                buttonsLayout.setVisibility(View.VISIBLE);
            } else {
                buttonsLayout.setVisibility(View.GONE);
            }
        }
    }

    private class ActivityPagerAdapter extends FragmentPagerAdapter {

        ArrayList<MiniActivity> list;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ActivityPagerAdapter(FragmentManager fm, ArrayList<MiniActivity> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return ActivityRecordsFragment.newInstance(this.list.get(position),position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).getName();
        }
    }

    // Hilo para enviar nueva evidencia al servidor
    public class SetEvidenceTask extends AsyncTask<Evidence, Boolean, Boolean> {

        int position;
        private ProgressDialog dialog;

        public SetEvidenceTask(int position, Context context) {
            this.position = position;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Enviando evidencia...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Evidence... evidences) {
            preferences = getSharedPreferences("data", MODE_PRIVATE);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/experienceActivityEntries/setEvidence.json?";
                HttpPost request = new HttpPost(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("activity_id", new StringBody(list.get(position).getId() + ""));
                if (evidences[0].getType().equals(Evidence.DOCUMENT)) {
                    entity.addPart("element_document", new FileBody(new File(evidences[0].getDocument())));
                } else if (evidences[0].getType().equals(Evidence.IMAGE)) {
                    String fileExtension
                            = MimeTypeMap.getFileExtensionFromUrl(evidences[0].getImage());
                    String mimeType
                            = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                    entity.addPart("element_image", new FileBody(new File(evidences[0].getImage()),mimeType));
                } else {
                    entity.addPart("element_video", new FileBody(new File(evidences[0].getVideo())));
                }
                entity.addPart("type", new StringBody(evidences[0].getType()));
                entity.addPart("text", new StringBody(evidences[0].getText()));
                request.setEntity(entity);
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
                Toast.makeText(ActivityRecordsActivity.this, getString(R.string.evidence_saved), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(ActivityRecordsActivity.this, getString(R.string.evidence_no_saved), Toast.LENGTH_SHORT).show();
            updateCurrentFragment();
        }
    }

    // Hilo para enviar nueva reflexión al servidor
    public class SetReflectionTask extends AsyncTask<Reflection, Boolean, Boolean> {

        int position;
        private ProgressDialog dialog;

        public SetReflectionTask(int position, Context context) {
            this.position = position;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Enviando reflexión...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Reflection... reflections) {
            preferences = getSharedPreferences("data", MODE_PRIVATE);
            try {
                HttpClient client = new DefaultHttpClient();
                String UrlId = "http://" + getString(R.string.server_address) + "/gl/experienceActivityEntries/setReflection.json?";
                HttpPost request = new HttpPost(UrlId);
                request.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("activity_id", "" + list.get(position).getId()));
                nameValuePairs.add(new BasicNameValuePair("type", reflections[0].getType()));
                nameValuePairs.add(new BasicNameValuePair("text", reflections[0].getText()));
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
                Toast.makeText(ActivityRecordsActivity.this, getString(R.string.reflection_saved), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(ActivityRecordsActivity.this, getString(R.string.reflection_no_saved), Toast.LENGTH_SHORT).show();
            updateCurrentFragment();
        }
    }
}