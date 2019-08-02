package gov.mohua.gtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionData implements Parcelable {
public QuestionData(){

}
    public static final Creator<QuestionData> CREATOR = new Creator<QuestionData>() {
        @Override
        public QuestionData createFromParcel(Parcel in) {
            return new QuestionData(in);
        }

        @Override
        public QuestionData[] newArray(int size) {
            return new QuestionData[size];
        }
    };

    @Expose
    private int id;
    @Expose
    private String question;
    @Expose
    private String option1;
    @Expose
    private String option2;
    @Expose
    private String option3;
    @Expose
    private String option4;
    @Expose
    @SerializedName("max_marks")
    private int maxMarks;
    @Expose
    private int mark1;
    @Expose
    private int mark2;
    @Expose
    private int mark3;
    @Expose
    private int mark4;
    @Expose
    @SerializedName("category")
    private String questionType;
    @Expose
    private int weight;
    @Expose(deserialize = false,serialize = false)
    private transient int obtainedmark = -1;

    public int getSelectedOptionNo() {
        return selectedOptionNo;
    }

    public void setSelectedOptionNo(int selectedOptionNo) {
        this.selectedOptionNo = selectedOptionNo;
    }

    private int selectedOptionNo;

    private QuestionData(Parcel in) {
        id = in.readInt();
        question = in.readString();
        option1 = in.readString();
        option2 = in.readString();
        option3 = in.readString();
        option4 = in.readString();
        maxMarks = in.readInt();
        mark1 = in.readInt();
        mark2 = in.readInt();
        mark3 = in.readInt();
        mark4 = in.readInt();
        questionType = in.readString();
        weight = in.readInt();
        obtainedmark = in.readInt();
        selectedOptionNo = in.readInt();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public int getMarks1() {
        return mark1;
    }

    public void setMarks1(int mark1) {
        this.mark1 = mark1;
    }

    public int getMarks2() {
        return mark2;
    }

    public void setMarks2(int mark2) {
        this.mark2 = mark2;
    }

    public int getMarks3() {
        return mark3;
    }

    public void setMarks3(int mark3) {
        this.mark3 = mark3;
    }

    public int getMarks4() {
        return mark4;
    }

    public void setMarks4(int mark4) {
        this.mark4 = mark4;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getObtainedmark() {
        return obtainedmark;
    }

    public void setObtainedmark(int obtainedmark) {
        this.obtainedmark = obtainedmark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(option1);
        dest.writeString(option2);
        dest.writeString(option3);
        dest.writeString(option4);
        dest.writeInt(maxMarks);
        dest.writeInt(mark1);
        dest.writeInt(mark2);
        dest.writeInt(mark3);
        dest.writeInt(mark4);
        dest.writeString(questionType);
        dest.writeInt(weight);
        dest.writeInt(obtainedmark);
        dest.writeInt(selectedOptionNo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
