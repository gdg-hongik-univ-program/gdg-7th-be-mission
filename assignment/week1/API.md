# 쇼핑몰 API 명세서 예시

> 1주차에서는 DTO를 사용하지 않고 `Product` 도메인 객체와 기본 컬렉션(`List`, `Map`)을 활용해 요청/응답을 처리합니다.

---

## 1. 소비자 (User) API

### 1-1. 상품 조회
특정 상품명으로 상품 정보를 조회합니다.

- **HTTP Method**: `GET`
- **URL**: `/products?name={상품명}`
- **Request**: Query Parameter (`name`)
- **Response**:
```json
{
  "id": 1,
  "name": "키보드",
  "price": 50000,
  "stock": 10
}
```

---

### 1-2. 상품 구매
하나 이상의 상품을 한 번에 구매합니다.

- **HTTP Method**: `POST`
- **URL**: `/products/purchase`
- **Request**:
```json
[
  {
    "id": 1,
    "stock": 2
  },
  {
    "id": 2,
    "stock": 1
  }
]
```
- **Response**:
  - `purchasedProducts`의 각 항목에서 `stock`은 구매 수량, `price`는 해당 상품의 총 소비 금액을 나타냅니다.
```json
{
  "totalPrice": 130000,
  "purchasedProducts": [
    {
      "id": 1,
      "name": "키보드",
      "price": 100000,
      "stock": 2
    },
    {
      "id": 2,
      "name": "마우스",
      "price": 30000,
      "stock": 1
    }
  ]
}
```

---

## 2. 관리자 (Admin) API

### 2-1. 상품 등록
새로운 상품을 등록합니다.

- **HTTP Method**: `POST`
- **URL**: `/admin/products`
- **Request**:
```json
{
  "name": "키보드",
  "price": 50000,
  "stock": 10
}
```
- **Response**:
```json
{
  "id": 1,
  "name": "키보드",
  "price": 50000,
  "stock": 10
}
```

---

### 2-2. 재고 추가
기존 상품의 재고를 추가합니다.

- **HTTP Method**: `PATCH`
- **URL**: `/admin/products/stock`
- **Request**:
```json
{
  "id": 1,
  "stock": 5
}
```
- **Response**:
```json
{
  "id": 1,
  "name": "키보드",
  "price": 50000,
  "stock": 15
}
```

---

### 2-3. 상품 삭제
하나 이상의 상품을 한 번에 삭제하고, 현재 남아있는 상품 목록을 반환합니다.

- **HTTP Method**: `DELETE`
- **URL**: `/admin/products`
- **Request**:
```json
[1, 2]
```
- **Response**:
```json
[
  {
    "id": 3,
    "name": "모니터",
    "price": 200000,
    "stock": 5
  }
]
```
