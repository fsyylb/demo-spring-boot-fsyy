参考：https://blog.csdn.net/csp732171109/article/details/126105180


Spring Security OAuth2登录+四种授权模式
参考：https://www.cnblogs.com/Chary/p/18006385
原始来源：https://blog.csdn.net/new_ord/article/details/126108524
原始来源文章可以详细看看


POST https://<你的应用域名>/oauth/token
client_secret_basic 是使用 HTTP Basic authentication 模式进行认证
其中 Authorization 请求头 Basic<空格> 后的值为 <client_id>:<client_secret> 的 base64 值
例如：Authorization:Basic NWNhNzY1ZTM5MzE5NGQ1ODxxxx
Request:
Headers:
    Authorization string Basic NWNhNzY1ZTM5MzE5NGQ1ODxxxx
    Content-Type string	application/x-www-form-urlencoded

Form Data Parameters:
    grant_type string authorization_code
    redirect_uri string	发起 OAuth2.0 授权登录时的 redirect_uri 值，必须与发起登录请求时的参数一致
    code string 获取到的授权码，一个 code 仅限一次性使用，用后作废，有效期 10 分钟



POST https://<你的应用域名>/oauth/token
client_secret_post 方式换取 token
将应用 ID 和应用密钥通过 POST Body 发送到 OAuth2.0 token 端点。
Request:
Headers:
    Content-Type string	application/x-www-form-urlencoded

Form Data Parameters:
    client_id string 应用 ID
    client_secret string 应用 Secret
    grant_type string authorization_code
    redirect_uri string	发起 OAuth2.0 授权登录时的 redirect_uri 值，必须与发起登录请求时的参数一致
    code string 获取到的授权码，一个 code 仅限一次性使用，用后作废，有效期 10 分钟

使用 curl 发送请求示例：
curl --location --request POST 'https://<你的应用域名>/oauth/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'code=61yhuOVrgyhKlFTU~bnEKA_fnnz' \
--data-urlencode 'client_id=5e37979f7b757ead14c534af' \
--data-urlencode 'client_secret=64b517f8de3648091654eb4ee9b479d3' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'redirect_uri=https://baidu.com'

返回示例：

{
  "access_token": "de60825d1bffd91474e9ac6a08a84bdc71f7f404",
  "token_type": "Bearer",
  "expires_in": 3599,
  "refresh_token": "c0b0b4acd686d30bb8b26dae73c2e64c1cec6698",
  "scope": "user"
}



client_secret_jwt
client_secret_jwt方式就是利用 JWT 进行认证。请求方和授权服务器，两者都知道客户端的 client_secret，通过相同的 HMAC 算法（对称签名算法）去加签和验签 JWT ，可以达到客户端认证的目的。
请求方 通过 HMAC算法，以 client_secret 作为密钥，将客户端信息加签生成 JWT ；
授权服务器 使用相同的 HMAC算法和client_secret，对请求方的 JWT 进行验签以认证客户端。

可参考网址列表：
https://markix.blog.csdn.net/article/details/126092213
https://blog.csdn.net/m13012606980/article/details/136740203
https://gitee.com/markix/spring-authorization-server-sample