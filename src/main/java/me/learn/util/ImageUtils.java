package me.learn.util;

import me.learn.App;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 图片工具类，主要提供将图片转换为Base64编码、十六进制、二进制数据，获取图片类型的功能
 *
 * @author i
 */
@SuppressWarnings("unused")
public class ImageUtils {
    /** 图片十六进制文件头与图片类型对应关系 */
    private static final Map<String, String> IMG_TYPE = new HashMap<>();
    private static final String PATH_PRE_HTTP = "http:";
    private static final String PATH_PRE_HTTPS = "https:";
    private static final String PATH_PRE_FTP = "ftp:";
    private static final String PATH_PRE_FILE = "file:";
    private static final String PATH_PRE_BASE64 = "data:image";
    static {
        IMG_TYPE.put("jpg", "FFD8FF");
        IMG_TYPE.put("png", "89504E47");
        IMG_TYPE.put("bmp", "424D");
        IMG_TYPE.put("gif", "47494638");
        IMG_TYPE.put("webp", "52494646");
    }

    public static void main(String[] args) {
        writehtml("D:\\Program\\UserData\\Desktop\\111.html",img2Base64Uri("D:\\Program\\UserData\\Desktop\\thymeleaf~1.png"));
    }

    /**
     * 将base64编码的图片写入到HTML文件
     *
     * @param htmlPath     目标HTML文件路径
     * @param imgBase64Uri 要添加的图片的base64编码数据
     */
    public static void writehtml(String htmlPath, String imgBase64Uri) {
        File hFile = new File(htmlPath);
        try (PrintWriter printWriter = new PrintWriter(hFile)) {
            String htmlStr = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "\t<img src=\"" + imgBase64Uri + "\">\n" +
                    "</body>\n" +
                    "</html>";
            printWriter.println(htmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对图片进行 Base64编码 获取图片的URI
     *
     * @param imgSrc 图片地址
     * @return 图片的Base64编码
     */
    public static String img2Base64Uri(String imgSrc) {
        // 读取图片转为二进制数据
        byte[] imgBytes = img2Bytes(imgSrc);
        // 进行Base64编码
        String imgBase64Str = img2Base64(imgBytes);
        // 获取图片的类型
        String imgType = getImgType(imgBytes);
        return img2Base64Uri(imgBase64Str, imgType);
    }

    /**
     * 拼接图片的Base64编码
     *
     * @param imgBase64Str 图片Base64编码
     * @param imgType      图片类型
     * @return 图片的Base64编码
     */
    public static String img2Base64Uri(String imgBase64Str, String imgType) {
        if (imgBase64Str == null || imgBase64Str.length() == 0) {
            return "";
        }
        if (imgType == null || imgType.length() == 0) {
            imgType = "jpg";
        }
        return "data:image/" + imgType + ";base64," + imgBase64Str;
    }

    /**
     * 对图片进行 Base64编码
     *
     * @param imgSrc 图片地址
     * @return 图片的Base64编码
     */
    public static String img2Base64(String imgSrc) {
        if (imgSrc != null && imgSrc.length() > 0) {
            // 本身就是Base64编码
            if (imgSrc.toLowerCase().startsWith(PATH_PRE_BASE64)) {
                return imgSrc;
            }
            // 读取图片转为二进制数据 并进行Base64编码
            return img2Base64(img2Bytes(imgSrc));
        }
        return "";
    }

    /**
     * 对图片进行 Base64编码
     *
     * @param imgBytes 图片的二进制数据
     * @return 图片的Base64编码
     */
    public static String img2Base64(byte[] imgBytes) {
        String imgBase64Str = "";
        if (imgBytes != null && imgBytes.length > 0) {
            // 进行Base64编码
            imgBase64Str = new String(Base64.encodeBase64(imgBytes));
        }
        return imgBase64Str;
    }

    /**
     * 图片转二进制数据
     *
     * @param imgSrc 图片地址(http: https: ftp: file: /)
     * @return 图片的二进制数据
     */
    public static byte[] img2Bytes(String imgSrc) {
        // 图片的二进制数据
        byte[] imgBytes = new byte[0];
        // 判空
        if (imgSrc != null && imgSrc.length() > 0) {
            // 最终会使用的图片路径
            String imgPath = imgSrc;
            // 图片路径转小写
            imgSrc = imgSrc.toLowerCase();
            try {
                // 网络图片
                if (imgSrc.startsWith(PATH_PRE_HTTP) || imgSrc.startsWith(PATH_PRE_HTTPS) || imgSrc.startsWith(PATH_PRE_FTP)) {
                    // 下载图片转为二进制数组信息
                    imgBytes = downImg2Bytes(imgPath);
                } else if (imgSrc.startsWith(PATH_PRE_FILE)) {
                    // 图片地址为指向本地的url格式
                    URI uri = URI.create(imgPath);
                    URL url = uri.toURL();
                    // 获取图片本地路径
                    imgPath = url.getFile();
                    // 读取本地图片转为二进制数组
                    imgBytes = readImg2Bytes(imgPath);
                } else {
                    // 大概率就是本地路径 读取本地图片转为二进制数组
                    File imgFile = new File(imgPath);
                    if (!imgFile.exists()) {
                        // 如果文件不存在,可能就是相对路径,拼接上md文件的路径
                        imgPath = App.mdFileDir + File.separator + imgPath;
                    }
                    imgBytes = readImg2Bytes(imgPath);
                }
            } catch (Exception e) {
                System.out.println("获取本地图片路径失败");
                e.printStackTrace();
            }
        }
        return imgBytes;
    }

    /**
     * 读取本地图片文件转为byte[]
     *
     * @param imgPath 图片路径
     * @return 图片的byte[]
     */
    public static byte[] readImg2Bytes(String imgPath) {
        byte[] imgBytes = new byte[0];
        try (InputStream is = new FileInputStream(imgPath);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len=is.read(buffer)) > -1) {
                // 读取到输出流
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            imgBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("读取本地图片转换为byte[]失败: ");
            e.printStackTrace();
        }
        return imgBytes;
    }

    /**
     * 下载图片获取图片的byte[]
     *
     * @param imgSrc 图片网址
     * @return 图片的byte[]
     */
    public static byte[] downImg2Bytes(String imgSrc) {
        byte[] imgBytes = new byte[0];
        InputStream inStream = null;
        HttpURLConnection conn = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // 创建url
            URL imgUrl = new URL(imgSrc);
            // 创建链接
            conn = (HttpURLConnection) imgUrl.openConnection();
            // 添加User-Agent
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
            //conn
            // 连接
            conn.connect();
            // 获取输入流
            inStream = conn.getInputStream();
            byte[] bytes = new byte[4096];
            int len;
            while ((len = inStream.read(bytes)) > -1) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            byteArrayOutputStream.flush();
            // 数据读取到byte[]中
            imgBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("下载图片获取输入流失败");
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                    System.out.println("下载图片获取输入流时关流失败");
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return imgBytes;
    }

    /**
     * 通过图片路径获取图片的类型
     *
     * @param imgSrc 图片路径
     * @return 图片类型
     */
    public static String getImgType(String imgSrc) {
        // 先转成十六进制数据再获取类型
        return getImgTypeFromHex(img2HexStr(imgSrc));
    }

    /**
     * 通过图片的二进制数据获取图片的类型
     *
     * @param imgBytes 图片二进制数据
     * @return 图片类型
     */
    public static String getImgType(byte[] imgBytes) {
        // 先转成十六进制数据再获取类型
        return getImgTypeFromHex(bytes2HexStr(imgBytes));
    }

    /**
     * 从图片的十六进制字符串中提取文件头从而获取图片类型
     *
     * @param hexStr 图片的十六进制字符串
     * @return 图片类型
     */
    public static String getImgTypeFromHex(String hexStr) {
        String imgType = "";
        if (hexStr != null && hexStr.length() > 0) {
            // 获取预先配置好的图片类型
            Set<String> keySet = IMG_TYPE.keySet();
            // 遍历
            for (String key : keySet) {
                String imgTypeHex = IMG_TYPE.get(key);
                // 判断图片的十六进制字符串是否符合指定的文件头
                if (hexStr.startsWith(imgTypeHex)) {
                    imgType = key;
                }
            }
        }
        return imgType;
    }

    /**
     * 图片转为十六进制字符串
     *
     * @param imgSrc 图片路径
     * @return 图片的十六进制数据
     */
    public static String img2HexStr(String imgSrc) {
        // 先转为二进制数据
        return bytes2HexStr(img2Bytes(imgSrc));
    }

    /**
     * byte数组转换为十六进制字符串
     *
     * @param srcBytes byte数组原始数据
     * @Return byte数组转换成的十六进制字符串(大写)
     */
    public static String bytes2HexStr(byte[] srcBytes) {
        String hexStr = "";
        // 判空
        if (srcBytes != null && srcBytes.length > 0) {
            StringBuilder hexSb = new StringBuilder();
            for (byte srcByte : srcBytes) {
                // 转为十六进制字符串
                String hexTmp = Integer.toHexString(srcByte & 0xFF);
                if (hexTmp.length() < 2) {
                    // 十六进制字符串中使用两位表示一位，位数不足则高位补0
                    hexSb.append(0);
                }
                // 拼接转化好的数据
                hexSb.append(hexTmp);
            }
            // 转为字符串
            hexStr = hexSb.toString().toUpperCase();
        }
        return hexStr;
    }
}
