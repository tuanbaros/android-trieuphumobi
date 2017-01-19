package app.trieuphumobi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.HexagonImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import data.DataQuestion;
import data.SeriaDataQuestion;
import data.LinkData;
import helper.CustomHttpClient;
import helper.ReadJson;

/**
 * Created by Dang Truong on 27/04/2016.
 */
public class LoadingActivity extends AppCompatActivity {

    ArrayList<DataQuestion> listQuestionAll = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listQuestionlv1 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listQuestionlv2 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listQuestionlv3 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listQuestionlv4 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiuplv = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiuplv1 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiuplv2 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiuplv3 = new ArrayList<DataQuestion>();
    ArrayList<DataQuestion> listTrogiuplv4 = new ArrayList<DataQuestion>();

    ArrayList<String> arrayListAudioQues12 = new ArrayList<String>();
    ArrayList<String> arrayListAudioAnsall = new ArrayList<String>();
    ArrayList<String> arrayListAudioAns2 = new ArrayList<String>();
    ArrayList<String> arrayListAudioAns3 = new ArrayList<String>();
    ArrayList<String> arrayListAudioAns4 = new ArrayList<String>();

    ArrayList<String> Diem = new ArrayList<String>();

    ImageView imgLoad;
    private Animation anim;
    private SeriaDataQuestion data;
    private String downloadAudioPath;
    Boolean checkDownload = false;
    Button btnDelete;
    private boolean checkend = false;
    HexagonImageView imgAvatar;
    ImageView imgKhach;
    TextView tvNameUser;
    private ArrayList<String> category = new ArrayList<>();
    String user_id = "", user_name = "", user_url = "", user_gender = "";
    Boolean check_login = false;
    private Integer tpmbTurn = 0;
    private TextView tvMoney, tvScore, tvLv;
    private int hlLv, hlMoney, hlScore;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Mobi Star");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Loading game");
        hlLv = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbLvhl", 0);
        hlMoney = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbMoneyhl", 0);
        hlScore = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbScorehl", 0);
        category.add(LinkData.QUESTION1);
        category.add(LinkData.QUESTION2);
        category.add(LinkData.QUESTION3);
        category.add(LinkData.QUESTION4);

        imgLoad = (ImageView) findViewById(R.id.imgLoad);
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        imgLoad.startAnimation(anim);
        Intent intent = getIntent();
        user_name = intent.getStringExtra("Name");
        user_url = intent.getStringExtra("Avatar");
        check_login = intent.getBooleanExtra("Login", false);

