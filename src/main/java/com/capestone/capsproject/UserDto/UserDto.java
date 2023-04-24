package com.capestone.capsproject.UserDto;

import java.util.ArrayList;

public class UserDto {
    private String fname;

    private ArrayList<String> List=new ArrayList<>();

    public ArrayList<String> getList() {
        return List;
    }

    public void setList(ArrayList<String> list) {
        List = list;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
}
