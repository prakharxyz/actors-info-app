package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ActorsActivity extends AppCompatActivity {

    ArrayList<String> moviesArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    ListView moviesListView;
    ImageView actorImageView;

//to get api url
    public class DownLoadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = "";
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

         //when all web api data is received in string we need to process data by json parsing
        // first we have a result array out of which we only need 1st result. then inside result[0] we have known_for array which contains info about all 3 movies.
         // out of each known_for array element we need its title object
        // results[ known_for[original_title(0),original_title(1),original_title(2),original_title(3),...] , * ]
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s); //create object of all the api data

                String results = jsonObject.getString("results"); //api has object results which is an array
                JSONArray resultsArray = new JSONArray(results); //since it is an array we create its array and we are interested in only the first result
                String result1 = resultsArray.getString(0); //to get first result ie 0 index from resultsArray

                JSONObject resultObject = new JSONObject(result1); //create new object of result1 string to process it further

                String actorProfile = resultObject.getString("profile_path");
                try {
                    MoviesActivity.ImageDownloader imageTask = new MoviesActivity.ImageDownloader();
                    Bitmap actorImage = imageTask.execute(actorProfile).get();
                    actorImageView.setImageBitmap(actorImage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                String knownFor = resultObject.getString("known_for"); //now inside this result1 we need list of movie name which are inside knownfor array
//                Log.i("known for",knownFor);

                JSONArray knownForArray = new JSONArray(knownFor);
                for (int i=0 ; i < knownForArray.length() ; i++) {
                    JSONObject moviePart = knownForArray.getJSONObject(i);
                    String movieTitle = moviePart.getString("title");
                    Log.i("movie"+i,movieTitle);
                    moviesArrayList.add(movieTitle);
                }

                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, moviesArrayList);
                moviesListView.setAdapter(arrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        TextView actorTextView = (TextView) findViewById(R.id.actorTextView);
        actorImageView = findViewById(R.id.actorImage);
        moviesListView = findViewById(R.id.moviesList);

        //interconnecting with mainactivity and getting useful data from there
        Intent intent = getIntent();
        Integer actorIndex = intent.getIntExtra("actorIndex", -1);
        String actorName = intent.getStringExtra("actorSelected");

        actorTextView.setText(actorName);

        //create object and execute url of api
        try {
            DownLoadTask task = new DownLoadTask();
            String result = task.execute("https://api.themoviedb.org/3/search/person?api_key=84916a5eb8cfe11618eba97de31932f0&language=en-US&query="+actorName+"&include_adult=true").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set item click listener for moving to MoviesActivity and pass useful data to that
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieSelected = (String) parent.getItemAtPosition(position);

                Intent intent1 = new Intent(getApplicationContext(), MoviesActivity.class);
                intent1.putExtra("movieSelected",movieSelected);
                startActivity(intent1);
            }
        });
    }
}
//

