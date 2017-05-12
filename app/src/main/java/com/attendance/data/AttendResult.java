package com.attendance.data;

/**
 * Created by peiqin on 2/26/2017.
 */

public class AttendResult {
    private String name;
    private String attend;
    private String early;
    private String late;
    private String sum;

    public AttendResult(String name, String attend, String early, String late,
                        String sum) {
        this.name = name;
        this.attend = attend;
        this.early = early;
        this.late = late;
        this.sum = sum;
    }

    public AttendResult() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttend() {
        return this.attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }

    public String getEarly() {
        return this.early;
    }

    public void setEarly(String early) {
        this.early = early;
    }

    public String getLate() {
        return this.late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getSum() {
        return this.sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
