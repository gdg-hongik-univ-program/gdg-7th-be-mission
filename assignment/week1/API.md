## 1. 소비자 (User) API

### 1-1. 전체 상품 목록 조회

홈 화면 첫 진입 시 등록된 전체 상품 리스트를 조회합니다.

- **HTTP Method**: `GET`
- **URL**: `/products`
- **Request**: 없음
- **Response**:

```json
[
  {
    "id": 1,
    "itemName": "원피스",
    "price": 1000,
    "quantity": 13,
    "category": "의류"
  },
  {
    "id": 2,
    "itemName": "청바지",
    "price": 200,
    "quantity": 7,
    "category": "의류"
  }
]
```

---

### 1-2. 상품명 단건 검색

상단 검색창에 상품명을 입력해 특정 상품 정보를 조회합니다.

- **HTTP Method**: `GET`
- **URL**: `/products?itemName={상품명}`
    - 예시: `/products?itemName=원피스`
- **Request**: Query Parameter (`itemName`)
- **Response**:

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

---

### 1-3. 장바구니 상품 일괄 구매

프론트엔드 장바구니에 담긴 상품들의 `id`와 구매 수량(`count`) 목록을 보내 실제 재고를 차감하고 구매 결과를 반환받습니다.

- **HTTP Method**: `POST`
- **URL**: `/products/purchase`
- **Request Body**:

```json
[
  {
    "id": 1,
    "count": 2
  },
  {
    "id": 2,
    "count": 1
  }
]
```

- **Response Body**:
    - `totalPrice`: 전체 구매 총액
    - `items`: 구매 완료된 상품 목록 (`price`는 `단가 × 구매 수량(count)`인 해당 상품의 소비 금액)

```json
{
  "totalPrice": 2200,
  "items": [
    {
      "itemName": "원피스",
      "count": 2,
      "price": 2000
    },
    {
      "itemName": "청바지",
      "count": 1,
      "price": 200
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
- **Request Body**:

```json
{
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

- `category`는 **필수**이며 `"의류"`, `"전자기기"`, `"화장품"`, `"식품"` 중 하나여야 합니다.
- **Response Body**:

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

---

### 2-2. 재고 추가

관리자 화면에서 입력한 **상품명(`itemName`)** 과 **추가할 수량(`quantity`)** 을 받아 기존 재고에 더합니다.

- **HTTP Method**: `PATCH`
- **URL**: `/admin/products/stock`
- **Request Body**:

```json
{
  "itemName": "원피스",
  "quantity": 5
}
```

- **Response Body**:

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 18,
  "category": "의류"
}
```

---

### 2-3. 상품 삭제 (상품명 기준)

관리자 화면에서 입력한 **상품명(`itemName`)** 에 해당하는 상품을 삭제하고, 현재 남아있는 전체 상품 목록을 반환합니다.

- **HTTP Method**: `DELETE`
- **URL**: `/admin/products`
- **Request Body**:

```json
{
  "itemName": "원피스"
}
```

- **Response Body**:

```json
[
  {
    "id": 2,
    "itemName": "청바지",
    "price": 200,
    "quantity": 7,
    "category": "의류"
  }
]
```

---