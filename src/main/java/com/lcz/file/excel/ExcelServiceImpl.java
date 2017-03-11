package com.lcz.file.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lcz.file.entity.ExcelData;
import com.lcz.file.entity.RuleEntity;
import com.lcz.file.utils.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelServiceImpl {
    /**
     * 返回数据时候 key + CELL_X就是当前的列
     */
    public static final String CELL_X = "CELL_X";
    /**
     * 返回数据时候CELL_Y就是当前的行
     */
    public static final String CELL_Y = "CELL_Y";

    public ExcelData getExcelData(File file) {
        Workbook workbook = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream buffer = new BufferedInputStream(inputStream);
            boolean is2007 = POIXMLDocument.hasOOXMLHeader(buffer);
            boolean is2003 = POIFSFileSystem.hasPOIFSHeader(buffer);
            if (is2007) {
                workbook = new XSSFWorkbook(buffer);// 2007
            } else if (is2003) {
                workbook = new HSSFWorkbook(buffer);// 2003
            } else {
                throw new RuntimeException("Excel格式发生错误！");
            }
        } catch (EncryptedDocumentException en) {
            throw new RuntimeException("Excel存在密码，无法导入");
        } catch (IOException e) {
            throw new RuntimeException("Excel格式发生错误！");
        } catch (Exception e) {
            throw new RuntimeException("Excel格式发生错误");
        }

        Sheet sheet = null;
        if (workbook.getSheetAt(0) == null) {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
            throw new RuntimeException("Excel格式发生错误！");
        }
        sheet = workbook.getSheetAt(0);
        // 验证数据
        if (sheet == null || sheet.getRow(0) == null || sheet.getRow(1) == null) {
            throw new RuntimeException("Excel中没有数据");
        }
        ExcelData excelData = new ExcelData();
        Map<String, Integer> headMap = Maps.newHashMap();
        List<Row> rowList = Lists.newArrayList();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rowList.add(row);
            }
            headMap = this.getHeadList(rowList.get(0));
        }

        List<String> headList = Lists.newArrayList();
        headList.addAll(headMap.keySet().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
        excelData.setExcelHead(headList);
        excelData.setExcelValue(this.handleSheet(sheet));
        excelData.setRuleEntities(handleRuleEntity(workbook.getSheetAt(1)));
        return excelData;
    }


    private List<RuleEntity> handleRuleEntity(Sheet sheet) {
        if (sheet == null) {
            return null;
        }
        List<Map<String, Object>> excelValue = this.handleSheet(sheet);
        if (excelValue == null || excelValue.size() == 0) {
            return null;
        }
        List<RuleEntity> list = Lists.newArrayList();

        for (Map<String, Object> map : excelValue) {
            RuleEntity ruleEntity = new RuleEntity();
            ruleEntity.setParentNode((String) map.get("XML根节点"));
            ruleEntity.setChildNode((String) map.get("XML字段"));
            ruleEntity.setHead((String) map.get("对账单字段"));
            list.add(ruleEntity);
        }
        return list;
    }

    private List<Map<String, Object>> handleSheet(Sheet sheet) {
        List<Row> rowList = Lists.newArrayList();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rowList.add(row);
            }
        }
        Map<String, Integer> headMap = this.getHeadList(rowList.get(0));
        List<Map<String, Object>> mapList = Lists.newArrayList();
        for (int i = 1; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            Map<String, Object> map = Maps.newHashMap();
            boolean isContinue = false;
            boolean isNullRow = true;
            for (String head : headMap.keySet()) {
                if (StringUtils.isBlank(head) || headMap.get(head) == null) {
                    continue;
                }
                Cell cell = row.getCell(headMap.get(head));
                if (cell != null && (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType())) {
                    throw new RuntimeException("excel包含公式运算或外部链接，无法导入");
                }
                if (cell != null && StringUtils.isNotBlank(cell.toString())) {
                    isNullRow = false;
                }
                if (cell != null && StringUtils.isNotBlank(cell.toString())) {
                    String value = cell.toString();
                    if (value.equals("总计：") || value.equals("合计：") || value.equals("小计：") || value.equals("总额：")) {
                        isContinue = true;
                        break;
                    }
                }
            }

            if (isNullRow || isContinue) {
                continue;
            }
            for (String head : headMap.keySet()) {
                if (StringUtils.isBlank(head) || headMap.get(head) == null) {
                    continue;
                }
                Object columnValue = getCellData(row.getCell(headMap.get(head)));
                if (columnValue instanceof String) {
                    columnValue = StringUtils.isNotBlank(columnValue.toString()) ? columnValue.toString().trim() : null;
                }
                map.put(head, columnValue);
                map.put(head + CELL_X, headMap.get(head));
            }
            map.put(CELL_Y, i + 1);
            mapList.add(map);
        }
        return mapList;
    }


    private Map<String, Integer> getHeadList(Object headObject) {
        Map<String, Integer> map = Maps.newHashMap();
        if (headObject instanceof Row) {
            Row head = (Row) headObject;
            for (int j = 0; j < head.getLastCellNum(); j++) {
                if (head.getCell(j) != null) {
                    String column = ExcelUtils.getStringCellData(head.getCell(j));
                    if (StringUtils.isNotBlank(column)) {
                        column = column.replaceAll("[（|\\(](.*)[\\)|）]", "");
                    }
                    map.put(column, j);
                }
            }
        } else if (headObject instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) headObject;
            for (Integer key : valueMap.keySet()) {
                Object valueObject = valueMap.get(key);
                if (valueObject != null) {
                    map.put(String.valueOf(valueObject), key);
                }
            }
        }
        return map;
    }

    private Object getCellData(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            if (StringUtils.isBlank(cell.toString())) {
                return null;
            }
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
            }
        }
        return null;
    }

}
