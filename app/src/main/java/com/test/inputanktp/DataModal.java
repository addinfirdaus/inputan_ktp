package com.test.inputanktp;

public class DataModal {

    private String nik;
    private String nama;
    private String alamat;
    private String til;
    private String jenis_kelamin;
    private String agama;
    private String negara;
    private String pekerjaan;
    private String satatus;
    private String masa_berlaku;
    public DataModal(String nik, String nama,String alamat,String til,String jenis_kelamin,String agama,String negara,String pekerjaan,String satatus,String masa_berlaku) {
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.til = til;
        this.jenis_kelamin = jenis_kelamin;
        this.agama = agama;
        this.negara = negara;
        this.pekerjaan = pekerjaan;
        this.satatus = satatus;
        this.masa_berlaku = masa_berlaku;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTil() {
        return til;
    }

    public void setTil(String til) {
        this.til = til;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }

    public String getNegara() {
        return negara;
    }

    public void setNegara(String negara) {
        this.negara = negara;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getSatatus() {
        return satatus;
    }

    public void setSatatus(String satatus) {
        this.satatus = satatus;
    }

    public String getMasa_berlaku() {
        return masa_berlaku;
    }

    public void setMasa_berlaku(String masa_berlaku) {
        this.masa_berlaku = masa_berlaku;
    }
}