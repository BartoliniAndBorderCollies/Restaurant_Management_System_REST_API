package com.example.Restaurant_Management_System_REST_API.util;

import org.apache.poi.ss.usermodel.Row;

public class StringValueSetter implements CellValueSetter<String>{
    @Override
    public void setCellValue(Row row, int cellIndex, String value) {
        row.createCell(cellIndex).setCellValue(value);
    }
}
