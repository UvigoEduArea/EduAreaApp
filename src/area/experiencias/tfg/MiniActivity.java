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
    private String description;
    private int experience_id;
    private String descriptionStudent;
    private boolean separador;
    private String definition = "";
    private String autor = "";
    private String updatedAt = "";
    private String permiso = "";

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
        this.description = null;
        this.setExperienceId(-1);
        this.setDescriptionStudent(null);
        this.setSeparador(false);
        this.definition = "";
        this.autor = "";
    }

    public MiniActivity(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.element_image_file_name = parcel.readString();
        this.start = parcel.readString();
        this.end = parcel.readString();
        this.progress = parcel.readString();
        this.description = parcel.readString();
        this.image = null;
        this.setExperienceId(parcel.readInt());
        this.setDescriptionStudent(parcel.readString());
        this.setSeparador(parcel.readInt() == 1 ? true : false);
        this.definition = parcel.readString();
        this.autor = parcel.readString();
        
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
                ", description='" + description + '\'' +
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
        dest.writeString(description);
        dest.writeInt(experience_id);
        dest.writeString(descriptionStudent);
        dest.writeInt(separador ? 1 : 0);
        dest.writeString(definition);
        dest.writeString(autor);
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getExperienceId() {
		return experience_id;
	}

	public void setExperienceId(int experience_id) {
		this.experience_id = experience_id;
	}

	public String getDescriptionStudent() {
		return descriptionStudent;
	}

	public void setDescriptionStudent(String descriptionStudent) {
		this.descriptionStudent = descriptionStudent;
	}

	public boolean isSeparador() {
		return separador;
	}

	public void setSeparador(boolean separador) {
		this.separador = separador;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getPermiso() {
		return permiso;
	}

	public void setPermiso(String permiso) {
		this.permiso = permiso;
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
