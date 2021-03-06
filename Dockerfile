FROM maven:3.6.3-openjdk-11
WORKDIR app
COPY sc-application ./sc-application
COPY sc-common ./sc-common
COPY sc-mysql ./sc-mysql
COPY pom.xml ./

ENV MYSQL_DB_HOST name
ENV MYSQL_DB_PORT 3306
ENV MYSQL_DATABASE shopping
ENV MYSQL_DB_USERNAME shopping
ENV MYSQL_DB_PASSWORD shopping
ENV EUREKA_HOST name
ENV EUREKA_PORT 8761
ENV ORDER_MS_URL http://order-service:8080
ENV PRODUCT_MS_URL http://product-service:8080
ENV CUSTOMER_MS_URL http://customer-service:8080
ENV CART_MS_URL http://cart-service:8080
ENV PAYMENT_MS_URL http://payment-service:8080

RUN mvn clean install -DskipTests -P prod
RUN mv sc-application/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]
