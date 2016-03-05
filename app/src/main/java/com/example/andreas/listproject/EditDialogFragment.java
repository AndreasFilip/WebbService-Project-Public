package com.example.andreas.listproject;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditDialogFragment extends android.support.v4.app.DialogFragment {
    MainActivity mainActivity;
    TaskActivity taskActivity;
    Bundle bundle;
    // Dialog Fragment for when user long-clicks an item in a list to remove it
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle.get("KEY") != null) {
            String whatActivity = bundle.getString("KEY");
            // If main activity
            if(whatActivity == "MAIN ACTIVITY") {
                Log.i("TAG", "MAIN ACTIVITY");
                mainActivity = (MainActivity) getActivity();

                return new AlertDialog.Builder(getActivity())
                        .setTitle("Remove this list?")
                        .setMessage("Are you sure you want to remove this list?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing (will close dialog)
                                Log.i("TAG", "you pressed no");
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do something
                                Log.i("TAG", "you pressed yes");
                                mainActivity.deleteList();

                            }
                        })
                        .create();
            }
            if(whatActivity == "TASK ACTIVITY") {
                Log.i("TAG", ""+whatActivity);
                taskActivity = (TaskActivity) getActivity();
                Log.i("TAG","id"+ taskActivity.idOfTask+ "position in list:"+taskActivity.positionInList);
                return new AlertDialog.Builder(getActivity())
                        .setTitle("Remove this task?")
                        .setMessage("Are you sure you want to remove this task?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing (will close dialog)
                                Log.i("TAG", "user pressed no");
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do something
                                Log.i("TAG", "user pressed yes");
                                taskActivity.pressedYes();
                            }
                        })
                        .create();
            }

    }
        Log.i("TAG","Bundle is empty");
        return new AlertDialog.Builder(getActivity())
                .setTitle("Remove this list?")
                .setMessage("Are you sure you want to remove this list?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                        Log.i("TAG", "you pressed no");
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something
                        Log.i("TAG", "you pressed yes");
                        mainActivity.deleteList();

                    }
                })
                .create();
}
}
