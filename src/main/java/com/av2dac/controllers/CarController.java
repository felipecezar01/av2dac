package com.av2dac.controllers;

import com.av2dac.dto.CarSummaryDto;
import com.av2dac.entities.Car;
import com.av2dac.repositories.CarRepository;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarRepository carRepository;

    @Autowired
    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // Listar carros com filtros - Acesso para USER e ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<CarSummaryDto>> getCars(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String licensePlate) {

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
        List<CarSummaryDto> carSummaries = filteredCars.stream()
                .map(car -> new CarSummaryDto(
                        car.getName(),
                        car.getPrice(),
                        car.getYear(),
                        car.getBrand(),
                        car.getCity(),
                        car.getLicensePlate()
                ))
                .toList();

        return ResponseEntity.ok(carSummaries);
    }

    // Criar um novo carro - Apenas ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car savedCar = carRepository.save(car);
        return ResponseEntity.ok(savedCar);
    }

    // Atualizar um carro existente - Apenas ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car carDetails) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            Car carToUpdate = carOptional.get();
            carToUpdate.setName(carDetails.getName());
            carToUpdate.setBrand(carDetails.getBrand());
            carToUpdate.setModel(carDetails.getModel());
            carToUpdate.setYear(carDetails.getYear());
            carToUpdate.setCity(carDetails.getCity());
            carToUpdate.setLicensePlate(carDetails.getLicensePlate());
            carToUpdate.setPrice(carDetails.getPrice());
            carToUpdate.setColor(carDetails.getColor());
            carToUpdate.setKilometers(carDetails.getKilometers());

            Car updatedCar = carRepository.save(carToUpdate);
            return ResponseEntity.ok(updatedCar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar um carro - Apenas ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            carRepository.delete(carOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para o admin baixar relatório em PDF
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report")
    public ResponseEntity<InputStreamResource> getCarsReport() throws IOException {
        List<Car> cars = carRepository.findAll();

        ByteArrayInputStream bis = generatePdfReport(cars);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=cars_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private ByteArrayInputStream generatePdfReport(List<Car> cars) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Criar o PDF
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Adicionar título
        document.add(new Paragraph("Relatório de Carros").setBold().setFontSize(18));

        // Criar tabela com 10 colunas
        float[] columnWidths = {40F, 60F, 60F, 60F, 40F, 60F, 60F, 60F, 60F, 60F};
        Table table = new Table(columnWidths);

        // Cabeçalhos da tabela
        table.addHeaderCell("ID");
        table.addHeaderCell("Nome");
        table.addHeaderCell("Marca");
        table.addHeaderCell("Modelo");
        table.addHeaderCell("Ano");
        table.addHeaderCell("Cidade");
        table.addHeaderCell("Placa");
        table.addHeaderCell("Preço");
        table.addHeaderCell("Cor");
        table.addHeaderCell("Quilometragem");

        // Dados da tabela
        for (Car car : cars) {
            table.addCell(String.valueOf(car.getId()));
            table.addCell(car.getName());
            table.addCell(car.getBrand());
            table.addCell(car.getModel());
            table.addCell(String.valueOf(car.getYear()));
            table.addCell(car.getCity());
            table.addCell(car.getLicensePlate());
            table.addCell(String.valueOf(car.getPrice()));
            table.addCell(car.getColor());
            table.addCell(String.valueOf(car.getKilometers()));
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Endpoint para o admin gerar QR code de um carro
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> getCarQrCode(@PathVariable Long id) throws WriterException, IOException {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();

            // Gerar o conteúdo do QR code
            String qrContent = generateCarQrContent(car);

            // Gerar a imagem do QR code
            byte[] qrImage = generateQrCodeImage(qrContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "car_" + id + "_qrcode.png");

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método para gerar o conteúdo do QR code
    private String generateCarQrContent(Car car) {
        return "Carro ID: " + car.getId() +
                "\nNome: " + car.getName() +
                "\nMarca: " + car.getBrand() +
                "\nModelo: " + car.getModel() +
                "\nAno: " + car.getYear() +
                "\nCidade: " + car.getCity() +
                "\nPlaca: " + car.getLicensePlate() +
                "\nPreço: " + car.getPrice() +
                "\nCor: " + car.getColor() +
                "\nQuilometragem: " + car.getKilometers();
    }

    // Método para gerar a imagem do QR code com suporte a UTF-8
    private byte[] generateQrCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300; // largura da imagem
        int height = 300; // altura da imagem

        // Configurações do QR code com UTF-8
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
