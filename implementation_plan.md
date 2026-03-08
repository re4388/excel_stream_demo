# Implementation Plan - Excel Stream Demo

[Overview]
建立一個名為 `excel_stream_demo` 的 Spring Boot 模組，用於展示並對比 XSSFWorkbook (非串流) 與 SXSSFWorkbook (串流) 在 POI 與 HTTP 層級的實作差異。

本計劃旨在透過實作兩個 API 節點，讓開發者直觀地觀察到當處理大數據量時，記憶體佔用與網路傳輸行為的不同。非串流模式會等待檔案完全生成後才開始傳輸，而串流模式能邊生成邊發送，顯著降低首位元組時間 (TTFB) 並優化伺服器資源管理。

[Types]  
使用簡單的 POJO 來代表匯出資料。

- `ExcelDataRow`:
    - `id`: Long
    - `name`: String
    - `content`: String
    - `timestamp`: String

[Files]
建立全新的 Maven 模組與資料夾結構。

- `excel_stream_demo/pom.xml`: 模組定義，依賴 spring-boot-starter-web 與 poi-ooxml。
- `excel_stream_demo/src/main/java/com/ben/excelstream/ExcelStreamDemoApplication.java`: Spring Boot 啟動類。
- `excel_stream_demo/src/main/java/com/ben/excelstream/model/ExcelDataRow.java`: 資料模型。
- `excel_stream_demo/src/main/java/com/ben/excelstream/controller/ExcelExportController.java`: 定義兩個 API 節點。
- `excel_stream_demo/src/main/java/com/ben/excelstream/service/ExportService.java`: 實作兩種 POI 匯出邏輯。
- `excel_stream_demo/src/main/resources/application.properties`: 基本配置。
- `excel_stream_demo/src/test/java/com/ben/excelstream/ExcelExportE2ETest.java`: E2E 測試類，驗證端點回應。

[Functions]
核心邏輯函式。

- `ExportService.exportStandard(OutputStream os, String fileName, int rowCount)`:
    - 使用 `XSSFWorkbook`。
    - 在記憶體中建立所有行與儲存格。
    - 最後一次性 `wb.write(os)`。
- `ExportService.exportStreaming(OutputStream os, String fileName, int rowCount)`:
    - 使用 `SXSSFWorkbook` (視窗大小設為 100)。
    - 邊迭代資料邊寫入檔案。
    - 強制每隔一段時間執行寫入輸出流。
- `ExcelExportController.noStreamEndpoint(String fileName, int count)`:
    - 調用 `exportStandard`。
    - 回傳 `ResponseEntity<byte[]>` 或直接寫入 `HttpServletResponse`。
- `ExcelExportController.streamingEndpoint(String fileName, int count)`:
    - 調用 `exportStreaming`。
    - 使用 `StreamingResponseBody` 確保 Spring 框架執行異步串流傳輸。

[Classes]
主要類別結構。

- `ExcelExportController`: `@RestController`，處理外來請求。
- `ExportService`: `@Service`，封裝 POI 核心邏輯。
- `ExcelDataRow`: 資料傳輸物件 (DTO)。

[Dependencies]
新增必要的相依性。

- `spring-boot-starter-web`: Web 支援。
- `org.apache.poi:poi-ooxml:5.2.5`: Excel 處理。
- `spring-boot-starter-test`: 測試框架。

[Implementation Order]
實作順序。

1. 修改 root `pom.xml` 加入新模組。
2. 建立 `excel_stream_demo` 目錄與其 `pom.xml`。
3. 實作 Model 與 Application 啟動類。
4. 實作 `ExportService` 中的 `XSSFWorkbook` 與 `SXSSFWorkbook` 邏輯。
5. 實作 `ExcelExportController` 並配置 `StreamingResponseBody`。
6. 新增 `application.properties`。
7. 撰寫 E2E 測試並執行驗證。

task_progress Items:
- [x] Step 1: 修改根目錄 pom.xml 加入 excel_stream_demo
- [x] Step 2: 建立 excel_stream_demo 模組與 pom.xml
- [x] Step 3: 建立基礎程式結構 (Application, Model)
- [x] Step 4: 實作 ExportService 的 XSSF 與 SXSSF 邏輯
- [x] Step 5: 實作 ExcelExportController 及其端點
- [x] Step 6: 建立 E2E 測試並驗證功能
