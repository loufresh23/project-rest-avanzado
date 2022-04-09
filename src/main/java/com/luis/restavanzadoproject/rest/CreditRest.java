package com.luis.restavanzadoproject.rest;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.luis.restavanzadoproject.entity.*;
import com.luis.restavanzadoproject.rest.common.ErrorMessage;
import com.luis.restavanzadoproject.service.CreditService;
import com.luis.restavanzadoproject.service.CustomerService;
import com.luis.restavanzadoproject.service.TypeCreditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/credit")
public class CreditRest {

    private final CreditService creditService;
    private final TypeCreditService typeCreditService;
    private final CustomerService customerService;

    public CreditRest(CreditService creditService, TypeCreditService typeCreditService, CustomerService customerService) {
        this.creditService = creditService;
        this.typeCreditService = typeCreditService;
        this.customerService = customerService;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(creditService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        CreditEntity credit = creditService.findById(id);

        if (Objects.isNull(credit)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(credit);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CreditEntity credit) {

        TypeCreditEntity typeCredit = typeCreditService.findById(credit.getTypeCredit().getId());
        CustomerEntity customer = customerService.findById(credit.getCustomer().getId());

        if (Objects.isNull(typeCredit) || Objects.isNull(customer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                    .description("No se encontro Tipo de credito o el Cliente")
                    .date(LocalDateTime.now())
                    .build());
        }

        credit.setTypeCredit(typeCredit);
        credit.setCustomer(customer);
        credit.setStatus(StatusCreditEnum.CREATED.name());
        creditService.save(credit);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/export-rejected")
    public ResponseEntity<?> exportData(HttpServletResponse response) {

        try {

            XSSFWorkbook workbookNuevo = new XSSFWorkbook();

            XSSFSheet sheetNuevo = workbookNuevo.createSheet("Credits");

            Row row = sheetNuevo.createRow(0);

            Cell cell = row.createCell(0);
            cell.setCellValue("ID");

            cell = row.createCell(1);
            cell.setCellValue("TYPECREDIT");

            cell = row.createCell(2);
            cell.setCellValue("AMOUNT");

            cell = row.createCell(3);
            cell.setCellValue("DUES");

            cell = row.createCell(4);
            cell.setCellValue("INTERESTS");

            cell = row.createCell(5);
            cell.setCellValue("CUSTOMER");

            List<CreditEntity> lst = creditService.findAll()
                    .stream()
                    .filter(c -> c.getStatus().equals("REJECTED"))
                    .collect(Collectors.toList());

            IntStream.range(0, lst.size()).forEach(i -> {

                Row rowData = sheetNuevo.createRow(i + 1);
                CreditEntity credit = lst.get(i);

                Cell cellData = rowData.createCell(0);
                cellData.setCellValue(credit.getId());

                cellData = rowData.createCell(1);
                cellData.setCellValue(credit.getTypeCredit().getDescription());

                cellData = rowData.createCell(2);
                cellData.setCellValue(credit.getAmount());

                cellData = rowData.createCell(3);
                cellData.setCellValue(credit.getDues());

                cellData = rowData.createCell(4);
                cellData.setCellValue(credit.getInterests());

                cellData = rowData.createCell(5);
                cellData.setCellValue(credit.getCustomer().getName());

            });

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Report-Rejected" + UUID.randomUUID().toString() + ".xlsx");

            ServletOutputStream outputStream = response.getOutputStream();
            workbookNuevo.write(outputStream);
            workbookNuevo.close();
            outputStream.close();
            return ResponseEntity.ok("Registro generado");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/evaluacion")
    public ResponseEntity<?> evaluar(@RequestBody EvaluationDTO evaluation, HttpServletResponse response) {

        try {

            CreditEntity credit = creditService.findById(evaluation.getIdCredit());

            if (credit == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            credit.setStatus(evaluation.getStatus());
            credit.setDescriptionEvaluation(evaluation.getDescriptionEvaluation());
            creditService.save(credit);

            String file = "D:/ambiente-desarrollo/Java/rest-avanzado/rest-avanzado-project/src/main/resources/templates/evaluacion.pdf";
            String file1 = "D:/ambiente-desarrollo/Java/rest-avanzado/rest-avanzado-project/src/main/resources/templates/evaluacion1.pdf";

            PdfReader pdfReader = new PdfReader(file);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(file1));
            int pageCount = pdfReader.getNumberOfPages();
            PdfLayer layer = new PdfLayer("Layer", pdfStamper.getWriter());
            for (int i = 1; i <= pageCount; i++) {
                Rectangle rect = pdfReader.getPageSize(i);
                PdfContentByte cb = pdfStamper.getOverContent(i);
                cb.beginLayer(layer);
                BaseFont bf = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
                cb.setColorFill(BaseColor.BLACK);
                cb.setFontAndSize(bf, 14);
                cb.beginText();

                log.info("i -> " + i);

                if (i == 1) {

                    // ESTADO CREDITO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, evaluation.getStatus(), rect.getWidth() - 760,
                            rect.getHeight() - 90, 0);


                    // ID CREDITO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getId()), rect.getWidth() - 122,
                            rect.getHeight() - 162, 0);

                    // FECHA EVALUACION
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, this.formatDate(new Date()), rect.getWidth() - 740,
                            rect.getHeight() - 250, 0);

                    // TIPO CREDITO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, credit.getTypeCredit().getDescription(), rect.getWidth() - 140,
                            rect.getHeight() - 250, 0);

                    // CUSTOMER FULLNAME
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, fullnameCustomer(credit), rect.getWidth() - 760,
                            rect.getHeight() - 380, 0);

                    // CUSTOMER EMAIL
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, credit.getCustomer().getEmail(), rect.getWidth() - 715,
                            rect.getHeight() - 440, 0);

                    // CREDITO MONTO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getAmount()), rect.getWidth() - 350,
                            rect.getHeight() - 380, 0);

                    // CREDITO CUOTAS
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getDues()), rect.getWidth() - 155,
                            rect.getHeight() - 380, 0);

