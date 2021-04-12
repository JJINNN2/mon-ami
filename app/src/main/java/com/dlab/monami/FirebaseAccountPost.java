package com.dlab.monami;


import java.util.HashMap;
import java.util.Map;

public class FirebaseAccountPost {
    public String email, name;


    public FirebaseAccountPost(String email, String name){
        this.email=email;
        this.name=name;
    }

    public FirebaseAccountPost(){}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("email",email);
        result.put("name",name);
        return result;
    }
}
