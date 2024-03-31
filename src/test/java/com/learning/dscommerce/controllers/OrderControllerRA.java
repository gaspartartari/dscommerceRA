package com.learning.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.learning.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class OrderControllerRA {

    private String adminUsername, clientUsername, adminPassword, clientPassword;
    private String adminToken, clientToken, invalidToken;
    private Long existingId, nonExistingId, otherId;
    private Map<String, Object> order;

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
        order = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = Map.of("productId", 5, "quantity", 1);
        Map<String, Object> item2 = Map.of("productId", 6, "quantity", 4);
        items.addAll(List.of(item1, item2));
        order.put("items", items);
        
    }

    // Inserção de pedido insere pedido com dados válidos quando logado como cliente
    @Test
    public void insertShouldInsertOrderWhenValidDataAndClientLogged(){
        JSONObject json = new JSONObject(order);
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .body(json)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/orders")
        .then()
            .statusCode(201);
    }

    // Inserção de pedido retorna 422 e mensagem customizadas com dados inválidos quando logado como cliente (ter pelo menos um item)
    @Test
    public void insertShouldReturnUnprocessableEntityWhenZeroItemsAndClientLogged(){
        order.remove("items");
        JSONObject json = new JSONObject(order);
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .body(json)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/orders")
        .then()
            .statusCode(422)
            .body("error", equalTo("Invalid data"))
            .body("errors.fieldName", hasItem("items"))
            .body("errors.message", hasItem("There must be at least one item"));
    }

    // Inserção de pedido retorna 401 quando não logado como admin ou cliente
    @Test
    public void insertShouldReturnUnauthorizedWhenNoUserLogged(){
        JSONObject json = new JSONObject(order);
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + invalidToken)
            .body(json)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/orders")
        .then()
            .statusCode(401);
    }

    // Inserção de pedido retorna 403 quando logado como admin
    @Test
    public void insertShouldReturnForbiddenWhenAdminLogged(){
        JSONObject json = new JSONObject(order);
        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .body(json)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/orders")
        .then()
            .statusCode(403);
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
