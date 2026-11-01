# API 명세서 (API Specification)

---

## 1. 소비자 (User) API

### 1-1. 상품 조회
특정 상품명으로 상품 정보를 조회합니다. (동일한 상품명은 하나만 존재)

#### Request
* **HTTP Method**: `GET`
* **URL**: `/api/v1/products/search?name={상품명}`
* **Request Query Parameter**:
    * `name` (string, required): 조회할 상품명

#### Response (200 OK) - 성공
```json
{
  "productId": 101,
  "name": "무선 마우스",
  "price": 25000,
  "stockQuantity": 50
}
```

#### Response (404 Not Found) - 에러: 등록되지 않은 상품명
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "해당 이름의 상품을 찾을 수 없습니다."
}
```

---

### 1-2. 상품 구매
하나 이상의 상품을 한 번에 구매합니다.

#### Request
* **HTTP Method**: `POST`
* **URL**: `/api/v1/orders`

#### Request Body
```json
{
  "items": [
    {
      "productId": 101,
      "quantity": 2
    },
    {
      "productId": 102,
      "quantity": 1
    }
  ]
}
```

#### Response (200 OK) - 성공
```json
{
  "totalAmount": 85000,
  "purchasedItems": [
    {
      "productName": "무선 마우스",
      "quantity": 2,
      "subtotalAmount": 50000
    },
    {
      "productName": "기계식 키보드",
      "quantity": 1,
      "subtotalAmount": 35000
    }
  ]
}
```

#### Response (400 Bad Request) - 에러: 수량 오류 (0 또는 음수)
```json
{
  "code": "INVALID_QUANTITY",
  "message": "구매 수량은 1개 이상이어야 합니다."
}
```

#### Response (400 Bad Request) - 에러: 재고 부족
```json
{
  "code": "OUT_OF_STOCK",
  "message": "무선 마우스 상품의 재고가 부족합니다.",
  "details": [
    {
      "productId": 101,
      "productName": "무선 마우스",
      "requestedQuantity": 5,
      "currentStock": 3
    }
  ]
}
```

#### Response (404 Not Found) - 에러: 존재하지 않는 상품 ID
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "요청한 상품(ID: 999)이 존재하지 않습니다."
}
```

---

## 2. 관리자 (Admin) API

### 2-1. 상품 등록
새로운 상품을 등록합니다. 

#### Request
* **HTTP Method**: `POST`
* **URL**: `/api/v1/admin/products`

#### Request Body
```json
{
  "name": "무선 마우스",
  "price": 25000,
  "stockQuantity": 50
}
```

#### Response (201 Created) - 성공
```json
{
  "productId": 101,
  "name": "무선 마우스",
  "price": 25000,
  "stockQuantity": 50
}
```

#### Response (400 Bad Request) - 에러: 유효하지 않은 입력값
```json
{
  "code": "INVALID_INPUT_VALUE",
  "message": "가격과 재고는 0 이상이어야 합니다."
}
```

#### Response (409 Conflict) - 에러: 동일 상품명 존재
```json
{
  "code": "DUPLICATE_PRODUCT_NAME",
  "message": "이미 존재하는 상품명입니다: 무선 마우스"
}
```

---

### 2-2. 재고 추가
기존 상품의 재고를 추가합니다. (상품 ID를 Request Body로 받도록 구조 변경)

#### Request 1. Body 이용하는 경우
* **HTTP Method**: `PATCH`
* **URL**: `/api/v1/admin/products/stock`

#### Request Body
```json
{
  "productId": 101,
  "quantityToAdd": 30
}
```
#### Request 2. path variable 이용하는 경우
* **HTTP Method**: `PATCH`
* **URL**: `/api/v1/admin/products/{productId}/stock`
* **Path Variable**: `productId (예: /api/v1/admin/products/101/stock)`
#### Request Body
```json
{
  "quantityToAdd": 30
}
```

#### Response (200 OK) - 성공
```json
{
  "productName": "무선 마우스",
  "finalStockQuantity": 80
}
```

#### Response (400 Bad Request) - 에러: 수량 오류
```json
{
  "code": "INVALID_QUANTITY",
  "message": "추가할 재고 수량은 1개 이상이어야 합니다."
}
```

#### Response (404 Not Found) - 에러: 등록되지 않은 상품
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "재고를 추가할 상품(ID: 101)이 존재하지 않습니다."
}
```

---

### 2-3. 상품 삭제
상품을 삭제합니다. 여러 개의 상품을 한 번에 삭제할 수 있습니다.

#### Request
* **HTTP Method**: `DELETE`
* **URL**: `/api/v1/admin/products`

#### Request Body
```json
{
  "productIds": [101, 102]
}
```

#### Response (200 OK) - 성공
```json
{
  "remainingProducts": [
    {
      "productName": "게이밍 헤드셋",
      "stockQuantity": 15
    },
    {
      "productName": "USB Hub",
      "stockQuantity": 100
    }
  ]
}
```

#### Response (404 Not Found) - 에러: 존재하지 않는 상품 삭제 시도
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "삭제하려는 상품 중 일부 또는 전체가 존재하지 않습니다. (ID: 102)"
}
```
