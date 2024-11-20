package models;

import java.util.Date;

public class ActivityLog {
    private Date date;
    private String activity;

    public ActivityLog(Date date, String activity) {
        this.date = date;
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
