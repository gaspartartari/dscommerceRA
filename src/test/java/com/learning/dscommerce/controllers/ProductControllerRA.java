package com.learning.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.learning.TokenUtil;

import io.restassured.http.ContentType;

public class ProductControllerRA {

    private Long existingProductId, nonExistingProductId, dependentId;
    private String productName;
    private Map<String, Object> product;
    private String adminUsername, clientUsername, adminPassword, clientPassword;
    private String adminToken, clientToken, invalidToken;

    @BeforeEach
    public void setup() {
        baseURI = "http://localhost:8080";

        product = new HashMap<>();
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        invalidToken = adminToken + "asda"; // generates invalid token
        existingProductId = 2L;
        nonExistingProductId = 100L;
        dependentId = 1L;

        product.put("name", "New product");
        product.put("price", 10.0);
        product.put("imgUrl", "www.imgurl.com");
        product.put("description", "here is a description");
        List<Map<String, Object>> categories = new ArrayList<>();
        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 1);
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);
        categories.add(category1);
        categories.add(category2);
        product.put("categories", categories);

    }

    // Atualização de produto atualiza produto com dados válidos quando logado como admin
    @Test
    public void updateShouldUpdateProductWhenValidDataAndExistingIdAndAdminLogged() {
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name",equalTo("New product"));
         
    }

    // 15. Atualização de produto retorna 404 para produto inexistente quando logado omo admin
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {
        JSONObject json = new JSONObject(product);
        nonExistingProductId = 100L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404);
         
    }

    // 16. Atualização de produto retorna 422 e mensagens customizadas com dados
    // inválidos quando logado como admin e campo name for inválido
    @Test
    public void updateShouldReturnUnprocessableEntityAndCustomErrorsWhenInvalidNamenAndAdminLogged() {
        product.replace("name", null );
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItem("name"))
                .body("errors.message", hasItem("Product name canot be blank"));
    }

    // 17. Atualização de produto retorna 422 e mensagens customizadas com dados
    // inválidos quando logado como admin e campo description for inválido
    @Test
    public void updateShouldReturnUnprocessableEntityAndCustomErrorsWhenInvalidDescriptionAndAdminLogged() {
        product.replace("description", null );
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItem("description"))
                .body("errors.message", hasItem("Product description canot be blank"));
    }

    // 18. Atualização de produto retorna 422 e mensagens customizadas com dados
    // inválidos quando logado como admin e campo price for negativo
    @Test
    public void updateShouldReturnUnprocessableEntityAndCustomErrorsWhenPriceIsNegativeAndAdminLogged() {
        product.replace("price", -100.0);
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItem("price"))
                .body("errors.message", hasItem("Product price must be a positive value"));
    }

    // 19. Atualização de produto retorna 422 e mensagens customizadas com dados
    // inválidos quando logado como admin e campo price for zero
    @Test
    public void updateShouldReturnUnprocessableEntityAndCustomErrorsWhenPriceIsZeroAndAdminLogged() {
        product.replace("price", 0.0);
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItem("price"))
                .body("errors.message", hasItem("Product price must be a positive value"));
    }

    // 20. Atualização de produto retorna 422 e mensagens customizadas com dados
    // inválidos quando logado como admin e não tiver categoria
    // associada
    @Test
    public void updateShouldReturnUnprocessableEntityAndCustomErrorsWhenCategoryNotInformedAndAdminLogged() {
        product.remove("categories");
        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItem("categories"));
    }

    // 21. Atualização de produto retorna 403 quando logado como cliente
    @Test
    public void updateShouldReturnForbiddenWhenClientLogged() {

        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    // 22. Atualização de produto retorna 401 quando não logado como admin ou
    // cliente
    @Test
    public void updateShouldReturnUnauthorizedWhenNoUserLogged() {

        JSONObject json = new JSONObject(product);
        existingProductId = 1L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }

    // Deleção de produto deleta produto existente quando logado como admin
    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndNotConstrainedAndAdminLogged() {
        existingProductId = 25L;
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(204);
    }

    // Deleção de produto retorna 404 para produto inexistente quando logado como
    // admin
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404);

    }

    // Deleção de produto retorna 400 para produto dependente quando logado como
    // admin
    @Test
    public void deleteShouldReturnBadRequestWhenDependentIdAndAdminLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/products/{id}", dependentId)
                .then()
                .statusCode(400);

    }

    // Deleção de produto retorna 403 quando logado como cliente
    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(403);

    }

    // Deleção de produto retorna 401 quando não logado como admin ou cliente
    @Test
    public void deleteShouldReturnUnauthorizedWhenNoUserLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(401);

    }

    // Inserção de produto retorna 422 e mensagens customizadas com dados inválidos
    // quando logado como admin e não tiver categoria associada
    @Test
    public void insertShouldReturnUnprocessableEntityAndCustomErrorsWhenNoCategoryAssociatedAndAdminLogged() {

        product.remove("categories");
        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("error", equalTo("Invalid data"));

    }

    // Inserção de produto retorna 403 quando logado como cliente
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() {

        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(403);

    }

    // Inserção de produto retorna 401 quando não logado como admin ou cliente
    @Test
    public void insertShouldReturnUnauthorizedWhenNoUserLogged() {

        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(401);

    }

    // Inserção de produto insere produto com dados válidos quando logado como admin
    @Test
    public void insertShouldReturnProductCreatedWhenValidDataAndAdminLogged() {

        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", equalTo("New product"))
                .body("categories.id", hasItems(1, 3));

    }

    // Inserção de produto retorna 422 e mensagens customizadas com dados inválidos
    // quando logado como admin e campo name for inválido
    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidNameAndAdminLogged() {

        product.replace("name", null);
        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItems("name"))
                .body("errors.message", hasItems("Product name canot be blank"));

    }

    // Inserção de produto retorna 422 e mensagens customizadas com dados inválidos
    // quando logado como admin e campo description for inválido
    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidDescriptionAndAdminLogged() {

        product.replace("description", "a");
        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItems("description"))
                .body("errors.message", hasItems("Description has to have at least 10 characters"));

    }

    // Inserção de produto retorna 422 e mensagens customizadas com dados inválidos
    // quando logado como admin e campo price for negativo
    @Test
    public void insertShouldReturnUnprocessableEntityWhenNegativePriceAndAdminLogged() {

        product.replace("price", -20.0);
        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.fieldName", hasItems("price"))
                .body("errors.message", hasItems("Product price must be a positive value"));

    }

    // Inserção de produto retorna 422 e mensagens customizadas com dados inválidos
    // quando logado como admin e campo price for zero
    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsZeroAndAdminLogged() {

        product.replace("price", 0.0);
        JSONObject json = new JSONObject(product);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(json)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors[0].fieldName", equalTo("price"));

    }

    // Busca paginada filtra produtos por nome e exibe listagem paginada quando
    // campo nome preenchidos
    @Test
    public void findAllShouldReturnPageWhenNameInformed() {
        productName = "Macbook Pro";

        given()
                .get("/products?name={productName}", productName)
                .then()
                .statusCode(200)
                .body("content[0].name", equalTo("Macbook Pro"));
    }

    // Busca paginada filtra produtos de forma paginada e filtra produtos com preço
    // maior que 2000.0
    @Test
    public void findAllShouldReturnPageProductsWithPriceGreaterThan2000() {

        given()
                .get("/products")
                .then()
                .statusCode(200)
                .body("content.findAll { it.price > 2000 }.name",
                        hasItems("Smart TV", "PC Gamer Hera", "PC Gamer Weed"));
    }

    // Busca paginada exibe listagem paginada quando campo nome não preenchido e
    // checa se os produtos Macbook Pro e PC Gamer Tera estão contidos
    @Test
    public void findAllShouldReturnPageWhenNameNotInformed() {

        given()
                .get("/products")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {

        given()
                .get("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("price", is(2190.0F))
                .body("imgUrl", equalTo(
                        "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Electronics", "Computers"));
    }
}
