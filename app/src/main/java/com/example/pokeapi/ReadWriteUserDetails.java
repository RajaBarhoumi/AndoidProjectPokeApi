package com.example.pokeapi;

public class ReadWriteUserDetails {

    private String  dob, mobile;

    public ReadWriteUserDetails() {
        // Default constructor required for Firebase
    }

    public ReadWriteUserDetails( String dateOfBirth, String tel) {

        this.dob = dateOfBirth;
        this.mobile = tel;
    }

    // Provide public getters and setters



    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
