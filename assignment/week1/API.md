소비자 (User)

1. 상품 조회
- 조건 : 이름으로 검색
- GET, /api/v1/products?name=키보드
- Request Body: 없음
- 응답 :
```json
[
  { "id": 1, "name": "키보드", "price": 30000, "stock": 10 }
]
```
- 정상 응답 : 200(검색 결과가 없으면 빈 배열)
- 실패 응답 : 400(검색어 누락 또는 빈 문자열)

2. 상품 구매
- 조건 : 여러 종류의 상품을 복수로 구매 가능, ID로 주문, 수량은 1 이상
- 여러 상품 중 실패한 상품은 건너뛰고 나머지 상품만 구매
- POST, /api/v1/orders
- Request Body: 객체 배열
```json
[
  { "productId": 1, "quantity": 2 },
  { "productId": 3, "quantity": 1 }
]
```
- 응답 : 성공한 상품 목록과 합계
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "키보드",
      "quantity": 2,
      "subtotal": 60000,
      "remainingStock": 8
    },
    {
      "productId": 3,
      "productName": "마우스",
      "quantity": 1,
      "subtotal": 20000,
      "remainingStock": 4
    }
  ],
  "totalPrice": 80000
}
```
- 정상 응답 : 201(한 상품 이상 구매 성공), 200(모두 실패하면 빈 items와 totalPrice 0)
- 실패 조건 : 수량이 0 이하, 해당 상품 없음, 재고 부족

관리자 (Admin)

1. 상품 등록
- 조건 : 동일 ID 또는 이름의 상품이 존재하면 에러, 여러 개 등록 가능
- 여러 상품 중 실패한 상품은 건너뛰고 나머지 상품만 등록
- POST, /api/v1/admin/products
- 요청 body :
```json
[
  { "id": 1, "name": "키보드", "price": 30000, "stock": 10 },
  { "id": 3, "name": "마우스", "price": 20000, "stock": 5 }
]
```
- 응답 : 등록에 성공한 상품 목록
```json
[
  { "id": 1, "name": "키보드", "price": 30000, "stock": 10 },
  { "id": 3, "name": "마우스", "price": 20000, "stock": 5 }
]
```
- 정상 응답 : 201(한 상품 이상 등록 성공), 200(모두 실패하면 빈 배열)
- 실패 조건 : 재고 수량이 0 이하, 중복 ID 또는 이름

2. 재고 추가
- 조건 : 추가 수량은 1 이상, 실패한 상품은 건너뛰고 나머지 상품만 처리
- PATCH, /api/v1/admin/products/stock
- 요청 body :
```json
[
  { "productId": 1, "quantity": 2 },
  { "productId": 3, "quantity": 1 }
]
```
- 응답 : 재고 추가에 성공한 상품 목록
```json
[
  { "id": 1, "name": "키보드", "price": 30000, "stock": 12 },
  { "id": 3, "name": "마우스", "price": 20000, "stock": 6 }
]
```
- 정상 응답 : 200(모두 실패하면 빈 배열)
- 실패 조건 : 수량 잘못됨, 상품 없음

3. 상품 삭제
- 조건 : 한 번에 여러 개 삭제 가능, 실패한 상품은 건너뛰고 나머지 상품만 삭제
- DELETE, /api/v1/admin/products
- 요청 body :
```json
{
  "productIds": [1, 3]
}
```
- 응답 : 삭제에 성공한 상품 목록
```json
[
  { "id": 1, "name": "키보드", "price": 30000, "stock": 10 },
  { "id": 3, "name": "마우스", "price": 20000, "stock": 5 }
]
```
- 정상 응답 : 200(모두 실패하면 빈 배열)
- 실패 조건 : 상품 없음
- 실패 응답 : 401(인증되지 않음), 403(관리자 권한 없음)
