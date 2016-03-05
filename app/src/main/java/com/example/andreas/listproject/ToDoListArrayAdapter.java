package com.example.andreas.listproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umyhfilian on 3/4/2016.
 */
public class ToDoListArrayAdapter extends ArrayAdapter<ToDoList> {

    private final Context context;
    public ArrayList<ToDoList> toDoListList;

    private void getListOfToDoLists (ArrayList<ToDoList> filledList){
        toDoListList = filledList;
    }

    public ToDoListArrayAdapter(Context context, int resource, List<ToDoList> toDoLists) {
        super(context, resource, toDoLists);
        this.context = context;
        this.toDoListList = (ArrayList) toDoLists;
    }
    //Obligatory method for listAdapter/listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
        TextView nameOfListTextView = (TextView)itemView.findViewById(R.id.nameOfListTextView);
        nameOfListTextView.setText(toDoListList.get(position).getNameOfList());

        return itemView;
    }
}
