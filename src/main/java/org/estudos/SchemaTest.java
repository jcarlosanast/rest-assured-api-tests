package org.estudos;

import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Test;
import org.xml.sax.SAXException;

import static io.restassured.RestAssured.*;

public class SchemaTest {

    @Test
    public void deveValidarSchemaXML() {

        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/usersXML")
        .then()
            .statusCode(200)
            .log().all()
            .body(RestAssuredMatchers.matchesXsdInClasspath("schemaxsd.xsd"))
        ;
    }

    /**
     * Informando a expected quando for um cenários negativo de schema que precisar ser validado
     */
    @Test(expected = SAXException.class)
    public void naoDeveValidarSchemaXML() {

        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/invalidusersXML")
        .then()
            .statusCode(200)
            .log().all()
            .body(RestAssuredMatchers.matchesXsdInClasspath("schemaxsd.xsd"));
    }

    @Test
    public void deveValidarSchemaJSON() {

        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
            .log().all()
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
        ;
    }


}
