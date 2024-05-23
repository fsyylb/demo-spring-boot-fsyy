# Chrome远程调试之WebSocket

var ws = new WebSocket('ws://localhost:9222/devtools/page/3c5c05fa-80b7-4cfe-8d1d-ebe79a7a5730');
ws.onopen= function() {
    ws.send('{"id": 1, "method": "Page.navigate", "params": {"url": "https://www.fangdushi.com"}}')
};
ws.onmessage= function(evt) {
    console.log('Received a message from the server!'+evt.data);
};
api文档地址https://chromedevtools.github.io/devtools-protocol/1-2

https://blog.csdn.net/weixin_44018338/article/details/103442831

搜索关键字"chrome console接受websocket信息" 或 "chrome websocket 测试"



# springboot整合websocket两种方式
方式1：html5原生支持方式
向spring容器中注入一个ServerEndpointExporter
package com.tinet.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author dsx
 */
@Configuration
public class WebsocketConfig {

    /**
     * ServerEndpointExporter 作用
     *
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}


添加一个serverEndpoint
用于给前端建立连接的server

package com.tinet.websocket.config;

import com.alibaba.fastjson.JSON;
import com.tinet.websocket.pojo.MessageBody;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author DENGSHAOXIANG
 */
@ServerEndpoint("/websocket/test/{userId}")
@Component
public class WebsocketServer {

    public WebsocketServer() {
        //每当有一个连接，都会执行一次构造方法
        System.out.println("新的连接。。。");
    }

    /**
     * 存放当前连接数
     */
    private static final AtomicInteger COUNT = new AtomicInteger();


    /**
     * 存放所有的连接
     */
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();


    //发送消息
    public void sendMessage(Session toSession, String message) {
        if (toSession != null) {
            try {
                toSession.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("对方不在线");
        }
    }

    public void sendMessageToUser(String user, String message) {
        Session toSession = SESSIONS.get(user);
        sendMessage(toSession, message);
    }


    @OnMessage
    public void onMessage(String message) {
        System.out.println("服务器收到消息：" + message);
        MessageBody messageBody = JSON.parseObject(message, MessageBody.class);
        String userId = messageBody.getUserId();
        sendMessageToUser(userId, messageBody.getMessage());


    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        if (SESSIONS.get(userId) != null) {
            return;
        }
        SESSIONS.put(userId, session);
        COUNT.incrementAndGet();
        System.out.println(userId + "上线了，当前在线人数：" + COUNT);

    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        SESSIONS.remove(userId);
        COUNT.decrementAndGet();
        System.out.println(userId + "下线了，当前在线人数：" + COUNT);
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }
}

后端代码就完成了

前端代码
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>WebSocket</title>

</head>
<script src="sockjs.js"></script>
<script src="stomp.js"></script>
<body>
<h3>hello socket</h3>
<p>【userId】：
<div><input id="userId" name="userId" type="text" value="10"></div>
<p>【toUserId】：
<div><input id="toUserId" name="toUserId" type="text" value="20"></div>
<p>【消息内容】：
<div><input id="contentText" name="contentText" type="text" value="hello websocket"></div>
<p>操作:
<div style="color: green"><a onclick="openSocketByStomp()">开启socket</a></div>
<p>操作:
<div style="color: green"><a onclick="closeSocket()">断开socket</a></div>
<p>【操作】：
<div style="color: green"><a onclick="sendMessage()">发送消息</a></div>
</body>
<script>


    var socket;
    let stompClient;

    function openSocket() {
        if (typeof (WebSocket) == "undefined") {
            alert("您的浏览器不支持WebSocket");
        } else {

            if (socket != null) {
                return;
            }

            //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
            var userId = document.getElementById('userId').value;
            // var socketUrl="ws://127.0.0.1:22599/webSocket/"+userId;
            var socketUrl = "ws://localhost/websocket/test/111";
            console.log(socketUrl);


            socket = new WebSocket(socketUrl);
            //打开事件
            socket.onopen = function () {
                console.log("websocket已打开");
                //socket.send("这是来自客户端的消息" + location.href + new Date());
            };
            //获得消息事件
            socket.onmessage = function (msg) {
                var serverMsg = "收到服务端信息：" + msg.data;
                console.log(serverMsg);
                //发现消息进入    开始处理前端触发逻辑
            };
            //关闭事件
            socket.onclose = function () {
                console.log("websocket已关闭");
            };
            //发生了错误事件
            socket.onerror = function () {
                console.log("websocket发生了错误");
            }
        }
    }

