package org.estudos;

import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import static io.restassured.RestAssured.*;

public class EnvioDadosTest {

    @Test
    public void deveEnviarValorViaQueryXML() {
        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/v2/users?format=xml")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.XML);
    }

    @Test
    public void deveEnviarValorViaQueryJSON() {
        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/v2/users?format=json")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void deveEnviarValorViaPAram() {
        given()
            .log().all()
                .queryParam("format", "xml")
                .queryParam("outra", "coisa")
        .when()
            .get("https://restapi.wcaquino.me/v2/users")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.XML)
            .contentType(Matchers.containsString("utf-8"));
    }

    @Test
    public void deveEnviarValorViaHeader() {
        given()
            .log().all()
                //Utilizar o Accept para ficar claro oq espera da resposta!
                .accept(ContentType.HTML)
        .when()
            .get("https://restapi.wcaquino.me/v2/users")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.HTML)
        ;
    }

}
