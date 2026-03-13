package com.ben.excelstream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExcelExportE2ETest {

   @LocalServerPort
   private int port;

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void testNoStreamEndpoint() {
      ResponseEntity<byte[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/export/no-stream?count=10",
            byte[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getHeaders().getContentType().toString()).contains("spreadsheetml.sheet");
      assertThat(response.getBody()).isNotEmpty();
   }

   @Test
   public void testStreamingEndpoint() {
      ResponseEntity<byte[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/export/streaming?count=10",
            byte[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getHeaders().getContentType().toString()).contains("spreadsheetml.sheet");
      assertThat(response.getBody()).isNotEmpty();
   }

   @Test
   public void testMultiSheetEndpoint() throws IOException {
      ResponseEntity<byte[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/export/multi-sheet?count=10&sheetCount=2",
            byte[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getHeaders().getContentType().toString()).contains("spreadsheetml.sheet");
      assertThat(response.getBody()).isNotEmpty();

      // Save to file
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      String fileName = "test_" + timestamp + ".xlsx";
      Path resultDir = Paths.get("test_result");
      if (!Files.exists(resultDir)) {
         Files.createDirectories(resultDir);
      }
      Path filePath = resultDir.resolve(fileName);
      Files.write(filePath, response.getBody());
      System.out.println("Test result saved to: " + filePath.toAbsolutePath());
   }
}
