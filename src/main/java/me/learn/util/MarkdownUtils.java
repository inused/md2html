package me.learn.util;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * 主要用来将Markdown文件转换为HTML文件
 * @author i
 */
public class MarkdownUtils {
    /** 设置项 */
    private static final MutableDataSet OPTIONS = new MutableDataSet();
    /** Markdown转换器 */
    private static final Parser MARKDOWN_PARSER;
    /** HTML渲染器 */
    private static final HtmlRenderer HTML_RENDERER;
    static {
        // Markdown格式
        OPTIONS.setFrom(ParserEmulationProfile.MARKDOWN);
        // 启用表格支持
        OPTIONS.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        // 创建转换器
        MARKDOWN_PARSER = Parser.builder(OPTIONS).build();
        // HTML渲染器
        HTML_RENDERER = HtmlRenderer.builder(OPTIONS).build();
    }

    public static String md2Html(String mdPath) {
        String htmlText = "";
        try (FileInputStream mdfis = new FileInputStream(mdPath);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len=mdfis.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0,len);
            }
            // 转成字符串
            String mdText = byteArrayOutputStream.toString();
            // 将Markdown文本转换为HTML
            htmlText = MarkdownUtils.mdText2Html(mdText);
        } catch (Exception e) {
            System.out.println("Markdown文件转HTML失败");
            e.printStackTrace();
        }
        return htmlText;
    }

    /**
     * Markdown字符串渲染为为HTML格式字符串
     *
     * @param mdContent markdown 语义文本
     * @return 转换完毕的HTML格式字符串
     */
    public static String mdText2Html(String mdContent) {
        // Markdown格式字符转化为Node
        Node document = MARKDOWN_PARSER.parse(mdContent);
        // 渲染成HTML
        return HTML_RENDERER.render(document);
    }
}
