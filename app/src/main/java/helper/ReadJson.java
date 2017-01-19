package helper;

// _._ _..._ .-',     _.._(`))
//'-. `     '  /-._.-'    ',/
//   )         \            '.
//  / _    _    |             \
// |  a    a    /              |
// \   .-.                     ;
//  '-('' ).-'       ,'       ;
//     '-;           |      .'
//        \           \    /
//        | 7  .__  _.-\   \
//        | |  |  ``/  /`  /
//       /,_|  |   /,_/   /
//          /,_/      '`-'

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import data.DataQuestion;

public class ReadJson {
    public static ArrayList<DataQuestion> getListQuestion(String hightLighJson) {
        ArrayList<DataQuestion> listPosts = new ArrayList<DataQuestion>();
        if (hightLighJson != null) {
            try {

                JSONObject data = new JSONObject(hightLighJson);
                JSONArray info = data.getJSONArray("data");

                int leng = info.length();
                for (int i = 0; i < leng; i++) {
                    String question = "";
                    String answer_1 = "";
                    String answer_2 = "";
                    String answer_3 = "";
                    String answer_4 = "";
                    String answer_true = "";
                    String question_audio = "";
                    String answer_1_audio = "";
                    String answer_2_audio = "";
                    String answer_3_audio = "";
                    String answer_4_audio = "";

                    ArrayList<DataQuestion> listComment = new ArrayList<DataQuestion>();
                    JSONObject channel = info.getJSONObject(i);
                    DataQuestion data_question=new DataQuestion();

                    if (!channel.isNull("question")) {
                        question = channel.getString("question");
                    }
                    if (!channel.isNull("answer_1")) {
                        answer_1 = channel.getString("answer_1");
                    }
                    if (!channel.isNull("answer_2")) {
                        answer_2 = channel.getString("answer_2");
                    }
                    if (!channel.isNull("answer_3")) {
                        answer_3 = channel.getString("answer_3");
                    }
                    if (!channel.isNull("answer_4")) {
                        answer_4 = channel.getString("answer_4");
                    }
                    if (!channel.isNull("answer_true")) {
                        answer_true = channel.getString("answer_true");
                    }
                    if (!channel.isNull("question_audio")) {
                        question_audio = channel.getString("question_audio");
                    }
                    if (!channel.isNull("answer_1_audio")) {
                        answer_1_audio = channel.getString("answer_1_audio");
                    }
                    if (!channel.isNull("answer_2_audio")) {
                        answer_2_audio = channel.getString("answer_2_audio");
                    }
                    if (!channel.isNull("answer_3_audio")) {
                        answer_3_audio = channel.getString("answer_3_audio");
                    }
                    if (!channel.isNull("answer_4_audio")) {
                        answer_4_audio = channel.getString("answer_4_audio");
                    }
                    data_question.setQuestion(question);
                    data_question.setAnswer_1(answer_1);
                    data_question.setAnswer_2(answer_2);
                    data_question.setAnswer_3(answer_3);
                    data_question.setAnswer_4(answer_4);
                    data_question.setAnswer_true(answer_true);
                    data_question.setQuestion_audio(question_audio);
                    data_question.setAnswer_1_audio(answer_1_audio);
                    data_question.setAnswer_2_audio(answer_2_audio);
                    data_question.setAnswer_3_audio(answer_3_audio);
                    data_question.setAnswer_4_audio(answer_4_audio);



                    listPosts.add(data_question);


                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return listPosts;

    }


}
