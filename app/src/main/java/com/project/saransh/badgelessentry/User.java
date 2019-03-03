package com.project.saransh.badgelessentry;

public class User {

    public String UID;
    public String fullName;
    public String designation;
    public String phoneNumber;

    User(String uid, String fN, String des, String phNo){
        this.UID = uid;
        this.fullName = fN;
        this.designation = des;
        this.phoneNumber = phNo;
    }
}
