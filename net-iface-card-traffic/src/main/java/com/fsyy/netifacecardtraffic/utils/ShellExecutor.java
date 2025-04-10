package com.fsyy.netifacecardtraffic.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellExecutor {
    public static String wondershaper;

    public static List<String> execute(String command) throws IOException {
        List<String> resultList = new ArrayList<>();
        if(SystemUtils.IS_OS_LINUX) {  // run in linux
            Process ps = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            try(InputStream in = ps.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(in))){
                String line;
                while(StringUtils.isNotBlank(line = br.readLine())){
                    resultList.add(line);
                }
            }
        }
        return resultList;
    }
}
