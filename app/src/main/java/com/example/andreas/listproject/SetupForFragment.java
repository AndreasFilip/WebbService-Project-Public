package com.example.andreas.listproject;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by umyhfilian on 3/2/2016.
 */
public class SetupForFragment {
    public void setUpForFragmentTasksAdd(TextView topText,EditText title,EditText description,CheckBox checkBox,Button addButton){
        topText.setText(R.string.EnterNameofTask);
        title.setHint(R.string.title);
        description.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.VISIBLE);
        addButton.setText(R.string.addTask);
    }
    public void setUpForFragmentTasksEdit(TextView topText,EditText title,EditText description,CheckBox checkBox,Button addButton){
        topText.setText(R.string.EnterNameofTask);
        title.setText(R.string.title);
        description.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.VISIBLE);
        addButton.setText(R.string.editTask);
    }
    public Task fetchDataFromViews(EditText eTtitle,EditText eTdescription,boolean done){

        String title = eTtitle.getText().toString();
        String description = eTdescription.getText().toString();
        Task task = new Task(title,done,description,0);
        return task;
    }
}
