package org.rest.test.refac;

import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.exercicio.base.BaseTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class AuthTest extends BaseTest {

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "semrecuperacao@yahoo.com");
        login.put("senha", "123456");

        String TOKEN =
                given()
                        .body(login)
                        .when()
                        .post("/signin")
                        .then()
                        .statusCode(200)
                        .extract().path("token");

        requestSpecification.header("Authorization","JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void naoDeveAcessarAPISemToken (){
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
            .get("/contas")
        .then()
            .statusCode(401);
    }
}
