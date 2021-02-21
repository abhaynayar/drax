package com.drax.notificationlistener;

public class Logs {
    private String timeStamp;
    private String packageName;
    private String title;
    private String text;

    @Override
    public String toString() {
        return "Logs{" +
                "timeStamp='" + timeStamp + '\'' +
                ", packageName='" + packageName + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public Logs(String timeStamp, String packageName, String title, String text) {
        this.timeStamp = timeStamp;
        this.packageName = packageName;
        this.title = title;
        this.text = text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
