package me.learn.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 主要是处理HTML中的图片
 * @author i
 */
public class JsoupUtils {

    /**
     * 将HTML字符串中img标签替换为Base64编码
     * @param htmlStr HTML格式字符串
     * @return img标签替换为Base64编码后的HTML字符串
     */
    public static String htmlImg2Base64(String htmlStr) {
        // 将HTML字符串转换为Document
        Document document = Jsoup.parse(htmlStr);
        // 获取所有带有src属性的img标签
        Elements imgEles = document.select("img[src]");
        if (imgEles != null) {
            for (Element imgEle : imgEles) {
                // 获取img标签的src属性
                String imgSrc = imgEle.attr("src");
                // 将路径指向的图片转换为Base64编码
                String imgBase64UriStr = ImageUtils.img2Base64Uri(imgSrc);
                // 替换
                imgEle.attr("src", imgBase64UriStr);
            }
        }
        // 重新转换为HTML字符串
        return document.toString();
    }
}
