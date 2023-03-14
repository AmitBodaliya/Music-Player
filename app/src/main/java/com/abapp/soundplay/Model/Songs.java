package com.abapp.soundplay.Model;

public class Songs {

    private int id;
    private String song;

    public Songs() {
    }


    public Songs(String song ){
        this.song = song;
    }

    public Songs(int id, String song) {
        this.id = id;
        this.song = song;
    }

    public void setSong(String song) {
        this.song = song;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }
    public int getId() {
        return id;
    }
}
