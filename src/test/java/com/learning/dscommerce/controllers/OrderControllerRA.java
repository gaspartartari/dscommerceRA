package com.learning.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.learning.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {

    private String adminUsername, clientUsername, adminPassword, clientPassword;
    private String adminToken, clientToken, invalidToken;
    private Long existingId, nonExistingId, otherId;

    @BeforeEach
    public void setup() {
        baseURI = "http://localhost:8080";

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        invalidToken = adminToken + "asda"; // generates invalid token
    }

    // Busca de pedido por id retorna pedido existente quando logado como admin
    @Test
    public void findByIdShouldReturnOrderWhenAdminLogged(){
        existingId = 1L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", existingId)
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("client.name", equalTo("Maria Brown"));

    }

    // Busca de pedido por id retorna pedido existente quando logado como cliente e o pedido pertence ao usuário
    @Test
    public void findByIdShouldReturnOrderWhenClientLoggedAndSelfOrder(){
        existingId = 1L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", existingId)
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("client.name", equalTo("Maria Brown"));
    }
    // Busca de pedido por id retorna 403 quando pedido não pertence ao usuário
    @Test
    public void findByIdShouldReturnForbiddenWhenClientLoggedAndOtherOrder(){
        
        otherId = 2L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", otherId)
        .then()
            .statusCode(403);
    }

    // Busca de pedido por id retorna 404 para pedido inexistente quando logado como admin
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged(){
        
        nonExistingId = 100L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", nonExistingId)
        .then()
            .statusCode(404);
    }

    // Busca de pedido por id retorna 404 para pedido inexistente quando logado como cliente
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged(){
        
        nonExistingId = 100L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", nonExistingId)
        .then()
            .statusCode(404);
    }


    // Busca de pedido por id retorna 401 quando não logado como admin ou cliente
    @Test
    public void findByIdShouldReturnUnauthorizedWhenNoUserLogged(){
        
        existingId = 1L;
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + invalidToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/orders/{id}", existingId)
        .then()
            .statusCode(401);
    }



}
