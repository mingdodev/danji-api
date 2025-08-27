package danji.danjiapi.global.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import danji.danjiapi.domain.market.dto.response.MarketDetail;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.yaml.snakeyaml.error.Mark;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

//    @Bean
//    @Qualifier("tokenRedisConnectionFactory")
//    public LettuceConnectionFactory tokenRedisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//        configuration.setDatabase(0);
//        return new LettuceConnectionFactory(configuration);
//    }

    @Bean
    @Qualifier("cacheRedisConnectionFactory")
    public LettuceConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

//    @Bean
//    @Qualifier("tokenRedisTemplate")
//    RedisTemplate<String, Object> tokenRedisTemplate(
//            @Qualifier("tokenRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
//        return redisTemplateResolver(redisConnectionFactory);
//    }

    @Bean
    @Qualifier("cacheRedisTemplate")
    RedisTemplate<String, List<MarketDetail>> cacheRedisTemplate(
            @Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, MarketDetail.class);

        Jackson2JsonRedisSerializer<List<MarketDetail>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, listType);

        RedisTemplate<String, List<MarketDetail>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

//    private RedisTemplate<String, Object> redisTemplateResolver(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//        redisTemplate.setValueSerializer(jsonRedisSerializer);
//        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
//
//        return redisTemplate;
//    }
}
