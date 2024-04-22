package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

/**
 * Hello world!
 */
public class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        // 读取源数据内容
        String paramDbStr = getFileContent("源数据内容.json");
        context.put("table", JSON.parseObject(paramDbStr));

        // 读取固定配置内容
        String paramConfigStr = getFileContent("固定配置项.json");
        context.put("configParam", JSON.parseObject(paramConfigStr));

        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);

        // 读取规则配置内容
        // String expressStr = getFileContent("规则数据.txt");

        // 读取规则配置内容
        String expressStr = getFileContent("规则数据-中文.txt");
        expressStr = alias(expressStr);

        // 下面五个参数意义分别是 表达式,上下文,errorList,是否缓存,是否输出日志
        Object result = runner.execute(expressStr, context, null, true, true);
        System.out.println(result);
    }
    
    public static String alias(String content) {
        String aliasConfigStr = getFileContent("别名替换.json");
        JSONObject jsonObject = JSON.parseObject(aliasConfigStr);
        List<String> keys = new ArrayList<>(jsonObject.keySet());
        keys.sort((l1, l2) -> {
            Collator instance = Collator.getInstance(Locale.CHINA);
            return instance.compare(l2, l1);
        });
        for (String key : keys) {
            content = content.replaceAll(key, jsonObject.get(key).toString());
        }
        return content;
    }

    public static String getFileContent(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        // 通过类加载器获取资源文件的输入流
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                System.out.println("资源文件不存在");
                return null;
            }
            // 使用 Scanner 读取输入流内容
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            stringBuilder.append(content);
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
