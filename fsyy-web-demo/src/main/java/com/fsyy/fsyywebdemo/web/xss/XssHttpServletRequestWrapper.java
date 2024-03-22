package com.fsyy.fsyywebdemo.web.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashSet;
import java.util.Set;


public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if(value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if(value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    private String cleanXSS(String value){
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        // value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        // value = value.replaceAll("e-xpression\\\\((.*?)\\\\)\"", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        value = value.replaceAll("[*]", "["+"*]");
        // value = value.replaceAll("[+]", "["+"+]");
        value = value.replaceAll("[?]", "[" + "?]");

        // replace sql 这里可以自由发挥
        String[] values = value.split(" ");

        /**
        String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|" +
                "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
                "table|from|grant|use|group_concat|column_name|" +
                "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|" +
                "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";//过滤掉的sql关键字，可以手动添加
         **/
        String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|" +
                "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|create|table|from|grant|use|" +
                "group_concat|column_name|information_schema.columns|table_schema|union|where|order|by|--|like|//|/|#";

        String[] badStrs = badStr.split("\\|");
        for(String str : badStrs){
            for(int j = 0; j < values.length; j++){
                if(values[j].equalsIgnoreCase(str)){
                    values[j] = "forbid";
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < values.length; i++){
            if(i == values.length - 1){
                sb.append(values[i]);
            }else {
                sb.append(values[i]).append(" ");
            }
        }
        value = sb.toString();
        return value;
    }

    public static void main(String[] args) {
        String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|" +
                "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
                "table|from|grant|use|group_concat|column_name|" +
                "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|" +
                "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";//过滤掉的sql关键字，可以手动添加
        String[] badStrs = badStr.split("\\|");
        Set<String> sets = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for(String str : badStrs){
            if(!sets.contains(str)){
                sb.append(str).append("|");
                sets.add(str);
            }
        }
        if(sb.length() > 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        // '|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|create|table|from|grant|use|group_concat|column_name|information_schema.columns|table_schema|union|where|order|by|--|like|//|/|#
        System.out.println(sb.toString());
        System.out.println(badStr.length());
        System.out.println(sb.length());
    }
}
