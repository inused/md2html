package me.learn.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 获取当前jar包的路径相关信息
 * @author i
 */
public class PathUtils {
    private static final File JAR_FILE = getThisFile();
    /** jar包绝对路径 */
    public static final String JAR_PATH = JAR_FILE.getAbsolutePath();
    /** jar包所在目录绝对路径 */
    public static final String JAR_DIR = getFileDir(JAR_FILE);
    /** jar包名 */
    public static final String JAR_NAME = JAR_FILE.getName();

    /**
     * 从File对象中获取所在目录绝对路径
     * @param filePath 文件路径
     * @return 文件获取所在目录
     */
    public static String getFileDir(String filePath) {
        return getFileDir(new File(filePath));
    }

    /**
     * 从File对象中获取所在目录
     * @param file File对象
     * @return 文件获取所在目录
     */
    public static String getFileDir(File file) {
        return file.getAbsoluteFile().getParent();
    }

    /**
     * 获取当前jar包的File对象
     * @return 当前jar包的File对象
     */
    private static File getThisFile() {
        // 获取jar包路径
        String path = PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // 处理中文和空格
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("处理路径中的中文和空格时出错");
            e.printStackTrace();
        }
        // 创建File对象
        return new File(path).getAbsoluteFile();
    }

    /**
     * 读取文件返回其内容
     * @param src 文件路径
     * @return 文件内容
     */
    public static String getText(String src) {
        String srcText = "";
        try (InputStream fis = ThymeleafUtils.class.getResourceAsStream(src);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int len;
            byte[] buffer = new byte[4096];
            // 读取文件
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 转为字符串
            srcText = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            //srcText = baos.toString();
        } catch (Exception e) {
            System.out.println("读取文件 " + src + " 失败");
            e.printStackTrace();
        }
        return srcText;
    }
}
