package com.company;

import java.sql.Connection;
import java.sql.SQLException;

public class Message {

    private String fromName;
    private String toName;
    private String message;

    private connection con;

    public Message (String fromName, String toName, String message) {
        this.fromName = fromName;
        this.toName = toName;
        this.message = message;

    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message from " + fromName + " to " + toName;
    }
}
