package com.av2dac.services;

import com.av2dac.dto.CarSummaryDto;
import com.av2dac.entities.Car;
import com.av2dac.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<CarSummaryDto> getFilteredCars(String name, String brand, String model, Integer year, String city, String licensePlate) {
        List<Car> cars = carRepository.findAll();

        // Aplicar filtros
        List<Car> filteredCars = cars.stream()
                .filter(car -> name == null || car.getName().equalsIgnoreCase(name))
                .filter(car -> brand == null || car.getBrand().equalsIgnoreCase(brand))
                .filter(car -> model == null || car.getModel().equalsIgnoreCase(model))
                .filter(car -> year == null || car.getYear() == year)
                .filter(car -> city == null || car.getCity().equalsIgnoreCase(city))
                .filter(car -> licensePlate == null || car.getLicensePlate().equalsIgnoreCase(licensePlate))
                .toList();

        // Mapear para CarSummaryDto
        return filteredCars.stream()
                .map(car -> new CarSummaryDto(
                        car.getName(),
                        car.getPrice(),
                        car.getYear(),
                        car.getBrand(),
                        car.getCity(),
                        car.getLicensePlate()
                ))
                .toList();
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public void deleteCar(Car car) {
        carRepository.delete(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}
