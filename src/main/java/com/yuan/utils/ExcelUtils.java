package com.yuan.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    public static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static List<List<Object>> TransformExcelAllDataToList(InputStream is,int columnNumber) {
        logger.info("execute transformExcelDataToList");
        List<List<Object>> resultList = new ArrayList<>();
            Workbook workbook = null;
            try {
                //这种方式 Excel 2003/2007/2010 都是可以处理的
                workbook = WorkbookFactory.create(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Sheet的数量
            int sheetCount = workbook.getNumberOfSheets();
            //遍历每个Sheet
            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (int num = columnNumber;num <= sheet.getLastRowNum();num++){
                    Row row = sheet.getRow(num);
                    Iterator<Cell> it = row.cellIterator();
                    List<Object> objectList = new ArrayList<>();
                    while (it.hasNext()){
                        Object cellVal = ExcelUtils.getCellValue(it.next());
                        objectList.add(cellVal);
                    }
                    resultList.add(objectList);
                }
            }
        return resultList;
    }

    public static List<Integer> TransformExcelDataToList(File file) {
        logger.info("execute transformExcelDataToList");
        List<Integer> showidList = new ArrayList<>();
        try {
            FileInputStream is = new FileInputStream(file); //文件流
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(is); //这种方式 Excel 2003/2007/2010 都是可以处理的
            } catch (Exception e) {
                e.printStackTrace();
            }
            int sheetCount = workbook.getNumberOfSheets();  //Sheet的数量
            //遍历每个Sheet
            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                //获取第一列数据，只会获取有数据的单元格
                for (Row row : sheet) {
                    Cell cell = row.getCell(0);
                    showidList.add(Integer.valueOf(getCellValue(cell)));
                }
            }
        } catch (IOException ex) {
            logger.error("transformExcelDataToList execute exeption={}", ex);
            return showidList;
        }
        return showidList;
    }
    public static String TransformExcelDataToStr(File file) {
        logger.info("execute transformExcelDataToList");
        String showidStr = "";
        try {
            FileInputStream is = new FileInputStream(file); //文件流
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(is); //这种方式 Excel 2003/2007/2010 都是可以处理的
            } catch (Exception e) {
                e.printStackTrace();
            }
            int sheetCount = workbook.getNumberOfSheets();  //Sheet的数量
            //遍历每个Sheet
            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                //获取第一列数据，只会获取有数据的单元格
                for (Row row : sheet) {
                    Cell cell = row.getCell(0);
                    showidStr += getCellValue(cell) + ",";
                }
            }
        } catch (IOException ex) {
            logger.error("transformExcelDataToList execute exeption={}", ex);
            return showidStr;
        }
        return showidStr.substring(0, showidStr.lastIndexOf(","));
    }
    private static String getCellValue(Cell cell){
        //SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if(cell == null){
            return null;
        }
        String value = null;
        //简单的查检列类型
        switch(cell.getCellType())
        {
            case HSSFCell.CELL_TYPE_STRING://字符串
                value = cell.getRichStringCellValue().getString();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC://数字
                long dd = (long)cell.getNumericCellValue();
                value = dd+"";
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN://boolean型值
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                break;
            default:
                break;
        }
        return value;
    }
}
