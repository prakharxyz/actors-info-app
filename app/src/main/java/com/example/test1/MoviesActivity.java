package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoviesActivity extends AppCompatActivity {

    TextView movieTextView;
    TextView dateTextView;
    TextView durationTextView;
    TextView ratingTextView;
    TextView infoTextView;
    ImageView movieImageView;

    //create imageDownLoader class to set web image
    public static class ImageDownloader extends AsyncTask<String,Void, Bitmap>{
        protected Bitmap doInBackground(String...urls){
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
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

        //when all web api data is received in string we need to process data by json parsing. there is no array in this json so we just have to create string to store different objects
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    String title = jsonObject.getString("Title");
                    String date = jsonObject.getString("Released");
                    String duration = jsonObject.getString("Runtime");
                    String rating = jsonObject.getString("imdbRating");
                    String genre = jsonObject.getString("Genre");
                    String director = jsonObject.getString("Director");
                    String cast = jsonObject.getString("Actors");
                    String plot = jsonObject.getString("Plot");
                    String poster = jsonObject.getString("Poster");

                    String info = "No data available!";

                    if (!title.equals("")) {
                        movieTextView.setText(title);
                    }
                    if (!date.equals("")) {
                        date = "Released date: " + date;
                        dateTextView.setText(date);
                    }
                    if (!duration.equals("")) {
                        duration = "Runtime: " + duration;
                        durationTextView.setText(duration);
                    }
                    if (!rating.equals("")) {
                        rating = "imdb: " + rating;
                        ratingTextView.setText(rating);
                    }
                    if (!genre.equals("") && !director.equals("") && !cast.equals("") && !plot.equals("")) {
                        info = "Directed by: "+director+"\n"+"Cast: "+cast+"\n\n"+"Genre: "+genre+"\n"+plot;
                        infoTextView.setText(info);
                    }

                    ImageDownloader imageTask = new ImageDownloader();
                    Bitmap movieImage = imageTask.execute(poster).get();
                    movieImageView.setImageBitmap(movieImage);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not find movie", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("Error", "some error occured");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        //interconnecting with ActorsActivity and getting useful data from there
        Intent intent1 = getIntent();
        String movie = intent1.getStringExtra("movieSelected");

        movieTextView = (TextView) findViewById(R.id.movieTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        movieImageView = findViewById(R.id.movieImageView);

        //create object and execute url of api
        try {
            DownLoadTask task = new DownLoadTask();
            String result = task.execute("https://www.omdbapi.com/?t="+movie+"&apikey=a91bb7fd").get();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}