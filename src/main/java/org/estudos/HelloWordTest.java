package org.estudos;

//import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class HelloWordTest {

    @Test
    public void testHelloWord(){
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode()== 200);
        Assert.assertTrue("Deveria ser 200",response.statusCode()== 200);
        Assert.assertEquals(200,response.statusCode());

        System.out.println(response.getBody().asString().equals("Ola Mundo!"));
        System.out.println(response.statusCode()== 200);

//        throw new RuntimeException();

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured(){
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        RestAssured.get("https://restapi.wcaquino.me/ola").then().statusCode(200);

        get("https://restapi.wcaquino.me/ola").then().statusCode(200);

        given()
        .when().
            get("https://restapi.wcaquino.me/ola")
        .then()
            .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat(128, Matchers.is(128));
        Assert.assertThat(128, Matchers.isA(Integer.class));
        Assert.assertThat(128d, Matchers.isA(Double.class));
        Assert.assertThat(128d, Matchers.greaterThan(120d));
        Assert.assertThat(128d, Matchers.lessThan(130d));

        List<Integer> impares = Arrays.asList(1,3,5,7,9);
        assertThat(impares, hasSize(5));
        assertThat(impares, contains(1,3,5,7,9));
        assertThat(impares, containsInAnyOrder(1,5,3,7,9));
        assertThat(impares, hasItem(1));
        assertThat(impares, hasItems(1,7));

        assertThat("Maria", is(not("Jão")));
        assertThat("Maria", not("Jão"));
        assertThat("Maria", anyOf(is("Maria"),is( "Joaquim")));
//        assertThat("Francisco", anyOf(is("Maria"),is( "Joaquim")));
        assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));

//    Doc Matches ResponseSpecificationImpl.HamcrestAssertionClosure
//        https://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html

    }

    @Test
    public void devoValidarOBody() {
        given()
        .when().
            get("https://restapi.wcaquino.me/ola")
        .then()
            .statusCode(200)
            .body(Matchers.is("Ola Mundo!"))
            .body(containsString("Mundo"))
            .body(is(not(nullValue())))
            .header("content-type", "text/plain; charset=utf-8");
    }

}
