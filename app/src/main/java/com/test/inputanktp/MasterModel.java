package com.test.inputanktp;

import com.google.gson.annotations.SerializedName;

public class MasterModel {
    @SerializedName(value = "religion", alternate = {"status", "jenis_kelamin", "name", "nama"})
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}