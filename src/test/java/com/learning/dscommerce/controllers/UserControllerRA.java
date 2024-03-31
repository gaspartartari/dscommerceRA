package com.learning.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.learning.TokenUtil;

import io.restassured.http.ContentType;

public class UserControllerRA {
    

    private String adminUsername, clientUsername, adminPassword, clientPassword;
    private String adminToken, clientToken, invalidToken;

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

    // 1. Busca de usuário retorna dados do usuário quando logado como admin

    @Test
    public void getMeShouldReturnUserWhenAdminLogged(){

        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me")
        .then()
            .statusCode(200)
            .body("id", is(2))
            .body("name", equalTo("Alex Green"))
            .body("email", equalTo("alex@gmail.com"));
    }



    // 2. Busca de usuário retorna dados do usuário quando logado como cliente
    @Test
    public void getMeShouldReturnUserWhenClientLogged(){

        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", equalTo("Maria Brown"))
            .body("email", equalTo("maria@gmail.com"));
    }

    // 3. Busca de usuário retorna 401 quando não logado como admin ou cliente
    @Test
    public void getMeShouldReturnUnauthorizedWhenNoUserLogged(){

        given()
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + invalidToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me")
        .then()
            .statusCode(401);
    }


}
