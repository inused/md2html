> Markdown文件转换成HTML文件

使用 [flexmark](https://github.com/vsch/flexmark-java) 转换Markdown至HTML格式    
使用 [jsoup](https://github.com/jhy/jsoup) 操作HTML中的img标签  
使用 [thymeleaf](https://github.com/thymeleaf/thymeleaf) 渲染生成最终HTML文件  
使用 [jQuery](https://github.com/jquery/jquery) js库  
使用 [highlight](https://github.com/highlightjs/highlight.js) 语法高亮  
使用 [highlightjs-line-numbers](https://github.com/wcoder/highlightjs-line-numbers.js) 生成行号  
使用 [zTree](https://gitee.com/zTree/zTree_v3) jQuery 树插件，生成目录树  
使用 [zTree_Toc](https://github.com/i5ting/jQuery.zTree_Toc.js) 突出显示当前所处目录位置  
[prism](https://github.com/PrismJS/prism/) 语法高亮的另一个库 未使用  

支持表格，图片会被转换为Base64编码

## 打包

```bash
# 进入项目根目录执行
mvn clean package
```

生成两个jar包

`md2html.jar`: 可执行，需将依赖jar包置于当前目录的`lib`文件夹下

`md2html.one-jar.jar`: 可直接执行，所有依赖jar包都已经打进了jar包中

## 使用

```bash
使用方法：
    [option] 参数...
        option:
            -h:	或者--html。
                后面跟 参数1，参数2。
                参数1代表 要转换的md文件名称，参数2为转换后的文件名称。
                参数2可不填，如果不填默认生成在当前文件夹下，名称和md文件相同。
```

```bash
# Windows下，字符编码问题
java -Dfile.encoding=utf-8 -jar md2html.one-jar.jar -h /path/to/markdown.md
# 其他
java -jar md2html.one-jar.jar -h /path/to/markdown.md
```