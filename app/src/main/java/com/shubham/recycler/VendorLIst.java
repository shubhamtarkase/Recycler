package com.shubham.recycler;

public class VendorLIst {
    private String name;
    private String address;
    private String profile;
    private String email;
    private String phone;
    private String userId;

    public VendorLIst(){}

    public VendorLIst(String name,String address,String profile,String email,String phone,String userId){
        this.name = name;
        this.address = address;
        this.profile = profile;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserId() {
        return userId;
    }
}
