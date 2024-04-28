【OpenFeign】Decoder读取response.body报错【stream is closed reading GET XXX】

解决方案：取消该方法的断点；

工具：IDEA 2022.1.2；

背景：Feign远程调用时对第三方返回数据进行解析，判断body是否为期望类型；

public class FeignDcoderConfig implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null) {
            throw new DecodeException(response.status(), "空数据", response.request());
        }
 
        String bodyStr = Util.toString(response.body().asReader(Util.UTF_8));
        System.out.println(bodyStr);
        return  null;
    }
}

