소비자 (User)
1. 상품 조회 
- 조건 : 동일한 상품명은 하나만 존재한다고 가정(시스템적으로 강제 x)
- GET, /api/products
- Request Body: 없음
- 응답 : 
```json
[
  {
    "id": ,
    "name": "",
    "price": ,
    "stock": 
  }
]
```
- 정상 응답 : 200
- 실패 응답 : 404(해당 상품 없음)

2.  상품 구매 
- 조건 : 한 종류의 상품을 복수로 구매 가능, ID로 주문, 갯수 < 0 이면 주문 불가능
- POST, /api/products/{productId}/purchases
- Request Body: 수량
```json
[
  {
      "quantity":
  }
]
```
- 응답 : 
```json
{
  "productId": ,
  "productName": "",
  "quantity": ,
  "totalPrice": ,
  "remainingStock": 
}
```
- 정상 응답 : 201
- 실패 응답 : 400(수량이 0이하임), 404(해당 상품 없음), 409(재고 없음)

관리자 (Admin)
1. 상품 등록
- 조건 : 동일 이름의 상품이 존재하면 에러
- POST, /api/admin/products
- 요청 body :
```json
{
  "id": ,
  "name": " ",
  "price": ,
  "stock": 
}
```
- 응답 :
```json
{
  "id": ,
  "name": " ",
  "price": ,
  "stock": 
}//요청과 같음
```
- 성공 응답 : 201
- 실패 응답 : 400(재고 수량이 0이하) 409( 중복 이름)

2. 재고 추가
- PATCH, /api/admin/products/{productId}/stock
- 요청 body :
```json
{
  "quantity": 
}
```

- 응답 :
```json
{
  "id": ,
  "name": "",
  "price": ,
  "stock": 
}
```
- 성공 응답 : 200
- 실패 응답 : 400(수량 잘못됨), 404(상품 없음)

3. 상품 삭제
- 조건 : 한 번에 여러 개도 삭제 가능
- DELETE, /api/admin/products/{productId}
- 요청 body : 
```json
{
  "productIds": [1, 2, 3]
}
```
- 응답 :
```json
{
    "id": ,
    "name": "",
    "price": ,
    "stock": 
}
```
- 성공 응답 : 200
- 실패 응답 : 404(없는 상품), 401(권한 없음)