package org.estudos;

import io.restassured.RestAssured;
import io.restassured.path.xml.element.Node;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

    @BeforeClass
    //Para utilização do Before é necessário criar uma classe Static
    public static void setup(){

    }

    @Test
    public void testeOraniza() {

        baseURI = "https://restapi.wcaquino.me";
//        port = 80;
//        basePath = "/v2";

        given()
            .log().all()
        .when()
            .get("/usersXML/3")
        .then()
            .statusCode(200)

            //forma para criar um parametro para não precisar de passar o path completo sempre
            .rootPath("user")
            .body("name", is("Ana Julia"))

            //@para referenciar um atraibuto
            .body("@id", is("3"))

            //tira a declaração de um parametro utilizado na consulta
            .rootPath("user.filhos")
            .body("name.size()", is(2))

            .detachRootPath("filhos")
            .body("filhos.name[0]", is("Zezinho"))
            .body("filhos.name[1]", is("Luizinho"))

            .appendRootPath("filhos")
            .body("name", hasItem("Luizinho"))
            .body("name", hasItems("Luizinho","Zezinho"));

    }

    @Test
    public void deveTrabalharComXML() {

        given()
        .when()
                .get("https://restapi.wcaquino.me/usersXML/3")
        .then()
            .statusCode(200)

                //forma para criar um parametro para não precisar de passar o path completo sempre
            .rootPath("user")
            .body("name", is("Ana Julia"))
                //@para referenciar um atraibuto
            .body("@id", is("3"))

                //tira a declaração de um parametro utilizado na consulta
            .rootPath("user.filhos")
            .body("name.size()", is(2))

            .detachRootPath("filhos")
            .body("filhos.name[0]", is("Zezinho"))
            .body("filhos.name[1]", is("Luizinho"))

            .appendRootPath("filhos")
            .body("name", hasItem("Luizinho"))
            .body("name", hasItems("Luizinho","Zezinho"));
    }

    @Test
    public void devoFazerPesquisaAvancadaComXML() {

        given()
        .when()
            .get("https://restapi.wcaquino.me/usersXML")
        .then()
            .statusCode(200)
            .body("users.user.size()", is(3))
            .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
            .body("users.user.@id", hasItems("1", "2", "3" ))
            .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
            .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina","Ana Julia"))
            .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678))
            .body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
            .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}",is("MARIA JOAQUINA"));
    }

    @Test
    public void deveFazerPesquisaAvancadoComXMLeJava() {
        ArrayList<Node> nomes = given()
        .when()
            .get("https://restapi.wcaquino.me/usersXML")
        .then()
            .statusCode(200)
            .extract().path("users.user.name.findAll{it.toString().contains('n')}");

        Assert.assertEquals(2, nomes.size());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
    }

    @Test
    public void deveFazerPesquisaAvancadoComXPath() {

        given()
        .when()
        .get("https://restapi.wcaquino.me/usersXML")
        .then()
            .statusCode(200)
                .body(hasXPath("count(/users/user)", is("3")))
                .body(hasXPath("/users/user[@id = '1']"))
                .body(hasXPath("//user[@id = '1']"))

                // Aula 22 Trabalho com XML - XPATH - Ensinando como criar
                //Doc Xpath - https://www.red-gate.com/simple-talk/development/dotnet-development/xpath-css-dom-and-selenium-the-rosetta-stone/

                .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
                .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"),containsString("Luizinho"))))
                .body(hasXPath("/users/user/name", is("João da Silva")))
                .body(hasXPath("//name", is("João da Silva")))
                .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
                .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
//                .body(hasXPath("count(users/user/name[contains(., 'n')])", is("2")))
                .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
                .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
                .body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
        ;
    }

}
