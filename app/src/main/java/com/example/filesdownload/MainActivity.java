package com.example.filesdownload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button picture;
    private Button video;
    private Button pdf;
    private Button excel;
    private Button doc;
    private Button audio;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picture = findViewById(R.id.picture);
        video = findViewById(R.id.video);
        pdf = findViewById(R.id.pdf);
        excel = findViewById(R.id.excel);
        doc = findViewById(R.id.doc);
        audio = findViewById(R.id.audio);

        buttonClickOperation();

    }

    private void buttonClickOperation() {
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://cdn.pixabay.com/photo/2016/01/08/11/57/butterfly-1127666__340.jpg";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://www.pdf995.com/samples/pdf.pdf";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });

        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://www.sample-videos.com/xls/Sample-Spreadsheet-10-rows.xls";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });

        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://iiswc.org/iiswc2012/sample.doc";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://sample-videos.com/audio/mp3/crowd-cheering.mp3";
                new DownloadFileHelper(MainActivity.this, url).startDownload();
            }
        });
    }
}
