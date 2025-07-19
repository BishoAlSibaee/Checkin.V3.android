package com.syriasoft.house_keeping;

public class User {
    public int id;
    public String name;
    public int jobNumber;
    public int Mobile;
    public String department;
    public String token;
    public String control;
    public int logedin ;
    private String my_token;

    public User(int id, String name, int jobNumber, int mobile, String department, String token,String control,int logedin,String my_token) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        Mobile = mobile;
        this.department = department;
        this.token = token;
        this.control = control;
        this.logedin = logedin;
        this.my_token = my_token;
    }

    void setMyToken(String token) {
        if (token != null) {
            this.my_token = token;
        }
    }

    String getMyToken() {
        return this.my_token;
    }
}
