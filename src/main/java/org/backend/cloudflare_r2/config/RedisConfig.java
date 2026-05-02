//package org.backend.cloudflare_r2.config;
//
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//
//        // ✅ Key serializer
//        template.setKeySerializer(new StringRedisSerializer());
//
//        // ✅ Value serializer (modern Jackson)
//        template.setValueSerializer(jacksonSerializer());
//
//        // ✅ Hash support (important for Spring Cache)
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(jacksonSerializer());
//
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    private RedisSerializer<Object> jacksonSerializer() {
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        // enable type info (important for polymorphic objects)
//        mapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
//
//        return new Jackson2JsonRedisSerializer<>(mapper, Object.class);
//    }
//}