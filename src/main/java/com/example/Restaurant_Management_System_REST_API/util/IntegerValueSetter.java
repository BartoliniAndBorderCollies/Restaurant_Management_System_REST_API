package com.example.Restaurant_Management_System_REST_API.util;

import org.apache.poi.ss.usermodel.Row;

public class IntegerValueSetter implements CellValueSetter<Integer> {
    @Override
    public void setCellValue(Row row, int cellIndex, Integer value) {
        row.createCell(cellIndex).setCellValue(value);
    }
}
