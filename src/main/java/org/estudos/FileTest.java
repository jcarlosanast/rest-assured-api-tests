package org.estudos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class FileTest {

    private static final Log log = LogFactory.getLog(FileTest.class);

    @Test public void deveObrigarEnvioFile() {
        given()
            .log().all()
        .when()
                .post("https://restapi.wcaquino.me/upload")
        .then()
            .log().all()
            .statusCode(404)
                .body("error", is("Arquivo não enviado"))
                ;
    }

    @Test public void deveFazerUploafToFile() {
        given()
            .log().all()
            .multiPart("arquivo", new File("src/main/resources/Error.pdf"))
        .when()
            .post("https://restapi.wcaquino.me/upload")
        .then()
            .log().all()
                /**
                Define o tempo maximo de resposta da chamada (Testes)
                 */
            .time(lessThan(3000L))
            .statusCode(200);
    }

    @Test public void deveDownloadFile() throws IOException {
        byte[] image =
        given()
            .log().all()
            .multiPart("arquivo", new File("src/main/resources/Error.pdf"))
        .when()
            .get("https://restapi.wcaquino.me/download")
        .then()
            .log().all()
            .statusCode(200)
            .extract().asByteArray()
        ;
        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        System.out.println(imagem.length());
        Assert.assertThat(imagem.length(), lessThan(100000L));
    }

}
