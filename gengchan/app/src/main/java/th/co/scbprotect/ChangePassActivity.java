package th.co.scbprotect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;

public class ChangePassActivity extends AppCompatActivity implements PostConnector.DataHandler{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
        final String accessToken = pref.getString(Configuration.Pref.ACCESS_TOKEN, "no token");
        final String email =  pref.getString(Configuration.Pref.EMAIL, "no email");

        findViewById(R.id.changepass_bt_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.changepass_cv_login_fail).setVisibility(View.GONE);

                EditText edit_text_oldpass = findViewById(R.id.changepass_et_oldpass);
                EditText edit_text_newpass = findViewById(R.id.changepass_et_newpass);
                EditText edit_text_confirm = findViewById(R.id.changepass_et_confirm_newpass);

                String oldpass = edit_text_oldpass.getText().toString().trim();
                String newpass = edit_text_newpass.getText().toString().trim();
                String confirm = edit_text_confirm.getText().toString().trim();

                if(TextUtils.isEmpty(oldpass)) {
                    edit_text_oldpass.setError("Old password required");
                    edit_text_oldpass.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(newpass)) {
                    edit_text_newpass.setError("New password required");
                    edit_text_newpass.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(confirm)) {
                    edit_text_confirm.setError("Confirm password required");
                    edit_text_confirm.requestFocus();
                    return;
                }
                if(!newpass.equals(confirm)){
                    edit_text_newpass.setError("Password not match");
                    edit_text_newpass.requestFocus();
                    return;
                }

                String json = String.format("{\"email\":\"%s\",\"old_password\":\"%s\",\"new_password\":\"%s\"}",email,oldpass,newpass);
                Log.e("PUN",json);

                PostConnector postConnector = new PostConnector( Configuration.getChangePasswordPath(),
                        json,
                        accessToken,
                        ChangePassActivity.this);
                postConnector.execute();
            }
        });


    }

    @Override
    public void processData(String data) {
        JsonObject json = new Gson().fromJson(data, JsonObject.class);
        String statusBody = json.get("status").getAsString();

        Log.e("PUN",statusBody);

        if (statusBody.equals("password changed")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Password Complete");
            builder.setMessage("Please login with your new password");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove(Configuration.Pref.ACCESS_TOKEN).apply();

                    Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            });
            builder.create();
            builder.show();
        } else if (statusBody.equals("error")){
            runOnUiThread(new Runnable() {
                public void run() {
                    findViewById(R.id.changepass_cv_login_fail).setVisibility(View.VISIBLE);

                    TextView errorTextView = findViewById(R.id.changepass_tv_login_fail);
                    errorTextView.setText("Old Password Incorrect");
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    findViewById(R.id.changepass_cv_login_fail).setVisibility(View.VISIBLE);

                    TextView errorTextView = findViewById(R.id.changepass_tv_login_fail);
                    errorTextView.setText("Error: Please Try again later");
                }
            });
        }

    }
}
