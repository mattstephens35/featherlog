package com.featherlog.apitests.env;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;
import java.time.Duration;

public final class FeatherLogEnvironment {

    private static final Network NETWORK = Network.newNetwork();

    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withNetwork(NETWORK)
            .withNetworkAliases("mysql")
            .withDatabaseName("featherlog")
            .withUsername("featherlog")
            .withPassword("featherlog");

    private static final GenericContainer<?> APP;

    static {
        MYSQL.start();

        APP = new GenericContainer<>(
                new ImageFromDockerfile("featherlog-api-test", false)
                        .withDockerfile(Paths.get("..", "backend", "Dockerfile")))
                .withNetwork(NETWORK)
                .withExposedPorts(8080)
                .withEnv("DB_HOST", "mysql")
                .withEnv("DB_PORT", "3306")
                .withEnv("DB_NAME", "featherlog")
                .withEnv("DB_USERNAME", "featherlog")
                .withEnv("DB_PASSWORD", "featherlog")
                .waitingFor(Wait.forHttp("/actuator/health")
                        .forPort(8080)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofMinutes(3)));

        APP.start();
        APP.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(FeatherLogEnvironment.class)));
    }

    private FeatherLogEnvironment() {
    }

    public static String baseUrl() {
        return "http://" + APP.getHost() + ":" + APP.getMappedPort(8080) + "/api";
    }
}
