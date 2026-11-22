소비자 (User)

0. 전체 상품 조회
- 조건 : 없음
- GET, /api/v1/products
- Request Body: 없음
- 응답 : 전체 상품 객체
```json
[
  { "id": 1, "itemName": "키보드", "category": "전자기기", "price": 30000, "quantity": 10 },
  { "id": 2, "itemName": "마우스", "category": "전자기기", "price": 10000, "quantity": 10 }
]
```
- 정상 응답 : 200(상품 조회 성공)
- 상품이 없으면 빈 배열 반환

1. 상품 조회
- 조건 : 상품명은 공백 없이 정확히 일치하는 이름으로 검색
- GET, /api/v1/products?itemName=키보드
- Request Body: 없음
- 응답 : 단일 상품 객체
```json
{ "id": 1, "itemName": "키보드", "category": "전자기기", "price": 30000, "quantity": 10 }
```
- 정상 응답 : 200(상품 조회 성공)
- 실패 응답 : 404(상품 없음)

2. 상품 구매
- 조건 : 여러 종류의 상품을 복수로 구매 가능, ID로 주문, 수량은 1 이상
- 요청한 순서대로 상품 존재 여부, 수량, 재고를 검증하고 재고를 차감
- 동일 상품이 여러 번 요청되면 앞선 요청에서 차감된 재고를 기준으로 검증
- 하나의 트랜잭션으로 일괄 구매 처리하며, 검증 또는 처리 중 오류가 발생하면 앞서 차감한 재고를 포함한 전체 변경을 롤백 (부분 구매 없음)
- POST, /api/v1/orders
- Request Body: 객체 배열
```json
[
  { "productId": 1, "count": 2 },
  { "productId": 3, "count": 1 }
]
```
- 응답 : 모든 상품 구매에 성공한 경우 구매 상품 목록과 합계
```json
{
  "items": [
    {
      "productId": 1,
      "itemName": "키보드",
      "count": 2,
      "subtotal": 60000,
      "quantity": 8
    },
    {
      "productId": 3,
      "itemName": "마우스",
      "count": 1,
      "subtotal": 20000,
      "quantity": 4
    }
  ],
  "totalPrice": 80000
}
```
- 정상 응답 : 201(모든 상품 구매 성공)
- 실패 응답 : 400(빈 배열, null 항목, 상품 ID 누락 또는 0 이하, 수량이 0 이하), 404(해당 상품 없음), 409(재고 부족)
- 순차 처리 중 처음 발생한 검증 오류를 반환하며, 전체 구매를 취소하고 재고 변경을 반영하지 않음

관리자 (Admin)

1. 상품 등록
- 조건 : 상품명과 카테고리는 필수이며 가격과 초기 재고는 0 이상. 이미 등록된 상품명은 사용할 수 없음
- 카테고리 : 의류, 전자기기, 화장품, 식품
- 여러 상품을 한 번에 등록할 수 있으며, 하나라도 실패하면 전체 롤백
- POST, /api/v1/admin/products
- 요청 body :
```json
[
  { "itemName": "키보드", "category": "전자기기", "price": 30000, "quantity": 10 },
  { "itemName": "마우스", "category": "전자기기", "price": 20000, "quantity": 5 }
]
```
- 응답 : 등록에 성공한 상품 목록
```json
[
  { "id": 1, "itemName": "키보드", "category": "전자기기", "price": 30000, "quantity": 10 },
  { "id": 3, "itemName": "마우스", "category": "전자기기", "price": 20000, "quantity": 5 }
]
```
- 정상 응답 : 201(전체 등록 성공)
- 실패 응답 : 400(잘못된 요청 또는 이미 등록된 상품명)

2. 재고 추가
- 조건 : 추가 수량은 1 이상, 하나라도 실패하면 전체 롤백
- PATCH, /api/v1/admin/products/stock
- 요청 body :
```json
[
  { "itemName": "키보드", "quantity": 2 },
  { "itemName": "마우스", "quantity": 1 }
]
```
- 응답 : 재고 추가에 성공한 상품 목록
```json
[
  { "id": 1, "itemName": "키보드", "price": 30000, "quantity": 12 },
  { "id": 3, "itemName": "마우스", "price": 20000, "quantity": 6 }
]
```
- 정상 응답 : 200(일괄 처리 성공)
- 실패 응답 : 400(잘못된 요청), 404(상품 없음)

3. 상품 삭제
- 조건 : 한 번에 여러 개 삭제 가능, 하나라도 실패하면 전체 롤백
- DELETE, /api/v1/admin/products
- 요청 body :
```json
[
  { "itemName": "키보드" },
  { "itemName": "마우스" }
]
```
- 응답 : 삭제에 성공한 상품 목록
```json
[
  { "id": 1, "itemName": "키보드", "category": "전자기기", "price": 30000, "quantity": 10 },
  { "id": 3, "itemName": "마우스", "category": "전자기기", "price": 20000, "quantity": 5 }
]
```
- 정상 응답 : 200(일괄 처리 성공)
- 실패 응답 : 400(잘못된 요청), 404(상품 없음)
- 실패 응답 : 401(인증되지 않음), 403(관리자 권한 없음)
