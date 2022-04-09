package com.luis.restavanzadoproject.service.impl;

import com.luis.restavanzadoproject.entity.CreditEntity;
import com.luis.restavanzadoproject.entity.FileCreditEntity;
import com.luis.restavanzadoproject.repository.FileCreditRepository;
import com.luis.restavanzadoproject.service.CreditService;
import com.luis.restavanzadoproject.service.FileCreditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class FileCreditServiceImpl implements FileCreditService {

    @Value(value = "${api.ruta.file}")
    private String ruta;

    private final FileCreditRepository repository;


    public FileCreditServiceImpl(FileCreditRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileCreditEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void save(FileCreditEntity fileCredit,CreditEntity credit, MultipartFile file) {
        try {

            // 1.- Save - Files Server(Disco)
            if (this.saveArchivo(credit,file)) {
                // 2.- Save - BD
                fileCredit.setName(file.getOriginalFilename());
                repository.save(fileCredit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveArchivo(CreditEntity credit, MultipartFile file) throws IOException {

        try {
            String filename = this.ruta + "CREDIT-" + credit.getId() + "-" + credit.getCustomer().getName() + "-" + file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();

            File targetFile = new File(filename);

            OutputStream outputStream = new FileOutputStream(targetFile);

            int read;

            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            return true;
        } catch (IOException e) {
            throw e;
        }
    }

}
