package com.veho.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Participant {

    private String email;
    private String name;
    private String company;
    private String position;

    public Participant(String email, String name, String company, String position) {
        this.email = email;
        this.name = name;
        this.company = company;
        this.position = position;
    }

    public Participant() {
    }
}
