FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/classes/java/main /app
CMD ["java", "primefactor.PrimeFactorServer"]