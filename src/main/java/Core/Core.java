package Core;

import ConfigAndData.ConfigProcessor;
import ConfigAndData.DataReader;
import org.bukkit.Server;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static ConfigAndData.DataReader.*;
import static Core.Core.Debug.info;


public class Core extends Addon {

    public static ConfigProcessor.Config config = null;
    public static ConfigProcessor.RegisteredUser registered_user = null;
    public static Debug debug = Debug.MUTE;
    public static Server ts = null;
    public static Addon ta = null;
    public static Logger logger = null;
    public static List<DataReader.Data> user_data = new ArrayList<>();

    public void init(){
        logger = getLogger();
        Debug.sendData("Logger got!");

        Debug.sendDetail("Core.Core.init() called!");

        ts = this.getServer();
        Debug.sendData("Server got!");

        ta = this;
        Debug.sendData("This addon got!");

        config = new Config<>(this,ConfigProcessor.Config.class).loadConfigObject();
        if (config != null) {
            debug = Debug.getByLevel(config.getDEBUG_LEVEL());
        } else {
            info("config读取错误！");
            throw new ExceptionInInitializerError("在初始化时发生错误");
        }
        Debug.sendData("Config got!");

        registered_user = new Config<>(this,ConfigProcessor.RegisteredUser.class).loadConfigObject();
        if (registered_user == null) {
            info("registered_user读取错误！");
            throw new ExceptionInInitializerError("在初始化时发生错误");
        }
        Debug.sendData("registered_user got!");

        info("开始读取用户信息！");
        readFile();
        getData();



    }

    /**
     * 控制台可以接收到的Debug信息等级。由0-4级越来越多。
     */
    public enum Debug {
        ALL_DATA(4),
        DETAIL(3),
        NORMAL_DATA(2),
        INFO_ONLY(1),
        MUTE(0);
        final int level;

        Debug(int level) {
            this.level = level;
        }

        public static Debug getByLevel(int debug_level) {
            switch (debug_level) {
                case 0 -> {
                    return MUTE;
                }
                case 1 -> {
                    return INFO_ONLY;
                }
                case 2 -> {
                    return NORMAL_DATA;
                }
                case 3 -> {
                    return DETAIL;
                }
                case 4 -> {
                    return ALL_DATA;
                }
            }
            throw new IllegalArgumentException("未知的debug等级");
        }

        public static void info(String mes){
            if (debug != MUTE) logger.info(mes);
        }

        public static void sendNormalData(String mes){
            if (debug != MUTE && debug != INFO_ONLY) logger.info(mes);
        }

        public static void sendDetail(String mes){
            if (debug == DETAIL || debug == ALL_DATA) logger.info(mes);
        }

        public static void sendData(String mes){
            if (debug == ALL_DATA) logger.info(mes);
        }
    }

    @Override
    public void onEnable() {
        init();

    }

    @Override
    public void onDisable() {

    }
}
