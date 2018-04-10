package com.example.mis.helloandroid;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    /* This will reference the text input box once onCreate has run, allowing us to get the entered
     * text (URL).
     */
    EditText textBox;
    // This will reference the text view, where contents are to be displayed.
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Unsavory hack to get rid of Error StrictMode$AndroidBlockGuardPolicy.onNetwork
         * (via https://stackoverflow.com/a/22395472)
         */
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        // Reference the text input box from activity_main.xml
        textBox = findViewById(R.id.editText);
        // Reference the text view from activity_main.xml
        textView = findViewById(R.id.textView);
    }

    // This will be run when the "Connect" button is pressed.
    public void fetchUrl(View view) {
        Thread fetchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the input from the text box
                String textBoxString = textBox.getText().toString();

                try {
                    // Convert it to a URL
                    URL textBoxURL = new URL(textBoxString);

                    /* fetch its contents
                     * (via https://developer.android.com/reference/java/net/HttpURLConnection.html
                     *  and https://stackoverflow.com/a/9856272)
                     */
                    HttpURLConnection connection = (HttpURLConnection) textBoxURL.openConnection();

                    try {
                        // Open URL stream, read it line by line into urlContent
                        InputStream urlStream = new BufferedInputStream(connection.getInputStream());
                        BufferedReader urlStreamReader = new BufferedReader(
                                new InputStreamReader(urlStream));
                        final StringBuilder urlContent = new StringBuilder();
                        String urlContentLine;

                        while ((urlContentLine = urlStreamReader.readLine()) != null) {
                            urlContent.append(urlContentLine);
                        }

                        /* This is needed to update the TextView successfully
                         * (via https://stackoverflow.com/a/5162096)
                         */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(urlContent);
                            }
                        });

                    } finally {
                            connection.disconnect();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    // show Toast to tell user what went wrong
                    Toast malformedURLToast = Toast.makeText(MainActivity.this, "Invalid URL",
                            Toast.LENGTH_SHORT);
                    malformedURLToast.show();

                } catch (IOException e) {
                    e.printStackTrace();

                    // show Toast to tell user what went wrong
                    Toast connectionErrorToast = Toast.makeText(MainActivity.this,
                            "Unable to connect", Toast.LENGTH_SHORT);
                    connectionErrorToast.show();
                }
            }
        });
        fetchThread.start();
    }
}
