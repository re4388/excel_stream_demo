package com.ben.excelstream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
