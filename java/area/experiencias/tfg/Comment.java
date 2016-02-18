package area.experiencias.tfg;

import java.util.Date;

/**
 * Created by adrianbouza on 31/05/14.
 */
public class Comment {

    private String user;
    private String comment;
    private Date date;

    public Comment() {
    }

    public Comment(String user, String comment, Date date) {
        this.user = user;
        this.comment = comment;
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user='" + user + '\'' +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                '}';
    }
}
