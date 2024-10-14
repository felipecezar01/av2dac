package com.av2dac.dto;

public class CarSummaryDto {

    private String name;
    private double price;
    private int year;
    private String brand;
    private String city;
    private String licensePlate;

    // Construtor
    public CarSummaryDto(String name, double price, int year, String brand, String city, String licensePlate) {
        this.name = name;
        this.price = price;
        this.year = year;
        this.brand = brand;
        this.city = city;
        this.licensePlate = licensePlate;
    }

    // Getters e Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
