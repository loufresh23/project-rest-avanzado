package com.luis.restavanzadoproject.rest;

import com.luis.restavanzadoproject.entity.CustomerEntity;
import com.luis.restavanzadoproject.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerRest {

    private final CustomerService service;

    public CustomerRest(CustomerService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        CustomerEntity customer = service.findById(id);

        if (Objects.isNull(customer)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> readData(@RequestParam MultipartFile file) {

        try {

            List<CustomerEntity> lst = new ArrayList<>();

            InputStream inputStream = file.getInputStream();

            @SuppressWarnings("resource")
            Workbook wb = new XSSFWorkbook(inputStream);

            Sheet sheet = wb.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();

            log.info("lastRowNum -> {}", lastRowNum);

            if (!Objects.isNull(sheet)) {

                for (int i = 1; i <= lastRowNum; i++) {

                    CustomerEntity customer = new CustomerEntity();

                    Row row = sheet.getRow(i); // Fila

                    if (Objects.isNull(row)) {
                        continue;
                    }

//                  ID
                    if ((row.getCell(0).getCellType() == CellType.STRING)) {
                        customer.setId(Long.valueOf(row.getCell(0).getStringCellValue()));
                    }

//                  NAME
                    if ((row.getCell(1).getCellType() == CellType.STRING)) {
                        customer.setName(row.getCell(1).getStringCellValue());
                    }

//                  SURNAME
                    if ((row.getCell(2).getCellType() == CellType.STRING)) {
                        customer.setSurname(row.getCell(2).getStringCellValue());
                    }

//                  EMAIL
                    if ((row.getCell(3).getCellType() == CellType.STRING)) {
                        customer.setEmail(row.getCell(3).getStringCellValue());
                    }

                    customer.setCreateAt(LocalDateTime.now());

                    log.info("{}", customer);
                    lst.add(customer);
                }
            }

            this.service.truncateReadData(lst);

            return ResponseEntity.ok("Se cargaron: " + lastRowNum + " clientes");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
