package org.estudos;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNível() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18));

    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");

        //path
        Assert.assertEquals(new Integer(1),response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s","id"));

        //jsonpath
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1,id);
    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("name", containsString("Joaquina"))
                //níveis do Json
                .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("name", containsString("Ana Júlia"))
             //Verifica a quantidade de objeticos dentro de uma lista (ARRAI) no Json

            .body("filhos", hasSize(2))

            // Valida um valor dentro de uma lista no Jason
            .body("filhos[0].name", is("Zezinho"))
            .body("filhos[1].name", is("Luizinho"))

            //Verifica um valor dentro de uma lista
            .body("filhos.name", hasItem("Luizinho"))
            .body("filhos.name", hasItems("Luizinho", "Zezinho"))

        ;

    }


}
