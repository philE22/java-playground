# 쿠폰 ERD

요구사항: [README.md](../README.md)

```mermaid
erDiagram
    COUPON ||--o{ COUPON_ISSUE : "발급"
    
    COUPON {
        bigint id PK
        string coupon_name
        int stock
        datetime started_at
        datetime ended_at
    }
    
    COUPON_ISSUE {
        bigint id PK
        bigint coupon_id FK,UK
        bigint user_id UK
        datetime issued_at
    }
```
