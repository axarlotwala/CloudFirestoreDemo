package com.example.firestoredemo.models;

public class PartsModel {

    private String partId;
    private String partName;
    private String group;
    private int stdWeight;
    private String remark;

    public PartsModel() {
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getStdWeight() {
        return stdWeight;
    }

    public void setStdWeight(int stdWeight) {
        this.stdWeight = stdWeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
