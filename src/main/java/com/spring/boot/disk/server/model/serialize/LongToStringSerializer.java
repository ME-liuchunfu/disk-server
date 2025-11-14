package com.spring.boot.disk.server.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * 自定义 Long 类型序列化器，将 Long 转为 String 输出
 */
public class LongToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // 将 Long 转为字符串写入 JSON
            gen.writeString(value.toString());
        } else {
            // 处理 null 值
            gen.writeNull();
        }
    }
}
