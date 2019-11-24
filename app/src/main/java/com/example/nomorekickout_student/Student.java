package com.example.nomorekickout_student;

public class Student {
    int ID, room, latecnt, noAlert, alarm;
    String building, name;

    public Student(){
        this.building = "우정2관";
        this.name = "";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getLatecnt() {
        return latecnt;
    }

    public void setLatecnt(int latecnt) {
        this.latecnt = latecnt;
    }

    public int getNoAlert() {
        return noAlert;
    }

    public void setNoAlert(int noAlert) {
        this.noAlert = noAlert;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
