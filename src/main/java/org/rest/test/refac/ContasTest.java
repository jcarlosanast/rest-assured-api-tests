package org.rest.test.refac;

import io.restassured.RestAssured;
import org.exercicio.base.BaseTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

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
    public void deveIncluirContaComSucesso () {
        given()
            .body("{\"nome\":\"Conta Inserida\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso () {
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        given()
            .body("{\"nome\":\"Conta alterada\"}")
            .pathParam("id", CONTA_ID)
        .when()
            .put("/contas/{id}")
        .then()
            .statusCode(200)
            .body("nome", is( "Conta alterada"));
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    @Test
    public void naoDeveInserirContanComMesmoNome () {
        given()
                .body("{\"nome\":\"Conta mesmo nome\"}")
        .when()
            .post("/Contas")
        .then()
            .statusCode(400)
            .body("error", is("Já existe uma conta com esse nome!"));
    }
}
