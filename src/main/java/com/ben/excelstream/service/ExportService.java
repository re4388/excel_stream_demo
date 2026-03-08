package com.ben.excelstream.service;

import com.ben.excelstream.model.ExcelDataRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Service
public class ExportService {

   public void exportStandard(OutputStream os, String fileName, int rowCount) throws IOException {
      try (Workbook workbook = new XSSFWorkbook()) {
         Sheet sheet = workbook.createSheet("Data");
         createHeader(sheet);
         for (int i = 0; i < rowCount; i++) {
            ExcelDataRow data = generateData(i);
            createRow(sheet, i + 1, data);
         }
         workbook.write(os);
      }
   }

   public void exportStreaming(OutputStream os, String fileName, int rowCount) throws IOException {
      // Window size of 100
      try (SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
         Sheet sheet = wb.createSheet("Data");
         createHeader(sheet);
         for (int i = 0; i < rowCount; i++) {
            ExcelDataRow data = generateData(i);
            createRow(sheet, i + 1, data);

            // SXSSFWorkbook will automatically flush rows to disk
            // once the row access window (100) is exceeded.
         }
         // Final write to the HTTP response output stream
         wb.write(os);
         os.flush();
         wb.dispose(); // Important for SXSSF to clean up temp files
      }
   }

   private void createHeader(Sheet sheet) {
      Row headerRow = sheet.createRow(0);
      headerRow.createCell(0).setCellValue("ID");
      headerRow.createCell(1).setCellValue("Name");
      headerRow.createCell(2).setCellValue("Content");
      headerRow.createCell(3).setCellValue("Timestamp");
   }

   private void createRow(Sheet sheet, int rowNum, ExcelDataRow excelDataRow) {
      Row row = sheet.createRow(rowNum);
      row.createCell(0).setCellValue(excelDataRow.getId());
      row.createCell(1).setCellValue(excelDataRow.getName());
      row.createCell(2).setCellValue(excelDataRow.getContent());
      row.createCell(3).setCellValue(excelDataRow.getTimestamp());
   }

   private ExcelDataRow generateData(int index) {
      return new ExcelDataRow(
            (long) index,
            "Name-" + index,
            "Content for row " + index,
            LocalDateTime.now().toString());
   }
}
