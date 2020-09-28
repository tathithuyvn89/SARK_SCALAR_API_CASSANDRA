package com.veho.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meeting {

    private String id;
    private String passcode;
    private String name;
    private String description;
    private String date;
    private String timefrom;
    private String timeto;
    private String url;
    private String participants;

    public Meeting(String id, String passcode, String name, String description,
                   String date, String timefrom, String timeto, String url) {
        this.id = id;
        this.passcode = passcode;
        this.name = name;
        this.description = description;
        this.date = date;
        this.timefrom = timefrom;
        this.timeto = timeto;
        this.url = url;
    }

    public Meeting(String name, String description,
                   String date, String timefrom, String timeto, String url) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.timefrom = timefrom;
        this.timeto = timeto;
        this.url = url;
    }

    public Meeting(String name, String description,
                   String date, String timefrom, String timeto, String url, String participants) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.timefrom = timefrom;
        this.timeto = timeto;
        this.url = url;
        this.participants = participants;
    }
    public Meeting() {
    }
}
