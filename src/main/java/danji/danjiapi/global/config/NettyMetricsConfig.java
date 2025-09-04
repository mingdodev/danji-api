package danji.danjiapi.global.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyMetricsConfig {

    @Bean
    public MeterBinder nettyMetrics() {
        return (registry) -> {
            UnpooledByteBufAllocator unpooledByteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
            PooledByteBufAllocator pooledByteBufAllocator = PooledByteBufAllocator.DEFAULT;

            Gauge.builder("netty.unpooled.allocator.used.direct.memory", unpooledByteBufAllocator,
                    (alloc) -> alloc.metric().usedDirectMemory())
                    .description("The number of bytes of direct memory used by Netty's unpooled allocator")
                    .register(registry);

            Gauge.builder("netty.pooled.allocator.used.direct.memory", pooledByteBufAllocator,
                            (alloc) -> alloc.metric().usedDirectMemory())
                    .description("The number of bytes of direct memory used by Netty's pooled allocator")
                    .register(registry);
        };
    }
}