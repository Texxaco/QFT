package net.contexx.qft.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

public class QFTSettings {

    public static final String PREFIX = "qft.";

    public static final Setting<String> FREEZER_FILE_NAME = new Setting<>("freezer.file.name", "freezer.qft.json", s -> s);
    public static final Setting<String> FREEZER_FILE_TYPE = new Setting<>("freezer.file.type", "json", s -> s);
    public static final Setting<Boolean> ACCEPT_ALL = new Setting<>("acceptAll", false, Boolean::valueOf);

    public static class Setting<T> {

        private final String key;
        private final T defaultValue;
        private final Function<String, T> converter;

        Setting(String key, T defaultValue, Function<String, T> converter) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.converter = converter;
        }

        public String getKey() {
            return key;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public T valueOf(String value) {
            if(value == null) return null;
            return converter.apply(value);
        }
    }

    private static final Properties properties = new Properties();
    static {
        final InputStream ressourceStream = QFTSettings.class.getClassLoader().getResourceAsStream("qft.properties");
        if(ressourceStream != null) {
            try {
                properties.load(
                        ressourceStream
                );
            } catch (IOException e) { /*do nothing*/ }
        }
    }

//    public static String getQFTFileName(){
//        return getProperty(FREEZER_FILE_NAME);
//    }
//
//    public static String getQFTFileType(){
//        return getProperty("freeser.file.type", "json");
//    }
//
//    public static boolean acceptAll(){
//        return Boolean.valueOf(getProperty("acceptAll", "false"));
//    }

    public static <T> T getProperty(Setting<T> setting) {
        String result = System.getProperty(PREFIX + setting.key);
        if(result == null) result = properties.getProperty(setting.key);
        if(result == null) return setting.defaultValue;
        return setting.converter.apply(result);
    }

    public static <T> T getProperty(Setting<T> setting, Function<String, String>...suppliers) {
        String result = System.getProperty(PREFIX + setting.key);
        if(result == null) result = properties.getProperty(setting.key);

        if(result == null) {
            for (Function<String, String> supplier : suppliers) {
                if(result == null) result = supplier.apply(PREFIX + setting.key);
            }
        }

        if(result == null) return setting.defaultValue;
        return setting.converter.apply(result);
    }

    public static boolean is(Setting<Boolean> setting) {
        return getProperty(setting);
    }

}
