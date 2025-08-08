import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '1m',
};

/**
 * [테스트 목적]
 * - 전체 가게 목록을 캐싱할 때, 응답 속도 개선과 메모리 사용량에 어떠한 변화가 있는지 알아본다.
 * - 데이터가 증가함에 따라 오히려 병목이 발생하지는 않을지, 캐싱으로 인한 직렬화/역직렬화 시간 또는 JVM 메모리 점유에 부하가 있지 않을지 관찰한다.
 *
 * [테스트 환경]
 * - 사용자 100명이 1분 동안 전체 가게 목록을 조회한다.
 * - 캐시 TTL은 10분이며, 가게 총 개수가 100개일 때, 1,000개일 때, 10,0000개일 때로 나눠서 테스트를 수행한다.
 * - 배포 인스턴스의 성능 문제가 존재하므로 (금전적 이슈) 로컬 인스턴스에서 수행하되, 컨테이너를 통해 배포 환경과 동일한 환경에서 테스트한다.
 *
 * [테스트 수행]
 * 1. marketService.searchMarkets로 디스크에서 데이터를 조회할 때의 성능을 측정한다.
 * 2. marketService.searchMarketsWithCache로 캐싱을 적용할 때의 성능을 측정한다. (no cache warming)
 * 3. 2를 수행한 후 10분 이내로 2를 다시 수행하여 성능을 측정한다. (cache warming)
 */

export function setup() {
    const loginRes = http.post('http://localhost:8080/api/auth/login',
        JSON.stringify({
            email: 'test@example.com',
            password: "password"
        }), { headers: { 'Content-Type': 'application/json' } });

    const body = JSON.parse(loginRes.body);
    const token = body.data.accessToken;

    return { token };
}

export default function (data) {
    const url = 'http://localhost:8080/api/markets?page=0&size=10'
    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${data.token}`,
        }
    }

    const res = http.get(url, params);

    check(res, {
        'status was 200': (r) => r.status === 200,
    });
}