package org.estudos;

import io.restassured.http.ContentType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class VerbosHTTP {

    private static final Log log = LogFactory.getLog(VerbosHTTP.class);

    @Test
    public void deveSalvarUsuario() {
        given()
                .log().all()
                /**opções de utilizar o aplication Application na chamada*/
//                .contentType("application/json")
//                .contentType(ContentType.XML)
                .header("Content-Type", "Application/json")
                .body("{\"name\":\"José\",\"age\":30}")
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name",is("José"))
                .body("age",is(30));
    }

    @Test
    public void deveSalvarUsuarioUsandoObjeto() {
        User user = new User("Usuario via objeto", 35);

        given()
            .log().all()
            .contentType("Application/json")
            .body(user)
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("name",is("Usuario via objeto"))
            .body("age",is(35));
    }


    @Test
    public void deveSalvarUsuarioUsandoMap() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via mpa");
        params.put("age", 25);
;
        given()
        .log().all()
            .header("Content-Type", "Application/json")
            .body(params)
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("name",is("Usuario via mpa"))
            .body("age",is(25));
    }

    @Test
    public void naoDeveSalvarUsuarioSemNove() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{\"age\":50}")
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
            .log().all()
            .statusCode(400)
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"));
    }

    @Test
    public void deveSalvarUsuarioViaXML() {
        given()
            .log().all()
            .contentType(ContentType.XML)
            .body("<user><name>Joca</name><age>50</age></user>")
        .when()
            .post("https://restapi.wcaquino.me/usersXML")
        .then()
            .log().all()
            .statusCode(201)
            .body("user.@id", is(notNullValue()))
            .body("user.name",is("Joca"))
            .body("user.age",is("50"));
    }

    @Test
    public void deveAlteraUsuario() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{\"name\":\"Usuário alterado\",\"age\":80}")
        .when()
            .put("https://restapi.wcaquino.me/users/1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name",is("Usuário alterado"))
            .body("salary",is(1234.5678f));
    }

    @Test
    public void deveCustomizarURL() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{\"name\":\"Usuário alterado\",\"age\":80}")
        .when()
            .put("https://restapi.wcaquino.me/{entidade}/{userId}","users","1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name",is("Usuário alterado"))
            .body("salary",is(1234.5678f));
    }

    @Test
    public void deveCustomizarURLParte2() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{\"name\":\"Usuário alterado\",\"age\":80}")
            .pathParam("entidade","users")
            .pathParam("userId",1)
        .when()
            .put("https://restapi.wcaquino.me/{entidade}/{userId}")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name",is("Usuário alterado"))
            .body("salary",is(1234.5678f));
    }

    @Test
    public void deveRemoverUsuario() {
        given()
            .log().all()
        .when()
            .delete("https://restapi.wcaquino.me/users/1")
        .then()
            .log().all()
                .statusCode(204);
    }

    @Test
    public void naoDeveRemoverUsuario() {
        given()
            .log().all()
        .when()
            .delete("https://restapi.wcaquino.me/users/1000")
        .then()
            .log().all()
            .statusCode(400)
                .body("error",is("Registro inexistente"));
    }

}