    function sendMessage() {
        if (socket === undefined || socket === null) {
            alert("请先连接");
            return;
        }
        if (typeof (WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        } else {
            // console.log("您的浏览器支持WebSocket");
            var toUserId = document.getElementById('toUserId').value;
            var contentText = document.getElementById('contentText').value;
            var msg = '{"userId":"' + toUserId + '","message":"' + contentText + '"}';
            console.log(msg);
            socket.send(msg);
        }
    }

    function closeSocket() {
        if (socket === undefined || socket === null) {
            alert("请先连接");
            return;
        }
        socket.close();
        socket = null;
    }
   
</script>
</html>

方式2：stomp协议方式
添加websocket配置
实现WebSocketMessageBrokerConfigurer并实现相关方法

package com.tinet.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Created by dengshaoxiang on 2019/11/21 11:36
 * description: broker
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint用来建立ws连接的
        registry.addEndpoint("/gs-guide-websocket", "/test2")
                .setHandshakeHandler(userHandleShake())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // to enable a simple memory-based message broker to carry
        // the greeting messages back to the client on destinations prefixed with "/topic".
        // broker广播器，定义一个广播器前缀，前端（客户端）可以向指定的channel发起订阅，当后端通过@sendTo 或者convertAndSend 可以向指定
        //通道发消息，订阅了该通道的客户端可以收到消息
        registry.enableSimpleBroker("/topic", "/chat");
        // designates the "/app" prefix for messages that are
        // bound for @MessageMapping-annotated methods.
        // This prefix will be used to define all the message mappings;
        // ws接口定义前缀，后端定义了接口，使用@MessageMapping 前端可以请求该接口并传数据
        // 总结  broker可以用来向客户端发送数据，destination可以用来向服务器发送数据 ，区别在于，客户端得先发起订阅
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public UserHandleShake userHandleShake() {
        return new UserHandleShake();
    }
}

重写DefaultHandshakeHandler
重写DefaultHandshakeHandler的determineUser方法，给每个连接session注入principal用户信息。

package com.tinet.websocket.config;

import com.tinet.websocket.pojo.UserInfo;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * @author dengsx
 * @create 2021/07/22
 **/
public class UserHandleShake extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        System.out.println("shake");
        UserInfo info = new UserInfo();
        info.setUserName(servletRequest.getParameter("userName"));
        return info;
    }
}


package com.tinet.websocket.pojo;

import java.security.Principal;

/**
 * @author dengsx
 * @create 2021/07/22
 **/
public class UserInfo implements Principal {
    private String userName;

    @Override
    public String getName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


聊天的controller
写一个简单的用来聊天的controller

package com.tinet.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * @author dengsx
 * @create 2021/07/19
 **/
@MessageMapping("/chat")
@Controller
public class WsController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/message")
    public void testWs(Map<String, Object> params) {
        template.convertAndSendToUser(String.valueOf(params.get("userName")), "/chat", params.getOrDefault("content", "内容提取失败，我是默认消息"));
    }
}

后端代码就已经完成了

前端代码 （vue实现）
<template>
<div>
  <el-button :disabled="disable" @click="openSocket('user1')" style="width: 200px">创建用户1连接</el-button>
  <el-button :disabled="disable" @click="openSocket('user2')" style="width: 200px">创建用户2连接</el-button>
  <el-input v-model="content" style="width: 200px"></el-input>
  <el-button @click="sendMessage1()" style="width: 200px">发送消息给用户1</el-button>
  <el-input v-model="content3" style="width: 200px"></el-input>
  <el-button @click="sendMessage2()" style="width: 200px">发送消息给用户2</el-button>
  <el-input v-model="content1" style="width: 200px">xxx</el-input>
  <el-button @click="sendNoAppMsg()" style="width: 200px">发送无app消息</el-button>
</div>
</template>

<script>
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
export default {
  name: "TestWebsocket",
  data(){
    return {
      stompClient:'',
      content:"",
      content1:"",
      content3:"",
      disable: false,
    };
  },
  created(){
  },
  methods:{
    openSocket(name){
      let socket = new SockJS('http://localhost/test2?userName='+name);
      // 获取STOMP子协议的客户端对象
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({},()=>{
        this.disable = true;
        this.$message({showClose: true, message: "连接成功", type: 'success'});
        // 订阅主题
        this.stompClient.subscribe("/user/chat",(msg)=>{
          console.log(msg);
          this.$message({showClose: true, message: msg.body, type: 'success'});
        })

        // 订阅无app主题，
        this.stompClient.subscribe("/topic1/noapp",(msg)=>{
          console.log(msg);
          this.$message({showClose: true, message: JSON.parse(msg.body).message, type: 'success'});
        })
      })
    },
    sendMessage1(){
      let obj = {
        userName:'user1',
        content:this.content
      }
      this.stompClient.send("/app/chat/message",{},JSON.stringify(obj))
      this.content = "";
    },
    sendMessage2(){
      let obj = {
        userName:'user2',
        content:this.content3
      }
      this.stompClient.send("/app/chat/message",{},JSON.stringify(obj))
      this.content = "";
    },
    sendNoAppMsg(){
      let obj = {
        userId:'dsx',
        message:this.content1
      }
      this.stompClient.send("/topic1/noapp",{},JSON.stringify(obj))
      this.content1 = "";
    }
  }
}
</script>

<style scoped>

</style>
前端订阅主题/user/chat,用于接受消息

前端向/app/chat/message 发送消息，传入指定userName参数，后端接受到消息后会将消息转发给指定的userName。
https://blog.csdn.net/qq_35249342/article/details/119324967


# https://gitee.com/52itstyle/spring-boot-seckill/blob/master/src/main/java/com/itstyle/seckill/common/webSocket/WebSocketServer.java


package com.itstyle.seckill.common.webSocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint("/websocket/{userId}")  
@Component  
public class WebSocketServer {  
	private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收userId
    private String userId="";
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:"+userId+",当前在线人数为" + getOnlineCount());
        this.userId=userId;
        try {
             sendMessage("连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口"+userId+"的信息:"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message,@PathParam("userId") String userId){
        log.info("推送消息到窗口"+userId+"，推送内容:"+message);
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个userId的，为null则全部推送
                if(userId==null) {
                    item.sendMessage(message);
                }else if(item.userId.equals(userId)){
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
  
}  
