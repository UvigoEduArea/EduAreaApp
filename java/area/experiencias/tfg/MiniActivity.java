package area.experiencias.tfg;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class MiniActivity implements Parcelable {

    public static final String NOT_STARTED = "notstarted", IN_PROGRESS = "inprogress", FINISHED = "finished";

    private int id;
    private String name;
    private String element_image_file_name;
    private Bitmap image;
    private String start;
    private String end;
    private String progress;

    public MiniActivity(int id, String name, String element_image_file_name, Bitmap image) {
        this.id = id;
        this.name = name;
        this.element_image_file_name = element_image_file_name;
        this.image = image;
    }

    public MiniActivity() {
        this.id = -1;
        this.name = null;
        this.element_image_file_name = null;
        this.image = null;
        this.start = null;
        this.end = null;
        this.progress = null;
    }

    public MiniActivity(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.element_image_file_name = parcel.readString();
        this.start = parcel.readString();
        this.end = parcel.readString();
        this.progress = parcel.readString();
        this.image = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement_image_file_name() {
        return element_image_file_name;
    }

    public void setElement_image_file_name(String element_image_file_name) {
        this.element_image_file_name = element_image_file_name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "MiniActivity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", element_image_file_name='" + element_image_file_name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(element_image_file_name);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(progress);
    }

    public static final Parcelable.Creator<MiniActivity> CREATOR = new Creator<MiniActivity>() {
        @Override
        public MiniActivity createFromParcel(Parcel source) {
            return new MiniActivity(source);
        }

        @Override
        public MiniActivity[] newArray(int size) {
            return new MiniActivity[size];
        }
    };
}