//        getData();
        imgAvatar = (HexagonImageView) findViewById(R.id.imgAvatar);
        tvNameUser = (TextView) findViewById(R.id.tvNameUser);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvLv = (TextView) findViewById(R.id.tvLv);
        tvScore = (TextView) findViewById(R.id.tvScore);
        if (hlLv > 0) {
            tvMoney.setText(hlMoney + " Vnđ");
            tvScore.setText(hlScore + " đ");
            tvLv.setText("Lv." + hlLv);
        }

        if (user_url.equals("") || user_url.equals(null) || user_url.length() == 0) {
            Picasso.with(getApplication()).load(R.drawable.ava)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar);
        } else {
            Picasso.with(getApplication()).load(user_url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ava)
                    .fit()
                    .into(imgAvatar);
        }
        if (user_name != "") {
            tvNameUser.setText(user_name);
        } else {
            tvNameUser.setText("Khách");
        }
        RequestTask task = new RequestTask();
        task.execute();
    }

    public void getData() {


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String s1 = null;
                String s2 = null;
                String s3 = null;
                String s4 = null;

                String url_ques_audio = null;
                String url_ans1_audio = null;
                String url_ans2_audio = null;
                String url_ans3_audio = null;
                String url_ans4_audio = null;

                String sTG1 = null;
                String sTG2 = null;
                String sTG3 = null;
                String sTG4 = null;

                CustomHttpClient httpClient = new CustomHttpClient(
                        LinkData.QUESTION1);
                CustomHttpClient httpClient2 = new CustomHttpClient(
                        LinkData.QUESTION2);
                CustomHttpClient httpClient3 = new CustomHttpClient(
                        LinkData.QUESTION3);
                CustomHttpClient httpClient4 = new CustomHttpClient(
                        LinkData.QUESTION4);

                CustomHttpClient httpClientTG1 = new CustomHttpClient("http://content.amobi.vn/api/quizgame?app_id=832799120072886&type=get-data&level=1&limit=1");
                CustomHttpClient httpClientTG2 = new CustomHttpClient("http://content.amobi.vn/api/quizgame?app_id=832799120072886&type=get-data&level=2&limit=1");
                CustomHttpClient httpClientTG3 = new CustomHttpClient("http://content.amobi.vn/api/quizgame?app_id=832799120072886&type=get-data&level=3&limit=1");
                CustomHttpClient httpClientTG4 = new CustomHttpClient("http://content.amobi.vn/api/quizgame?app_id=832799120072886&type=get-data&level=4&limit=1");

                try {
                    httpClient.addHeader("amobi", "amobi@#123");
                    s1 = httpClient.request();
                    ReadJson read = new ReadJson();
                    listQuestionlv1 = read.getListQuestion(s1);

                    httpClient2.addHeader("amobi", "amobi@#123");
                    s2 = httpClient2.request();
                    listQuestionlv2 = read.getListQuestion(s2);

                    httpClient3.addHeader("amobi", "amobi@#123");
                    s3 = httpClient3.request();
                    listQuestionlv3 = read.getListQuestion(s3);

                    httpClient4.addHeader("amobi", "amobi@#123");
                    s4 = httpClient4.request();
                    listQuestionlv4 = read.getListQuestion(s4);

                    listQuestionAll.addAll(listQuestionlv1);
                    listQuestionAll.addAll(listQuestionlv2);
                    listQuestionAll.addAll(listQuestionlv3);
                    listQuestionAll.addAll(listQuestionlv4);

                    //Duyệt mảng và lấy ra urldownload
                    for (int i = 0; i < 2; i++) {
                        url_ques_audio = listQuestionAll.get(i).getQuestion_audio();
                        url_ans1_audio = listQuestionAll.get(i).getAnswer_1_audio();
                        url_ans2_audio = listQuestionAll.get(i).getAnswer_2_audio();
                        url_ans3_audio = listQuestionAll.get(i).getAnswer_3_audio();
                        url_ans4_audio = listQuestionAll.get(i).getAnswer_4_audio();


                        Log.d("url_ques_audio", url_ques_audio);
                        arrayListAudioQues12.add(url_ques_audio);
                        arrayListAudioQues12.add(url_ans1_audio);
                        arrayListAudioQues12.add(url_ans2_audio);
                        arrayListAudioQues12.add(url_ans3_audio);
                        arrayListAudioQues12.add(url_ans4_audio);

//                        DownloadFileAsync task1 = new DownloadFileAsync(arrayListAudioQues12);
//                        task1.execute();
                    }
                    DownloadFileAsync task1 = new DownloadFileAsync(arrayListAudioQues12);
                    task1.execute();

                    //Lấy dữ liệu trợ giúp đổi câu hỏi
                    httpClientTG1.addHeader("amobi", "amobi@#123");
                    sTG1 = httpClientTG1.request();
                    ReadJson readTG1 = new ReadJson();
                    listTrogiuplv1 = readTG1.getListQuestion(sTG1);

                    httpClientTG2.addHeader("amobi", "amobi@#123");
                    sTG2 = httpClientTG2.request();
                    ReadJson readTG2 = new ReadJson();
                    listTrogiuplv2 = readTG2.getListQuestion(sTG2);

                    httpClientTG3.addHeader("amobi", "amobi@#123");
                    sTG3 = httpClientTG3.request();
                    ReadJson readTG3 = new ReadJson();
                    listTrogiuplv3 = readTG3.getListQuestion(sTG3);

                    httpClientTG4.addHeader("amobi", "amobi@#123");
                    sTG4 = httpClientTG4.request();
                    ReadJson readTG4 = new ReadJson();
                    listTrogiuplv4 = readTG4.getListQuestion(sTG4);

                    listTrogiuplv1.addAll(listTrogiuplv2);
                    listTrogiuplv1.addAll(listTrogiuplv3);
                    listTrogiuplv1.addAll(listTrogiuplv4);

                    LoadingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                        }
                    });
                } catch (Exception e) {
                    LoadingActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                        }
                    });

                }
            }
        });
        thread.start();

    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        ArrayList<String> urlMp3;

        public DownloadFileAsync(ArrayList<String> urlMp3) {
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
                Log.d("urlMp3.size()", urlMp3.size() + "");
                for (int i = 0; i < urlMp3.size(); i++) {
                    URL url = new URL(urlMp3.get(i));
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    String nameMp3 = extractFilename(urlMp3.get(i));
                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlMp3.get(i)).openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000); // timeout 10 secs
                    conn.connect();
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + LinkData.FOLDER_NAME;
                    Log.d("file_path", file_path);
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
//                    if (i == 4) {
//                        checkend = true;
//                    }
//                    Log.d("iiiiii",""+i);
                }
                checkDownload = true;

            } catch (Exception e) {
                checkDownload = false;
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
//            Log.d("ra day","ra day");
            if (checkDownload = true) {
                imgLoad.clearAnimation();
                imgLoad.setVisibility(View.GONE);
                tpmbTurn = getSharedPreferences("TPMBSHAF", MODE_PRIVATE).getInt("tpmbTurn", 5);
                tpmbTurn = tpmbTurn - 1;
                getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", tpmbTurn).commit();
                Log.d("tpmbTurn", tpmbTurn + "\n" + getSharedPreferences("TPMBSHAF", MODE_PRIVATE).edit().putInt("tpmbTurn", tpmbTurn));
                Intent intent = new Intent(LoadingActivity.this, MoneyActivity.class);
                intent.putExtra("dataArray", new SeriaDataQuestion(listQuestionAll));
                intent.putExtra("dataTrogiup", new SeriaDataQuestion(listTrogiuplv));
                intent.putExtra("Name", user_name);
                intent.putExtra("Avatar", user_url);
                intent.putExtra("Login", check_login);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.no_animation);
            }
        }
    }


    // Đặt tên file theo phần cuối của link
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


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Log.d("onREsum1", "onResum1");
        super.onResume();
        Log.d("onREsum", "onResum");
    }

    public class RequestTask extends
            AsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < category.size(); i++) {

                CustomHttpClient httpClient = new CustomHttpClient(
                        category.get(i));
                httpClient.addHeader("amobi", "amobi@#123");
                Log.d("asynctask", i + " " + category.get(i));
                try {
                    s = httpClient.request();
                    list.add(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("sizeList", "" + list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {// update screen
            super.onPostExecute(result);
            Log.d("asynctask", " " + result.size());
            if (result != null && result.size() == 4) {
                for (int i = 0; i < result.size(); i++) {
                    if (i == 0) {
                        listQuestionlv1 = ReadJson.getListQuestion(result.get(i));

                    } else if (i == 1) {
                        listQuestionlv2 = ReadJson.getListQuestion(result.get(i));

                    } else if (i == 2) {
                        listQuestionlv3 = ReadJson.getListQuestion(result.get(i));

                    } else if (i == 3) {
                        listQuestionlv4 = ReadJson.getListQuestion(result.get(i));
                        Log.d("listQuestionlv4", listQuestionlv4.size() + "");
                    }
                }
                listTrogiuplv.add(listQuestionlv1.get(5));
                listTrogiuplv.add(listQuestionlv2.get(5));
                listTrogiuplv.add(listQuestionlv3.get(4));
                if (listQuestionlv4.size() > 1) {
                    listTrogiuplv.add(listQuestionlv4.get(1));
                }
                Log.d("listQuestionAll", listQuestionlv1.size() + "\n" + listQuestionlv2.size() + "\n" +
                        listQuestionlv3.size() + "\n" + listQuestionlv4.size() + "\n" + listTrogiuplv.size());
                listQuestionAll.removeAll(listQuestionAll);
                listQuestionAll.addAll(listQuestionlv1);
                listQuestionAll.addAll(listQuestionlv2);
                listQuestionAll.addAll(listQuestionlv3);
                listQuestionAll.addAll(listQuestionlv4);
                listQuestionAll.removeAll(listTrogiuplv);
                Log.d("listQuestionAll", listQuestionAll.size() + "");
                for (int i = 0; i < 2; i++) {
                    arrayListAudioQues12.add(listQuestionAll.get(i).getQuestion_audio());
                    arrayListAudioQues12.add(listQuestionAll.get(i).getAnswer_1_audio());
                    arrayListAudioQues12.add(listQuestionAll.get(i).getAnswer_2_audio());
                    arrayListAudioQues12.add(listQuestionAll.get(i).getAnswer_3_audio());
                    arrayListAudioQues12.add(listQuestionAll.get(i).getAnswer_4_audio());
                }

                Log.d("arrayListAudioAnsall", arrayListAudioAnsall.size() + "");
                DownloadFileAsync task1 = new DownloadFileAsync(arrayListAudioQues12);
                task1.execute();

            } else {

            }
        }

    }
}
