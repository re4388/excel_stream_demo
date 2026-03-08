package com.ben.excelstream.controller;

import com.ben.excelstream.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;

@RestController
public class ExcelExportController {

   private final ExportService exportService;

   public ExcelExportController(ExportService exportService) {
      this.exportService = exportService;
   }

   @GetMapping("/export/no-stream")
   public ResponseEntity<byte[]> noStreamEndpoint(
         @RequestParam(defaultValue = "standard.xlsx") String fileName,
         @RequestParam(defaultValue = "1000") int count) throws Exception {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exportService.exportStandard(baos, fileName, count);
      byte[] content = baos.toByteArray();

      return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .body(content);
   }

   @GetMapping("/export/streaming")
   public StreamingResponseBody streamingEndpoint(
         @RequestParam(defaultValue = "streaming.xlsx") String fileName,
         @RequestParam(defaultValue = "1000") int count,
         HttpServletResponse response) {

      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

      return outputStream -> {
         exportService.exportStreaming(outputStream, fileName, count);
      };
   }
}
