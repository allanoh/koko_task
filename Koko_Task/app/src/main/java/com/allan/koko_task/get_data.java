package com.allan.koko_task;

import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;




public class get_data extends AppCompatActivity {
    ProgressDialog progress_dialog;
    static final String data_url = "http://api.worldbank.org/v2/datacatalog?format=json";
    String transactionsResults,results,value,id;

    private ListView list_view;
    ArrayList<HashMap<String, String>> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        dataList = new ArrayList<>();

        list_view = (ListView) findViewById(R.id.listdata);

        new GetDataList().execute();
    }

    private class GetDataList extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress_dialog = new ProgressDialog(get_data.this);
            progress_dialog.setMessage("fetching data");
            progress_dialog.setIndeterminate(false);
            progress_dialog.setCancelable(true);
            progress_dialog.show();

        }

        @Override
        protected String doInBackground(final Void... urls) {
            try {
                URL url = new URL(data_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                  return e.getMessage();
              }
        }

        @Override
        protected void onPostExecute(final String result) {

            try {
                JSONObject details = (JSONObject) new JSONTokener(result).nextValue();
                results = details.getString("datacatalog");
                if (results != null) {
                    progress_dialog.dismiss();
                    try {
                        JSONArray resultobj = new JSONArray(results);
                        transactionsResults = resultobj.getString(0);
                        JSONObject dataObject = (JSONObject) new JSONTokener(transactionsResults).nextValue();
                        String data = dataObject.getString("metatype");
                        JSONArray jarray = new JSONArray(data);
                        String dataAll = jarray.getString(0);

                        for (int i = 0; i < dataAll.length(); i++) {
                            JSONObject c = new JSONObject(dataAll);

                            value = c.getString("value");
                            id = c.getString("Catalogue id");
                            HashMap<String, String> mapValue = new HashMap<>();
                            mapValue.put("Catalogue id", id);
                            mapValue.put("value", value);


                            dataList.add(mapValue);
                        }

                    } catch (final JSONException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                    }

                }

                final ListAdapter adapter = new SimpleAdapter(get_data.this, dataList, R.layout.format_data,
                        new String[]{"id", "value"},
                        new int[]{R.id.id, R.id.value});
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                     @Override
                                                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                         @SuppressWarnings("unchecked")
                                                         HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);

                                                         String clicked = (String) map.get("value");
                                                         Toast.makeText(getApplicationContext(), "Selected " + clicked, Toast.LENGTH_LONG).show();
                                                     }
                                                 }
                );

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}




