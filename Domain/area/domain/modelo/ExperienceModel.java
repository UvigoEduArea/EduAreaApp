package area.domain.modelo;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Created by adrianbouza on 23/02/14.
 */
public class ExperienceModel implements Parcelable{

    private int id;
    private String name;
    private String element_image_file_name;
    private String description;
    private String shortDescription;
    private Bitmap image;
    private int activity_sequence_id;
    private ArrayList<Integer> activities_ids;

    public ExperienceModel(int id, String name, String element_image_file_name, String description,
                      String shortDescription, Bitmap image, int activity_sequence_id, ArrayList<Integer> activities_ids) {
        this.id = id;
        this.name = name;
        this.element_image_file_name = element_image_file_name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.image = image;
        this.activity_sequence_id = activity_sequence_id;
        this.activities_ids = activities_ids;
    }

    public ExperienceModel(){
        this.id = -1;
        this.name = null;
        this.element_image_file_name = null;
        this.description = null;
        this.shortDescription = null;
        this.image = null;
        this.activity_sequence_id = -1;
        this.activities_ids = new ArrayList<Integer>();
    }

    public ExperienceModel(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.element_image_file_name = parcel.readString();
        this.description = parcel.readString();
        this.shortDescription = parcel.readString();
        this.activity_sequence_id = parcel.readInt();
        this.activities_ids = (ArrayList<Integer>) parcel.readSerializable();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public List<Integer> getActivities_ids() {
        return activities_ids;
    }

    public void setActivities_ids(ArrayList<Integer> activities_ids) {
        this.activities_ids = activities_ids;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", element_image_file_name='" + element_image_file_name + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", image=" + image +
                ", activity_sequence_id=" + activity_sequence_id +
                ", activities_ids=" + activities_ids +
                '}';
    }

    public String activitiesIdsToString(){
        return "Activities{" + activities_ids.toString() + "}";
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
        dest.writeString(description);
        dest.writeString(shortDescription);
        dest.writeInt(activity_sequence_id);
        dest.writeSerializable(activities_ids);
    }

    public static final Parcelable.Creator<ExperienceModel> CREATOR = new Creator<ExperienceModel>() {

        public ExperienceModel createFromParcel(Parcel source) {

            return new ExperienceModel(source);
        }

        public ExperienceModel[] newArray(int size) {

            return new ExperienceModel[size];
        }

    };

    public int getActivity_sequence_id() {
        return activity_sequence_id;
    }

    public void setActivity_sequence_id(int activity_sequence_id) {
        this.activity_sequence_id = activity_sequence_id;
    }
    
   
    public class ExperienceModelSerializer extends TypeAdapter<List<ExperienceModel>>{

    	@Override
    	public List<ExperienceModel> read(JsonReader in) throws IOException {
    		
    		ArrayList<ExperienceModel> lista = new ArrayList<ExperienceModel>();
    		in.beginObject();
    		while (in.hasNext()) {
                String key = in.nextName();
                String value = in.nextString();
                //lista.add(new BasicNameValuePair(key,value));
              }  
            in.endObject();   
    		return lista;
    	}

    	@Override
    	public void write(JsonWriter out, List<ExperienceModel> lista)
    			throws IOException {
    		out.beginObject();
		    for(int i=0; i<lista.size();i++){
		    	out.name("id");
		    	out.value(lista.get(i).getId());
		    	out.name("name");
		    	out.value(lista.get(i).getName());
		    	out.name("element_image_file_name");
		    	out.value(lista.get(i).getElement_image_file_name());
		    	out.name("description");
		    	out.value(lista.get(i).getDescription());
		    	out.name("shortDescription");
		    	out.value(lista.get(i).getShortDescription());
		    	out.name("activity_sequence_id");
		    	out.value(lista.get(i).getActivity_sequence_id());
		    	out.name("activities_ids");
		    	out.value(lista.get(i).getActivities_ids().toString());
		    }
		    out.endObject();
    		
    	}
    	
    }
    
    
    
    
    
}


