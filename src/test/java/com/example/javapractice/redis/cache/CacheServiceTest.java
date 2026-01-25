package com.example.javapractice.redis.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CacheServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheService cacheService;

    @Test
    @DisplayName("캐싱 동작 확인: 두 번째 호출은 캐시에서 반환되어 빠르고 동일한 결과")
    void cacheableTest() {
        String cacheId = "test-1";

        // 첫 번째 호출 - DB 조회 (slowDbCall로 인해 1초 이상 소요)
        long start1 = System.currentTimeMillis();
        CacheDto firstResult = cacheService.getCache(cacheId);
        long duration1 = System.currentTimeMillis() - start1;

        // 두 번째 호출 - 캐시에서 반환 (빠름)
        long start2 = System.currentTimeMillis();
        CacheDto secondResult = cacheService.getCache(cacheId);
        long duration2 = System.currentTimeMillis() - start2;

        // 검증
        System.out.println("첫 번째 호출 시간: " + duration1 + "ms");
        System.out.println("두 번째 호출 시간: " + duration2 + "ms");
        System.out.println("첫 번째 결과: " + firstResult);
        System.out.println("두 번째 결과: " + secondResult);

        // 첫 번째 호출은 1초 이상, 두 번째 호출은 100ms 미만
        assertThat(duration1).isGreaterThanOrEqualTo(1000);
        assertThat(duration2).isLessThan(100);

        // 캐시된 동일한 객체 반환 확인
        assertThat(secondResult.generatedAt()).isEqualTo(firstResult.generatedAt());
    }

    @Test
    @DisplayName("캐시 무효화 확인: evict 후 새로운 데이터 반환")
    void cacheEvictTest() {
        String cacheId = "test-2";

        // 첫 번째 호출 - 캐시에 저장
        CacheDto firstResult = cacheService.getCache(cacheId);

        // 캐시 무효화
        cacheService.evictCache(cacheId);

        // 다시 호출 - 새로운 데이터 생성 (시간 소요)
        long start = System.currentTimeMillis();
        CacheDto afterEvict = cacheService.getCache(cacheId);
        long duration = System.currentTimeMillis() - start;

        System.out.println("evict 전 결과: " + firstResult);
        System.out.println("evict 후 결과: " + afterEvict);
        System.out.println("evict 후 호출 시간: " + duration + "ms");

        // evict 후에는 다시 DB 조회 (1초 이상 소요)
        assertThat(duration).isGreaterThanOrEqualTo(1000);

        // generatedAt이 달라야 함 (새로 생성됨)
        assertThat(afterEvict.generatedAt()).isNotEqualTo(firstResult.generatedAt());
    }

    @Test
    @DisplayName("다른 cacheId는 각각 캐싱됨")
    void differentCacheIdTest() {
        String cacheId1 = "test-3";
        String cacheId2 = "test-4";

        // 다른 cacheId로 호출 - 각각 DB 조회
        long start1 = System.currentTimeMillis();
        CacheDto result1 = cacheService.getCache(cacheId1);
        long duration1 = System.currentTimeMillis() - start1;

        long start2 = System.currentTimeMillis();
        CacheDto result2 = cacheService.getCache(cacheId2);
        long duration2 = System.currentTimeMillis() - start2;

        System.out.println("cacheId1 호출 시간: " + duration1 + "ms");
        System.out.println("cacheId2 호출 시간: " + duration2 + "ms");

        // 둘 다 첫 호출이므로 1초 이상 소요
        assertThat(duration1).isGreaterThanOrEqualTo(1000);
        assertThat(duration2).isGreaterThanOrEqualTo(1000);

        // 서로 다른 결과
        assertThat(result1.id()).isNotEqualTo(result2.id());
    }
}
