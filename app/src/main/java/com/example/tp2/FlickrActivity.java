package com.example.tp2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FlickrActivity extends AppCompatActivity {
    private ImageView flickrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr);

        flickrImage = findViewById(R.id.imageViewFromFlickr);

        Button getImageBtn = findViewById(R.id.buttonGetImage);
        Button backToMainBtn = findViewById(R.id.backToMainFromFlickr);

        getImageBtn.setOnClickListener(new GetImageOnClickListener());

        backToMainBtn.setOnClickListener(v -> {
            Intent backToMainIntent = new Intent(this, MainActivity.class);
            startActivity(backToMainIntent);
        });
    }

    class GetImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json";
            new AsyncFlickrJSONData().execute(url);
        }
    }

    class AsyncFlickrJSONData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;
            URL url;
            try {
                url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String response = readStream(in);
                    response = response.substring("jsonFlickrFeed(".length(), response.length() - 1);

                    jsonObject = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i("JSONObject", jsonObject.toString());

            String imageUrl = null;
            try {
                JSONObject item = jsonObject.getJSONArray("items").getJSONObject(0);

                JSONObject media = item.getJSONObject("media");

                imageUrl = media.getString("m");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new AsyncBitmapDownloader().execute(imageUrl);
        }
    }

    class AsyncBitmapDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                bitmap = BitmapFactory.decodeStream(in);


                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            flickrImage.setImageBitmap(bitmap);
        }
    }
}