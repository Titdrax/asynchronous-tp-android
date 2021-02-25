package com.example.tp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText loginEditText;
    private EditText pwdEditText;
    private TextView resultTextView;
    private JSONObject authenticationResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        loginEditText = findViewById(R.id.editTextTextLogin);
        pwdEditText = findViewById(R.id.editTextPassword);
        resultTextView = findViewById(R.id.textViewResult);

        Button authenticateBtn = findViewById(R.id.buttonAuthenticate);
        Button backToMainBtn = findViewById(R.id.backToMainFromAuthenticate);

        authenticationResponse = null;

        authenticateBtn.setOnClickListener(v -> {
            Thread t = new Thread(() -> {
                URL url;
                try {
                    url = new URL("https://httpbin.org/basic-auth/bob/sympa");
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                    String credentials = loginEditText.getText() + ":" + pwdEditText.getText();
                    String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    urlConnection.setRequestProperty("Authorization", basicAuth);
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        authenticationResponse = new JSONObject(readStream(in));

                        runOnUiThread(() -> {
                            boolean res = false;
                            try {
                                res = authenticationResponse.getBoolean("authenticated");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("JFL", Boolean.toString(res));

                            resultTextView.setText(Boolean.toString(res));
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        });

        backToMainBtn.setOnClickListener(v -> {
            Intent backToMainIntent = new Intent(this, MainActivity.class);
            startActivity(backToMainIntent);
        });
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