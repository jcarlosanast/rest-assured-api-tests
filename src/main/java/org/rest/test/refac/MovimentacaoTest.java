package org.rest.test.refac;

import io.restassured.RestAssured;
import org.exercicio.base.BaseTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tests.Movimentacao;
import org.utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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
    public void deveInserirMovimentacaoSucesso () {
        Movimentacao mov = getMovimentacaoValida();

        given()
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao () {

        given()
            .body("{}")
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(8))
            .body("msg", hasItems("Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"));
    }

    @Test
    public void naoDeveInserirMovimentacaoComDataFutura () {
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
/**
* Quando o retorno vem em uma lista (Array) utilizar o hasItem que verifica em tudo
*/
        .body("$", hasSize(1))
        .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao () {
        Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");

        given()
            .pathParam("id", CONTA_ID)
        .when()
            .delete("/contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacao () {
        Integer MOV_ID = getIdMovPelaDescricao("Movimentacao para exclusao");

        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("/transacoes/{id}")
        .then()
            .statusCode(204);
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    public Integer getIdMovPelaDescricao(String desc) {
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
//        mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
}
