package com.example.macros;

public class SearchNode {
    private String uID;
    private String name;

    public String getUID() {
        return uID;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchNode() {
    }

    public SearchNode(String uID, String name) {
        this.uID = uID;
        this.name = name;
    }
}
