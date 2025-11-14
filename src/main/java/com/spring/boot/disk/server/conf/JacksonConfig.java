package com.spring.boot.disk.server.conf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.spring.boot.disk.server.model.serialize.LongToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Jackson 配置类，全局处理 Long 类型序列化
 */
@Configuration
public class JacksonConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();
        // 创建自定义模块
        SimpleModule module = new SimpleModule();
        // 注册 Long 类型的自定义序列化器
        module.addSerializer(Long.class, new LongToStringSerializer());
        module.addSerializer(long.class, new LongToStringSerializer()); // 处理基本类型 long
        // 注册模块到 ObjectMapper
        objectMapper.registerModule(module);

        // 创建消息转换器并设置自定义 ObjectMapper
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
