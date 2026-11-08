# Week 2 Postman 동작 확인

## 1. 상품 등록

- 요청: POST /admin/products
- 결과: 201 Created
- 상품명, 가격, 초기 재고를 입력하여 상품을 등록했습니다.

![상품 등록](<./images/postman-create week2.png>)

## 2. 상품 조회

- 요청: GET /products?name=keyboard
- 결과: 200 OK
- 등록한 상품의 ID, 이름, 가격, 재고를 확인했습니다.

![상품 조회](<./images/postman-get week2.png>)

## 3. 상품 삭제

- 요청: DELETE /admin/products
- 결과: 200 OK
- 응답의 남은 상품 목록에서 삭제된 상품이 제외됐습니다.

![상품 삭제](<./images/postman-delete week2.png>)

## 4. 삭제 후 재조회

- 요청: GET /products?name=keyboard
- 삭제한 상품을 다시 조회한 결과, 상품을 찾을 수 없는 것을 확인했습니다.

![삭제 후 재조회](<./images/postman-get2 week2.png>)