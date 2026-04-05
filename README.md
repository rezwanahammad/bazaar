# Bazaar

A Spring Boot based online marketplace.

## Project Description
The project aims to deliver a simple yet elegant online fashion market.The webapp focuses on men's tops and bottom wears and provides a minimalistic but intuitive category based navigation UI so that users can easily find the product of their desire.

Here users can browse their favorite products and add them to card or buy them. The sellers can add/create products and manages the product inventories. The admins oversee the buy/sell interaction and also controls the order processing and delivery. 

## Architecture
Architecture type: Monolithic layered web application (Spring MVC + service + repository) with server-side rendered Thymeleaf views.

The app contains of a Spring MVC based frontend written using ThymeLeaf for the frontend architecture. The HTTP endpoints follow REST naming principles and connect the frontend to the business logic layer. The backend database is a PostgresDB hosted on render as a service. Spring security is used to provide authentication and authorization.

![alt text](<architecture SEPM.png>)

## ER Diagram 
The entities in the project are described below:
1. Users: The users who actually interact with the system. Divided into 3 roles:
    A. Buyer: Can buy(order) products or add them to cart. Also can provide reviews to products.
    B. Seller: Manages product inventory and adds/removes products as needed.
    C. Admin: Monitors all interactions. Manages order processing and payment system.
2. Products: The products are categorized items based on their types. Each product is listed by a seller.
3. Reviews: User's opinion on a product. Contains a rating and a text description of the user review.
4. Cart Items: The direct many-to-many cart link between users and products. Stores a user-product pair for each item in cart.
5. Order Items: Products involved in a order placed by the user. Cannot be changed.
6. Orders: A list of order items that comprises an order placed by the user.
7. Payments: A record of payments made by users.

The relationships are described below:
1. User (1:M) Orders: An user can place multiple orders.
2. Order (1:M) Order Items: An order can contain one or more order items.
3. Product (1:M) Order Items: A product can appear in many order items across many orders.
4. User (M:N) Product for Cart: A user can have many products in cart and a product can appear in many users' carts.
5. Order (1:1) Payment: Payment for each order has to be paid in a single payment transaction.
6. Product (1:M) Reviews: A product can have multiple reviews. 

![alt text](<ER SEPM.png>)

## API Endpoints
Note: This app is server-rendered (Thymeleaf), so these are HTTP web endpoints (not JSON REST APIs).

- `GET /`: Home page with featured active products.
- `GET /error`: Generic error page.
- `GET /register`: Registration form page.
- `POST /register`: Create a new user account.
- `GET /login`: Login page.
- `GET /profile`: Authenticated user profile page.
- `GET /products`: Product catalog with optional category filter.
- `GET /products/{id}`: Product details with related products and reviews.
- `POST /products/{id}/reviews`: Submit a review and rating for a product.
- `GET /cart`: View current user cart summary.
- `POST /cart/add`: Add a product to the current user's cart.
- `POST /cart/remove`: Remove a product from the current user's cart.
- `GET /checkout`: Checkout page with cart totals and payment methods.
- `POST /checkout/place`: Place an order from the current user's cart using the selected payment method.
- `GET /orders`: View orders of the logged-in user.
- `GET /orders/{orderId}/success`: Order success/confirmation page.
- `GET /admin`: Admin dashboard summary.
- `GET /admin/users`: List all users for admin management.
- `POST /admin/users/{id}/make-admin`: Promote a user to admin role.
- `POST /admin/users/{id}/remove-admin`: Demote an admin back to buyer role.
- `POST /admin/users/{id}/make-seller`: Promote a user to seller role.
- `POST /admin/users/{id}/remove-seller`: Demote a seller back to buyer role.
- `GET /admin/products`: Admin product management list.
- `GET /admin/products/create`: Admin product creation form.
- `POST /admin/products`: Admin create product action.
- `GET /admin/products/{id}/edit`: Admin product edit form.
- `POST /admin/products/{id}`: Admin update product action.
- `POST /admin/products/{id}/delete`: Admin delete product action.
- `GET /admin/payments`: Admin list and review payment submissions.
- `POST /admin/payments/{paymentId}/approve`: Approve a payment.
- `POST /admin/payments/{paymentId}/reject`: Reject a payment with optional reason.
- `GET /seller/products`: Seller product management list (own products).
- `GET /seller/products/create`: Seller product creation form.
- `POST /seller/products`: Seller create product action.
- `GET /seller/products/{id}/edit`: Seller edit form for owned product.
- `POST /seller/products/{id}`: Seller update owned product.
- `POST /seller/products/{id}/delete`: Seller delete owned product.

## CI/CD
CI is implemented with GitHub Actions in `.github/workflows/maven.yml`, which runs on pushes and pull requests to `main`, sets up JDK 21, caches Maven dependencies, and runs `./mvnw clean verify` with the `test` profile. This test verifiacation phase is essential to ensure authorized and valid pushes to main.

CD is container-oriented in this repository: the project includes a production-ready `Dockerfile` and `docker-compose.yml` to build and run the app with Postgres service hosted on render. Render deploys the project fronted as a webservice using the docker configuration from github repo/main branch. The env variables are provided in the docker webservice environment.

## Run Instructions
IMPORTANT NOTE: The .env file is mandatory to have for the project to run properly.

1. To run tests only: mvn clean test
2. To run the project using maven:  ./mvnw spring-boot:run
3. To build the project using docker: docker compose up --build

To run using your own .env file the following environment variables must be provided:
1. POSTGRES_DB
2. POSTGRES_USER
3. POSTGRES_PASSWORD
4. SPRING_DATASOURCE_URL
5. SPRING_DATASOURCE_USERNAME
6. SPRING_DATASOURCE_PASSWORD

## Live Deployment URL
Published app URL: `https://bazaar-fm9o.onrender.com/`
