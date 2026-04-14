package client.network.message;


import client.network.request.*;
import client.network.response.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


public abstract class Message {
    // тип сосбщения (request, response, notification)
    private String kind;
    // тип операции (login, send_message, и др.)
    private String type;

    public Message(){}

    public Message(String kind, String type){
        this.kind = kind;
        this.type = type;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }
}

