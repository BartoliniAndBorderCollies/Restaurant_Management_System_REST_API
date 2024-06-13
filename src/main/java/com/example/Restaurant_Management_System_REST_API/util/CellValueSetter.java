package com.example.Restaurant_Management_System_REST_API.util;

import org.apache.poi.ss.usermodel.Row;

public interface CellValueSetter<T> {
    void setCellValue(Row row, int cellIndex, T value);
}
