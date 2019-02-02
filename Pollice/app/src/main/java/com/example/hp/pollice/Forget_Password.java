package com.example.hp.pollice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Forget_Password extends AppCompatActivity {
    private TextInputEditText email,contactNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);
        email=(TextInputEditText) findViewById(R.id.forgetEmail);
        contactNumber=(TextInputEditText) findViewById(R.id.forgetContactNumber);
    }

    public void cancle(View view) {
        finish();
    }

    public void EnterBtn(View view) {
        new ForgetUserCheck_Android_to_Mysql().execute("checkForForgetPassword",email.getText().toString(), contactNumber.getText().toString());
    }

    public class ForgetUserCheck_Android_to_Mysql extends AsyncTask<String, Void, String> {
        private  String forgetPasswordEmail="",forgetPasswordContactNumber="";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Forget_Password.this);
            pd.setTitle("Checking User");
            pd.setMessage("Please wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... voids) {
            //String url_checkForForgetPassword = "http://192.168.0.100/New_folder/Pollice/server/checkForForgetPassword.php";
            String method = voids[0];
            if (method.equals("checkForForgetPassword")) { //     check forget password user
                forgetPasswordEmail = voids[1];
                forgetPasswordContactNumber = voids[2];
                try {
                    URL url = new URL(new publicClass().url_checkForForgetPassword);
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoOutput(true);
                    huc.setDoInput(true);
                    OutputStream os = huc.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String data = URLEncoder.encode("user_email", "UTF-8") + "=" + URLEncoder.encode(forgetPasswordEmail, "UTF-8") + "&" +
                            URLEncoder.encode("user_contact", "UTF-8") + "=" + URLEncoder.encode(forgetPasswordContactNumber, "UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    os.close();

                    InputStream is = huc.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    String respose = "";
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        respose += line;
                    }
                    br.close();
                    is.close();
                    huc.disconnect();
                    return respose;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //return e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    //return e.getMessage();
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            if (result.equals("Forget User Found")){
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(), Change_Password.class);
                i.putExtra("Email", forgetPasswordEmail);
                i.putExtra("ContactNumber", forgetPasswordContactNumber);
                i.putExtra("From", "ForgetPass");
                startActivity(i);
            }
            else if (result.equals("Forget User Not Found")){
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}