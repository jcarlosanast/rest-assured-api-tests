package org.estudos;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    public void deveAcessarSWAPI() {

        given()
            .log().all()
        .when()
            .get("https://swapi.dev/api/people/1")
        .then()
            .log().all()
            .body("name", is( "Luke Skywalker"));
    }

    /**
     * Teste ficará que inativo devido token
     */
    @Test
    public void deveObterClima() {

        given()
            .log().all()
                .queryParam("q", "Fortaleza,BR")
                .queryParam("appid", "69ed4c1bcdb9ac84842d246a2509c463")
                .queryParam("units", "metric")
        .when()
            .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Fortaleza"))
            .body("coord.lon", is(-38.5247F))
            .body("main.temp", greaterThan(25f))
                ;
    }

    @Test
    public void naoDeveAcessarSemSenha() {

       given()
            .log().all()
       .when()
            .get("https://restapi.wcaquino.me/basicauth")
       .then()
             .log().all()
             .statusCode(401);
    }

    @Test
    public void deveFazerAutenticacaoBasica() {

        given()
            .log().all()
        .when()
            .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaTwo() {

        given()
            .log().all()
            .auth().basic("admin","senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge() {

        given()
            .log().all()
            .auth().preemptive().basic("admin","senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth2")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"));
    }

    @Test
    public void deveFazerAutenticacaoComToken (){
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "semrecuperacao@yahoo.com");
        login.put("senha", "123456");

        // Login na API
        // Receber o Token

        String toke  = given()
            .log().all()
            .body(login)
            .contentType(ContentType.JSON)
        .when()
            .post("https://barrigarest.wcaquino.me/signin")
        .then()
            .log().all()
            .statusCode(200)
            .body("nome", is("Dimitri Tom"))
            .extract().path("token");

        //Obter as Contas
        given()
                .log().all()
                .header("Authorization", "JWT "+ toke)
        .when()
                .get("https://barrigarest.wcaquino.me/contas")
        .then()
                .statusCode(200)
                .body("nome", hasItem("New Test"));
    }

    @Test
    public void deveAcessarAplicacaoWeb(){
        //Login

        String cookie =
        given()
            .log().all()
            .formParam("email", "semrecuperacao@yahoo.com")
            .formParam("senha", "123456")
            .contentType(ContentType.URLENC.withCharset("UTF-8"))
        .when()
            .post("https://seubarriga.wcaquino.me/logar")
        .then()
            .log().all()
            .statusCode(200)
            .extract().header("set-cookie")

                ;

        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        String body =
        given()
            .log().all()
                .cookie("connect.sid", cookie)
        .when()
            .get("https://seubarriga.wcaquino.me/contas")
        .then()
            .log().all()
            .statusCode(200)
                .body("html.body.table.tbody.tr[0].td[0]", is("New Test"))
                .extract().body().asString()

        ;
        System.out.println("---------------");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }

}
