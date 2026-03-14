### 1. 基本呼叫（使用預設值）

這個指令會下載預設檔名為 `streaming.xlsx` 的檔案，內容包含 1000 筆資料。

```bash
curl -O http://localhost:8080/export/streaming
```

### 2. 指定檔名與資料筆數

如果你想自定義檔名和匯出的資料筆數（例如 5000 筆）：

```bash
curl -G "http://localhost:8080/export/streaming" \
     --data-urlencode "fileName=my_report.xlsx" \
     --data-urlencode "count=5000" \
     -o my_report.xlsx
```


### 1. 使用預設值進行測試

這會執行輕微的 CPU 計算、分配 10 萬個 UUID 字串到記憶體，並阻塞 2 秒。

```bash
curl "http://localhost:8080/profile-test"
```

### 2. 增加 CPU 負載 (加重運算)

`cpuLoad` 設為 40 以上時（遞迴費氏數列），你會在 Profiler 的 __CPU Timeline__ 中看到明顯的飆升。

```bash
curl "http://localhost:8080/profile-test?cpuLoad=42"
```

### 3. 增加記憶體壓力 (觸發 GC)

`memorySize` 設為 100 萬或更高，你會在 Profiler 的 __Memory/Heap__ 圖表中看到鋸齒狀（分配後 GC 回收）。

```bash
curl "http://localhost:8080/profile-test?memorySize=1000000"
```

### 4. 增加執行緒阻塞 (模擬延遲)

`sleepMs` 設長一點，可以在 __Thread Dump__ 或 __Threads View__ 中觀察到執行緒長期處於 `TIMED_WAITING` 狀態。

```bash
curl "http://localhost:8080/profile-test?sleepMs=5000"
```

### 5. 全力負載測試

同時加重三項指標：

```bash
curl "http://localhost:8080/profile-test?cpuLoad=40&memorySize=1000000&sleepMs=3000"
```

這些參數能幫助你練習如何檢測 CPU 瓶頸、記憶體洩漏/壓力以及執行緒阻塞問題。
