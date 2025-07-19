# Стадия 1: Сборка проекта с помощью Maven
FROM maven:3-openjdk-17 AS build

# Установите рабочую директорию
WORKDIR /app

# Копируйте pom.xml для установки зависимостей
COPY pom.xml .

# Установите зависимости
RUN mvn dependency:go-offline -B

# Копируйте остальные файлы проекта
COPY . .

# Соберите проект
RUN mvn clean package -DskipTests

# Стадия 2: Создание производственного образа
FROM eclipse-temurin:17-jdk-jammy

# Установите рабочую директорию
WORKDIR /app

# Копируйте собранный jar-файл из стадии сборки
COPY --from=build /app/target/registry-backend-0.0.1-SNAPSHOT.jar app.jar

# Определите порт
EXPOSE 8080

# Запустите приложение
ENTRYPOINT ["java", "-jar", "app.jar"]