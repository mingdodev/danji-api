package danji.danjiapi.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    @Qualifier("tokenRedisConnectionFactory")
    public LettuceConnectionFactory tokenRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(0);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("cacheRedisConnectionFactory")
    public LettuceConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("tokenRedisTemplate")
    RedisTemplate<String, Object> tokenRedisTemplate(
            @Qualifier("tokenRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return redisTemplateResolver(redisConnectionFactory);
    }

    @Bean
    @Qualifier("cacheRedisTemplate")
    RedisTemplate<String, Object> cacheRedisTemplate(
            @Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return redisTemplateResolver(redisConnectionFactory);
    }

    private RedisTemplate<String, Object> redisTemplateResolver(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }
}
