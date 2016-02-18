package area.experiencias.tfg;

import java.util.Date;

/**
 * Created by adrianbouza on 12/05/14.
 */
public class Entry {

    public static final int REFLECTION = 0;
    public static final int EVIDENCE = 1;

    private int entryType;
    private Date lastUpdate;

    public Entry(int entryType, Date lastUpdate) {
        this.entryType = entryType;
        this.lastUpdate = lastUpdate;
    }

    public Entry(int entryType) {
        this.entryType = entryType;
    }

    public Entry() {
    }

    public int getEntryType() {
        return entryType;
    }

    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
