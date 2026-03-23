package com.example.javaplayground.redis.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RedisEvictionPolicyTest {

    // maxmemory를 1mb로 극단적으로 작게 설정 → 적은 키로도 eviction 발생
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withCommand("redis-server",
                    "--maxmemory", "2mb",
                    "--maxmemory-policy", "allkeys-lru",
                    "--maxmemory-samples", "10"); // 샘플 수를 높여 LRU 정확도 향상 (기본값 5)

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    @DisplayName("allkeys-lru: 메모리 초과 시 가장 오래전에 접근된 키부터 제거된다")
    void allkeysLruEvictionTest() {
        // 1단계: 키를 많이 넣어서 메모리를 채운다
        // 값을 크게 만들어서 빠르게 메모리를 초과시킴
        String largeValue = "x".repeat(10_000); // 10KB per key
        int totalKeys = 200;

        for (int i = 0; i < totalKeys; i++) {
            redisTemplate.opsForValue().set("key:" + i, largeValue);
        }

        // 2단계: 메모리 초과로 일부 키가 eviction됨을 확인
        List<String> survivingKeys = new ArrayList<>();
        List<String> evictedKeys = new ArrayList<>();

        for (int i = 0; i < totalKeys; i++) {
            String key = "key:" + i;
            if (redisTemplate.hasKey(key)) {
                survivingKeys.add(key);
            } else {
                evictedKeys.add(key);
            }
        }

        System.out.println("전체 키 수: " + totalKeys);
        System.out.println("생존 키 수: " + survivingKeys.size());
        System.out.println("제거된 키 수: " + evictedKeys.size());

        // eviction이 실제로 발생했는지 확인
        assertThat(evictedKeys).isNotEmpty();
        assertThat(survivingKeys.size()).isLessThan(totalKeys);

        // LRU 특성 검증: 나중에 넣은 키(최근 접근)가 더 많이 생존
        // 제거된 키들의 평균 인덱스 < 생존 키들의 평균 인덱스
        double avgEvictedIndex = evictedKeys.stream()
                .mapToInt(k -> Integer.parseInt(k.split(":")[1]))
                .average().orElse(0);
        double avgSurvivingIndex = survivingKeys.stream()
                .mapToInt(k -> Integer.parseInt(k.split(":")[1]))
                .average().orElse(0);

        System.out.println("제거된 키 평균 인덱스: " + avgEvictedIndex);
        System.out.println("생존 키 평균 인덱스: " + avgSurvivingIndex);

        // 먼저 넣은(오래된) 키가 먼저 제거되므로, 제거된 키의 평균 인덱스가 더 낮아야 함
        assertThat(avgEvictedIndex).isLessThan(avgSurvivingIndex);
    }

    @Test
    @DisplayName("allkeys-lru: 최근에 접근한 키는 eviction에서 살아남는다")
    void lruRecentAccessSurvivesTest() throws InterruptedException {
        String largeValue = "x".repeat(10_000);

        // 1단계: 키를 넣어서 메모리를 채운다
        for (int i = 0; i < 80; i++) {
            redisTemplate.opsForValue().set("key:" + i, largeValue);
        }

        // LRU 클럭 해상도(~1초)를 넘기기 위해 대기
        Thread.sleep(1100);

        // 2단계: 일부 키(0~39)만 접근하여 LRU 시간을 확실히 갱신
        for (int i = 0; i < 40; i++) {
            redisTemplate.opsForValue().get("key:" + i);
        }
        // key:40~79는 접근하지 않음 → LRU 시간이 1초 이상 오래됨

        // 3단계: 새 키를 넣어 eviction 유발
        for (int i = 80; i < 160; i++) {
            redisTemplate.opsForValue().set("key:" + i, largeValue);
        }

        // 4단계: 생존율 비교
        long accessedSurvived = 0;
        for (int i = 0; i < 40; i++) {
            if (redisTemplate.hasKey("key:" + i)) {
                accessedSurvived++;
            }
        }

        long untouchedSurvived = 0;
        for (int i = 40; i < 80; i++) {
            if (redisTemplate.hasKey("key:" + i)) {
                untouchedSurvived++;
            }
        }

        System.out.println("최근 접근(0~39) 생존: " + accessedSurvived + "/40");
        System.out.println("미접근(40~79) 생존: " + untouchedSurvived + "/40");

        // 최근 접근한 키가 더 많이 살아남아야 함
        assertThat(accessedSurvived).isGreaterThan(untouchedSurvived);
    }
}
