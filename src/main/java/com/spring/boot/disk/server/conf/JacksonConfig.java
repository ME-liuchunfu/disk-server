package com.spring.boot.disk.server.conf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.spring.boot.disk.server.model.serialize.LongToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

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

    // 全局日期格式化模式
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();

        // 1. 配置 java.util.Date 格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.setDateFormat(simpleDateFormat);

        // 2. 配置 JDK8 时间类（LocalDateTime/LocalDate）格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // LocalDateTime 序列化器
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        // 其他时间类（如 LocalDate）可按需添加
        // javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        objectMapper.registerModule(javaTimeModule);

        // 3. 禁用时间戳序列化（强制输出字符串格式）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 4. 反序列化时适配时区
        objectMapper.disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);

        return objectMapper;
    }

}