                    // CREDITO INTERESES
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getInterests()), rect.getWidth() - 350,
                            rect.getHeight() - 440, 0);

                    // CREDITO MOTIVO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getReason()), rect.getWidth() - 90,
                            rect.getHeight() - 495, 0);

                }

                cb.endText();

                cb.endLayer();
            }
            pdfStamper.close();

            InputStream inputImage = new FileInputStream(file1);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[512];

            int l = inputImage.read(buffer);

            while (l >= 0) {

                outputStream.write(buffer, 0, l);

                l = inputImage.read(buffer);
            }

            HttpHeaders headers = new HttpHeaders();

            headers.set("Content-Type", "application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Evaluacion_" + UUID.randomUUID().toString() + ".pdf");
            return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @PostMapping("/constancia/{id}")
    public ResponseEntity<?> constancia(@PathVariable Long id, HttpServletResponse response) {

        try {

            CreditEntity credit = creditService.findById(id);

            if (credit == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String file = "D:/ambiente-desarrollo/Java/rest-avanzado/rest-avanzado-project/src/main/resources/templates/constancia_plantilla.pdf";
            String file1 = "D:/ambiente-desarrollo/Java/rest-avanzado/rest-avanzado-project/src/main/resources/templates/constancia_plantilla1.pdf";

            PdfReader pdfReader = new PdfReader(file);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(file1));
            int pageCount = pdfReader.getNumberOfPages();
            PdfLayer layer = new PdfLayer("Layer", pdfStamper.getWriter());
            for (int i = 1; i <= pageCount; i++) {
                Rectangle rect = pdfReader.getPageSize(i);
                PdfContentByte cb = pdfStamper.getOverContent(i);
                cb.beginLayer(layer);
                BaseFont bf = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
                cb.setColorFill(BaseColor.BLACK);
                cb.setFontAndSize(bf, 14);
                cb.beginText();

                log.info("i -> " + i);

                if (i == 1) {


                    // ID CREDITO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getId()), rect.getWidth() - 122,
                            rect.getHeight() - 162, 0);

                    // FECHA EVALUACION
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, this.formatDate(new Date()), rect.getWidth() - 740,
                            rect.getHeight() - 250, 0);

                    // TIPO CREDITO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, credit.getTypeCredit().getDescription(), rect.getWidth() - 140,
                            rect.getHeight() - 250, 0);

                    // CUSTOMER FULLNAME
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, fullnameCustomer(credit), rect.getWidth() - 760,
                            rect.getHeight() - 380, 0);

                    // CUSTOMER EMAIL
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, credit.getCustomer().getEmail(), rect.getWidth() - 715,
                            rect.getHeight() - 440, 0);

                    // CREDITO MONTO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getAmount()), rect.getWidth() - 350,
                            rect.getHeight() - 380, 0);

                    // CREDITO CUOTAS
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getDues()), rect.getWidth() - 155,
                            rect.getHeight() - 380, 0);

                    // CREDITO INTERESES
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getInterests()), rect.getWidth() - 350,
                            rect.getHeight() - 440, 0);

                    // CREDITO MOTIVO
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(credit.getReason()), rect.getWidth() - 90,
                            rect.getHeight() - 495, 0);

                }

                cb.endText();

                cb.endLayer();
            }
            pdfStamper.close();

            InputStream inputImage = new FileInputStream(file1);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[512];

            int l = inputImage.read(buffer);

            while (l >= 0) {

                outputStream.write(buffer, 0, l);

                l = inputImage.read(buffer);
            }

            HttpHeaders headers = new HttpHeaders();

            headers.set("Content-Type", "application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Constancia_" + UUID.randomUUID().toString() + ".pdf");
            return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    private String fullnameCustomer(CreditEntity credit) {
        return credit.getCustomer().getName() + " " + credit.getCustomer().getSurname();
    }

}
