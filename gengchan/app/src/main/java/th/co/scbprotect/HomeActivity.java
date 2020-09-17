package th.co.scbprotect;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import th.co.scbprotect.search.SearchActivity;
import th.co.scbprotect.util.Configuration;
import th.co.scbprotect.util.PostConnector;
import th.co.scbprotect.util.SwipeListener;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.GENGCHAN_PREF, MODE_PRIVATE);
        final String name = pref.getString(Configuration.Pref.NAME, "no name");
        TextView greeting = findViewById(R.id.home_tv_greeting);
        greeting.setText(name);

        findViewById(R.id.home_iv_profile_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdf();
                /*
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                PostConnector postConnector = new PostConnector(
                        Configuration.getFilePath(),
                        String.format("{\"key\":\"%s\"}", "0ab0a34d-ef7d-45d0-a018-9825bdf26b1d.pdf"),
                        "accessToken",
                        new getFileHandler());
                postConnector.execute();
                */
            }
        });


        SwipeListener listener = new SwipeListener(this) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                finishAffinity();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        findViewById(R.id.home_linear_layout).setOnTouchListener((View.OnTouchListener)(listener));


        final ImageView iv_dropdown = (ImageView) findViewById(R.id.home_iv_dropdown);
        final HorizontalScrollView expandLayout = (HorizontalScrollView) findViewById(R.id.expandableLayout);
        final CardView cardView = findViewById(R.id.cardView);

        iv_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandLayout.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandLayout.setVisibility(View.VISIBLE);
                    iv_dropdown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandLayout.setVisibility(View.GONE);
                    iv_dropdown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

            }
        });
        //getVisibility() == 8
        final CardView personal_stat = findViewById(R.id.home_sub_cv_personal_stat);

        personal_stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, WebViewStatActivity.class);
                intent.putExtra("url", "https://13.229.69.212:8000/Leaderboard/5f56f4d74ebdb9237d5654f6/1/province");
                startActivity(intent);
            }
        });
    }

    private static class getFileHandler implements PostConnector.DataHandler {
        @Override
        public void processData(String data) throws IOException {
            Log.e("TAG","SAVE FILE");

        }
    }

    protected void pdf() {

        File fileBrochure = new File(Environment.getExternalStorageDirectory() + "/" + "sample.pdf");
        if (!fileBrochure.exists())
        {
            CopyAssetsbrochure();
        }

        /** PDF reader code */
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "sample.pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            getApplicationContext().startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(HomeActivity.this, "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
        }
    }

    //method to write the PDFs file to sd card
    private void CopyAssetsbrochure() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list("");
        }
        catch (IOException e)
        {
            Log.e("tag", e.getMessage());
        }
        for(int i=0; i<files.length; i++)
        {
            String fStr = files[i];
            if(fStr.equalsIgnoreCase("sample.pdf"))
            {
                InputStream in = null;
                OutputStream out = null;
                try
                {
                    in = assetManager.open(files[i]);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + files[i]);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                    break;
                }
                catch(Exception e)
                {
                    Log.e("tag", e.getMessage());
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
