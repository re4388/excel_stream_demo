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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

   @GetMapping("/export/multi-sheet")
   public StreamingResponseBody multiSheetEndpoint(
         @RequestParam(defaultValue = "multi-sheet.xlsx") String fileName,
         @RequestParam(defaultValue = "1000") int count,
         @RequestParam(defaultValue = "3") int sheetCount,
         HttpServletResponse response) {

      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

      return outputStream -> {
         exportService.exportMultiSheet(outputStream, count, sheetCount);
      };
   }

   /**
    * 新增的測試端點，用於效能分析 (Profiling) 練習。
    * 包含 CPU 密集、記憶體密集及執行緒阻塞三種異常行為。
    */
   @GetMapping("/profile-test")
   public String profileTest(
         @RequestParam(defaultValue = "10") int cpuLoad,
         @RequestParam(defaultValue = "100000") int memorySize,
         @RequestParam(defaultValue = "2000") int sleepMs) {

      long startTime = System.currentTimeMillis();

      // 1. CPU 密集型：費氏數列計算 (CPU Bound)
      long fibResult = performCpuIntensiveTask(cpuLoad);

      // 2. 記憶體密集型：建立大量暫時性物件 (Memory Bound / GC Pressure)
      int listSize = performMemoryIntensiveTask(memorySize);

      // 3. 執行緒阻塞：模擬 I/O 等待 (Thread Blocking)
      performBlockingTask(sleepMs);

      long duration = System.currentTimeMillis() - startTime;

      return String.format(
            "Profiling test completed in %d ms.\n" +
            "- CPU Result: Fib(%d) calculation done.\n" +
            "- Memory Result: Created %d UUID strings.\n" +
            "- Blocked for: %d ms.",
            duration, cpuLoad, listSize, sleepMs);
   }

   private long performCpuIntensiveTask(int n) {
      // 故意使用效率極低的遞迴方式計算費氏數列
      if (n <= 1) return n;
      return performCpuIntensiveTask(n - 1) + performCpuIntensiveTask(n - 2);
   }

   private int performMemoryIntensiveTask(int size) {
      // 快速分配大量物件到 List 中，增加 Heap 壓力並觸發 GC
      List<String> list = new ArrayList<>();
      for (int i = 0; i < size; i++) {
         list.add(UUID.randomUUID().toString() + "-" + i);
      }
      return list.size();
   }

   private void performBlockingTask(int ms) {
      try {
         // 模擬資料庫查詢或 API 呼叫的延時
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }
}
