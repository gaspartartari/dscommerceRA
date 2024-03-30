package com.learning;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TokenUtil {

    public static String obtainAccessToken(String username, String password){
        Response response = authRequest(username, password);
        JsonPath jsonBoy = response.jsonPath();
        String token = jsonBoy.getString("access_token");
        return token;
    }

    public static Response authRequest(String username, String password){

        return given()
                .auth()
                .preemptive()
                .basic("myclientid", "myclientsecret")
            .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                    .post("/oauth2/token");
    }
    
}
