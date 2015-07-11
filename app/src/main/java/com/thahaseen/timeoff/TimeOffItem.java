package com.thahaseen.timeoff;

/**
 * Created by Thahaseen on 7/11/2015.
 */
public class TimeOffItem {

    TimeOffItem(){}

    int id;
    String username;
    String name;
    String dept;
    String location;
    String reason;
    String remarks;
    int timelate;
    String datasync;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getTimelate() {
        return timelate;
    }

    public void setTimelate(int timelate) {
        this.timelate = timelate;
    }

    public String getDatasync() {
        return datasync;
    }

    public void setDatasync(String datasync) {
        this.datasync = datasync;
    }
}
