# 상품 API 명세서

## 한눈에 보기

API는 소비자용 3개(`/products`)와 관리자용 3개(`/admin/products`)로 나뉨.

| 구분 | 기능 | 방식 | 주소 |
| --- | --- | --- | --- |
| 소비자 | 전체 상품 목록 조회 | `GET` | `/products` |
| 소비자 | 상품명 단건 조회 | `GET` | `/products?itemName={상품명}` |
| 소비자 | 장바구니 일괄 구매 | `POST` | `/products/purchase` |
| 관리자 | 상품 등록 | `POST` | `/admin/products` |
| 관리자 | 재고 추가 | `PATCH` | `/admin/products/stock` |
| 관리자 | 상품 삭제 | `DELETE` | `/admin/products` |

## 공통 규칙

모든 요청과 응답은 JSON으로 주고받음. 에러가 나면 항상 아래 형식으로 응답함.

```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "없는 상품이에요."
}
```

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 요청 값이 잘못됨 | 400 | `INVALID_REQUEST` |
| 상품이 없음 | 404 | `PRODUCT_NOT_FOUND` |
| 같은 이름의 상품이 이미 있음 | 409 | `DUPLICATE_PRODUCT_NAME` |
| 재고가 모자람 | 409 | `INSUFFICIENT_STOCK` |

## 소비자 1.1 전체 상품 목록 조회

홈 화면에 처음 들어갈 때 등록된 전체 상품 리스트를 조회함. 여기서 받은 `id`로 나중에 구매함.

| 항목 | 내용 |
| --- | --- |
| 방식 | `GET` |
| 주소 | `/products` |
| 보내는 값 | 없음 |

**응답 예시**

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

| 필드 | 설명 |
| --- | --- |
| `id` | 상품 번호 |
| `itemName` | 상품 이름 |
| `price` | 1개 가격 |
| `quantity` | 남은 재고 |
| `category` | 상품 종류 (`의류`, `전자기기`, `화장품`, `식품` 중 하나) |

**에러**: 없음

## 소비자 1.2 상품명 단건 조회

상단 검색창에 상품명을 입력해 특정 상품 정보를 조회함. 예를 들어 `/products?itemName=원피스`로 요청하면 '원피스' 정보만 조회됨.

| 항목 | 내용 |
| --- | --- |
| 방식 | `GET` |
| 주소 | `/products?itemName={상품명}` |
| 보내는 값 | `itemName` (쿼리 파라미터, 조회할 상품명) |

**응답 예시**

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

| 필드 | 설명 |
| --- | --- |
| `id` | 상품 번호 |
| `itemName` | 상품 이름 |
| `price` | 1개 가격 |
| `quantity` | 남은 재고 |
| `category` | 상품 종류 (`의류`, `전자기기`, `화장품`, `식품` 중 하나) |

**에러**

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 그런 이름의 상품이 없음 | 404 | `PRODUCT_NOT_FOUND` |



## 소비자 1.3 장바구니 일괄 구매

상품을 한 번에 여러 개 살 수 있음. 하나라도 재고가 모자라면 전부 취소됨.

| 항목 | 내용 |
| --- | --- |
| 방식 | `POST` |
| 주소 | `/products/purchase` |

**요청 예시**

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

| 필드 | 설명 |
| --- | --- |
| `id` | 구매할 상품 번호 (같은 번호를 두 번 넣을 수 없음) |
| `count` | 구매할 개수 (1 이상) |

**응답 예시**

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

| 필드 | 설명 |
| --- | --- |
| `totalPrice` | 전체 구매 총액 |
| `items` | 구매한 상품 목록 |
| `items[].itemName` | 상품 이름 |
| `items[].count` | 구매 수량 |
| `items[].price` | 그 상품에 쓴 금액 (1개 가격 × 구매 수량) |

**에러**

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 없는 상품 번호가 있음 | 404 | `PRODUCT_NOT_FOUND` |
| 재고보다 많이 사려고 함 | 409 | `INSUFFICIENT_STOCK` |



## 관리자 2.1 상품 등록

새 상품을 등록함. 같은 이름이 이미 있으면 에러가 남. 응답으로 만들어진 상품 번호(`id`)를 알려주며, 재고 추가나 삭제할 때 참고할 수 있음.

| 항목 | 내용 |
| --- | --- |
| 방식 | `POST` |
| 주소 | `/admin/products` |

**요청 예시**

```json
{
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

| 필드 | 설명 |
| --- | --- |
| `itemName` | 상품 이름 |
| `price` | 1개 가격 |
| `quantity` | 남은 재고 |
| `category` | 상품 종류 (`의류`, `전자기기`, `화장품`, `식품` 중 하나) |

**응답 예시**

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 13,
  "category": "의류"
}
```

**에러**

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 같은 이름의 상품이 이미 있음 | 409 | `DUPLICATE_PRODUCT_NAME` |



## 관리자 2.2 재고 추가

상품명과 추가할 양을 보내면 이미 있는 상품의 재고를 늘림. 응답으로 늘어난 재고가 반영된 상품 정보를 줌.

| 항목 | 내용 |
| --- | --- |
| 방식 | `PATCH` |
| 주소 | `/admin/products/stock` |

**요청 예시**

```json
{
  "itemName": "원피스",
  "quantity": 5
}
```

| 필드 | 설명 |
| --- | --- |
| `itemName` | 재고를 추가할 상품 이름 |
| `quantity` | 추가할 개수 (1 이상) |

**응답 예시**

```json
{
  "id": 1,
  "itemName": "원피스",
  "price": 1000,
  "quantity": 18,
  "category": "의류"
}
```

| 필드 | 설명 |
| --- | --- |
| `id` | 상품 번호 |
| `itemName` | 상품 이름 |
| `price` | 1개 가격 |
| `quantity` | 추가 후 남은 재고 |
| `category` | 상품 종류 (`의류`, `전자기기`, `화장품`, `식품` 중 하나) |

**에러**

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 없는 상품명임 | 404 | `PRODUCT_NOT_FOUND` |



## 관리자 2.3 상품 삭제 (상품명 기준)

상품명에 해당하는 상품을 한 번에 한 개만 삭제함. 삭제한 뒤 남은 전체 상품 목록을 응답으로 줌.

| 항목 | 내용 |
| --- | --- |
| 방식 | `DELETE` |
| 주소 | `/admin/products` |

**요청 예시**

```json
{
  "itemName": "원피스"
}
```

| 필드 | 설명 |
| --- | --- |
| `itemName` | 삭제할 상품 이름 |

**응답 예시**

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

| 필드 | 설명 |
| --- | --- |
| `id` | 상품 번호 |
| `itemName` | 상품 이름 |
| `price` | 1개 가격 |
| `quantity` | 남은 재고 |
| `category` | 상품 종류 (`의류`, `전자기기`, `화장품`, `식품` 중 하나) |

**에러**

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 없는 상품명임 (아무것도 삭제되지 않음) | 404 | `PRODUCT_NOT_FOUND` |