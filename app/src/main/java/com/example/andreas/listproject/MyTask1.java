package com.example.andreas.listproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

import butterknife.Bind;

/**
 * Created by umyhfilian on 3/4/2016.
 */
public class MyTask1 extends AsyncTask<String, Void, String> {
    public Context context;
    @Bind(R.id.listView) ListView listView;
    FragmentManager fm;
    int positionInList;
    ToDoListArrayAdapter arrayAdapter;
    public ArrayList<ToDoList> toDoLists;
    public JSONArray lists;
    public JSONObject listsWithName;
    String editTextStringForNewList;
    HttpURLConnection connection;
    URL url;
    MainActivity mainActivity;
    private String URLEN = "http://api.cmdemo.se/";
    @Override
    protected void onPreExecute(){

    fm = mainActivity.getFm();

    }
    @Override //task.execute("GET", "list/")
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
                    lists = new JSONArray(response.toString());
                    listsWithName = new JSONObject();
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
                    StringBuffer response2 = new StringBuffer();
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
                    StringBuffer response3 = new StringBuffer();

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

    public void setListAdapter(){
        arrayAdapter = new ToDoListArrayAdapter(context, R.layout.list_item_layout, toDoLists);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("KEY", toDoLists.get(positionInList).getId());
                mainActivity.startActivity(intent);
                Log.i("TAG", toDoLists.get(positionInList).getId());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                positionInList = position;
                EditDialogFragment removeListItemFragment = new EditDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("KEY","MAIN ACTIVITY");
                removeListItemFragment.setArguments(bundle);
                Log.i("TAG", toDoLists.get(positionInList).getId());
                removeListItemFragment.show(fm, "fragment_remove_list_item");
                return true;
            }
        });

    }

}
