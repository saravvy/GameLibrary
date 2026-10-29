package app.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private static final String BASE_URL = "http://localhost:7070/api";

    @BeforeEach
    void setUpAll() {
        RestAssured.baseURI = BASE_URL;
    }


    @Test
    @DisplayName("Test get all games")
    void getAllGames() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/game/")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Test get game by id")
    void getGameById() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/game/1")
                .then()
                .statusCode(200);
    }
}