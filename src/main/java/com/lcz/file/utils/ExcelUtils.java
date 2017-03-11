package com.lcz.file.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;


public class ExcelUtils {

    public static String getStringCellData(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (StringUtils.isBlank(cell.toString())) {
                    return null;
                }
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                Double cellValue = cell.getNumericCellValue();
                if (StringUtils.isNotBlank(cellValue.toString())) {
                    if ((String.valueOf(cellValue)).indexOf("E") > 0) {
                        BigDecimal db = new BigDecimal(cellValue);
                        return getExcelNumberString(db.toPlainString());

                    } else if (String.valueOf(cellValue).indexOf(".") > 0) {
                        return String.valueOf(cellValue).substring(0, String.valueOf(cellValue).lastIndexOf("."));
                    }
                    return String.valueOf(cellValue);
                }

        }
        return null;
    }

    /**
     * excel表格，超过15位的数字，将15位和16位的数字四舍五入，后面的位数补0（excel本身的限制造成的，excel只能支持到15位数字）
     */
    public static String getExcelNumberString(String value) {
        int length = value.length();
        if (length < 16) {
            return value;
        }
        long longValue = Math.round(Long.parseLong(value.substring(0, 16)) / 10.0);
        int zeroLength = length - 15;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < longValue; i++) {
            buffer.append("0");
        }
        return String.valueOf(zeroLength) + buffer.toString();

    }

}
