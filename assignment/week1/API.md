소비자 (User)

1. 상품 조회

GET /api/products?name=상품이름

JSON 응답
{
"id": 1,
"name": "상품이름",
"price": 50000,
"stock": 10
}

성공 -> 200 OK
오류
해당 이름의 상품이 없음 -> 404 Not Found

2. 상품 구매

POST /api/orders
Content-Type: application/json

JSON 요청
{
  "products": [
   {
     "productId": 1,
     "quantity": 2
   },
   {
     "productId": 2,
     "quantity": 1
   }
 ]
}

JSON 응답
{
    "totalPrice": 20000,
    "products": [
        {   "name": "상품이름",
            "quantity": 2,
            "amount": 10000
        },
        {   "name": "상품이름",
            "quantity": 1,
            "amount": 10000
        }
    ]
}

성공 -> 201 Created
오류
상품 목록이 비었거나 수량이 양의 정수가 아님 -> 400 Bad Request
존재하지 않는 상품 ID -> 404 Not Found
구매 수량보다 재고가 부족함 -> 409 Conflict


관리자 (Admin)

1. 상품 등록

POST /api/products
Content-Type: application/json

JSON 요청
{
     "name": "상품이름",
     "price": 3000,
     "stock": 100
}

JSON 응답

{
    "id": 1,
    "name": "상품이름",
    "price": 3000,
    "stock": 100
}

성공 -> 201 Created
오류
상품명이 비어 있거나 가격·재고가 유효하지 않음	-> 400 Bad Request
동일한 상품명이 이미 존재함 -> 409 Conflict

2. 재고 추가

POST /api/stock
Content-Type: application/json

JSON 요청
{
    "productId": 1,
    "quantity": 20
}

JSON 응답
{
    "name": "상품이름",
    "stock": 120
}

성공 -> 200 OK
오류
존재하지 않는 상품 ID -> 404 Not Found
추가할 수량이 유효하지 않음 -> 400 Bad Request


3. 상품 삭제

DELETE /api/products
Content-Type: application/json

JSON 요청
{
    "products":[
        {
            "productId": 1
        },
        {
            "productId": 2
        }
    ]
}

JSON 응답
{
    "products":[
        {
            "name": "상품이름",
            "stock": 100
        },
        {
            "name": "상품이름",
            "stock": 200
        }
    ]
}

성공 -> 200 OK
오류
존재하지 않는 상품 ID -> 404 Not Found
삭제할 상품의 목록이 비어 있는 경우 -> 400 Bad Request
