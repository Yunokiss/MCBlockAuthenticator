package ConfigAndData;

import Authenticator.FirstLoginRegistrar;
import Authenticator.Interaction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.entity.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static Core.Core.*;
import static Core.Core.Debug.*;

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
        if(clazz.isEmpty())return;
        if(!clazz.contains("UUID")){
            title.createCell(title.getLastCellNum()+1).setCellValue("UUID");
        }
        Data.clazz = new ArrayList<>(clazz);
        user_data.clear();
        if(config.getConfirmation() == null)
            config.setConfirmation(clazz.get(clazz.size()-1));
        for (int row = ++ topRow; row <= sheet.getLastRowNum(); row++) {
            new Data(sheet.getRow(row));
        }
        if(user_data.isEmpty())return;
        List<Map.Entry<String, List<String>>> sort_list = new ArrayList<>(Data.clazz_elements.entrySet());
        sort_list.sort(Comparator.comparingInt(o->o.getValue().size()));
        clazz.clear();
        sort_list.forEach(o->clazz.add(o.getKey()));
        if(Data.clazz_elements.getOrDefault(config.getConfirmation(),new ArrayList<>()).size() == user_data.size()) {
            clazz.remove(config.getConfirmation());
        } else {
            config.setConfirmation(clazz.get(clazz.size()-1));
            clazz.remove(config.getConfirmation());
        }
        Data.clazz = new ArrayList<>(clazz);
        buildTree();
        try {
            data.close();
        } catch (IOException e) {
            info("excel文件出现IO错误！");
            sendData(e.getLocalizedMessage());
        }
    }

    private static void buildTree() {
        user_data.forEach(data1 -> {
            String name = data1.user_data.get(Data.clazz.get(0));
            DataTreeNode now = new DataTreeNode(DataTreeNode.origin_node, new ArrayList<>(), name, data1);
            for (int i = 1; i < Data.clazz.size(); i++) {
                name = data1.user_data.get(Data.clazz.get(i));
                now = DataTreeNode.addNode(now, i, name, data1);
            }
        });
    }


    public static class DataTreeNode {
        public static DataTreeNode origin_node;
        static {
            origin_node = new DataTreeNode();
        }
        final DataTreeNode father;
        final List<DataTreeNode> children;
        final String name;
        final Data data;

        public static DataTreeNode addNode(DataTreeNode father, int i, String name, Data data1){
            if (Data.clazz.size()-1 != i) {
                data1 = null;
            }
            for (DataTreeNode child : father.children) {
                if (child.name.equals(name)) {
                    return child;
                }
            }
            DataTreeNode e = new DataTreeNode(father, new ArrayList<>(), name, data1);
            father.children.add(e);
            return e;
        }

        public DataTreeNode getFather() {
            return father;
        }

        public String getName() {
            return name;
        }

        public DataTreeNode(DataTreeNode father, List<DataTreeNode> children, String name, Data data) {
            this.father = father;
            this.children = children;
            this.name = name;
            this.data = data;
        }

        DataTreeNode(){
            name = null;
            data = null;
            father = null;
            children = new ArrayList<>();
        }

        public DataTreeNode ask(Player player) throws InterruptedException {
            if(data != null) {
                FirstLoginRegistrar.registering.remove(player);
                return this;
            }
            String ans;
            player.sendMessage("===========================");
            player.sendMessage("请在下方选择你的信息：");
            for (DataTreeNode child : children) {
                if (child.name != null) {
                    player.sendMessage(child.name);
                }
            }
            player.sendMessage("===========================");
            while(FirstLoginRegistrar.registering.get(player).equals("!")){
                Thread.sleep(20);
            }
            ans = FirstLoginRegistrar.registering.get(player);
            FirstLoginRegistrar.registering.put(player,"!");
            if(ans == null) {
                return ask(player);
            }
            for (DataTreeNode child : children) {
                if(Objects.equals(child.name, ans)){
                    return child.ask(player);
                }
            }
            throw new InterruptedException("未能完成注册");
        }

        public Data getData() {
            return data;
        }
    }

    public static class Data {
        public static List<String> clazz = new ArrayList<>();
        public static final Map<String, List<String>> clazz_elements = new HashMap<>();
        private final Map<String, String> user_data = new HashMap<>();
        private final UUID uuid;

        public UUID getUuid(){
            return uuid;
        }

        public Map<String, String> getUser_data(){
            return user_data;
        }

        Data(Row row){
            Core.Core.user_data.add(new Data(row));
            for (int i = 0; i <= row.getLastCellNum(); i++) {
                user_data.put(clazz.get(i),row.getCell(i).getStringCellValue());
                clazz_elements.getOrDefault(clazz.get(i), new ArrayList<>()).add(row.getCell(i).getStringCellValue());
            }
            if(user_data.containsKey("UUID")){
                uuid = UUID.fromString(user_data.get("UUID"));
            } else uuid = UUID.randomUUID();
            row.createCell(row.getLastCellNum()+1).setCellValue(uuid.toString());
        }
    }

    DataReader(){
    }
}
