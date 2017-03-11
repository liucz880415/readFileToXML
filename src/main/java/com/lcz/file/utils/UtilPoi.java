package com.lcz.file.utils;

import com.lcz.file.excel.RowMapper;
import com.lcz.file.excel.SheetDataHandler;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;

public class UtilPoi {

    public static SheetDataHandler read(InputStream inputStream, long size) throws Exception {
        SheetDataHandler handler = new SheetDataHandler((int) (size / 50));
        read(inputStream, handler);
        return handler;
    }

    public static void read(InputStream in, RowMapper mapper) throws Exception {
        XSSFReader reader = new XSSFReader(OPCPackage.open(in));
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        mapper.setSharedStringsTable(reader.getSharedStringsTable());
        parser.setContentHandler(mapper);
        parser.parse(new InputSource(reader.getSheet("rId" + 1)));
    }
}
