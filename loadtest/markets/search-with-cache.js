import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://localhost:8080';
const TEST_EMAIL = 'test@example.com';
const TEST_PASSWORD = '1234';

const API_PATHS = {
    login: `${BASE_URL}/api/auth/login`,
    getMarkets: `${BASE_URL}/api/markets?page=0&size=10`,
};

export const options = {
    vus: 100,
    duration: '1m',
};

export function setup() {
    const requestBody = JSON.stringify({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
    });
    const params = {
        headers: { 'Content-Type': 'application/json' }
    };
    const loginResponse = http.post(API_PATHS.login, requestBody, params);

    check(loginResponse, { 'login status 200': (r) => r.status === 200 });
    const responseBody = JSON.parse(loginResponse.body);
    const accessToken = responseBody?.data?.accessToken;

    if (!accessToken) {
        throw new Error('Failed to obtain access token from login response');
    }
    return { accessToken };
}

/**
 * [테스트 목적]
 * - 전체 가게 목록을 캐싱할 때, 응답 속도 개선과 메모리 사용량에 어떠한 변화가 있는지 알아본다.
 * - 데이터가 증가함에 따라 오히려 병목이 발생하지는 않을지, 캐싱으로 인한 직렬화/역직렬화 시간 또는 JVM 메모리 점유에 부하가 있지 않을지 관찰한다.
 *
 * [테스트 환경]
 * - 사용자 100명이 1분 동안 전체 가게 목록을 조회한다.
 * - 캐시 TTL은 10분이며, 가게 총 개수가 100개일 때, 1,000개일 때, 100,000개일 때로 나눠서 테스트를 수행한다.
 * - 배포 인스턴스의 성능 문제가 존재하므로 (금전적 이슈) 로컬 인스턴스에서 수행하되, 컨테이너를 통해 배포 환경과 동일한 환경에서 테스트한다.
 *
 * [테스트 수행]
 * 1. marketService.searchMarkets로 디스크에서 데이터를 조회할 때의 성능을 측정한다.
 * 2. marketService.searchMarketsWithCache로 캐싱을 적용할 때의 성능을 측정한다. (no cache warming)
 * 3. 2를 수행한 후 10분 이내로 2를 다시 수행하여 성능을 측정한다. (cache warming)
 */

export default function (data) {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${data.token}`,
        }
    }

    const response = http.get(API_PATHS.getMarkets, params);

    check(response, {
        'status was 200': (r) => r.status === 200,
    });
}