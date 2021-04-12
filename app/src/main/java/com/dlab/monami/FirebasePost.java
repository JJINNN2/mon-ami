package com.dlab.monami;


import java.util.HashMap;
import java.util.Map;

public class FirebasePost {
    public int type;
    public boolean favorite;
    public String title;
    public String symptom;
    public String img;
    public String comment;
    public String time;
    public String writer;

    public FirebasePost(int type, String title, String symptom, String img, String comment, String time, String writer, boolean favorite){
        this.title=title;
        this.symptom=symptom;
        this.img=img;
        this.comment=comment;
        this.time=time;
        this.writer=writer;
        this.type=type;
        this.favorite=false;
    }

    public FirebasePost(){}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("title",title);
        result.put("symptom",symptom);
        result.put("img",img);
        result.put("comment",comment);
        result.put("writer",writer);
        result.put("time",time);
        result.put("type",type);
        result.put("favorite",favorite);
        return result;
    }
}
