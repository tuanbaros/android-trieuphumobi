package app.trieuphumobi.offline.getdata;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

/**
 * Created by tuannt on 27/09/2016.
 */

public class OfflineGetDataPresenter {

    private OfflineGetDataView mOfflineGetDataView;

    private Context mContext;

    private Vector<Integer> vector = new Vector<>();

    OfflineGetDataPresenter(Context mContext, OfflineGetDataView mOfflineGetDataView) {
        this.mContext = mContext;
        this.mOfflineGetDataView = mOfflineGetDataView;
    }

    void getDataOffline(){
        DataOfflineModel dataOfflineModel = DataOfflineModel.getInstance();
        dataOfflineModel.getmOfflineQuestion().clear();
        dataOfflineModel.getmOfflineQuestionForUserChange().clear();
        get5QuestionsLevel1();
        get5QuestionsLevel2();
        get4QuestionsLevel3();
        get1QuestionsLevel4();
        mOfflineGetDataView.getOfflineDataSuccess();
    }

    void get5QuestionsLevel1(){
        DataOfflineModel dataOfflineModel = DataOfflineModel.getInstance();
        vector.clear();
        Gson gson = new Gson();
        OfflineData offlineData = gson.fromJson(loadJSONFromAsset("quizgame_lv1_1500.json"), OfflineData.class);
        int size = offlineData.getData().length;
        for (int i = 0; i<=5; i++){
            if (i<5){
                dataOfflineModel.getmOfflineQuestion().add(offlineData.getData()[randomNumber(size)]);
            }else{
                dataOfflineModel.getmOfflineQuestionForUserChange().add(offlineData.getData()[randomNumber(size)]);
            }
        }
    }



    void get5QuestionsLevel2(){
        DataOfflineModel dataOfflineModel = DataOfflineModel.getInstance();
        vector.clear();
        Gson gson = new Gson();
        OfflineData offlineData = gson.fromJson(loadJSONFromAsset("quizgame_lv2_1500.json"), OfflineData.class);
        int size = offlineData.getData().length;
        for (int i = 0; i<=5; i++){
            if (i<5){
                dataOfflineModel.getmOfflineQuestion().add(offlineData.getData()[randomNumber(size)]);
            }else{
                dataOfflineModel.getmOfflineQuestionForUserChange().add(offlineData.getData()[randomNumber(size)]);
            }
        }
    }

    void get4QuestionsLevel3(){
        DataOfflineModel dataOfflineModel = DataOfflineModel.getInstance();
        vector.clear();
        Gson gson = new Gson();
        OfflineData offlineData = gson.fromJson(loadJSONFromAsset("quizgame_lv3_1200.json"), OfflineData.class);
        int size = offlineData.getData().length;
        for (int i = 0; i<=4; i++){
            if (i<4){
                dataOfflineModel.getmOfflineQuestion().add(offlineData.getData()[randomNumber(size)]);
            }else{
                dataOfflineModel.getmOfflineQuestionForUserChange().add(offlineData.getData()[randomNumber(size)]);
            }
        }
    }

    void get1QuestionsLevel4(){
        DataOfflineModel dataOfflineModel = DataOfflineModel.getInstance();
        vector.clear();
        Gson gson = new Gson();
        OfflineData offlineData = gson.fromJson(loadJSONFromAsset("quizgame_lv4_2.json"), OfflineData.class);
        int size = offlineData.getData().length;
        for (int i = 0; i<=1; i++){
            if (i<1){
                dataOfflineModel.getmOfflineQuestion().add(offlineData.getData()[randomNumber(size)]);
            }else{
                dataOfflineModel.getmOfflineQuestionForUserChange().add(offlineData.getData()[randomNumber(size)]);
            }
        }
    }

    private int randomNumber(int n){
        Random rd = new Random();
        int a = rd.nextInt(n);
        if (vector.contains(a)){
            return randomNumber(n);
        }else{
            vector.add(a);
            return a;
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = this.mContext.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            int c = is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
