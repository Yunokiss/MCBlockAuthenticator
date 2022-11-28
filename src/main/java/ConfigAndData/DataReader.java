package ConfigAndData;

import Authenticator.FirstLoginRegistrar;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static Core.Core.*;
import static Core.Core.Debug.*;

public class DataReader {

    public static ExcelReaderBuilder erb;

    public static ExcelListener el = new ExcelListener();

    private static boolean has_uuid;

    public static boolean readFile() {
        sendData("readFile called");
        String data_path = config.getData_path();
        File data_file = new File(data_path);
        if (!data_file.exists()) {
            info("Excel do not exist!");
            return false;
        } else {
            erb = EasyExcel.read(data_file, el);
            erb.doReadAllSync();
            DataTreeNode.origin_node.show();
        }
        return true;
    }

    public static void getData() {
        sendData("getData called!");
        List<String> clazz = new ArrayList<>(el.getImportHeads().keySet());
        sendDetail("classes:" + clazz);
        if(clazz.isEmpty())return;
        has_uuid = clazz.contains("UUID");
        Data.clazz = new ArrayList<>(clazz);
        user_data.clear();
        if(config.getConfirmation() == null)
            config.setConfirmation(clazz.get(clazz.size()-1));
        List<JSONObject> dataList = el.getDataList();
        for (JSONObject jsonObject : dataList) {
            new Data(jsonObject);
        }
        if(user_data.isEmpty())return;
        List<Map.Entry<String, List<String>>> sort_list = new ArrayList<>(Data.clazz_elements.entrySet());
        sort_list.sort(Comparator.comparingInt(o->o.getValue().size()));
        clazz.clear();
        sort_list.forEach(o->clazz.add(o.getKey()));
        if(Data.clazz_elements.getOrDefault(config.getConfirmation(),new ArrayList<>()).size() != user_data.size()) {
            config.setConfirmation(clazz.get(clazz.size()-1));
        }
        if(!has_uuid) clazz.add("UUID");
        Data.clazz = new ArrayList<>(clazz);
        buildTree();
        saveData();
    }

    public static void saveData() {
        String data_path = config.getData_path();
        List<List<String>> heads = new ArrayList<>();
        Data.clazz.forEach(o -> heads.add(new ArrayList<>(Set.of(o))));
        List<List<String>> contents = new ArrayList<>();
        user_data.forEach(o -> contents.add(o.toList()));
        try {
            EasyExcel.write(data_path).head(heads).sheet(0).doWrite(contents);
        } catch (Exception e) {
            sendNormalData(e.getLocalizedMessage());
        }
    }

    private static void buildTree() {
        user_data.forEach(data1 -> {
            String name = data1.getUser_data().get(Data.clazz.get(0));
            DataTreeNode now =DataTreeNode.addNode(DataTreeNode.origin_node, 0, name, data1);
            for (int i = 1; i < Data.clazz.size(); i++) {
                sendData(now.name);
                name = data1.getUser_data().get(Data.clazz.get(i));
                now = DataTreeNode.addNode(now, i, name, data1);
            }
        });
    }

    @Getter
    @Setter
    public static class DataTreeNode {
        public static DataTreeNode origin_node;
        static {
            origin_node = new DataTreeNode();
        }

        public static Map<Player, Consumer<DataTreeNode>> done = new HashMap<>();
        final DataTreeNode father;
        final List<DataTreeNode> children;
        final String name;
        final Data data;
        final String type_name;

        public static DataTreeNode addNode(DataTreeNode father, int i, String name, Data data1){
            if (Data.clazz.size()-1 != i) {
                data1 = null;
            }
            for (DataTreeNode child : father.children) {
                if (child.name.equals(name)) {
                    return child;
                }
            }
            DataTreeNode e = new DataTreeNode(father, new ArrayList<>(), name, data1, Data.clazz.get(i));
            father.children.add(e);
            return e;
        }

