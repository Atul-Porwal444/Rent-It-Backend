package com.rentit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson.CoreJacksonModule;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

// By this class we don't have to mark classes as serialize
@Configuration
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        // This acts as a security measure so Redis doesn't deserialize malicious classes.
        // Allowing 'Object.class' ensures your custom classes and Spring Security objects
        // can be safely serialized and deserialized with their @class metadata.
        PolymorphicTypeValidator pvt = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        // Build the ObjectMapper using Jackson 3's Builder Pattern
        return JsonMapper.builder()

                // Automatically finds and registers the JavaTimeModule on the classpath
                .findAndAddModules()
                // Register Spring Security's Jackson mixins so it knows how to build User, SimpleGrantedAuthority, etc.
                .addModule(new CoreJacksonModule())

                // Embeds type information (@class) into the JSON so Spring Data Redis
                // knows exactly which class to instantiate when reading the cache
                .activateDefaultTyping(pvt, DefaultTyping.NON_FINAL)
                .build();
    }

    // will be used when manually putting the data in the redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // for serializing the key as string
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // Serializing values as JSON
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(redisObjectMapper()))
                );
    }

}
