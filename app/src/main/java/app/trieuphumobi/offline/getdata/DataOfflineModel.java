package app.trieuphumobi.offline.getdata;

import java.util.ArrayList;

import data.DataQuestion;

/**
 * Created by tuannt on 27/09/2016.
 */

public class DataOfflineModel {

    private static DataOfflineModel instance = null;

    private ArrayList<DataQuestion> mOfflineQuestion = new ArrayList<>();

    private ArrayList<DataQuestion> mOfflineQuestionForUserChange = new ArrayList<>();

    private DataOfflineModel() {}

    public static synchronized DataOfflineModel getInstance() {
        if (instance == null) instance = new DataOfflineModel();
        return instance;
    }

    public ArrayList<DataQuestion> getmOfflineQuestion() {
        return mOfflineQuestion;
    }

    public ArrayList<DataQuestion> getmOfflineQuestionForUserChange() {
        return mOfflineQuestionForUserChange;
    }
}
