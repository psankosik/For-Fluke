package th.co.scbprotect.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import th.co.scbprotect.R;

public class SearchDetailActivity extends AppCompatActivity {

    TextView fileTitle;
    TextView fileStage;
    TextView fileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra("data1");
        String stage = intent.getStringExtra("data2");
        String content = intent.getStringExtra("data3");

        fileTitle = findViewById(R.id.search_detail_title);
        fileStage = findViewById(R.id.search_detail_stage);
        fileContent = findViewById(R.id.search_detail_content);

        fileTitle.setText(title);
        fileStage.setText(stage);
        fileContent.setText(content);

    }
}