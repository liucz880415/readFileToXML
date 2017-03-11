package com.lcz.file;

import com.google.common.collect.Lists;
import com.lcz.file.entity.GenerateXmlParams;
import com.lcz.file.entity.RuleEntity;
import com.lcz.file.utils.DateFormatUtil;
import com.lcz.file.entity.ExcelData;
import com.lcz.file.excel.ExcelServiceImpl;
import com.lcz.file.utils.DateUtil;
import com.lcz.file.utils.ExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ReadFile {

    private final static String DATA_FORMAT = "data-format";
    private final static String CHARSET = "UTF-8";
    private final static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

    enum DataFormat {
        FORMAT_YMD("YYYY-MM-DD"),
        FORMAT_YMDHMS("YYYY-MM-DD hh:mm:ss"),
        FORMAT_INTEGER("Integer"),
        FORMAT_DOUBLE("Double");

        private final String code;

        DataFormat(String code) {
            this.code = code;

        }

        public String getCode() {
            return code;
        }

        public static DataFormat getDataFormatByCode(String code) {
            for (DataFormat format : DataFormat.values()) {
                if (format.getCode().equals(code)) {
                    return format;
                }
            }
            return null;
        }
    }

    class PathEntity {
        private String path;
        private int sort;
        private DataFormat dataFormat;

        public PathEntity(String path, int sort, DataFormat dataFormat) {
            this.path = path;
            this.sort = sort;
            this.dataFormat = dataFormat;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public DataFormat getDataFormat() {
            return dataFormat;
        }

        public void setDataFormat(DataFormat dataFormat) {
            this.dataFormat = dataFormat;
        }
    }

    private void handleElementNode(List<PathEntity> xmlNodesPathList, Element element) {

        Iterator iterator = element.elements().iterator();
        if (!iterator.hasNext()) {
            Integer sort = xmlNodesPathList.size();
            DataFormat dataFormat = null;
            String dataFormatAttr = element.attributeValue(DATA_FORMAT);
            if (StringUtils.isNotBlank(dataFormatAttr)) {
                dataFormat = DataFormat.getDataFormatByCode(dataFormatAttr);
            }

            xmlNodesPathList.add(new PathEntity(element.getPath() + "/", sort, dataFormat));
            return;
        }
        while (iterator.hasNext()) {
            Element sub = (Element) iterator.next();
            handleElementNode(xmlNodesPathList, sub);
        }
    }

    public void parseXml(GenerateXmlParams generateXmlParams) {

        File templateFile = generateXmlParams.getXmlTemplateFile();
        File excelFile = generateXmlParams.getExcelFile();

        String xmlStorePath = generateXmlParams.getXmlStorePath();

        String fileNamePre = generateXmlParams.getFileNamePre();
        String fileNameCol = generateXmlParams.getFileName();

        String batchNo = generateXmlParams.getBatchNo();//批次号 相同

        try {
            List<PathEntity> xmlNodesPathList = getXmlTemplatePathNodes(templateFile);

            //获取excel中的数据
            ExcelData excelData = new ExcelServiceImpl().getExcelData(excelFile);
            List<Map<String, Object>> dataList = excelData.getExcelValue();
            List<RuleEntity> ruleList = excelData.getRuleEntities();

            dataList.stream().forEach(data -> {
                StringBuilder targetArchiveNo = new StringBuilder();
                targetArchiveNo.append(StringUtils.isBlank(generateXmlParams.getArchiveNOPre()) ? "" : generateXmlParams.getArchiveNOPre())
                        .append(StringUtils.isBlank(generateXmlParams.getArchiveNo()) ? "" : generateXmlParams.getArchiveNo());

                Long archivePost = StringUtils.isBlank(generateXmlParams.getArchiveNoPost()) ? 0 : Long.parseLong(generateXmlParams.getArchiveNoPost());

                //如果是 0000000001 这样的 保留格式

                String getArchiveNoPost = getArchiveNoPost(archivePost, generateXmlParams.getArchiveNoPost());

                targetArchiveNo.append(getArchiveNoPost).append(archivePost);

                //创建一个新的document xml
                Document newDoc = DocumentHelper.createDocument();

                // 以模板为准，表头中有则输出值，没有则创建一个空的节点
                xmlNodesPathList.stream().forEach(item -> createElement(newDoc, item, data, ruleList, batchNo, targetArchiveNo.toString()));

                // 写xml文件
                String fileName = getXmlFileName(fileNamePre, fileNameCol, data, dataList);

                writeXml(newDoc, fileName, xmlStorePath, templateFile.getAbsolutePath());
                archivePost += 1;
                generateXmlParams.setArchiveNoPost(getArchiveNoPost + String.valueOf(archivePost));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getArchiveNoPost(Long archivePost, String archiveNoPost) {
        StringBuilder targetArchiveNo = new StringBuilder();
        if (archivePost != null && archiveNoPost != null) {
            for (int i = 0; i < archiveNoPost.length() - archivePost.toString().length(); i++) {
                targetArchiveNo.append("0");
            }
        }
        return targetArchiveNo.toString();
    }

    private String getXmlFileName(String fileNamePre, String fileNameCol, Map<String, Object> data, List<Map<String, Object>> dataList) {
        String fileName = "";
        if (StringUtils.isNotBlank(fileNamePre)) {
            fileName += fileNamePre;
        }
        String tempFileNameKey = fileNameCol == null ? "" : fileNameCol.trim();
        String tempFileName = (String) data.get(tempFileNameKey);
        if (StringUtils.isBlank(tempFileName) && StringUtils.isBlank(fileName)) {
            fileName = "temp" + dataList.indexOf(data);
        } else if (StringUtils.isNotBlank(tempFileName)) {
            fileName += tempFileName;
        } else {
            fileName += dataList.indexOf(data);
        }
        return fileName;
    }

    private List<PathEntity> getXmlTemplatePathNodes(File templateFile) {

        List<PathEntity> xmlNodesPathList = Lists.newArrayList();
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(templateFile);

            Element root = document.getRootElement();

            handleElementNode(xmlNodesPathList, root);
        } catch (Exception e) {
            System.out.println("==== 解析xml 模板 异常");
        }
        return xmlNodesPathList;
    }

    private String getHeadName(List<RuleEntity> ruleList, String path) {

        for (RuleEntity rule : ruleList) {
            StringBuilder rulePath = new StringBuilder();
            if (StringUtils.isNotBlank(rule.getParentNode())) {
                rulePath.append("/").append(rule.getParentNode());
            }
            if (StringUtils.isNotBlank(rule.getChildNode())) {
                rulePath.append("/").append(rule.getChildNode());
            }
            rulePath.append("/");

            if (path.contains(rulePath.toString())) {
                return rule.getHead();
            }
        }

        return null;
    }

    private void writeXml(Document document, String fileName, String xmlPath, String dataPath) {
        try {
            if (StringUtils.isBlank(xmlPath)) {
                xmlPath = dataPath.substring(0, dataPath.lastIndexOf("/"));
            } else if (!xmlPath.endsWith("/")) {
                xmlPath += "/";
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding(CHARSET);

            File file = new File(xmlPath);
            if (!file.exists()) {
                file.mkdir();
            }

            XMLWriter writer = new XMLWriter(new FileOutputStream(xmlPath + fileName + ".xml", false), outputFormat);

            writer.write(document);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createElement(Document document, PathEntity path, Map<String, Object> data, List<RuleEntity> ruleList, String batchNo, String archiveNo) {

        //从ruleList中找到对应的节点path
        String headName = getHeadName(ruleList, path.getPath());

        Object targetValue = null;
        if (path.getPath().contains("/claim_case/batch_no/")) {
            targetValue = batchNo;
        } else if (path.getPath().contains("/claim_case/archive_no/")) {
            targetValue = archiveNo;
        } else {
            if (headName != null) {
                targetValue = data.get(headName);
            }
        }

        String xmlPath = path.getPath().substring(0, path.getPath().length() - 1);

        List newNodes = document.selectNodes(xmlPath);
        String parseValue = getElementValue(targetValue, path.getDataFormat());
        parseValue = parseValue == null ? "" : parseValue;
        if (CollectionUtils.isEmpty(newNodes)) {

            Element temp = DocumentHelper.makeElement(document, xmlPath);
            temp.addText(parseValue);

        } else {
            Element firstNode = (Element) newNodes.get(0);
            Element parentNode = firstNode.getParent();

            for (Object obj : newNodes) {
                Element objEle = (Element) obj;

                Element copyEle = DocumentHelper.createElement(objEle.getName());
                copyEle.setText(parseValue);
                parentNode.add(copyEle);
            }
        }
    }

    private Date getDateCell(Object valueObject, DataFormat format) {
        try {
            if (valueObject != null) {
                if (valueObject instanceof Double) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Double) valueObject);
                } else if (valueObject instanceof String) {

                    Pattern temp = Pattern.compile("^([0-9]{6})$");//为了识别 199007类型日期
                    if (temp.matcher(((String) valueObject)).matches()) {
                        return DateUtil.parseDateMonth((String) valueObject);
                    }

                    Pattern pattern = Pattern.compile("^[0-9]+$");
                    if (pattern.matcher(((String) valueObject)).matches()) {
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) valueObject));
                    }
                    switch (format) {
                        case FORMAT_YMD:
                            return DateUtil.parseDate((String) valueObject);
                        case FORMAT_YMDHMS:
                            return DateUtil.parseDateMinute((String) valueObject);
                    }

                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("===== 转换日期格式错误");
        }
        return null;
    }

    private String getStringCell(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Double) {
            Double doubleValue = (Double) value;
            if (StringUtils.isNotBlank(doubleValue.toString())) {
                if ((String.valueOf(doubleValue)).indexOf("E") > 0) {
                    BigDecimal db = new BigDecimal(doubleValue);
                    return ExcelUtils.getExcelNumberString(db.toPlainString());

                } else if (String.valueOf(doubleValue).indexOf(".") > 0) {
                    return String.valueOf(doubleValue).substring(0, String.valueOf(doubleValue).lastIndexOf("."));
                }
                return String.valueOf(doubleValue);
            }
        }
        return "";
    }

    private String getElementValue(Object value, DataFormat dataFormat) {
        if (value == null) {
            return "";
        }
        if (dataFormat != null) {
            switch (dataFormat) {
                case FORMAT_YMD:
                    return DateFormatUtil.format(getDateCell(value, dataFormat), DateFormatUtil.FORMAT_YMD);
                case FORMAT_YMDHMS:
                    return DateFormatUtil.format(getDateCell(value, dataFormat), DateFormatUtil.FORMAT_YMDHMS);
                case FORMAT_INTEGER:
                    String tempValue = value.toString().contains(".") ? value.toString().substring(0, value.toString().indexOf(".")) : value.toString();
                    Integer intVal = Integer.valueOf(tempValue);
                    return String.valueOf(intVal);
                case FORMAT_DOUBLE:
                    Double doubleVal = Double.valueOf(value.toString());
                    return DOUBLE_FORMAT.format(doubleVal);
            }
        }
        return getStringCell(value);
    }

}
