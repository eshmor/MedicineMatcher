package com.rdln.medicinematcher;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MedicineMatcher extends AppCompatActivity {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";


    private NfcAdapter mNfcAdapter;



    @Override
    protected void onResume() {
        super.onResume();

        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {

        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_matcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            // mTextView.setText("NFC is disabled.");
            Toast.makeText(getApplicationContext(),"NFC is disabled.",Toast.LENGTH_LONG).show();
        } else {
            // mTextView.setText("Read Content : ");
            Toast.makeText(getApplicationContext(),"Read Content : ",Toast.LENGTH_LONG).show();
        }

        Button openBtn = (Button) findViewById(R.id.openBtn);
        openBtn.setEnabled(false);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    Button openBtn = (Button) findViewById(R.id.openBtn);
                    openBtn.setEnabled(false);
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String cloudUrl = SP.getString("example_text", "NA");

                    TextView patientTextView = (TextView) findViewById(R.id.patientIDEditText);

                    CharSequence patientIdText = patientTextView.getText();


                    TextView medicineEditText = (TextView) findViewById(R.id.medicineIdEditText);
                    CharSequence medicineEditTextText = medicineEditText.getText();

                    cloudUrl = cloudUrl + "/Open/" + patientIdText;

                    Toast.makeText(getApplicationContext(), "Calling url " + cloudUrl, Toast.LENGTH_SHORT).show();

                    AsyncTask<String, Void, String> checkMedicineTask = new CheckMedicineTask().execute(cloudUrl);
                    String result = checkMedicineTask.get();
                    Toast.makeText(v.getContext(), "Open", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }


            }
        });



        final Button clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button openBtn = (Button) findViewById(R.id.openBtn);
                openBtn.setEnabled(false);

                TextView isAllowedTextView = (TextView) findViewById(R.id.isAllowedTextView);
                isAllowedTextView.setText("");

                TextView patientTextView = (TextView) findViewById(R.id.patientIDEditText);

                patientTextView.setText("");


                TextView medicineEditText = (TextView) findViewById(R.id.medicineIdEditText);
                medicineEditText.setText("");


            }
        });


        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();


                button.setLayoutParams(layoutParams);


                try {
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                    String cloudUrl = SP.getString("example_text", "NA");

                    TextView patientTextView = (TextView) findViewById(R.id.patientIDEditText);

                    CharSequence patientIdText = patientTextView.getText();


                    TextView medicineEditText = (TextView) findViewById(R.id.medicineIdEditText);
                    CharSequence medicineEditTextText = medicineEditText.getText();

                    cloudUrl=cloudUrl+"/MedicineMatcher/"+patientIdText+"/"+medicineEditTextText;

                    Toast.makeText(getApplicationContext(), "Calling url "+cloudUrl, Toast.LENGTH_SHORT).show();

                    AsyncTask<String, Void, String> checkMedicineTask = new CheckMedicineTask().execute(cloudUrl);
                    String result =checkMedicineTask.get();
                    ParseXml pxml = new ParseXml();
                    List parsedElements = pxml.parse(result);
                    if(parsedElements.size()>0)
                    {
                        Entry entry= (Entry)parsedElements.get(0);

                        TextView patientNameTextView = (TextView) findViewById(R.id.patientNameTextView);
                        patientNameTextView.setText(entry.patientName);



                        TextView medicineNametextView = (TextView) findViewById(R.id.medicineNametextView);
                        medicineNametextView.setText(entry.medicineName);


                        TextView isAllowedTextView = (TextView) findViewById(R.id.isAllowedTextView);
                        if(entry.IsAllowed.equals("true"))
                        {

                             Button openBtn = (Button) findViewById(R.id.openBtn);
                             openBtn.setEnabled(true);
                            isAllowedTextView.setText("Allowed");
                        }else
                        {
                            isAllowedTextView.setText("Rejected");
                        }




                        Toast.makeText(getApplicationContext(), "Is allowd: "+entry.IsAllowed, Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                    Log.e("ERR", e.getMessage());
                }
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        handleIntent(getIntent());
    }

    class CheckMedicineTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            String ret = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("accept-charset", "application/x-www-form-urlencoded; charset=utf-8");
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");


                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(in);
                Charset charset = Charset.forName("UTF8");
                InputStreamReader is = new InputStreamReader(in, charset);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(is);

                String read = br.readLine();

                while (read != null) {
                    //System.out.println(read);
                    sb.append(read);
                    read = br.readLine();

                }

                ret = sb.toString();

                Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT).show();







            } catch (Exception e1) {
                Log.e("ERR", e1.getMessage());
            } finally {
                urlConnection.disconnect();
            }
            return ret;
        }



        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            if (feed != null) {
                Log.e("ERR", feed);
                //Toast.makeText(getApplicationContext(), feed, Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicine_matcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                AsyncTask<Tag, Void, String> executeResult = new NdefReaderTask().execute(tag);
                try {
                    String s = executeResult.get();
                    TextView patientTextView = (TextView) findViewById(R.id.patientIDEditText);
                    if(patientTextView.isFocused())
                    {
                        patientTextView.setText(s);
                    }
                    TextView medicineEditText = (TextView) findViewById(R.id.medicineIdEditText);
                    CharSequence medicineEditTextText = medicineEditText.getText();
                    if(medicineEditText.isFocused())
                    {
                        medicineEditText.setText(s);
                    }



                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "When trying to read text for ACTION_NDEF_DISCOVERED from nfc task got : " + e);
                }


            } else {
                Log.e(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    AsyncTask<Tag, Void, String> execute = new NdefReaderTask().execute(tag);
                    try {
                        String s = execute.get();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "When trying to read text for  from nfc task got : " + e);
                    }
                    break;
                }
            }
        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 128) == 0)?"UTF-8":"UTF-16";
            int languageCodeLength = payload[0] & 0063;
            String languageCode = new String(payload, 1, languageCodeLength,"US-ASCII");
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //mTextView.setText("Read content: " + result);
                TextView patientNameTextView = (TextView) findViewById(R.id.patientNameTextView);
                patientNameTextView.setText(result);
                //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();;
            }

        }

    }

}
