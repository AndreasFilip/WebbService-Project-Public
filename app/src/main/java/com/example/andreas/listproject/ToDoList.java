package com.example.andreas.listproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by robin on 2016-02-18.
 */
public class ToDoList implements Parcelable{


    private String nameOfList;
    private String id;

    public ToDoList(String nameOfList, String id){
        this.nameOfList = nameOfList;
        this.id = id;
    }

    public ToDoList(Parcel in) {
        this.nameOfList = in.readString();
        this.id = in.readString();
    }


    public String getNameOfList(){
        return this.nameOfList;
    }

    public String getId(){
        return this.id;
    }

    @Override
    public String toString(){
        return this.nameOfList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nameOfList);
    }

    //To regenerate person a CREATOR have to be implemented
    public static final Parcelable.Creator<ToDoList> CREATOR = new Parcelable.Creator<ToDoList>() {
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };


}
