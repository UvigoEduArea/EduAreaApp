package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by adrianbouza on 17/03/14.
 */
public class Evidence extends Entry{

    public static final String DOCUMENT = "Document";
    public static final String IMAGE = "Image";
    public static final String VIDEO = "Video";

    private int id;
    private String type;
    private String document;
    private String image;
    private String video;
    private String videoThumbnail;
    private String text;
    private Bitmap bitmap;

    public Evidence(String type, String param, String text, Date lastUpdate) {
        super(Entry.EVIDENCE, lastUpdate);
        this.type = type;
        if(type.equals(IMAGE)){
            this.image = param;
        }else if(type.equals(DOCUMENT)){
            this.document = param;
        }else{
            this.video = param;
        }
        this.text = text;
    }

    public Evidence(){
        super(Entry.EVIDENCE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", document='" + document + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", video_thumbnail='" + videoThumbnail + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }
}
