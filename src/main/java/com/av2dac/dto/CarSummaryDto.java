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

    public double getPrice() {
        return price;
    }

    public int getYear() {
        return year;
    }

    public String getBrand() {
        return brand;
    }

    public String getCity() {
        return city;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}
