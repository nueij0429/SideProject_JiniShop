import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        { duration: '5s', target: 1 },    // 예열: 1명
        { duration: '10s', target: 50 },  // 동시 50명까지 증가
        { duration: '10s', target: 50 },  // 50명 유지
        { duration: '5s', target: 0 },    // 다시 0으로 감소
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/api/products');

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
