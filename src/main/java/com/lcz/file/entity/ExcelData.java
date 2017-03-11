package com.lcz.file.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ExcelData implements Serializable {
    private List<Map<String, Object>> excelValue;
    private List<String> excelHead;
    private List<RuleEntity> ruleEntities;

    public List<RuleEntity> getRuleEntities() {
        return ruleEntities;
    }

    public void setRuleEntities(List<RuleEntity> ruleEntities) {
        this.ruleEntities = ruleEntities;
    }

    public List<Map<String, Object>> getExcelValue() {
        return excelValue;
    }

    public void setExcelValue(List<Map<String, Object>> excelValue) {
        this.excelValue = excelValue;
    }

    public List<String> getExcelHead() {
        return excelHead;
    }

    public void setExcelHead(List<String> excelHead) {
        this.excelHead = excelHead;
    }
}