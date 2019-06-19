package com.example.filesdownload;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class DownloadFileHelper {
    private Context context;
    private String downloadUrl;
    private BroadcastReceiver downloadBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("android.intent.action.DOWNLOAD_COMPLETE")) {
                Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
                context.unregisterReceiver(downloadBroadcastReceiver);
            }
        }
    };

    public DownloadFileHelper(Context context) {
        this.context = context;
    }

    public DownloadFileHelper(Context context, String downloadUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public DownloadFileHelper startDownload() {
        if (downloadUrl == null)
            throw new RuntimeException("You need to set the Download URL first.");
        context.registerReceiver(downloadBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownloadDocumentActivity();
        return this;
    }

    public DownloadFileHelper startDownload(String downloadUrl) {
        if (downloadUrl == null)
            throw new RuntimeException("Invalid Download URL.");
        if (downloadUrl.isEmpty())
            throw new RuntimeException("Invalid Download URL.");
        context.registerReceiver(downloadBroadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownloadDocumentActivity();
        return this;
    }

    public void unRegisterDownloadFileHelper(){
        if (context != null && downloadBroadcastReceiver != null){
            try {
                context.unregisterReceiver(downloadBroadcastReceiver);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }
    private void startDownloadDocumentActivity() {
        Intent intent = new Intent(context, DownloadDocumentActivity.class);
        intent.putExtra("downloadUrl",downloadUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    public static class DownloadDocumentActivity extends AppCompatActivity {
        private static final int REQUEST_FOR_WRITE_EXTERNAL_STORAGE = 505;
        private static String downloadUrl;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getIntent().getStringExtra("downloadUrl")!=null) {
                if (getIntent().hasExtra("downloadUrl")) {
                    downloadUrl = getIntent().getStringExtra("downloadUrl");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionForWriteExternalStorage();
            }
            else {
                proceedAfterPermission();
                finish();
            }
        }


        private boolean requestPermissionForWriteExternalStorage() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
// explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_WRITE_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_WRITE_EXTERNAL_STORAGE);
                }
                return true;
            } else {
                proceedAfterPermission();
                finish();
                return false;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQUEST_FOR_WRITE_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
                        proceedAfterPermission();
                        finish();
                    } else {
                        Toast.makeText(this, "Write external storage permission needed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }

            }
        }

        private void proceedAfterPermission() {
            String fileName = null;
            Uri uri= Uri.parse(downloadUrl);
            for (int i=downloadUrl.length()-1;i>0;i--){
                String singleChar = String.valueOf(downloadUrl.charAt(i));
                if (singleChar.equals("/")){
                    fileName = downloadUrl.substring(i+1,downloadUrl.length());
                    break;
                }
            }
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);

// set title and description
            request.setTitle(fileName);
            request.setDescription("Downloading file");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//set the local destination for download file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
//            request.setMimeType("*/*");
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        }
    }
}


