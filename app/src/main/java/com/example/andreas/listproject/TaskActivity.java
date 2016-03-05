package com.example.andreas.listproject;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskActivity extends AppCompatActivity {

    android.support.v4.app.DialogFragment removeListItemFragment;
    public int idOfTask;
    public String isCheck;
    public String title;
    public String description;
    Task task;
    TaskActivity taskActivity;
    String position;
    @Bind(R.id.addButton) Button addButton;
    @Bind(R.id.listView) ListView listView;
    int positionInList;
    ArrayAdapter arrayAdapter;
    public ArrayList<Task> tasks;
    public JSONArray lists2;
    public JSONObject listsWithName;
    android.support.v4.app.FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        init();
        getPositionInLIst();
        MyTask t = new MyTask();
        t.execute("GET", "lists/"+position+"/tasks/");
        setListAdapter();
    }
    private void setListAdapter(){
        arrayAdapter = new PersonArrayAdapter(this, R.layout.list_item_layout, tasks);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                int id2 = tasks.get(positionInList).getId();
                Log.i("TAG", "id:" + id2 + " " + position);
                // Might have to initilize task
                task = tasks.get(positionInList);
                Fragment addTaskItemFragment = new AddListItemFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "TASK ACTIVITY EDIT");
                addTaskItemFragment.setArguments(bundle);
                transaction.replace(R.id.relativeLayout, addTaskItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                addButton.setVisibility(View.INVISIBLE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                removeListItemFragment = new EditDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("KEY","TASK ACTIVITY");
                removeListItemFragment.setArguments(bundle);
                String id1 = Integer.toString(tasks.get(positionInList).getId());
                Log.i("TAG", id1);
                removeListItemFragment.show(fm, "fragment_remove_list_item");
                return true;
            }

        });
    }
    private class MyTask extends AsyncTask<String, Void, String>{

        HttpURLConnection connection;
        URL url;
        //Target url
        private String URLEN = "http://api.cmdemo.se/";

        @Override
        protected void onPreExecute(){


        }

        @Override
        protected String doInBackground(String... params) { //params[0] = method, params[1] = URI
            try {
                url = new URL(URLEN + params[1]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            try {
                connection = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.setRequestMethod(params[0]);

                switch(params[0]){
                    case "GET":
                        connection.setDoOutput(false); //No body = false
                        connection.setDoInput(true); //We want response from server = true
                        connection.setRequestProperty("Accept", "application/json");
                        connection.connect();

                        int responseCode = connection.getResponseCode();
                        Log.i("responsecode", "The responsecode was: " + responseCode);


                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        lists2 = new JSONArray(response.toString());
                        listsWithName = (JSONObject) lists2.get(0);
                        connection.disconnect();


                        break;
                    case "PUT" :
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Content-type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");

                        connection.connect();
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        JSONObject editTask = new JSONObject();
                        editTask.put("title", title);
                        editTask.put("done", isCheck);
                        editTask.put("description", description);
                        writer.write(editTask.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        BufferedReader in2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine2;
                        StringBuffer response2 = new StringBuffer();

                        while ((inputLine2 = in2.readLine()) != null) {
                            response2.append(inputLine2);
                        }
                        in2.close();

                        Log.i("responsetext", response2.toString());

                        connection.disconnect();
                        break;
                    case "POST":
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Content-type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");

                        connection.connect();
                        OutputStream os1 = connection.getOutputStream();
                        BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(os1, "UTF-8"));
                        JSONObject newTask = new JSONObject();
                        newTask.put("title", title);
                        newTask.put("done", isCheck);
                        newTask.put("description", description);
                        writer1.write(newTask.toString());
                        writer1.flush();
                        writer1.close();
                        os1.close();

                        BufferedReader in4 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine4;
                        StringBuffer response4 = new StringBuffer();

                        while ((inputLine4 = in4.readLine()) != null) {
                            response4.append(inputLine4);
                        }
                        in4.close();

                        Log.i("TAG", response4.toString());
                        connection.disconnect();
                        break;
                    case "DELETE":
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.connect();

                        responseCode = connection.getResponseCode();
                        Log.i("responsecode", "The responsecode was: " + responseCode);


                        BufferedReader in3 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine3;
                        StringBuffer response3 = new StringBuffer();

                        while ((inputLine3 = in3.readLine()) != null) {
                            response3.append(inputLine3);
                        }
                        in3.close();
                        Log.i("TAG", response3.toString());
                        connection.disconnect();
                        break;
                    default:
                        break;
                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result){
            tasks.clear();
            for(int i = 0; i < lists2.length(); i++){
                Task t = null;
                try {
                    listsWithName = (JSONObject) lists2.get(i);
                    String title = listsWithName.getString("title");
                    String description = listsWithName.getString("description");
                    boolean done = listsWithName.getBoolean("done");
                    int id = listsWithName.getInt("id");
                    Log.i("title", "title" + title + "id" + id);
                    t = new Task(title,done,description,id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tasks.add(t);
            }

            setListAdapter();
            arrayAdapter.notifyDataSetChanged();
        }
    }
    private class PersonArrayAdapter extends ArrayAdapter<Task> {

        private final Context context;
        private final ArrayList<Task> personList;

        public PersonArrayAdapter(Context context, int resource, List<Task> persons) {

            super(context, resource, tasks);
            this.context = context;
            this.personList = (ArrayList)tasks;
        }
        //Obligatory function for ArrayAdapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
            TextView firstnameTextView = (TextView)itemView.findViewById(R.id.nameOfListTextView);
            firstnameTextView.setText(tasks.get(position).getTitle());
            return itemView;

        }
    }
    public void init(){
        lists2 = new JSONArray();
        taskActivity = this;
        listsWithName = new JSONObject();
        fm = getSupportFragmentManager();
        tasks = new ArrayList<>();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment addTaskItemFragment = new AddListItemFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "TASK ACTIVITY CREATE NEW");
                addTaskItemFragment.setArguments(bundle);
                transaction.replace(R.id.relativeLayout, addTaskItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                addButton.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void getInfoForNewTask(String titleFromFragment,String descriptionFromFragment,boolean checkFromFragment){

        if(checkFromFragment){
            isCheck = "true";
        }
        if(!checkFromFragment){
            isCheck = "false";
        }
        title = titleFromFragment;
        description = descriptionFromFragment;
        MyTask t5 = new MyTask();
        MyTask t6 = new MyTask();
        t5.execute("POST", "lists/" + position + "/tasks/");
        t6.execute("GET", "lists/"+position+"/tasks/");
        arrayAdapter.notifyDataSetChanged();
    }
    public void getInfoForEditTask(String titleFromFragment,String descriptionFromFragment,boolean checkFromFragment){

        if(checkFromFragment){
            isCheck = "true";
        }
        if(!checkFromFragment){
            isCheck = "false";
        }
        title = titleFromFragment;
        description = descriptionFromFragment;
        MyTask t5 = new MyTask();
        MyTask t7 = new MyTask();
        t5.execute("PUT", "lists/"+position+"/tasks/"+idOfTask+"/");
        t7.execute("GET", "lists/"+position+"/tasks/");
        arrayAdapter.notifyDataSetChanged();
    }
    protected void hideSoftKeyboard(EditText input) {
        input.setInputType(0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
    public void getPositionInLIst(){
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            position = extras.getString("KEY");
        }
        else
        {
            Log.i("TAG","NOTHING IN BUNDLE");
        }
        Log.i("TAG", "postition is" + position);

    }
    // User pressed yes in delete dialog fragment!
    public void pressedYes(){
        MyTask t2 = new MyTask();
        t2.execute("DELETE", "lists/" + position + "/tasks/" + tasks.get(positionInList).getId() + "/"); // 33/ för min lista
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
        MyTask t3 = new MyTask();
        setListAdapter();
        t3.execute("GET", "lists/"+position+"/tasks/");
        arrayAdapter.notifyDataSetChanged();
    }
}