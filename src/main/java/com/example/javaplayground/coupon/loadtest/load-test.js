import http from 'k6/http';
import {check} from 'k6';
import exec from 'k6/execution';
import {Counter} from 'k6/metrics';

const BASE_URL = 'http://localhost:8080';
const COUPON_ID = '1';
const RATE = Number(1000);
const DURATION = '11s';

http.setResponseCallback(http.expectedStatuses(201, 409));

const issued = new Counter('coupon_issued');
const soldOut = new Counter('coupon_sold_out');
const duplicated = new Counter('coupon_duplicated');
const unexpected = new Counter('coupon_unexpected');

export const options = {
    scenarios: {
        open_rush: {
            executor: 'constant-arrival-rate',
            rate: RATE,
            timeUnit: '1s',
            duration: DURATION,
            preAllocatedVUs: Math.ceil(RATE * 0.5),
            maxVUs: RATE * 2,
        }
    },
    thresholds: {
        dropped_iterations: ['count<1'],
        'http_req_failed{name:issue}': ['rate<0.001'],
        coupon_unexpected: ['count<1'],
        'http_req_duration{status:201}': ['p(99)<500'],
    },
    summaryTrendStats: ['avg', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};


export function setup() {
    const res = http.get(`${BASE_URL}/api/coupons/${COUPON_ID}/stock`);
    if (res.status !== 200) {
        throw new Error(`서버 또는 쿠폰 준비 안 됨: ${res.status} ${res.body}`);
    }
    const stock = res.json();
    console.log(`시작 상태 total=${stock.totalQuantity} issued=${stock.issuedQuantity}`);
    return stock;
}

export default function () {
    const userId = exec.scenario.iterationInTest + 1;

    const res = http.post(
        `${BASE_URL}/api/coupons/${COUPON_ID}/issue`,
        null,
        {
            headers: { 'X-USER-ID': String(userId) },
            tags: { name: 'issue' },
            timeout: '10s',
        },
    );

    if (res.status === 201) {
        issued.add(1);
    } else if (res.status === 409) {
        if (res.body === 'COUPON_SOLD_OUT') soldOut.add(1);
        else if (res.body === 'ALREADY_ISSUED') duplicated.add(1);
        else unexpected.add(1);
    } else {
        unexpected.add(1);
    }

    check(res, {
        '201 or 409': (r) => r.status === 201 || r.status === 409,
    });
}

export function teardown(before) {
    const res = http.get(`${BASE_URL}/api/coupons/${COUPON_ID}/stock`);
    const after = res.json();
    console.log(`종료 상태 total=${after.totalQuantity} issued=${after.issuedQuantity} (이번 실행 발급 ${after.issuedQuantity - before.issuedQuantity})`);
}
