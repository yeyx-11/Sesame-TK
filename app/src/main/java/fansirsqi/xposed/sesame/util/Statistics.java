package fansirsqi.xposed.sesame.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Data;

import java.io.File;
import java.util.Calendar;

@Data
public class Statistics {

    private static final String TAG = Statistics.class.getSimpleName();
    public static final Statistics INSTANCE = new Statistics();

    private TimeStatistics year = new TimeStatistics();
    private TimeStatistics month = new TimeStatistics();
    private TimeStatistics day = new TimeStatistics();

    /**
     * 增加指定数据类型的统计量
     * @param dt 数据类型（收集、帮助、浇水）
     * @param i 增加的数量
     */
    public static void addData(DataType dt, int i) {
        Statistics stat = INSTANCE;
        switch (dt) {
            case COLLECTED:
                stat.day.collected += i;
                stat.month.collected += i;
                stat.year.collected += i;
                break;
            case HELPED:
                stat.day.helped += i;
                stat.month.helped += i;
                stat.year.helped += i;
                break;
            case WATERED:
                stat.day.watered += i;
                stat.month.watered += i;
                stat.year.watered += i;
                break;
        }
    }

    /**
     * 获取指定时间和数据类型的统计值
     * @param tt 时间类型（年、月、日）
     * @param dt 数据类型（时间、收集、帮助、浇水）
     * @return 统计值
     */
    public static int getData(TimeType tt, DataType dt) {
        Statistics stat = INSTANCE;
        int data = 0;
        TimeStatistics ts = null;
        switch (tt) {
            case YEAR:
                ts = stat.year;
                break;
            case MONTH:
                ts = stat.month;
                break;
            case DAY:
                ts = stat.day;
                break;
        }
        if (ts != null) {
            switch (dt) {
                case TIME:
                    data = ts.time;
                    break;
                case COLLECTED:
                    data = ts.collected;
                    break;
                case HELPED:
                    data = ts.helped;
                    break;
                case WATERED:
                    data = ts.watered;
                    break;
            }
        }
        return data;
    }

    /**
     * 获取统计文本信息
     * @return 包含年、月、日统计信息的字符串
     */
    public static String getText() {
        // 添加表头
        return "今年  收: " + getData(TimeType.YEAR, DataType.COLLECTED) +
                " 帮: " + getData(TimeType.YEAR, DataType.HELPED) +
                " 浇: " + getData(TimeType.YEAR, DataType.WATERED) +
                "\n今月  收: " + getData(TimeType.MONTH, DataType.COLLECTED) +
                " 帮: " + getData(TimeType.MONTH, DataType.HELPED) +
                " 浇: " + getData(TimeType.MONTH, DataType.WATERED) +
                "\n今日  收: " + getData(TimeType.DAY, DataType.COLLECTED) +
                " 帮: " + getData(TimeType.DAY, DataType.HELPED) +
                " 浇: " + getData(TimeType.DAY, DataType.WATERED);
    }

    /**
     * 加载统计数据
     * @return 统计实例
     */
    public static synchronized Statistics load() {
        File statisticsFile = FileUtil.getStatisticsFile();
        try {
            if (statisticsFile.exists()) {
                String json = FileUtil.readFromFile(statisticsFile);
                JsonUtil.copyMapper().readerForUpdating(INSTANCE).readValue(json);
                String formatted = JsonUtil.toFormatJsonString(INSTANCE);
                if (formatted != null && !formatted.equals(json)) {
                    Log.runtime(TAG, "重新格式化 statistics.json");
                    Log.system(TAG, "重新格式化 statistics.json");
                    FileUtil.write2File(formatted, statisticsFile);
                }
            } else {
                JsonUtil.copyMapper().updateValue(INSTANCE, new Statistics());
                Log.runtime(TAG, "初始化 statistics.json");
                Log.system(TAG, "初始化 statistics.json");
                FileUtil.write2File(JsonUtil.toFormatJsonString(INSTANCE), statisticsFile);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
            Log.runtime(TAG, "统计文件格式有误，已重置统计文件");
            Log.system(TAG, "统计文件格式有误，已重置统计文件");
            try {
                JsonUtil.copyMapper().updateValue(INSTANCE, new Statistics());
                FileUtil.write2File(JsonUtil.toFormatJsonString(INSTANCE), FileUtil.getStatisticsFile());
            } catch (JsonMappingException e) {
                Log.printStackTrace(TAG, e);
            }
        }
        return INSTANCE;
    }

    /**
     * 卸载当前统计数据
     */
    public static synchronized void unload() {
        try {
            JsonUtil.copyMapper().updateValue(INSTANCE, new Statistics());
        } catch (JsonMappingException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    /**
     * 保存统计数据
     */
    public static synchronized void save() {
        save(Calendar.getInstance());
    }

    /**
     * 保存统计数据并更新日期
     * @param nowCalendar 当前日期
     */
    public static synchronized void save(Calendar nowCalendar) {
        if (updateDay(nowCalendar)) {
            Log.system(TAG, "重置 statistics.json");
        } else {
            Log.system(TAG, "保存 statistics.json");
        }
        FileUtil.write2File(JsonUtil.toFormatJsonString(INSTANCE), FileUtil.getStatisticsFile());
    }

    /**
     * 更新日期并重置统计数据
     * @param nowCalendar 当前日期
     * @return 如果日期已更改，返回 true；否则返回 false
     */
    public static Boolean updateDay(Calendar nowCalendar) {
        int ye = nowCalendar.get(Calendar.YEAR);
        int mo = nowCalendar.get(Calendar.MONTH) + 1;
        int da = nowCalendar.get(Calendar.DAY_OF_MONTH);
        if (ye != INSTANCE.year.time) {
            INSTANCE.year.reset(ye);
            INSTANCE.month.reset(mo);
            INSTANCE.day.reset(da);
        } else if (mo != INSTANCE.month.time) {
            INSTANCE.month.reset(mo);
            INSTANCE.day.reset(da);
        } else if (da != INSTANCE.day.time) {
            INSTANCE.day.reset(da);
        } else {
            return false;
        }
        return true;
    }

    @Data
    public static class TimeStatistics {
        int time;
        int collected, helped, watered;

        public TimeStatistics() {
        }

        TimeStatistics(int i) {
            reset(i);
        }

        public void reset(int i) {
            time = i;
            collected = 0;
            helped = 0;
            watered = 0;
        }
    }

    public enum TimeType {
        YEAR, MONTH, DAY
    }

    public enum DataType {
        TIME, COLLECTED, HELPED, WATERED
    }
}
