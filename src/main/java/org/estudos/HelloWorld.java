package org.estudos;

import groovy.transform.ASTTest;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

public class HelloWorld {

    public static void main(String[] args) {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        System.out.println(response.getBody().asString());

    }

    @Test
    public void test(){
        given()
                .when().get()
                .then();
    }


}
