package com.ben.excelstream.model;

public class ExcelDataRow {
   private Long id;
   private String name;
   private String content;
   private String timestamp;

   public ExcelDataRow() {
   }

   public ExcelDataRow(Long id, String name, String content, String timestamp) {
      this.id = id;
      this.name = name;
      this.content = content;
      this.timestamp = timestamp;
   }

   // Getters and Setters
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }
}
