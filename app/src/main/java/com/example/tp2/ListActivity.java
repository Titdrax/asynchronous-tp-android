package com.example.tp2;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = findViewById(R.id.list);
        EditText searchTagsEditText = findViewById(R.id.editTextSearchTags);
        Button searchBtn = findViewById(R.id.buttonSearch);

        searchBtn.setOnClickListener(v -> {
            String searchTags = searchTagsEditText.getText().toString();
            String url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=" + searchTags + "&format=json";

            list.setAdapter(new MyAdapter());
            new AsyncFlickrJSONDataForList((MyAdapter) list.getAdapter()).execute(url);
        });
    }

    class MyAdapter extends BaseAdapter {
        Vector<String> vector;

        public MyAdapter() {
            vector = new Vector<>();
        }

        public void dd(String url) {
            vector.add(url);
        }

        @Override
        public int getCount() {
            return vector.size();
        }

        @Override
        public Object getItem(int position) {
            return vector.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_text_view, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.textViewInList)).setText((String) getItem(position));
            */

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.layout_bitmap, parent, false);
            }
            ImageView imageView = convertView.findViewById(R.id.imageViewInList);

            Response.Listener<Bitmap> rep_listener = response -> imageView.setImageBitmap(response.copy(Bitmap.Config.RGB_565, false));

            ImageRequest imageRequest = new ImageRequest(
                    (String) getItem(position)
                    , rep_listener
                    , 0
                    , 0
                    , imageView.getScaleType()
                    , Bitmap.Config.RGB_565
                    , null
            );

            MySingleton.getInstance(parent.getContext()).addToRequestQueue(imageRequest);

            return convertView;
        }
    }

    class AsyncFlickrJSONDataForList extends AsyncTask<String, Void, JSONObject> {
        private final MyAdapter myAdapter;

        public AsyncFlickrJSONDataForList(MyAdapter myAdapter) {
            super();

            this.myAdapter = myAdapter;
        }

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

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject media = item.getJSONObject("media");

                    String imageUrl = media.getString("m");
                    myAdapter.dd(imageUrl);

                    Log.i("JFL", "Adding to adapter url : " + imageUrl);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myAdapter.notifyDataSetChanged();
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
    }


}