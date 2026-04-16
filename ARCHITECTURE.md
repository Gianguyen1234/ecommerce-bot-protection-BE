# Project Structure

Project được tổ chức theo kiểu **DDD + Hexagonal Architecture**, chạy dưới dạng **multi-module (modular monolith)**.

Mục tiêu của structure này:

```text
tách rõ business logic khỏi framework
giữ domain “sạch”
dễ mở rộng mà không biến thành spaghetti
```

---

## Tổng quan

```text
platform/
├── pom.xml                (aggregator - root)
├── build-parent/          (parent config)
├── domain/                (core business logic)
├── application/           (use cases)
├── infrastructure/        (technical implementation)
└── api/                   (entry point - Spring Boot)
```

---

## 1. Root (aggregator)

📁 `platform/pom.xml`

Vai trò:

```text
- chỉ để aggregate các module
- không chứa dependency
- không chứa logic
```

Nó đơn giản là:

```text
"gom tất cả module lại để build cùng nhau"
```

---

## 2. build-parent

📁 `build-parent/pom.xml`

Vai trò:

```text
- quản lý version (Spring Boot, Java, Lombok…)
- quản lý plugin
- shared config cho toàn bộ module
```

Hiểu đơn giản:

```text
nơi cấu hình chung, tránh lặp lại ở từng module
```

---

## 3. domain (trái tim của hệ)

📁 `domain/`

Đây là nơi quan trọng nhất.

Chứa:

* Entity / Value Object
* Rule (DetectionRule, RuleEngine…)
* Policy (DecisionPolicy)
* Port (interface cho infra)

Ví dụ:

```text
domain/
├── model/
├── rule/
├── decision/
├── port/
```

---

### Nguyên tắc

```text
KHÔNG phụ thuộc Spring
KHÔNG phụ thuộc database
KHÔNG phụ thuộc framework
```

---

Hiểu đúng:

```text
domain = logic thuần
```

---

## 4. application (use case layer)

📁 `application/`

Vai trò:

```text
- orchestrate flow
- gọi domain
- kết nối giữa api ↔ domain
```

Ví dụ:

```text
AnalyzeRequestUseCase
VerifyChallengeUseCase (sẽ có)
```

---

### Đặc điểm

```text
- không chứa business rule phức tạp
- chỉ điều phối
```

---

Hiểu đúng:

```text
application = "use case layer"
```

---

## 5. infrastructure

📁 `infrastructure/`

Vai trò:

```text
- implement các port từ domain
- kết nối Redis, DB, external system
```

Ví dụ:

```text
RedisRequestMetricsAdapter
RedisSuspicionAdapter
RedisChallengeAdapter
```

---

### Đặc điểm

```text
- phụ thuộc Spring
- phụ thuộc thư viện (Redis, Kafka…)
```

---

Hiểu đúng:

```text
infrastructure = "implementation detail"
```

---

## 6. api (entry point)

📁 `api/`

Đây là nơi:

```text
- chạy Spring Boot
- nhận HTTP request
- gọi application layer
```

Chứa:

* `BotDetectionFilter` (điểm chặn request)
* Controller (debug / challenge)
* Config (BeanConfig)

---

### Flow chính

```text
Request
 → Filter (BotDetectionFilter)
 → UseCase (application)
 → RuleEngine (domain)
 → DecisionPolicy (domain)
 → Response
```

---

## 7. Dependency Direction (rất quan trọng)

```text
api → application → domain
api → infrastructure → domain
```

---

### Domain KHÔNG biết:

```text
- api
- infrastructure
```

---

Hiểu đơn giản:

```text
mọi thứ phụ thuộc vào domain, không phải ngược lại
```

---

## 8. Tại sao lại làm phức tạp như vậy?

Vì nếu không:

```text
mọi thứ sẽ dồn vào controller/service
→ khó maintain
→ khó test
→ khó mở rộng
```

---

Còn với structure này:

```text
- domain có thể test độc lập
- thay Redis → không ảnh hưởng logic
- thêm rule → không đụng API
```

---

## 9. Tình trạng hiện tại

Structure này đã:

```text
✔ tách layer rõ ràng
✔ chuẩn DDD + hexagonal
✔ sẵn sàng mở rộng
```

---

Nhưng:

```text
anti-bot logic vẫn chỉ là backend
→ chưa đủ để chống bot thực tế
```

---

## 10. Kết luận

* Đây không phải là project anti-bot hoàn chỉnh
* Đây là:

```text
một backend core được tổ chức đúng cách
```

---

Nếu bạn quan tâm đến:

```text
cách tổ chức project backend
```

→ phần này đáng xem

---

Còn nếu bạn quan tâm đến:

```text
chống bot thực tế
```

→ phần này mới chỉ là điểm bắt đầu