        public void show(){
            StringBuilder sb = new StringBuilder(name + " children:");
            for (DataTreeNode child : children) {
                sb.append(child.getName()).append(' ');
            }
            sendData(sb.toString());
            children.forEach(DataTreeNode::show);
        }

        public DataTreeNode(DataTreeNode father, List<DataTreeNode> children, String name, Data data, String type_name) {
            this.father = father;
            this.children = children;
            this.name = name;
            this.data = data;
            this.type_name = type_name;
        }

        DataTreeNode(){
            name = "First";
            data = null;
            father = null;
            children = new ArrayList<>();
            type_name = "All";
        }

        public DataTreeNode ask(Player player) throws InterruptedException {
            if(data != null) {
                FirstLoginRegistrar.registering.remove(player);
                sendData("choose done");
                done.get(player).accept(this);
                return this;
            }
            if(children.size()==1){
                sendData("skip choose");
                FirstLoginRegistrar.registering.put(player,children.get(0));
                return children.get(0).ask(player);
            }
            try {
                //noinspection ResultOfMethodCallIgnored
                UUID.fromString(children.get(0).name);
            } catch (IllegalArgumentException e){
                player.sendMessage("===========================");
                player.sendMessage("请在下方选择你的信息,并输入它：");
                for (DataTreeNode child : children) {
                    if (child.name != null) {
                        player.sendMessage(child.name);
                    }
                }
                player.sendMessage("===========================");
                return null;
            }
            player.sendMessage("===========================");
            player.sendMessage("请在下方选择你的信息,并输入它：");
            for (DataTreeNode child : children) {
                if (child.getChildren().get(0).name != null) {
                    player.sendMessage(child.getChildren().get(0).name);
                }
            }
            player.sendMessage("===========================");
            return null;

        }

        public String getName(String confirmation) {
            if(type_name.equals(confirmation)) return name;
            else if (father != null) {
                return father.getName(confirmation);
            } else return null;
        }
    }

    @Getter
    @Setter
    public static class Data {
        public static List<String> clazz = new ArrayList<>();
        public static final Map<String, List<String>> clazz_elements = new HashMap<>();
        private final Map<String, String> user_data = new HashMap<>();
        private final UUID uuid;

        public Data(JSONObject row){
            Core.Core.user_data.add(this);
            for (int i = 0; i < row.size(); i++) {
                @SuppressWarnings("SuspiciousMethodCalls")
                String s = (String) row.get(i);
                user_data.put(clazz.get(i), s);
                List<String> list = clazz_elements.getOrDefault(clazz.get(i), new ArrayList<>());
                if(!list.contains(s))
                    list.add(s);
                clazz_elements.put(clazz.get(i), list);
                sendData(clazz_elements.toString());
            }
            if(has_uuid){
                uuid = UUID.fromString(user_data.get("UUID"));
            } else {
                uuid = UUID.randomUUID();
                user_data.put("UUID",uuid.toString());
            }

        }

        public List<String> toList() {
            List<String> r = new ArrayList<>();
            clazz.forEach(o -> r.add(user_data.get(o)));
            return r;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    @Setter
    public static class ExcelListener extends AnalysisEventListener<Object> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ExcelListener.class);
        /**
         * 自定义用于暂时存储data
         */
        private List<JSONObject> dataList = new ArrayList<>();

        /**
         * 导入表头
         */
        private Map<String, Integer> importHeads = new HashMap<>(16);

        /**
         * 这个每一条数据解析都会来调用
         */
        @Override
        public void invoke(Object data, AnalysisContext context) {
            String headStr = JSON.toJSONString(data);
            dataList.add(JSONObject.parseObject(headStr));
        }

        /**
         * 这里会一行行的返回头
         */
        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            for (Integer key : headMap.keySet()) {
                if (importHeads.containsKey(headMap.get(key))) {
                    continue;
                }
                importHeads.put(headMap.get(key), key);
            }
        }

        /**
         * 所有数据解析完成了 都会来调用
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            LOGGER.info("Excel解析完毕");
        }
    }

    DataReader(){
    }
}
