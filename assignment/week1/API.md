소비자 (User)

1. 상품 조회
- 조건 : id 또는 name 중 하나만 입력, ID는 1 이상, 상품명은 공백 없이 정확히 일치하는 이름으로 검색
- GET, /api/v1/products?id=1 또는 /api/v1/products?name=키보드
- Request Body: 없음
- 응답 : 단일 상품 객체
```json
{ "id": 1, "name": "키보드", "price": 30000, "stock": 10 }
```
- 정상 응답 : 200(상품 조회 성공)
- 실패 응답 : 400(조회 조건 누락·동시 입력·잘못된 값), 404(상품 없음)

2. 상품 구매
- 조건 : 여러 종류의 상품을 복수로 구매 가능, ID로 주문, 수량은 1 이상
- 요청한 순서대로 상품 존재 여부, 수량, 재고를 검증하고 재고를 차감
- 동일 상품이 여러 번 요청되면 앞선 요청에서 차감된 재고를 기준으로 검증
- 하나의 트랜잭션으로 일괄 구매 처리하며, 검증 또는 처리 중 오류가 발생하면 앞서 차감한 재고를 포함한 전체 변경을 롤백 (부분 구매 없음)
- POST, /api/v1/orders
- Request Body: 객체 배열
```json
[
  { "productId": 1, "quantity": 2 },
  { "productId": 3, "quantity": 1 }
]
```
- 응답 : 모든 상품 구매에 성공한 경우 구매 상품 목록과 합계
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
- 정상 응답 : 201(모든 상품 구매 성공)
- 실패 응답 : 400(수량이 0 이하), 404(해당 상품 없음), 409(재고 부족)
- 순차 처리 중 처음 발생한 검증 오류를 반환하며, 전체 구매를 취소하고 재고 변경을 반영하지 않음

관리자 (Admin)

1. 상품 등록
- 조건 : 동일 ID 또는 이름의 상품이 존재하면 에러, 여러 개 등록 가능
- 여러 상품 중 실패한 상품은 건너뛰고 나머지 상품만 등록
- POST, /api/v1/admin/products
- 요청 body :
```json
[
  { "name": "키보드", "price": 30000, "stock": 10 },
  { "name": "마우스", "price": 20000, "stock": 5 }
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
