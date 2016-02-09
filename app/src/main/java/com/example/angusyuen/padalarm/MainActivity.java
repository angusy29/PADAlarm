package com.example.angusyuen.padalarm;

import android.app.PendingIntent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Dungeon> allDungeons;

    private char userGroupID;
    private ArrayAdapter<CharSequence> groupSpinnerAdapter;

    // Front end interface elements
    private Spinner groupIDSpinner;
    private ImageButton searchDungeonsButton;
    private RecyclerView dungeonRV;

    private TextView noDungeonsText;

    private RecyclerView.Adapter dungeonAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Pending Intent notification
    private PendingIntent pendingIntent;

    // Constants
    public final int AMERICA = 2;
    public final char[] VALIDGROUPS = {'A', 'B', 'C', 'D', 'E'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allDungeons = new ArrayList<Dungeon>();

        initGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isGroupValid(char groupID) {
        for (char c : VALIDGROUPS) {
            if (c == groupID) {
                return true;
            }
        }
        return false;
    }

    // Dialog box
    /*public void openDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Enter a group ID");
        alertDialogBuilder.setMessage("Please enter a valid group ID of A, B, C, D, or E.");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg) {
                        // do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/

    // initialise all front end elements including clicklisteners
    public void initGUI() {
        groupIDSpinner = (Spinner) findViewById(R.id.groupID);
        dungeonRV = (RecyclerView) findViewById(R.id.dungeonRV);
        searchDungeonsButton = (ImageButton) findViewById(R.id.searchDungeonsButton);
        noDungeonsText = (TextView) findViewById(R.id.noDungeonsText);
        dungeonAdapter = new MyAdapter(allDungeons);

        // setting up the recycler view cards
        mLayoutManager = new LinearLayoutManager(this);
        //dungeonRV.addItemDecoration(new SimpleDividerItemDecoration(this));
        dungeonRV.setAdapter(dungeonAdapter);
        dungeonRV.setLayoutManager(mLayoutManager);

        // noDungeons text should not be showing at the beginning
        noDungeonsText.setVisibility(View.GONE);

        // setting up the spinner for choosing the user's group
        groupIDSpinner.setAdapter(groupSpinnerAdapter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        groupSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.groupID, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        groupIDSpinner.setAdapter(groupSpinnerAdapter);

        searchDungeonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                allDungeons.clear();
                dungeonRV.setAdapter(null);
                noDungeonsText.setVisibility(View.GONE);

                userGroupID = groupIDSpinner.getSelectedItem().toString().charAt(6);  // character 6 is the group

                // access API, and sets allDungeons
                new RetrieveDungeonsTask().execute();
            }
        });

    }

    // code from http://www.androidauthority.com/use-remote-web-api-within-android-app-617869/
    // this is thte code to allow the user to connect to PADHerder and use the API
    // to fetch the required dungeons
    public class RetrieveDungeonsTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL("https://www.padherder.com/api/events/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            try {
                JSONArray json =  new JSONArray(response);

                for (int i = 0; i < json.length(); i++) {
                    String title = json.getJSONObject(i).get("title").toString();
                    String country = json.getJSONObject(i).get("country").toString();
                    String group = json.getJSONObject(i).get("group_name").toString();
                    DateTime dateTime = new DateTime(json.getJSONObject(i).get("starts_at").toString());

                    // if the country is america we want to save it
                    if (Integer.valueOf(country) == AMERICA && userGroupID == group.charAt(0)) {
                        Dungeon temp = new Dungeon(title, country, group, dateTime);
                        allDungeons.add(temp);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // front end setting list of dungeons
            // commented out, this was for simple list view
            //dungeonAdapter = new ArrayAdapter<Dungeon>(MainActivity.this, android.R.layout.simple_list_item_1, allDungeons);
            //dungeonLV.setAdapter(dungeonAdapter);

            // for testing purposes
            /*DateTime time = new DateTime("2014-01-08T07:00:00Z");
            Dungeon dungeon = new Dungeon("Dungeon Title", "2", "D", time);
            allDungeons.add(dungeon);

            time = new DateTime("2014-01-08T07:00:00Z");
            dungeon = new Dungeon("Dungeon Title", "2", "D", time);
            allDungeons.add(dungeon);*/

            dungeonAdapter = new MyAdapter(allDungeons);
            dungeonRV.setAdapter(dungeonAdapter);

            if (allDungeons.isEmpty()) {
                noDungeonsText.setVisibility(View.VISIBLE);
            }
        }
    }
}
