package data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dang Truong on 27/04/2016.
 */
public class SeriaDataQuestion implements Serializable {
    ArrayList<DataQuestion> data;

    public SeriaDataQuestion(ArrayList<DataQuestion> data) {
        this.data = data;
    }

    public ArrayList<DataQuestion> getData() {
        return data;
    }

    public void setData(ArrayList<DataQuestion> data) {
        this.data = data;
    }
}
