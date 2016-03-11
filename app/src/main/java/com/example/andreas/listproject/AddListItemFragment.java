package com.example.andreas.listproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment for adding a task or list
 */
public class AddListItemFragment extends Fragment {


    public AddListItemFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.buttonCancel)
    Button cancelButton;
    @Bind(R.id.textViewTop)
    TextView toptextView;
    @Bind(R.id.editText2)
    EditText description;
    @Bind(R.id.editText)
    EditText title;
    @Bind(R.id.addButton)
    Button addButton;
    @Bind(R.id.checkBox)
    CheckBox checkBox;
    SetupForFragment setupForFragment;
    MainActivity mainActivity;
    TaskActivity taskActivity;
    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_list_item, container, false);
        ButterKnife.bind(this, view);
        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle.get("KEY") != null) {

            String whatActivity = bundle.getString("KEY");
            Log.i("TAG", whatActivity);
            /*Checks bundle that is passed from the activity to determine what the user wants to to and load the
            correct setup, in this case the user wants to add a list*/
            if (whatActivity == "MAIN ACTIVITY" && whatActivity != null) {
                mainActivity = (MainActivity) getActivity();
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // The user didn't input a name
                        if (title.getText().toString() == "") {
                            Toast.makeText(getActivity(), R.string.toastDidntEnteraName, Toast.LENGTH_SHORT).show();
                        } else { // Everythings fine, let's add the list!
                            mainActivity.getStringForNewListItem(title.getText().toString());
                            //Let's Show our add button again and hide the keeboard since it isn't needed anymore and go back
                            mainActivity.addButton.setVisibility(View.VISIBLE);
                            mainActivity.hideSoftKeyboard(title);
                            mainActivity.onBackPressed();
                        }
                    }
                });
            }
            /*Checks bundle that is passed from the activity to determine what the user wants to to and load the
            correct setup, in this case the user wants to create a new task*/
            if (whatActivity == "TASK ACTIVITY CREATE NEW") {
                taskActivity = (TaskActivity) getActivity();
                setupForFragment = new SetupForFragment();
                setupForFragment.setUpForFragmentTasksAdd(toptextView, title, description, checkBox, addButton);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //User didn't enter a title or description
                        if (title.getText().toString() == "" || description.getText().toString() == "") {
                            Toast.makeText(getActivity(), R.string.toastDidntEnteraName, Toast.LENGTH_SHORT).show();
                        } else {// let's add the task!
                            taskActivity.getInfoForNewTask(title.getText().toString(), description.getText().toString(), isCheckboxChecked());
                            hideViewsAndGoBack();
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideViewsAndGoBack();
                    }
                });

            }
            /*Checks bundle that is passed from the activity to determine what the user wants to to and load the
            correct setup, in this case the user wants to edit an existing task*/
            if (whatActivity == "TASK ACTIVITY EDIT" && whatActivity != null) {
                taskActivity = (TaskActivity) getActivity();
                setupForFragment = new SetupForFragment();
                setupForFragment.setUpForFragmentTasksEdit(toptextView, title, description, checkBox, addButton);
                Log.i("TAG", "id of task is :" + taskActivity.idOfTask);
                setDatatoViews(taskActivity.task);
                //User pushes the edit task button
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (title.getText().toString() == taskActivity.title && description.getText().toString() == taskActivity.description && (isBothCheckedOrNot() == 1 || isBothCheckedOrNot() == 2)) {
                            Toast.makeText(getActivity(), R.string.toastTextisTheSame, Toast.LENGTH_SHORT).show();
                            hideViewsAndGoBack();
                        } else {
                            taskActivity.getInfoForEditTask(title.getText().toString(), description.getText().toString(), isCheckboxChecked());
                            hideViewsAndGoBack();
                        }
                    }
                });
                // user pushes the cancel button
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideViewsAndGoBack();
                    }
                });

            }
        } else {
            Log.i("TAG", "No bundle,something went wrong");
        }
        return view;
    }

    //Just a simple function to see if a checkbox is checked
    private boolean isCheckboxChecked() {

        boolean isChecked = checkBox.isChecked();

        if (isChecked) {
            return true;
        }
        if (!isChecked) {
            return false;
        } else {
            return false;
        }
    }

    //Sets data to views when editing a task
    private void setDatatoViews(Task task) {
        description.setText(task.getDescription());
        title.setText(task.getTitle());
        taskActivity.idOfTask = task.getId();
        Log.i("TAG", "id is " + taskActivity.idOfTask);
        if (!task.getDone()) {
            checkBox.setChecked(false);
        }
        if (task.getDone()) {
            checkBox.setChecked(true);
        } else {
            Log.i("TAG", "something went wrong with checkbox");
        }

    }

    // Compares current checkbox with stored one to see if it has changed
    public int isBothCheckedOrNot() {
        if (isCheckboxChecked() && taskActivity.isCheck == "true") {
            return 1;
        }
        if (!isCheckboxChecked() && taskActivity.isCheck == "false") {
            return 2;
        } else {
            return 0;
        }
    }
    public void hideViewsAndGoBack() {
    taskActivity.addButton.setVisibility(View.VISIBLE);
    taskActivity.hideSoftKeyboard(title);
    taskActivity.onBackPressed();
}
    public void onStart() {
        super.onStart();
        cancelButton.setVisibility(View.VISIBLE);
    }
}