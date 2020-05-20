package me.learn.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 主要是渲染HTML
 * @author i
 */
public class ThymeleafUtils {

    /** 默认的HTML的thymeleaf模板路径前缀 */
    private static final String TEMPLATE_PREFIX = "/template/";
    /** 默认的HTML的thymeleaf模板名 */
    private static final String TEMPLATE_NAME = "template";
    /** 默认的HTML的thymeleaf模板路径后缀 */
    private static final String TEMPLATE_SUFFIX = ".html";
    /** Thymeleaf模板引擎 */
    private static final TemplateEngine THYMELEAF_TEMPLATE_ENGINE = new TemplateEngine();
    private static final ClassLoaderTemplateResolver THYMELEAF_TEMPLATE_RESOLVER = new ClassLoaderTemplateResolver();
    /** 所需的JS与CSS文件 */
    private static final Map<String, Object> JS_CSS_TEXT = new HashMap<>();
    static {
        //模板所在目录，相对于当前classloader的classpath。
        THYMELEAF_TEMPLATE_RESOLVER.setPrefix(TEMPLATE_PREFIX);
        //模板文件后缀
        THYMELEAF_TEMPLATE_RESOLVER.setSuffix(TEMPLATE_SUFFIX);
        THYMELEAF_TEMPLATE_ENGINE.setTemplateResolver(THYMELEAF_TEMPLATE_RESOLVER);

        // Markdown样式
        JS_CSS_TEXT.put("mdCSS", getJsOrcss("/css/markdown-github.css"));
        // jQuery
        JS_CSS_TEXT.put("jqueryJS", getJsOrcss("/js/jquery.min.js"));
        // 目录树样式
        JS_CSS_TEXT.put("zTreeCSS", getJsOrcss("/css/zTreeStyle.css"));
        // 目录树js
        JS_CSS_TEXT.put("zTreeJS", getJsOrcss("/js/jquery.ztree.all.min.js"));
        // 目录树js
        JS_CSS_TEXT.put("zTreeTocJS", getJsOrcss("/js/jquery.ztree_toc.min.js"));
        // 高亮语法
        JS_CSS_TEXT.put("highlightCSS", getJsOrcss("/css/highlightjs-atom-one-light.css"));
        // 高亮JS
        //JS_CSS_TEXT.put("highlightJS", getJsOrcss("/js/prism.min.js"));
        JS_CSS_TEXT.put("highlightJS", getJsOrcss("/js/highlight.pack.js"));
        // 高亮JS 行号
        //JS_CSS_TEXT.put("highlightNumberJS", getJsOrcss("/js/highlightjs-line-numbers.min.js"));
        JS_CSS_TEXT.put("highlightNumberJS", getJsOrcss("/js/highlightjs-line-numbers-noReverseColor.min.js"));
    }

    /**
     * 注入数据生成HTML文件，使用默认模板 template.html
     * @param params HTML模板中绑定的参数，title/body
     * @param targetHtmlPath 要生成的HTML文件路径
     */
    public static void generate(Map<String, Object> params, String targetHtmlPath) {
        try (FileWriter fileWriter = new FileWriter(new File(targetHtmlPath))) {
            // Markdown文本转换成的HTML添加到最终HTML中
            String finalHtmlText = render(TEMPLATE_NAME, params);
            // 将HTML中的img标签转为Base64
            finalHtmlText = JsoupUtils.htmlImg2Base64(finalHtmlText);
            // 写入文件
            fileWriter.write(finalHtmlText);
        } catch (Exception e) {
            System.out.println("生成HTML文件失败");
            e.printStackTrace();
        }
    }

    /**
     * 使用 Thymeleaf 渲染 HTML，返回HTML格式字符串
     *
     * @param template HTML模板名
     * @param params   HTML模板中绑定的参数，title/body
     * @return 渲染完成的HTML格式字符串
     */
    public static String render(String template, Map<String, Object> params) {
        // 构造上下文,model
        Context context = getContext(params);
        // 渲染
        return THYMELEAF_TEMPLATE_ENGINE.process(template, context);
    }

    /**
     * 使用 Thymeleaf 渲染 HTML 并写入文件
     *
     * @param template HTML模板名
     * @param params   HTML模板中绑定的参数，title/body
     * @param targetHtmlPath 要写入的文件路径
     */
    public static void render(String template, Map<String, Object> params, String targetHtmlPath) {
        // 构造上下文,model
        Context context = getContext(params);
        try (FileWriter fileWriter = new FileWriter(targetHtmlPath)) {
            // 渲染并写入文件
            THYMELEAF_TEMPLATE_ENGINE.process(template, context, fileWriter);
        } catch (Exception e) {
            System.out.println("渲染写入HTML失败");
            e.printStackTrace();
        }
    }

    /**
     * 使用 Thymeleaf 构造上下文环境,即将 HTML中的th标签的变量绑定
     *
     * @param params   HTML模板中绑定的参数，JS和CSS数据
     * @return 注入了参数的上下文环境
     */
    private static Context getContext(Map<String, Object> params) {
        // 构造上下文,model
        Context context = new Context();
        // 注入JS/CSS文件信息
        context.setVariables(JS_CSS_TEXT);
        // 注入参数，包括title body
        context.setVariables(params);
        return context;
    }

    /**
     * 读取JS或者CSS文件返回其内容
     * @param src JS/CSS文件路径
     * @return 文件内容
     */
    private static String getJsOrcss(String src) {
        // 读取文件内容，去除换行
        return PathUtils.getText(src).replaceAll("(\r\n|\r|\n|\n\r)", "");
    }
}
