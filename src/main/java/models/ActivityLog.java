package models;

import java.util.Date;

/**
 * Represents an activity log entry with a date and description of the activity.
 */
public class ActivityLog {
    private Date date;
    private String activity;

    /**
     * Constructs an ActivityLog with the specified date and activity description.
     *
     * @param date the date of the activity
     * @param activity the description of the activity
     */
    public ActivityLog(Date date, String activity) {
        this.date = date;
        this.activity = activity;
    }

    /**
     * Returns the description of the activity.
     *
     * @return the activity description
     */
    public String getActivity() {
        return activity;
    }

    /**
     * Sets the description of the activity.
     *
     * @param activity the new activity description
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * Returns the date of the activity.
     *
     * @return the activity date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the activity.
     *
     * @param date the new activity date
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
