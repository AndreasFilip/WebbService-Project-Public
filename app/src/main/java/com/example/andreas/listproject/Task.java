package com.example.andreas.listproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by robin on 2016-02-18.
 */
public class Task implements Parcelable{


    private String title;
    private boolean done;
    private String description;
    private int id;

    public Task(String title, boolean done, String description,int id){
        this.title = title;
        this.done = done;
        this.description = description;
        this.id = id;

    }

    public Task(Parcel in) {
        this.title = in.readString();
    }


    public String getTitle(){
        return this.title;
    }

    public boolean getDone(){
        return this.done;
    }

    public String getDescription(){
        return this.description;
    }

    public int getId(){return this.id;}


    @Override
    public String toString(){
        return this.title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
    }

    //To regenerate Task a CREATOR have to be implemented
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };


}
