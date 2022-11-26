package ConfigAndData;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static Core.Core.Debug.*;
import static Core.Core.config;
import static Core.Core.user_data;

public class DataReader {

    public static Workbook data = null;
    public static Sheet sheet = null;

    public static void readFile() {
        sendData("readFile called");
        String data_path = config.getData_path();
        File data_file = new File(data_path);
        if (!data_file.exists()) {
            info("Excel文件不存在！自动创建默认空文件！");
            try {
                data_file.createNewFile();
            } catch (IOException e) {
                info("文件创建失败！");
                sendData(e.getLocalizedMessage());
            }
        }
        try {
            if(data_file.getName().endsWith(".xlsx"))
                data = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(data_file)));
            else if(data_file.getName().endsWith(".xls"))
                data = new HSSFWorkbook(new BufferedInputStream(new FileInputStream(data_file)));
            else info("文件格式不匹配！");
        } catch (IOException e) {
            info("Excel文件不存在！");
            sendData(e.getLocalizedMessage());
        }

        if (data.getNumberOfSheets() != 1) {
            info("可能存在未被阅读到的工作表，请核对工作表数据！");
        }
        if (data.getNumberOfSheets() == 0){
            info("空工作表！");
            return;
        }
        sheet = data.getSheetAt(0);
    }

    public static void getData() {
        sendData("getData called!");
        short topRow = sheet.getTopRow();
        Row title = sheet.getRow(topRow);
        List<String> clazz = new ArrayList<>();
        title.cellIterator().forEachRemaining(cell -> clazz.add(cell.getStringCellValue()));
        sendDetail("classes:" + clazz);
        Data.clazz = new ArrayList<>(clazz);
        user_data.clear();
        if(config.getConfirmation() == null)
            config.setConfirmation(clazz.get(clazz.size()-1));
        for (int row = ++ topRow; row <= sheet.getLastRowNum(); row++) {
            user_data.add(new Data(sheet.getRow(row)));
        }
        List<Map.Entry<String, List<String>>> sort_list = new ArrayList<>(Data.clazz_elements.entrySet());
        sort_list.sort(Comparator.comparingInt(o->o.getValue().size()));
        clazz.clear();
        sort_list.forEach(o->clazz.add(o.getKey()));

    }

    public static class Data {
        static List<String> clazz = new ArrayList<>();
        static Map<String, List<String>> clazz_elements = new HashMap<>();
        Map<String, String> user_data = new HashMap<>();
        Data(Row row){
            for (int i = 0; i <= row.getLastCellNum(); i++) {
                user_data.put(clazz.get(i),row.getCell(i).getStringCellValue());
                clazz_elements.getOrDefault(clazz.get(i), new ArrayList<>()).add(row.getCell(i).getStringCellValue());
            }
        }
    }

    DataReader(){

    }
}
