package fansirsqi.xposed.sesame.util;

import android.content.Context;
import android.os.Environment;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
  private static final String TAG = FileUtil.class.getSimpleName();

  public static final String CONFIG_DIRECTORY_NAME = "sesame";
  public static final File MAIN_DIRECTORY_FILE = getMainDirectoryFile();
  public static final File CONFIG_DIRECTORY_FILE = getConfigDirectoryFile();
  public static final File LOG_DIRECTORY_FILE = getLogDirectoryFile();
  private static File cityCodeFile;
  private static File wuaFile;
  private  Context context;


  private static File getMainDirectoryFile() {
    String storageDirStr = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "media" + File.separator + ClassUtil.PACKAGE_NAME;
    File storageDir = new File(storageDirStr);
    File mainDir = new File(storageDir, CONFIG_DIRECTORY_NAME);
    if (mainDir.exists()) {
      if (mainDir.isFile()) {
        mainDir.delete();
        mainDir.mkdirs();
      }
    } else {
      mainDir.mkdirs();
    }
    return mainDir;
  }

  private static File getLogDirectoryFile() {
    File logDir = new File(MAIN_DIRECTORY_FILE, "log");
    if (logDir.exists()) {
      if (logDir.isFile()) {
        logDir.delete();
        logDir.mkdirs();
      }
    } else {
      logDir.mkdirs();
    }
    return logDir;
  }

  private static File getConfigDirectoryFile() {
    File configDir = new File(MAIN_DIRECTORY_FILE, "config");
    if (configDir.exists()) {
      if (configDir.isFile()) {
        configDir.delete();
        configDir.mkdirs();
      }
    } else {
      configDir.mkdirs();
    }
    return configDir;
  }

  public static File getUserConfigDirectoryFile(String userId) {
    File configDir = new File(CONFIG_DIRECTORY_FILE, userId);
    if (configDir.exists()) {
      if (configDir.isFile()) {
        configDir.delete();
        configDir.mkdirs();
      }
    } else {
      configDir.mkdirs();
    }
    return configDir;
  }

  public static File getDefaultConfigV2File() {
    return new File(MAIN_DIRECTORY_FILE, "config_v2.json");
  }

  public static boolean setDefaultConfigV2File(String json) {
    return write2File(json, new File(MAIN_DIRECTORY_FILE, "config_v2.json"));
  }

  public static File getConfigV2File(String userId) {
    File file = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "config_v2.json");
    if (!file.exists()) {
      File oldFile = new File(CONFIG_DIRECTORY_FILE, "config_v2-" + userId + ".json");
      if (oldFile.exists()) {
        if (write2File(readFromFile(oldFile), file)) {
          oldFile.delete();
        } else {
          file = oldFile;
        }
      }
    }
    return file;
  }

  public static boolean setConfigV2File(String userId, String json) {
    return write2File(json, new File(CONFIG_DIRECTORY_FILE + "/" + userId, "config_v2.json"));
  }

  public static boolean setUIConfigFile(String json) {
    return write2File(json, new File(MAIN_DIRECTORY_FILE, "ui_config.json"));
  }

  public static File getSelfIdFile(String userId) {
    File file = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "self.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getFriendIdMapFile(String userId) {
    File file = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "friend.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File runtimeInfoFile(String userId) {
    File runtimeInfoFile = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "runtimeInfo.json");
    if (!runtimeInfoFile.exists()) {
      try {
        runtimeInfoFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return runtimeInfoFile;
  }

  public static File getCooperationIdMapFile(String userId) {
    File file = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "cooperation.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getStatusFile(String userId) {
    File file = new File(CONFIG_DIRECTORY_FILE + "/" + userId, "status.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getStatisticsFile() {
    File statisticsFile = new File(MAIN_DIRECTORY_FILE, "statistics.json");
    if (statisticsFile.exists() && statisticsFile.isDirectory()) {
      statisticsFile.delete();
    }
    if (statisticsFile.exists()) {
      Log.runtime(TAG, "[statistics]读:" + statisticsFile.canRead() + ";写:" + statisticsFile.canWrite());
    } else {
      Log.runtime(TAG, "statisticsFile.json文件不存在");
    }
    return statisticsFile;
  }

  public static File getReserveIdMapFile() {
    File file = new File(MAIN_DIRECTORY_FILE, "reserve.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getBeachIdMapFile() {
    File file = new File(MAIN_DIRECTORY_FILE, "beach.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getUIConfigFile() {
    File file = new File(MAIN_DIRECTORY_FILE, "ui_config.json");
    if (file.exists() && file.isDirectory()) {
      file.delete();
    }
    return file;
  }

  public static File getExportedStatisticsFile() {
    String storageDirStr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + CONFIG_DIRECTORY_NAME;
    File storageDir = new File(storageDirStr);
    if (!storageDir.exists()) {
      storageDir.mkdirs();
    }
    File exportedStatisticsFile = new File(storageDir, "statistics.json");
    if (exportedStatisticsFile.exists() && exportedStatisticsFile.isDirectory()) {
      exportedStatisticsFile.delete();
    }
    return exportedStatisticsFile;
  }

  public static File getFriendWatchFile() {
    File friendWatchFile = new File(MAIN_DIRECTORY_FILE, "friendWatch.json");
    if (friendWatchFile.exists() && friendWatchFile.isDirectory()) {
      friendWatchFile.delete();
    }
    return friendWatchFile;
  }

  public static File getWuaFile() {
    if (wuaFile == null) {
      wuaFile = new File(MAIN_DIRECTORY_FILE, "wua.list");
    }
    return wuaFile;
  }

  public static File exportFile(File file) {
    String exportDirStr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + CONFIG_DIRECTORY_NAME;
    File exportDir = new File(exportDirStr);
    if (!exportDir.exists()) {
      exportDir.mkdirs();
    }
    File exportFile = new File(exportDir, file.getName());
    if (exportFile.exists() && exportFile.isDirectory()) {
      exportFile.delete();
    }
    if (FileUtil.copyTo(file, exportFile)) {
      return exportFile;
    }
    return null;
  }

  public static File getCityCodeFile() {
    if (cityCodeFile == null) {
      cityCodeFile = new File(MAIN_DIRECTORY_FILE, "cityCode.json");
      if (cityCodeFile.exists() && cityCodeFile.isDirectory()) {
        cityCodeFile.delete();
      }
    }
    return cityCodeFile;
  }

  public static File getRuntimeLogFile() {
    File runtimeLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("runtime"));
    if (runtimeLogFile.exists() && runtimeLogFile.isDirectory()) {
      runtimeLogFile.delete();
    }
    if (!runtimeLogFile.exists()) {
      try {
        runtimeLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return runtimeLogFile;
  }

  public static File getRecordLogFile() {
    File recordLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("record"));
    if (recordLogFile.exists() && recordLogFile.isDirectory()) {
      recordLogFile.delete();
    }
    if (!recordLogFile.exists()) {
      try {
        recordLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return recordLogFile;
  }

  public static File getSystemLogFile() {
    File systemLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("system"));
    if (systemLogFile.exists() && systemLogFile.isDirectory()) {
      systemLogFile.delete();
    }
    if (!systemLogFile.exists()) {
      try {
        systemLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return systemLogFile;
  }

  public static File getDebugLogFile() {
    File debugLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("debug"));
    if (debugLogFile.exists() && debugLogFile.isDirectory()) {
      debugLogFile.delete();
    }
    if (!debugLogFile.exists()) {
      try {
        debugLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return debugLogFile;
  }

  public static File getForestLogFile() {
    File forestLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("forest"));
    if (forestLogFile.exists() && forestLogFile.isDirectory()) {
      forestLogFile.delete();
    }
    if (!forestLogFile.exists()) {
      try {
        forestLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return forestLogFile;
  }

  public static File getFarmLogFile() {
    File farmLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("farm"));
    if (farmLogFile.exists() && farmLogFile.isDirectory()) {
      farmLogFile.delete();
    }
    if (!farmLogFile.exists()) {
      try {
        farmLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return farmLogFile;
  }

  public static File getOtherLogFile() {
    File otherLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("other"));
    if (otherLogFile.exists() && otherLogFile.isDirectory()) {
      otherLogFile.delete();
    }
    if (!otherLogFile.exists()) {
      try {
        otherLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return otherLogFile;
  }

  public static File getErrorLogFile() {
    File errorLogFile = new File(LOG_DIRECTORY_FILE, Log.getLogFileName("error"));
    if (errorLogFile.exists() && errorLogFile.isDirectory()) {
      errorLogFile.delete();
    }
    if (!errorLogFile.exists()) {
      try {
        errorLogFile.createNewFile();
      } catch (Throwable ignored) {
      }
    }
    return errorLogFile;
  }

  public static void clearLog() {
    File[] files = LOG_DIRECTORY_FILE.listFiles();
    if (files == null) {
      return;
    }
    SimpleDateFormat sdf = Log.DATE_FORMAT_THREAD_LOCAL.get();
    if (sdf == null) {
      sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    String today = sdf.format(new Date());
    for (File file : files) {
      String name = file.getName();
      if (name.endsWith(today + ".log")) {
        if (file.length() < 104_857_600) {
          continue;
        }
      }
      try {
        file.delete();
      } catch (Exception e) {
        Log.printStackTrace(e);
      }
    }
  }


  /**
   * 关闭流对象
   *
   * @param c 要关闭的流对象
   */
  public static void close(Closeable c) {
    try {
      if (c != null) c.close(); // 关闭流
    } catch (Throwable t) {
      // 捕获并打印关闭流时的异常
      Log.printStackTrace(TAG, t);
    }
  }

  /**
   * 从文件中读取内容
   *
   * @param f 要读取的文件
   * @return 文件内容，如果读取失败或没有权限，返回空字符串
   */
  public static String readFromFile(File f) {
    // 检查文件是否存在
    if (!f.exists()) {
      return "";
    }
    // 检查文件是否可读
    if (!f.canRead()) {
//      Toast.show(f.getName() + "没有读取权限！", true);
      ToastUtil.showToast(f.getName()+"没有读取权限！");
      return "";
    }
    StringBuilder result = new StringBuilder();
    FileReader fr = null;
    try {
      // 使用 FileReader 读取文件内容
      fr = new FileReader(f);
      char[] chs = new char[1024];
      int len;
      // 按块读取文件内容
      while ((len = fr.read(chs)) >= 0) {
        result.append(chs, 0, len);
      }
    } catch (Throwable t) {
      // 捕获并记录异常
      Log.printStackTrace(TAG, t);
    } finally {
      // 关闭文件流
      close(fr);
    }
    return result.toString();
  }

  /**
   * 将字符串写入文件
   *
   * @param s 要写入的字符串
   * @param f 目标文件
   * @return 写入是否成功
   */
  public static boolean write2File(String s, File f) {
    // 文件已存在，检查是否有写入权限
    if (f.exists()) {
      if (!f.canWrite()) {
//        Toast.show(f.getAbsoluteFile() + "没有写入权限！", true);
        ToastUtil.showToast(f.getAbsoluteFile()+"没有写入权限！");
        return false;
      }
      // 如果是目录，则删除并重新创建文件
      if (f.isDirectory()) {
        f.delete();
        f.getParentFile().mkdirs();
      }
    } else {
      // 文件不存在，创建父目录
      f.getParentFile().mkdirs();
    }
    boolean success = false;
    FileWriter fw = null;
    try {
      // 使用 FileWriter 写入文件
      fw = new FileWriter(f);
      fw.write(s);
      fw.flush();
      success = true;
    } catch (Throwable t) {
      // 捕获并记录异常
      Log.printStackTrace(TAG, t);
    } finally {
      // 关闭文件流
      close(fw);
    }
    return success;
  }

  /**
   * 将字符串追加到文件末尾
   *
   * @param s 要追加的字符串
   * @param f 目标文件
   * @return 追加是否成功
   */
  public static boolean append2File(String s, File f) {
    // 文件已存在，检查是否有写入权限
    if (f.exists() && !f.canWrite()) {
//      Toast.show(f.getAbsoluteFile() + "没有写入权限！", true);
      ToastUtil.showToast(f.getAbsoluteFile()+"没有写入权限！");
      return false;
    }
    boolean success = false;
    FileWriter fw = null;
    try {
      // 使用 FileWriter 追加内容到文件末尾
      fw = new FileWriter(f, true);
      fw.append(s);
      fw.flush();
      success = true;
    } catch (Throwable t) {
      // 捕获并记录异常
      Log.printStackTrace(TAG, t);
    } finally {
      // 关闭文件流
      close(fw);
    }
    return success;
  }



  /**
   * 将源文件的内容复制到目标文件
   *
   * @param source 源文件
   * @param dest 目标文件
   * @return 如果复制成功返回 true，否则返回 false
   */
  public static boolean copyTo(File source, File dest) {
    // 使用 try-with-resources 来自动管理 FileInputStream 和 FileOutputStream 以及 FileChannel 的关闭
    try (
            FileInputStream fileInputStream = new FileInputStream(source);
            FileOutputStream fileOutputStream = new FileOutputStream(createFile(dest));
            FileChannel inputChannel = fileInputStream.getChannel();
            FileChannel outputChannel = fileOutputStream.getChannel()
    ) {
      // 将源文件的内容传输到目标文件
      outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
      return true;  // 复制成功
    } catch (IOException e) {
      // 捕获并打印文件操作中的异常
      Log.printStackTrace(e);
    }
    return false;  // 复制失败
  }



  /**
   * 将输入流（source）中的数据拷贝到输出流（dest）中。
   * 会循环读取输入流的数据并写入输出流，直到读取完毕。
   * 最终关闭输入输出流。
   *
   * @param source 输入流
   * @param dest 输出流
   * @return 如果数据拷贝成功，返回 true；如果发生 IO 异常或拷贝失败，返回 false
   */
  public static boolean streamTo(InputStream source, OutputStream dest) {
    byte[] buffer = new byte[1024]; // 创建一个缓冲区，每次读取 1024 字节
    int length;

    try {
      // 循环读取输入流中的数据并写入输出流
      while ((length = source.read(buffer)) > 0) {
        dest.write(buffer, 0, length); // 写入数据到输出流
        dest.flush(); // 强制将数据从输出流刷新到目的地
      }
      return true; // 成功拷贝数据
    } catch (IOException e) {
      // 捕获 IO 异常并打印堆栈信息
      Log.printStackTrace(e);
    } finally {
      // 关闭输入流和输出流
      closeStream(source);
      closeStream(dest);
    }
    return false; // 拷贝失败或发生异常
  }

  /**
   * 关闭流并处理可能发生的异常
   *
   * @param stream 需要关闭的流对象
   */
  private static void closeStream(AutoCloseable stream) {
    if (stream != null) {
      try {
        stream.close(); // 关闭流
      } catch (Exception e) {
        // 捕获并打印关闭流时的异常
        Log.printStackTrace(e);
      }
    }
  }




  /**
   * 创建一个文件，如果文件已存在且是目录，则先删除该目录再创建文件。
   * 如果文件不存在，则会先创建父目录，再创建该文件。
   *
   * @param file 需要创建的文件对象
   * @return 创建成功返回文件对象；如果创建失败或发生异常，返回 null
   */
  public static File createFile(File file) {
    // 如果文件已存在且是目录，则先删除该目录
    if (file.exists() && file.isDirectory()) {
      // 如果删除目录失败，返回 null
      if (!file.delete()) return null;
    }
    // 如果文件不存在，则尝试创建文件
    if (!file.exists()) {
      try {
        // 获取父目录文件对象
        File parentFile = file.getParentFile();
        if (parentFile != null) {
          // 如果父目录不存在，则创建父目录
          boolean ignore = parentFile.mkdirs();
        }
        // 创建新的文件
        // 如果文件创建失败，返回 null
        if (!file.createNewFile()) return null;
      } catch (Exception e) {
        // 捕获异常并打印堆栈信息
        Log.printStackTrace(e);
        return null;
      }
    }
    // 文件已存在或成功创建，返回文件对象
    return file;
  }


  public static File createDirectory(File file) {
    if (file.exists() && file.isFile()) {
      if (!file.delete()) {
        return null;
      }
    }
    if (!file.exists()) {
      try {
        if (!file.mkdirs()) {
          return null;
        }
      } catch (Exception e) {
        Log.printStackTrace(e);
        return null;
      }
    }
    return file;
  }

  /**
   * 清空文件内容, 并返回是否清空成功
   *
   * @param file 文件
   * @return 是否清空成功
   */
  public static Boolean clearFile(File file) {
    // 检查文件是否存在
    if (file.exists()) {
      FileWriter fileWriter = null;
      try {
        // 使用 FileWriter 清空文件内容
        fileWriter = new FileWriter(file);
        fileWriter.write(""); // 写入空字符串，清空文件内容
        fileWriter.flush(); // 刷新缓存，确保内容写入文件
        return true; // 返回清空成功
      } catch (IOException e) {
        // 发生 IO 异常时打印堆栈信息
        Log.printStackTrace(e);
      } finally {
        // 确保 FileWriter 在操作完成后关闭，防止资源泄露
        try {
          if (fileWriter != null) {
            fileWriter.close(); // 关闭文件写入流
          }
        } catch (IOException e) {
          // 如果关闭流时发生异常，打印堆栈信息
          Log.printStackTrace(e);
        }
      }
    }
    // 如果文件不存在，则返回 false
    return false;
  }

  /**
   * 删除文件或目录（包括子文件和子目录）。如果是目录，则递归删除其中的所有文件和目录。
   *
   * @param file 要删除的文件或目录
   * @return 如果删除成功返回 true，失败返回 false
   */
  public static Boolean deleteFile(File file) {
    // 如果文件或目录不存在，则返回删除失败
    if (!file.exists()) return false;

    // 如果是文件，直接删除文件
    if (file.isFile()) return file.delete();

    // 如果是目录，获取目录下的所有文件和子目录
    File[] files = file.listFiles();

    // 如果目录为空或无法列出文件，尝试删除目录
    if (files == null) return file.delete();

    // 遍历所有文件和子目录，递归调用 deleteFile 删除
    for (File innerFile : files) {
      // 如果递归删除失败，返回 false
      if (!deleteFile(innerFile)) return false;
    }

    // 删除空目录
    return file.delete();
  }
}
