package com.example.spl.promocomo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    TextView textElement;
    TextView textElementResult;
    TextView convertFrom;
    TextView convertTo;
    String DEBUG_TAG ="hiiiii";
    String QUERYTAG = "query";
    String RESULTSTAG="results";
    String RATETAG="rate";
    Double ExchangeRate = 0.00;
    String str = "";
    int from;
    int to;
    String[] value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textElement = (TextView) findViewById(R.id.textView);
        textElementResult = (TextView) findViewById(R.id.textView2);
        textElement.setMovementMethod(new ScrollingMovementMethod());
        textElement.setText("click on the button below to find USD INR EXCHANGE RATE");
        convertFrom = (TextView) findViewById(R.id.convertFrom);
        convertTo = (TextView) findViewById(R.id.convertTo);
        value  = getResources().getStringArray(R.array.value);


        //
        //using spinner
        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.name, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerFrom.setAdapter(adapter);
        spinnerFrom.setOnItemSelectedListener(new mySpinnerListener(1));
        spinnerTo.setAdapter(adapter);
        spinnerTo.setOnItemSelectedListener(new mySpinnerListener(2));







        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "feature comming soon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


    }

    // function for Convert Button
    public void myClickHandler(View view) {
        textElementResult.setText("Loading ...");
        InputMethodManager inputMethodManager= (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        String currencyFrom = value[from];      //convertFrom.getText().toString();
        String currencyTo = value[to];          //.getText().toString();
        String stringUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20%28%22"+currencyFrom+currencyTo+"%22%29&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);

            Snackbar.make(view, "n/w available", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            textElement.setText("No network connection available.");
        }
    }

    //AsynchronousT
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            textElement.setText(result);
            try {
                JSONObject mainObj = new JSONObject(result);
                JSONObject queryObj= mainObj.getJSONObject(QUERYTAG);
                JSONObject resultsObj= queryObj.getJSONObject(RESULTSTAG);
                JSONObject rateObj= resultsObj.getJSONObject(RATETAG);
                ExchangeRate = rateObj.getDouble("Rate");
                textElementResult.setText(ExchangeRate.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 1259;

        String response1 ="";

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response1 += line;
            }

            //log the details
            Log.d(DEBUG_TAG, "The response is: " + response1);

            //return contentAsString;
            return response1;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }



    public void infoButtonPressed(View view) {
        textElement.setText("info button pressed");

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

    class mySpinnerListener implements Spinner.OnItemSelectedListener
    {
        int ide;
        mySpinnerListener(int i)
        {
            ide =i;
        }
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {
            // TODO Auto-generated method stub
            Toast.makeText(parent.getContext(),
                    parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
            String[] tab_names = getResources().getStringArray(R.array.value);
            String currencyCode=tab_names[0];
            if(ide == 1)
                from = position;
            else if(ide == 2)
                to = position;
            textElementResult.setText("Loading ..."+ value[from]+value[to]);
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }


}
