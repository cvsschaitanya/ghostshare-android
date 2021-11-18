package com.example.ghostshare;

public class DatabaseHelper {
    public String filename, status;

    public DatabaseHelper(String filename, String status) {
        this.filename = filename;
        this.status = status;
    }

    public DatabaseHelper(){
        this("","waiting");
    }
}
