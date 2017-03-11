package com.lcz.file.excel;

import com.google.common.collect.Lists;
import com.lcz.file.entity.CellEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RowMapper extends DefaultHandler {
    private SharedStringsTable sst;
    private Map<Integer, String> strMap;
    private int sheetIndex = -1, rowIndex = -1;
    private List<CellEntity> row;
    private String cellS;
    private String cellType;
    private String cellName;
    private boolean valueFlag;
    private StringBuilder value;

    public void setSharedStringsTable(SharedStringsTable sst) {
        this.sst = sst;
        strMap = new HashMap<>(sst.getCount());
    }

    private void clearSheet() {
        sst = null;
        strMap = null;
        row = null;
        cellS = null;
        cellType = null;
        cellName = null;
        value = null;
        rowIndex = 0;
    }

    private Object convertCellValue() {
        String tmp = value.toString();
        Object result = tmp;

        if ("s".equals(cellType)) {     //字符串
            Integer key = Integer.parseInt(tmp);
            result = strMap.get(key);
            if (result == null)
                strMap.put(key, (String) (result = new XSSFRichTextString(sst.getEntryAt(key)).toString()));
        } else if ("n".equals(cellType)) {
            if ("2".equals(cellS)) {        //日期
                result = HSSFDateUtil.getJavaDate(Double.valueOf(tmp));
            }
        }
        return result;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if ("sheetData".equals(name)) {
            sheetIndex++;
        } else if ("row".equals(name)) {
            rowIndex++;
            row = Lists.newArrayList();
        } else if ("c".equals(name)) {
            cellName = attributes.getValue("r");
            cellS = attributes.getValue("s");
            cellType = attributes.getValue("t");
        } else if ("v".equals(name)) {
            valueFlag = true;
            value = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if ("sheetData".equals(name)) {
            clearSheet();
        } else if ("row".equals(name)) {
            mapRow(sheetIndex, rowIndex, row);
        } else if ("v".equals(name)) {
            String temp = StringUtils.isNotBlank(cellName) ? cellName.replaceAll("\\d", "") : null;
            Object value = convertCellValue();
            if (value != null && StringUtils.isNotBlank(String.valueOf(value)) && StringUtils.isNotBlank(temp)) {
                row.add(new CellEntity(temp, CellReference.convertColStringToIndex(temp), rowIndex + 1, value));
            }
            valueFlag = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (valueFlag) value.append(ch, start, length);
    }

    abstract void mapRow(int sheetIndex, int rowIndex, List<CellEntity> row);
}