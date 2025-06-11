# 전환형 프로젝트 설계 - 편의점 주문 시스템

## 🎯 목표
- 콘솔 프로그램 → REST API → MSA 구조로 전환되는 백엔드 프로젝트 구축
- 도메인 중심 설계, 확장 가능한 구조, 테스트 중심 개발 경험 확보

---

## 1. 기술 스택
| 단계 | 기술                                                  |
|------|-----------------------------------------------------|
| 공통 | Java 21, Gradle, JUnit5, Mockito                    |
| API | Spring Boot, Spring Web, JPA, MySQL, Redis, Swagger |
| MSA | Spring Cloud Gateway, Eureka, Config, Redis, Docker |

---

## 2. 프로젝트 단계별 전환 흐름

### 🧱 1단계: 콘솔 기반 도메인 설계
- 객체지향 설계 + 도메인 모델링 기반 구조 수립
- 구성:
  - 도메인: `Product`, `Order`, `OrderItem`
  - 서비스: 비즈니스 로직을 담당 (`OrderService`, `ProductService`)
  - 입출력: `ConsoleView`
- 흐름:
  1. 상품 목록 조회
  2. 주문 생성
  3. 주문 목록 출력

### 🌐 2단계: REST API 어플리케이션 확장
- API 서버로 구조 전환, API 명세 및 응답 설계
- 기술스택: Spring Boot, JPA, MySQL, Swagger
- 구성:
  - Controller → Service → Domain → Repository 계층화
  - Swagger/OpenAPI 문서 자동화
  - 단위 테스트: JUnit5, Mockito

### ☁️ 3단계: MSA 구조로 전환
- 서비스 분리 + 통신/라우팅 구조 구현
- 기술스택: Spring Cloud (Eureka, Gateway, Config), Docker, Redis
- 서비스 분리 예:
  - `user-service`: 사용자 등록/조회/인증
  - `product-service`: 상품 등록/조회
  - `order-service`: 주문 생성/조회
- 공통 요소:
  - Config Server: 공통 설정 관리
  - Eureka Server: 서비스 레지스트리
  - Gateway: 진입 라우터 역할

---

## 3. 디렉토리 구조 (2단계: REST API 기준)
```
com.convenience
├── product
│   ├── controller
│   ├── model
│   ├── resource
│   └── service
├── order
│   ├── controller
│   ├── model
│   ├── resource
│   └── service
├── promotion
│   ├── model
│   ├── resource
│   └── service
├── common
│   ├── config
│   ├── exception
│   └── util
└── Application.java
```

## 4. 설계 흐름

### 📦 주문 생성 (OrderService)
```
POST /orders
  ↓
OrderController.createOrder(request)
  ↓
OrderService.createOrder()
  ↓
- 상품 재고 검증 (ProductService.validateStock 재사용)
- 주문 정보 생성
- 주문 항목 저장
  ↓
OrderRepository.save()
```

### ✅ 주문 유효성 검사
```
POST /orders/validate
  ↓
OrderController.validateOrder(request)
  ↓
ProductService.validateOrderItems(request.items)
  ↓
- 상품 ID 존재 여부 확인
- 각 상품 재고 수량 확인
  ↓
검증 결과 응답
```

+ 주문 생성 시에도 재고 상태는 변경될 수 있으므로 동일한 유효성 검증 메서드 (`validateOrderItems`)로 최종 검증한다.


### 📌 상품-주문 간 연관관계
- `Order` → `OrderItem` (1:N)
- `OrderItem` → `Product` (N:1)

---

## ✅ 확장 아이디어
- Kafka로 주문 이벤트 발행
- 주문 상태 변경을 위한 스케줄러 (예: 미결제 자동 취소)
- 관리자 전용 상품/재고 관리 API
- CI/CD 자동화 (GitHub Actions 기반)
