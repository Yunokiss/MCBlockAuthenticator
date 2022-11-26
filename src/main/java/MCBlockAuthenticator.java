import Config.ConfigProcessor;
import org.bukkit.Server;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;

import java.util.logging.Logger;


public class MCBlockAuthenticator extends Addon {

    public static ConfigProcessor.Config config = null;
    public static ConfigProcessor.RegisteredUser registered_user = null;
    public static Debug debug = Debug.NO_DEBUG;
    public static Server t = null;
    public static Logger logger = null;

    public void init(){
        logger = getLogger();
        Debug.sendData("Logger got!");

        Debug.sendDetail("MCBlockAuthenticator.init() called!");

        t = this.getServer();
        Debug.sendData("Server got!");

        config = new Config<>(this,ConfigProcessor.Config.class).loadConfigObject();
        if (config != null) {
            debug = Debug.getByLevel(config.getDEBUG_LEVEL());
        } else {
            Debug.sendSimpleData("config读取错误！");
            throw new ExceptionInInitializerError("在初始化时发生错误");
        }
        Debug.sendData("Config got!");

        registered_user = new Config<>(this,ConfigProcessor.RegisteredUser.class).loadConfigObject();
        if (registered_user == null) {
            Debug.sendSimpleData("registered_user读取错误！");
            throw new ExceptionInInitializerError("在初始化时发生错误");
        }
        Debug.sendData("registered_user got!");
    }

    /**
     * 控制台可以接收到的Debug信息等级。由0-4级越来越多。
     */
    enum Debug {
        ALL_DATA(4),
        DETAIL(3),
        NORMAL_DATA(2),
        SIMPLE_DATA(1),
        NO_DEBUG(0);
        final int level;

        Debug(int level) {
            this.level = level;
        }

        public static Debug getByLevel(int debug_level) {
            switch (debug_level) {
                case 0 -> {
                    return NO_DEBUG;
                }
                case 1 -> {
                    return SIMPLE_DATA;
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

        public static void sendSimpleData(String mes){
            if (debug != NO_DEBUG) logger.info(mes);
        }

        public static void sendNormalData(String mes){
            if (debug != NO_DEBUG && debug != SIMPLE_DATA) logger.info(mes);
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
