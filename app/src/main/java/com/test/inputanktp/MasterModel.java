package com.test.inputanktp;

import com.google.gson.annotations.SerializedName;

public class MasterModel {
    // Menampung berbagai kemungkinan nama kolom dari database (religion, status, jenis_kelamin, dll)
    @SerializedName(value = "religion", alternate = {"status", "jenis_kelamin", "name", "nama"})
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}