package com.dlab.monami;

public class AccountInfo {

    private String email, name;

    public AccountInfo(String _email, String _name) {
        this.email = _email;
        this.name = _name;
    }

    public void setEmail(String email) { email = email; }
    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        name = name;
    }
    public String getName() {
        return name;
    }

}
