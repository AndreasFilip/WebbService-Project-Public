package com.example.andreas.listproject;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class MainActivity extends AppCompatActivity {
    //New comment to verify that branch is created
    @Bind(R.id.addButton) Button addButton;
    @Bind(R.id.listView) ListView listView;
    private String editTextStringForNewList;
    private int positionInList;
    ArrayAdapter arrayAdapter;
    private ArrayList<ToDoList> toDoLists;
    private JSONArray lists;
    private JSONObject listsWithName;
    MainActivity mainActivity;
    android.support.v4.app.FragmentManager fm;
    android.support.v4.app.DialogFragment removeListItemFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        MyTask t = new MyTask();
        t.execute("GET", "lists/");
        setListAdapter();
    }
    /**
     *Function for setting the ArrayAdapter to the ListView
     */
    private void setListAdapter(){
        arrayAdapter = new ToDoListArrayAdapter(this, R.layout.list_item_layout, toDoLists);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("KEY", toDoLists.get(positionInList).getId());
                startActivity(intent);
                Log.i("TAG", toDoLists.get(positionInList).getId());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                removeListItemFragment = new DeleteDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "MAIN ACTIVITY");
                removeListItemFragment.setArguments(bundle);
                Log.i("TAG", toDoLists.get(positionInList).getId());
                removeListItemFragment.show(fm, "fragment_remove_list_item");
                return true;
            }
        });
    }
    /**
     *Class that sends requests to server
     */
    private class MyTask extends AsyncTask<String, Void, String>{
        HttpURLConnection connection;
        URL url;
        private String URLEN = "http://api.cmdemo.se/";
        @Override
        protected void onPreExecute(){
            //TODO Check if user has network!
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
                        if(!isResponseNull(response)) {
                            lists = new JSONArray(response.toString());
                            listsWithName = new JSONObject();
                        }
                        else {
                            Log.i("TAG","Response was null");
                        }
                        connection.disconnect();
                        break;
                    case "POST":
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Content-type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.connect();
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        String outputString = "{ \"title\":\""+editTextStringForNewList+"\" }";
                        writer.write(outputString);
                        writer.flush();
                        writer.close();
                        os.close();
                        BufferedReader in2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine2;
                        StringBuilder response2 = new StringBuilder();
                        while ((inputLine2 = in2.readLine()) != null) {
                            response2.append(inputLine2);
                        }
                        in2.close();
                        Log.i("responsetext", response2.toString());
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
                        StringBuilder response3 = new StringBuilder();

                        while ((inputLine3 = in3.readLine()) != null) {
                            response3.append(inputLine3);
                        }
                        in3.close();
                        connection.disconnect();
                        Log.i("responsetext", response3.toString());
                        break;
                    default:
                        break;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            toDoLists.clear();
            for(int i = 0; i < lists.length(); i++){
                ToDoList p = null;
                try {
                    listsWithName = (JSONObject) lists.get(i);
                    String title = listsWithName.getString("title");
                    String id = listsWithName.getString("id");
                    Log.i("title", "title" + title + "id" + id);
                    p = new ToDoList(title, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toDoLists.add(p);
            }
            setListAdapter();
            arrayAdapter.notifyDataSetChanged();
        }
    }
    /**
     *ArrayAdapter for ListView
     */
    private class ToDoListArrayAdapter extends ArrayAdapter<ToDoList> {

        private final Context context;
        private final ArrayList<ToDoList> toDoListList;

        protected ToDoListArrayAdapter(Context context, int resource, List<ToDoList> toDoLists) {
            super(context, resource, toDoLists);
            this.context = context;
            this.toDoListList = (ArrayList) toDoLists;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
            TextView nameOfListTextView = (TextView)itemView.findViewById(R.id.nameOfListTextView);
            nameOfListTextView.setText(toDoLists.get(position).getNameOfList());

            return itemView;
        }
    }
    /**
     * Function to load list after user has removed a list
     */
    protected void deleteList(){
        MyTask t2 = new MyTask();
        t2.execute("DELETE", "lists/" + toDoLists.get(positionInList).getId() + "/");
        Log.i("TAG", toDoLists.get(positionInList).getId());
        MyTask t3 = new MyTask();
        t3.execute("GET", "lists/");
    }
    /**
     *Creates an instance of necessary variables and sets on click listener for add list item button
     */
    private void init(){
        toDoLists = new ArrayList<>();
        mainActivity = this;
        fm = getSupportFragmentManager();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addListItemFragment = new AddListItemFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "MAIN ACTIVITY");
                addListItemFragment.setArguments(bundle);
                transaction.replace(R.id.relativeLayout, addListItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                addButton.setVisibility(View.INVISIBLE);
            }
        });
    }
    /**
     * Gets user-inputted text from an EditText in the AddListItemFragment for the creation of a new listItem
     * @param textFromEditText String to contain the text from the EditText
     */
    protected void getStringForNewListItem(String textFromEditText){
        editTextStringForNewList = textFromEditText;
        MyTask t5 = new MyTask();
        t5.execute("POST", "lists/");
        MyTask t4 = new MyTask();
        t4.execute("GET", "lists/");
        arrayAdapter.notifyDataSetChanged();
    }
    /**
     *Method for hiding the soft-keyboard after user has pressed , yes after creating a new listItem
     * @param input the EditText which keyboard you want to close
     */
    protected void hideSoftKeyboard(EditText input) {
        input.setInputType(0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
    /**
     * @param response String buffer response from a HTTP request
     * @return true if response is null, false if it isn't
     */
    protected boolean isResponseNull(StringBuffer response){
        return response.toString().equals("");
    }
}