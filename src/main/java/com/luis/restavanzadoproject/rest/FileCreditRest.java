package com.luis.restavanzadoproject.rest;

import com.luis.restavanzadoproject.entity.CreditEntity;
import com.luis.restavanzadoproject.entity.FileCreditEntity;
import com.luis.restavanzadoproject.rest.common.ErrorMessage;
import com.luis.restavanzadoproject.service.CreditService;
import com.luis.restavanzadoproject.service.FileCreditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/file-credit")
public class FileCreditRest {

    private final FileCreditService fileCreditService;
    private final CreditService creditService;

    public FileCreditRest(FileCreditService fileCreditService, CreditService creditService) {
        this.fileCreditService = fileCreditService;
        this.creditService = creditService;
    }

    @PostMapping()
    public ResponseEntity<?> saveFile(@RequestParam("idCredit") Long idCredit,
                                      @RequestParam MultipartFile file) {

        CreditEntity credit = creditService.findById(idCredit);

        if (Objects.isNull(credit)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.builder()
                    .description("No se encontro Credito")
                    .date(LocalDateTime.now())
                    .build());
        }

        fileCreditService.save(FileCreditEntity.builder()
                .idCredit(idCredit)
                .build(), credit, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}


