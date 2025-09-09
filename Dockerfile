# Usar JDK 17 como base
FROM openjdk:17-jdk-alpine

# Definir diretório de trabalho
WORKDIR /app

# Copiar o arquivo pom.xml e baixar dependências
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao mvnw
RUN chmod +x ./mvnw

# Baixar dependências
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar a aplicação
RUN ./mvnw clean package -DskipTests

# Expor a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "target/TaskManager-0.0.1-SNAPSHOT.jar"]