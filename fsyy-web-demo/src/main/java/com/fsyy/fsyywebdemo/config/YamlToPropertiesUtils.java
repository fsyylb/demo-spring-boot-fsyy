package com.fsyy.fsyywebdemo.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.util.Properties;

public class YamlToPropertiesUtils {
    private static YAMLMapper yamlMapper = new YAMLMapper();

    private static JavaPropsMapper javaPropsMapper = new JavaPropsMapper();

    private YamlToPropertiesUtils(){
        super();
    }

    public static Properties yamlToProperties(String yamlContent) throws IOException {
        JsonNode jsonNode = yamlMapper.readTree(yamlContent);
        return javaPropsMapper.writeValueAsProperties(jsonNode);
    }
}
