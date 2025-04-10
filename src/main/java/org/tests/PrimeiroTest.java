package org.tests;

import org.exercicio.base.BaseTest;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class PrimeiroTest extends BaseTest {

    private String TOKEN;

    @Before
    public void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "semrecuperacao@yahoo.com");
        login.put("senha", "123456");

        TOKEN =
                given()
                    .body(login)
                .when()
                    .post("/signin")
                .then()
                    .statusCode(200)
                    .extract().path("token");
    }

    @Test
    public void naoDeveAcessarAPISemToken (){
        given()
        .when()
            .get("/contas")
        .then()
            .statusCode(401);
    }

    @Test
    public void deveIncluirContaComSucesso () {
        Integer id =
        given()
            .header("Authorization","JWT " + TOKEN)
            .body("{\"nome\":\"conta qualquer\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201)
            .extract().path("id");
        System.out.println(id);
    }

    @Test
    public void deveAlterarContaComSucesso () {
        given()
            .header("Authorization","JWT " + TOKEN)
            .body("{\"nome\":\"conta alterada\"}")
        .when()
            .put("/contas/2435036")
        .then()
            .statusCode(200)
            .body("nome", is("conta alterada"));
    }

//    @Test
//    public void deveExcluirContaComSucesso () {
//        given()
//            .header("Authorization","JWT " + TOKEN)
//            .body("{\"nome\":\"conta alterada\"}")
//        .when()
//            .delete("/removerConta/"+id )
//        .then()
//            .statusCode(200)
//            .body("mensagem", is("Conta removida com sucesso!"));
//    }

    @Test
    public void naoDeveInserirContanComMesmoNome () {
        given()
            .header("Authorization","JWT " + TOKEN)
            .body("{\"nome\":\"conta alterada\"}")
        .when()
            .post("/Contas")
        .then()
            .statusCode(400)
            .body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void deveInserirMovimentacaoSucesso () {
        Movimentacao mov = getMovimentacaoValida();

        given()
            .header("Authorization","JWT " + TOKEN)
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao () {

        given()
            .header("Authorization","JWT " + TOKEN)
            .body("{}")
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
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
        mov.setData_transacao("20/05/2028");

        given()
            .header("Authorization","JWT " + TOKEN)
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
                /**
                 * Quando o retorno vem em uma lista (Array) utilizar o hasItem que verifica em tudo
                 */
            .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
            .body("$", hasSize(1))
        ;
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(2435042);
//        mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2025");
        mov.setData_pagamento("10/10/2030");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }


    @Test
    public void naoDeveRemoverContaComMovimentacaoInserir () {

        given()
            .header("Authorization","JWT " + TOKEN)
        .when()
            .delete("/contas/2435042")
        .then()
            .statusCode(500)
            .body("constraint", is("transacoes_conta_id_foreign"));
    }

}