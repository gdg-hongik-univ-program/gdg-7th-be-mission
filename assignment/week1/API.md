# API 명세서 (쉬운 버전)

모든 요청과 응답은 JSON으로 주고받아요.
주소는 `/api`로 시작해요.

## 에러가 나면?

항상 이렇게 알려줘요.

```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "없는 상품이에요."
}
```

| 상황 | 상태 코드 | code |
|------|-----------|------|
| 요청 값이 잘못됨 | 400 | INVALID_REQUEST |
| 상품이 없음 | 404 | PRODUCT_NOT_FOUND |
| 같은 이름의 상품이 이미 있음 | 409 | DUPLICATE_PRODUCT_NAME |
| 재고가 모자람 | 409 | INSUFFICIENT_STOCK |

---

# 소비자 (손님)

## 1. 상품 조회

상품 이름으로 상품을 찾아요.
여기서 받은 `productId`로 나중에 구매해요.

- 방식: `GET`
- 주소: `/api/products?name=키보드`

**보내는 값**: 주소 뒤에 `name`(상품 이름)만 붙여요.

**받는 값**

```json
{
  "productId": 1,
  "name": "키보드",
  "price": 30000,
  "stock": 15
}
```

- `productId`: 상품 번호
- `name`: 상품 이름
- `price`: 1개 가격
- `stock`: 남은 재고

**에러**: 그런 이름의 상품이 없으면 404

---

## 2. 상품 구매

상품을 한 번에 여러 개 살 수 있어요.
하나라도 재고가 모자라면 전부 취소돼요.

- 방식: `POST`
- 주소: `/api/orders`

**보내는 값**

```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

- `productId`: 살 상품 번호
- `quantity`: 살 개수 (1 이상)
- 같은 상품 번호를 두 번 넣으면 안 돼요.

**받는 값**

```json
{
  "totalPrice": 75000,
  "items": [
    { "name": "키보드", "quantity": 2, "price": 60000 },
    { "name": "마우스", "quantity": 1, "price": 15000 }
  ]
}
```

- `totalPrice`: 전부 합친 금액
- `name`: 상품 이름
- `quantity`: 산 개수
- `price`: 그 상품에 쓴 금액 (1개 가격 × 개수)

**에러**
- 없는 상품 번호가 있으면 404
- 재고보다 많이 사려고 하면 409

---

# 관리자

## 1. 상품 등록

새 상품을 만들어요.
같은 이름이 이미 있으면 에러가 나요.

- 방식: `POST`
- 주소: `/api/admin/products`

**보내는 값**

```json
{
  "name": "키보드",
  "price": 30000,
  "stock": 20
}
```

- `name`: 상품 이름
- `price`: 1개 가격
- `stock`: 처음 재고

**받는 값**

```json
{
  "productId": 1,
  "name": "키보드",
  "price": 30000,
  "stock": 20
}
```

만들어진 상품 번호(`productId`)를 알려줘요.
재고 추가나 삭제할 때 필요해서 넣었어요.

**에러**: 같은 이름이 이미 있으면 409

---

## 2. 재고 추가

있는 상품의 재고를 늘려요.

- 방식: `PATCH`
- 주소: `/api/admin/products/1/stock`
  (`1` 자리에 상품 번호를 넣어요)

**보내는 값**

```json
{
  "quantity": 10
}
```

- `quantity`: 추가할 개수 (1 이상)

**받는 값**

```json
{
  "name": "키보드",
  "stock": 30
}
```

- `name`: 상품 이름
- `stock`: 늘린 뒤 최종 재고

**에러**: 없는 상품 번호면 404

---

## 3. 상품 삭제

상품을 지워요. 여러 개를 한 번에 지울 수 있어요.
지운 뒤에 남은 상품 목록을 알려줘요.

- 방식: `DELETE`
- 주소: `/api/admin/products`

**보내는 값**

```json
{
  "productIds": [1, 2]
}
```

- `productIds`: 지울 상품 번호들

**받는 값**

```json
{
  "products": [
    { "name": "마우스", "stock": 8 },
    { "name": "모니터", "stock": 3 }
  ]
}
```

- 남은 상품의 이름과 재고예요.
- 다 지웠으면 `[]`(빈 목록)이에요.

**에러**: 없는 상품 번호가 하나라도 있으면 404 (아무것도 안 지워져요)

---

# 한눈에 보기

| 누가 | 기능 | 방식 | 주소 |
|------|------|------|------|
| 손님 | 상품 조회 | GET | `/api/products?name=이름` |
| 손님 | 상품 구매 | POST | `/api/orders` |
| 관리자 | 상품 등록 | POST | `/api/admin/products` |
| 관리자 | 재고 추가 | PATCH | `/api/admin/products/{상품번호}/stock` |
| 관리자 | 상품 삭제 | DELETE | `/api/admin/products` |