package com.fsyy.fsyywebdemo.web;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Apache Shiro 是一个强大且易用的Java安全框架，它具有身份验证、访问授权、数据加密、会话管理等功能。
 * Apache Shiro 发布更新版本，修复了一个身份验证绕过漏洞，漏洞编号：CVE-2023-34478，漏洞危害等级：高危。
 * Apache Shiro 版本 1.12.0 之前和 2.0.0-alpha-3 之前容易受到路径遍历攻击，当与基于非规范化请求路由请求的 API 或其他 web 框架一起使用时，可能导致身份验证绕过。
 * 规避方案 1. 根据实际业务请求情况，在不影响业务的请求下，手动在防护设备中添加规则，拦截请求路径中含有"%2f", "%2e", "./", "../", "/."等字符的URL请求.
 *
 */

@Component
public class RouteTraversalFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if(!checkOk(uri)){
            throw new ServletException("uri : " + uri + ", contains route travelsal string, please check!");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean checkOk(String uri){
        if (uri == null){
            return true;
        }
        uri = uri.toLowerCase();
        return !uri.contains("%2f") && !uri.contains("%2e") &&
                !uri.contains("./") && !uri.contains("../") && !uri.contains("/.");
    }
}
