package me.learn;

import me.learn.util.MarkdownUtils;
import me.learn.util.PathUtils;
import me.learn.util.ThymeleafUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author i
 */
public class App {
    /** 帮助信息 */
    private static final String USAGE_TEXT = PathUtils.getText("/usage.txt");
    /** Markdown文件所处目录 */
    public static String mdFileDir;

    public static void main(String[] args) {
        if (args == null || args.length <= 1) {
            // 没有参数，打印帮助信息
            showUsage();
            return;
        }
        String option = args[0];
        // 获取参数
        if ("-h".equalsIgnoreCase(option) || "--html".equalsIgnoreCase(option)) {
            // md文件路径
            String mdPath = args[1];
            // 生成的HTML文件路径
            String targetHtmlPath = args.length >= 3 ? args[2] : mdPath.replace(".md", ".html");
            // 生成
            justDoIt(mdPath, targetHtmlPath);
            System.out.println("生成成功: " + targetHtmlPath);
        } else {
            App.showUsage();
            return;
        }
    }

    /**
     * md文件转HTML
     * @param mdPath Markdown文件路径
     * @param targetHtmlPath 目标HTML保存路径
     */
    public static void justDoIt(String mdPath, String targetHtmlPath) {
        // 获取Markdown文件
        File mdFile = new File(mdPath);
        mdFile = mdFile.getAbsoluteFile();
        // Markdown文件所在目录
        mdFileDir = mdFile.getParent();
        // 文件名,用作HTML的title
        String mdFileName = mdFile.getName();
        mdFileName = mdFileName.substring(0,mdFileName.lastIndexOf("."));
        // 将Markdown文本转换为HTML
        String htmlText = MarkdownUtils.md2Html(mdPath);
        // 绑定参数信息
        Map<String, Object> params = new HashMap<>();
        params.put("title", mdFileName);
        params.put("body", htmlText);
        // Markdown文本转换成的HTML添加到最终HTML，写入到文件
        ThymeleafUtils.generate(params, targetHtmlPath);
    }

    /**
     * 打印帮助信息
     */
    private static void showUsage() {
        System.out.println(USAGE_TEXT);
    }
}
