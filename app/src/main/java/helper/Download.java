package helper;

/**
 * Created by Dang Truong on 06/05/2016.
 */

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import data.LinkData;


public class Download extends AsyncTask<String, String, String> {
        ArrayList<String> urlMp3;
        Boolean checkDownload = false;

        public Download(ArrayList<String> urlMp3) {
            this.urlMp3 = urlMp3;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                for (int i = 0; i < urlMp3.size(); i++) {
                    URL url = new URL(urlMp3.get(i));
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    String nameMp3 = extractFilename(urlMp3.get(i));
//                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    HttpURLConnection conn = (HttpURLConnection) new URL(urlMp3.get(i)).openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000); // timeout 10 secs
                    conn.connect();
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + LinkData.FOLDER_NAME;
//                    Log.d("file_path", file_path);
                    File dir = new File(file_path);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, nameMp3);
                    InputStream input = conn.getInputStream();
                    FileOutputStream fOut = new FileOutputStream(file);
                    int byteCount = 0;
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        fOut.write(buffer, 0, bytesRead);
                        byteCount += bytesRead;
                    }
                    fOut.flush();
                    fOut.close();

                }
                checkDownload = true;

            } catch (Exception e) {
//                checkDownload = false;
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
            if (checkDownload = true){
//
            }
        }

    private String extractFilename(String urlDownloadLink) {
        if (urlDownloadLink.equals("")) {
            return "";
        }
        String newFilename = "";
        if (urlDownloadLink.contains("/")) {
            int dotPosition = urlDownloadLink.lastIndexOf("/");
            newFilename = urlDownloadLink.substring(dotPosition + 1, urlDownloadLink.length());
        } else {
            newFilename = urlDownloadLink;
        }
        return newFilename;
    }


}