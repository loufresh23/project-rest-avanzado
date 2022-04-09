package com.luis.restavanzadoproject.rest;

import com.luis.restavanzadoproject.entity.TypeCreditEntity;
import com.luis.restavanzadoproject.service.TypeCreditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/type-credit")
public class TypeCreditRest {

    private final TypeCreditService service;

    public TypeCreditRest(TypeCreditService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        TypeCreditEntity typeCredit = service.findById(id);

        if (Objects.isNull(typeCredit)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(typeCredit);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> readData(@RequestParam MultipartFile file) {

        try {

            List<TypeCreditEntity> lst = new ArrayList<>();

            InputStream inputStream = file.getInputStream();

            @SuppressWarnings("resource")
            Workbook wb = new XSSFWorkbook(inputStream);

            Sheet sheet = wb.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();

            log.info("lastRowNum -> {}", lastRowNum);

            if (!Objects.isNull(sheet)) {

                for (int i = 1; i <= lastRowNum; i++) {

                    TypeCreditEntity typeCredit = new TypeCreditEntity();

                    Row row = sheet.getRow(i); // Fila

                    if (Objects.isNull(row)) {
                        continue;
                    }

//                  ID
                    if ((row.getCell(0).getCellType() == CellType.STRING)) {
                        typeCredit.setId(Long.valueOf(row.getCell(0).getStringCellValue()));
                    }

//                  DESCRIPTION
                    if ((row.getCell(1).getCellType() == CellType.STRING)) {
                        typeCredit.setDescription(row.getCell(1).getStringCellValue());
                    }

                    log.info("{}", typeCredit);
                    lst.add(typeCredit);
                }
            }

            this.service.truncateReadData(lst);

            return ResponseEntity.ok("Se cargaron: " + lastRowNum + " tipos de creditos");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}


