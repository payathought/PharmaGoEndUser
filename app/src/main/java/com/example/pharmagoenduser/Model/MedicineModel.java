package com.example.pharmagoenduser.Model;

public class MedicineModel {
    private String medecine_name;
    private String medecine_price;
    private String medicine_id;
    private String pharmacy_id;

    public MedicineModel(String medecine_name, String medecine_price, String medicine_id) {
        this.medecine_name = medecine_name;
        this.medecine_price = medecine_price;
        this.medicine_id = medicine_id;
    }

    public MedicineModel() {

    }

    public String getMedecine_name() {
        return medecine_name;
    }

    public void setMedecine_name(String medecine_name) {
        this.medecine_name = medecine_name;
    }

    public String getMedecine_price() {
        return medecine_price;
    }

    public void setMedecine_price(String medecine_price) {
        this.medecine_price = medecine_price;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }
}
