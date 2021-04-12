package com.dlab.monami;

import androidx.annotation.NonNull;

class RecordItem {
    private String Time;
    private String Writer;
    private String Title;
    private String Symptom;
    private String ImgUrl;
    private String Comment;
    private int Type;

    //, String _Title, String _Symptom, String ImgUrl, String Comment
    public RecordItem(String _Time, String _Writer, String _Title, String _Symptom, String _ImgUrl, String _Comment, int _Type)
    {
        this.Time = _Time;
        this.Writer = _Writer;
        this.Title = _Title;
        this.Symptom = _Symptom;
        this.ImgUrl = _ImgUrl;
        this.Comment = _Comment;
        this.Type = _Type;
    }


    public int getType() {
        return Type;
    }

    public void setType(int type) { Type = type; }

    public void setTime(String time) {
        Time = time;
    }

    public String getTime() {
        return Time;
    }

    public void setWriter(String writer) { Writer = writer; }

    public String getWriter() {
        return Writer;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle() {
        return Title;
    }

    public void setSymptom(String symptom) {
        Symptom = symptom;
    }

    public String getSymptom() {
        return Symptom;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getComment() {
        return Comment;
    }
    @NonNull
    @Override
    public String toString() {
        super.toString();
        return "작성일시: " + Time + " 작성자: " + Writer;
    }

    //public boolean isSelected() { return isSelected; }

//    public void setSelected(boolean isSelected) {
//        this.isSelected = isSelected;
//    }

    //    public String getId() { return id;}
}
