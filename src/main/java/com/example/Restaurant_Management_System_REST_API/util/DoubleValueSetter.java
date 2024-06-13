package com.example.Restaurant_Management_System_REST_API.util;

import org.apache.poi.ss.usermodel.Row;

public class DoubleValueSetter implements CellValueSetter<Double> {
    @Override
    public void setCellValue(Row row, int cellIndex, Double value) {
        row.createCell(cellIndex).setCellValue(value);
    }
}
