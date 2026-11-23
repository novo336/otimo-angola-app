package ao.otimoangola.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    // CORES
    private final int FUNDO = Color.rgb(13, 15, 18);
    private final int CARD = Color.rgb(26, 29, 34);
    private final int VERDE = Color.rgb(205, 255, 53);
    private final int BRANCO = Color.WHITE;
    private final int CINZA = Color.rgb(160, 165, 172);

    // CONFIGURAÇÕES
    private final int BASE = 35000;
    private final int POR_KM = 1050;
    private final int COMISSAO = 10;

    private final String WHATSAPP = "244962670068";
    private final String PIN_ADMIN = "2026";

    // DADOS
    private SharedPreferences prefs;
    private JSONArray pedidos;
    private JSONArray motoristas;
    private String telefoneCliente = "";

    private LinearLayout layout;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(FUNDO);
        getWindow().setNavigationBarColor(FUNDO);

        prefs = getSharedPreferences("OTIMO_DATA", MODE_PRIVATE);

        pedidos = carregarArray("pedidos");
        motoristas = carregarArray("motoristas");

        telefoneCliente = prefs.getString("user_phone", "");

        if (telefoneCliente.isEmpty()) {
            boasVindas();
        } else {
            inicio();
        }
    }

    // =========================================
    // BOAS-VINDAS
    // =========================================

    private void boasVindas() {
        base();

        espacamento(35);

        texto("ÓTIMO", 42, VERDE, true);
        texto("ANGOLA", 28, BRANCO, true);

        espacamento(10);

        texto("Movemos o que importa.", 16, CINZA, false);

        espacamento(35);

        card("TRANSPORTE SIMPLES.\nRÁPIDO. CONFIÁVEL.");

        espacamento(20);

        botao("COMEÇAR", this::cadastroCliente);

        botaoSecundario("Já tenho conta", this::entrarCliente);

        espacamento(15);

        botaoSecundario("Sou motorista", this::cadastroMotorista);

        botaoSecundario("Área administrativa", this::administrador);
    }

    // =========================================
    // REGISTO / LOGIN CLIENTE
    // =========================================

    private void cadastroCliente() {
        EditText telefone = campo("Número de telefone");

        new AlertDialog.Builder(this)
                .setTitle("Criar conta")
                .setMessage("Introduza o seu número de telefone.")
                .setView(telefone)
                .setPositiveButton("CONTINUAR", (d, w) -> {

                    String numero = telefone.getText().toString().trim();

                    if (numero.length() < 9) {
                        aviso("Introduza um número de telefone válido.");
                        return;
                    }

                    telefoneCliente = numero;

                    prefs.edit()
                            .putString("user_phone", numero)
                            .apply();

                    inicio();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void entrarCliente() {
        EditText telefone = campo("Número de telefone");
        telefone.setText(telefoneCliente);

        new AlertDialog.Builder(this)
                .setTitle("Entrar")
                .setMessage("Introduza o seu número de telefone.")
                .setView(telefone)
                .setPositiveButton("ENTRAR", (d, w) -> {

                    String numero = telefone.getText().toString().trim();

                    if (numero.length() < 9) {
                        aviso("Número inválido.");
                        return;
                    }

                    telefoneCliente = numero;

                    prefs.edit()
                            .putString("user_phone", numero)
                            .apply();

                    inicio();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =========================================
    // MENU PRINCIPAL
    // =========================================

    private void inicio() {
        base();

        topo("ÓTIMO ANGOLA", "Movemos o que importa.");

        card("BEM-VINDO\n" + telefoneCliente);

        texto("O QUE PRECISA DE TRANSPORTAR?", 15, VERDE, true);

        botao("MUDANÇA RESIDENCIAL", () ->
                cliente("Mudança residencial"));

        botao("TRANSPORTE DE MERCADORIAS", () ->
                cliente("Transporte de mercadorias"));

        botao("ENTREGA", () ->
                cliente("Entrega"));

        espacamento(15);

        botaoSecundario("OS MEUS PEDIDOS", this::meusPedidos);

        botaoSecundario("ALTERAR TELEFONE", this::cadastroCliente);

        botaoSecundario("CADASTRAR COMO MOTORISTA", this::cadastroMotorista);

        botaoSecundario("ÁREA ADMINISTRATIVA", this::administrador);

        espacamento(25);

        texto("ÓTIMO ANGOLA", 12, CINZA, true);
    }

    // =========================================
    // NOVO PEDIDO
    // =========================================

    private void cliente(String servico) {
        base();

        topo("NOVO PEDIDO", servico);

        EditText origem = campo("Local de partida");
        EditText destino = campo("Local de destino");
        EditText distancia = campo("Distância estimada em km");
        EditText descricao = campo("Descrição da carga (opcional)");

        texto("Tipo de veículo", 14, VERDE, true);

        Spinner veiculo = spinner(new String[]{
                "Carrinha pequena",
                "Carrinha grande",
                "Caminhão",
                "Outro"
        });

        adicionar(origem);
        adicionar(destino);
        adicionar(distancia);
        adicionar(descricao);

        botao("CALCULAR PREÇO", () -> {

            String o = origem.getText().toString().trim();
            String d = destino.getText().toString().trim();
            String k = distancia.getText().toString().trim();

            if (o.isEmpty() || d.isEmpty() || k.isEmpty()) {
                aviso("Preencha a partida, destino e distância.");
                return;
            }

            double km;

            try {
                km = Double.parseDouble(k.replace(",", "."));
            } catch (Exception e) {
                aviso("Distância inválida.");
                return;
            }

            if (km <= 0 || km > 10000) {
                aviso("Introduza uma distância válida.");
                return;
            }

            int preco = BASE + (int) Math.round(km * POR_KM);

            confirmarPedido(
                    servico,
                    o,
                    d,
                    km,
                    preco,
                    veiculo.getSelectedItem().toString(),
                    descricao.getText().toString().trim()
            );
        });

        botaoSecundario("VOLTAR", this::inicio);
    }

    // =========================================
    // CONFIRMAÇÃO
    // =========================================

    private void confirmarPedido(
            String servico,
            String origem,
            String destino,
            double km,
            int preco,
            String veiculo,
            String descricao
    ) {
        base();

        topo("CONFIRMAR PEDIDO", "Revise os dados.");

        card("SERVIÇO: " + servico
                + "\n\nPARTIDA: " + origem
                + "\nDESTINO: " + destino
                + "\nDISTÂNCIA: " + km + " km"
                + "\nVEÍCULO: " + veiculo
                + "\n\nVALOR ESTIMADO: " + formatar(preco) + " Kz");

        texto("O valor poderá ser ajustado após confirmação das condições do transporte.",
                13, CINZA, false);

        botao("CONFIRMAR PEDIDO", () -> {

            try {
                JSONObject p = new JSONObject();

                p.put("id", System.currentTimeMillis());
                p.put("cliente", telefoneCliente);
                p.put("servico", servico);
                p.put("origem", origem);
                p.put("destino", destino);
                p.put("km", km);
                p.put("preco", preco);
                p.put("veiculo", veiculo);
                p.put("descricao", descricao);

                p.put("estado", "PENDENTE");
                p.put("data", System.currentTimeMillis());

                p.put("motorista", "");
                p.put("motorista_telefone", "");

                int comissao = (int) Math.round(preco * COMISSAO / 100.0);
                int repasse = preco - comissao;

                p.put("comissao_percent", COMISSAO);
                p.put("comissao", comissao);
                p.put("motorista_valor", repasse);

                p.put("pagamento_estado", "PENDENTE");
                p.put("pagamento_metodo", "Ainda não definido");
                p.put("pagamento_tipo", "A definir");

                pedidos.put(p);
                salvar();

                sucessoPedido(
                        servico,
                        origem,
                        destino,
                        preco,
                        veiculo,
                        descricao
                );

            } catch (Exception e) {
                aviso("Não foi possível registar o pedido.");
            }
        });

        botaoSecundario("EDITAR", () -> cliente(servico));

        botaoSecundario("VOLTAR", this::inicio);
    }

    // =========================================
    // PEDIDO REGISTADO
    // =========================================

    private void sucessoPedido(
            String servico,
            String origem,
            String destino,
            int preco,
            String veiculo,
            String descricao
    ) {
        base();

        topo("PEDIDO REGISTADO", "O seu pedido foi guardado.");

        card("PEDIDO RECEBIDO!\n\n"
                + "VALOR ESTIMADO: " + formatar(preco) + " Kz"
                + "\n\nESTADO: PENDENTE");

        texto("A nossa equipa poderá entrar em contacto para confirmar os detalhes.",
                14, CINZA, false);

        botao("CONTACTAR ÓTIMO ANGOLA", () -> {

            String mensagem =
                    "Olá, ÓTIMO ANGOLA! Solicitei um serviço.\n\n"
                    + "Serviço: " + servico
                    + "\nPartida: " + origem
                    + "\nDestino: " + destino
                    + "\nVeículo: " + veiculo
                    + "\nValor: " + formatar(preco) + " Kz"
                    + "\nCliente: " + telefoneCliente
                    + "\nDescrição: " + descricao;

            whatsapp(mensagem);
        });

        botaoSecundario("OS MEUS PEDIDOS", this::meusPedidos);

        botaoSecundario("PÁGINA INICIAL", this::inicio);
    }

    // =========================================
    // PEDIDOS DO CLIENTE
    // =========================================

    private void meusPedidos() {
        base();

        topo("OS MINHAS SOLICITAÇÕES", telefoneCliente);

        boolean encontrou = false;

        for (int i = pedidos.length() - 1; i >= 0; i--) {
            JSONObject p = pedidos.optJSONObject(i);

            if (p == null) continue;

            if (telefoneCliente.equals(p.optString("cliente"))) {
                encontrou = true;

                card("SERVIÇO: " + p.optString("servico")
                        + "\n\nPARTIDA: " + p.optString("origem")
                        + "\nDESTINO: " + p.optString("destino")
                        + "\n\nVALOR: " + formatar(p.optInt("preco")) + " Kz"
                        + "\nESTADO: " + p.optString("estado")
                        + "\nPAGAMENTO: " + p.optString("pagamento_estado")
                        + "\nMOTORISTA: " + p.optString("motorista", "Por atribuir"));
            }
        }

        if (!encontrou) {
            card("Ainda não tem pedidos registados.");
        }

        botaoSecundario("VOLTAR", this::inicio);
    }

    // =========================================
    // CADASTRO MOTORISTA
    // =========================================

    private void cadastroMotorista() {
        base();

        topo("CADASTRO DE MOTORISTA", "Registe os seus dados.");

        EditText nome = campo("Nome completo");
        EditText telefone = campo("Telefone");
        EditText bi = campo("Número do BI");
        EditText matricula = campo("Matrícula");
        EditText municipio = campo("Município");
        EditText marca = campo("Marca / modelo");

        texto("Tipo de veículo", 14, VERDE, true);

        Spinner tipo = spinner(new String[]{
                "Carrinha pequena",
                "Carrinha grande",
                "Caminhão",
                "Outro"
        });

        adicionar(nome);
        adicionar(telefone);
        adicionar(bi);
        adicionar(matricula);
        adicionar(municipio);
        adicionar(marca);

        botao("ENVIAR CADASTRO", () -> {

            String n = nome.getText().toString().trim();
            String t = telefone.getText().toString().trim();
            String b = bi.getText().toString().trim();
            String m = matricula.getText().toString().trim();
            String mun = municipio.getText().toString().trim();
            String marc = marca.getText().toString().trim();

            if (n.isEmpty() || t.length() < 9 || b.isEmpty()
                    || m.isEmpty() || mun.isEmpty() || marc.isEmpty()) {

                aviso("Preencha todos os campos, incluindo o BI.");
                return;
            }

            try {
                JSONObject motorista = new JSONObject();

                motorista.put("id", System.currentTimeMillis());
                motorista.put("nome", n);
                motorista.put("telefone", t);
                motorista.put("bi", b);
                motorista.put("matricula", m);
                motorista.put("municipio", mun);
                motorista.put("marca", marc);
                motorista.put("veiculo", tipo.getSelectedItem().toString());

                motorista.put("estado", "PENDENTE");
                motorista.put("disponivel", false);

                motoristas.put(motorista);
                salvar();

                new AlertDialog.Builder(this)
                        .setTitle("Cadastro enviado")
                        .setMessage("O seu cadastro aguarda aprovação da ÓTIMO ANGOLA.")
                        .setPositiveButton("OK", (d, w) -> boasVindas())
                        .show();

            } catch (Exception e) {
                aviso("Erro ao registar motorista.");
            }
        });

        botaoSecundario("VOLTAR", this::boasVindas);
    }

    // =========================================
    // ADMINISTRADOR
    // =========================================

    private void administrador() {
        EditText pin = campo("PIN");
        pin.setInputType(2);

        new AlertDialog.Builder(this)
                .setTitle("Área administrativa")
                .setMessage("Introduza o PIN de acesso.")
                .setView(pin)
                .setPositiveButton("ENTRAR", (d, w) -> {

                    if (PIN_ADMIN.equals(pin.getText().toString().trim())) {
                        menuAdmin();
                    } else {
                        aviso("PIN incorreto.");
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void menuAdmin() {
        base();

        topo("PAINEL ADMINISTRATIVO", "Gestão ÓTIMO ANGOLA");

        card("PEDIDOS: " + pedidos.length()
                + "\nMOTORISTAS: " + motoristas.length());

        botao("GESTÃO DE PEDIDOS", this::listaPedidos);

        botao("GESTÃO DE MOTORISTAS", this::listaMotoristas);

        botao("FINANCEIRO / COMISSÕES", this::financeiro);

        botaoSecundario("VOLTAR", this::boasVindas);
    }

    // =========================================
    // LISTA DE PEDIDOS
    // =========================================

    private void listaPedidos() {
        base();

        topo("GESTÃO DE PEDIDOS", "Todos os pedidos.");

        if (pedidos.length() == 0) {
            card("Não existem pedidos.");
        }

        for (int i = pedidos.length() - 1; i >= 0; i--) {
            final int indice = i;

            JSONObject p = pedidos.optJSONObject(i);
            if (p == null) continue;

            card("CLIENTE: " + p.optString("cliente")
                    + "\nSERVIÇO: " + p.optString("servico")
                    + "\nPARTIDA: " + p.optString("origem")
                    + "\nDESTINO: " + p.optString("destino")
                    + "\nVEÍCULO: " + p.optString("veiculo")
                    + "\nVALOR: " + formatar(p.optInt("preco")) + " Kz"
                    + "\nESTADO: " + p.optString("estado")
                    + "\nMOTORISTA: " + p.optString("motorista")
                    + "\nPAGAMENTO: " + p.optString("pagamento_estado"));

            botao("GERIR PEDIDO", () -> gerirPedido(indice));
        }

        botaoSecundario("VOLTAR", this::menuAdmin);
    }

    private void gerirPedido(int indice) {
        JSONObject p = pedidos.optJSONObject(indice);

        if (p == null) return;

        String[] opcoes = {
                "Atribuir motorista",
                "Marcar concluído",
                "Cancelar pedido",
                "Registar pagamento",
                "Voltar"
        };

        new AlertDialog.Builder(this)
                .setTitle("Gestão do pedido")
                .setItems(opcoes, (d, escolha) -> {

                    if (escolha == 0) atribuirMotorista(indice);
                    else if (escolha == 1) mudarEstadoPedido(indice, "CONCLUÍDO");
                    else if (escolha == 2) mudarEstadoPedido(indice, "CANCELADO");
                    else if (escolha == 3) registarPagamento(indice);
                    else listaPedidos();
                })
                .show();
    }

    // =========================================
    // ATRIBUIR MOTORISTA
    // =========================================

    private void atribuirMotorista(int indicePedido) {
        JSONArray aprovados = new JSONArray();

        for (int i = 0; i < motoristas.length(); i++) {
            JSONObject m = motoristas.optJSONObject(i);

            if (m != null && "APROVADO".equals(m.optString("estado"))) {
                aprovados.put(m);
            }
        }

        if (aprovados.length() == 0) {
            aviso("Não existem motoristas aprovados.");
            return;
        }

        String[] nomes = new String[aprovados.length()];

        for (int i = 0; i < aprovados.length(); i++) {
            JSONObject m = aprovados.optJSONObject(i);

            nomes[i] = m.optString("nome")
                    + " • " + m.optString("veiculo")
                    + " • " + m.optString("matricula");
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecionar motorista")
                .setItems(nomes, (d, escolha) -> {

                    JSONObject m = aprovados.optJSONObject(escolha);
                    JSONObject p = pedidos.optJSONObject(indicePedido);

                    if (m == null || p == null) return;

                    putJson(p, "motorista", m.optString("nome"));
                    putJson(p, "motorista_telefone", m.optString("telefone"));
                    putJson(p, "estado", "ATRIBUÍDO");

                    salvar();

                    aviso("Motorista atribuído.");

                    listaPedidos();
                })
                .show();
    }

    private void mudarEstadoPedido(int indice, String estado) {
        JSONObject p = pedidos.optJSONObject(indice);

        if (p == null) return;

        putJson(p, "estado", estado);

        salvar();

        aviso("Pedido atualizado: " + estado);

        listaPedidos();
    }

    // =========================================
    // FINANCEIRO
    // =========================================

    private void financeiro() {
        base();

        topo("FINANCEIRO", "Comissões da plataforma.");

        int totalConcluido = 0;
        int comissaoConcluida = 0;
        int repasseConcluido = 0;
        int pagamentosFeitos = 0;
        int pagamentosPendentes = 0;

        for (int i = 0; i < pedidos.length(); i++) {
            JSONObject p = pedidos.optJSONObject(i);

            if (p == null) continue;

            if ("CONCLUÍDO".equals(p.optString("estado"))) {
                totalConcluido += p.optInt("preco");
                comissaoConcluida += p.optInt("comissao");
                repasseConcluido += p.optInt("motorista_valor");
            }

            if ("PAGO".equals(p.optString("pagamento_estado"))) {
                pagamentosFeitos += p.optInt("preco");
            } else if (!"CANCELADO".equals(p.optString("estado"))) {
                pagamentosPendentes += p.optInt("preco");
            }
        }

        card("SERVIÇOS CONCLUÍDOS\n"
                + formatar(totalConcluido) + " Kz");

        card("COMISSÃO DA ÓTIMO ANGOLA (10%)\n"
                + formatar(comissaoConcluida) + " Kz");

        card("REPASSE A MOTORISTAS (90%)\n"
                + formatar(repasseConcluido) + " Kz");

        card("VALORES MARCADOS COMO PAGOS\n"
                + formatar(pagamentosFeitos) + " Kz");

        card("PAGAMENTOS PENDENTES\n"
                + formatar(pagamentosPendentes) + " Kz");

        texto("A comissão é contabilizada nos serviços concluídos.",
                13, CINZA, false);

        botao("REGISTAR PAGAMENTO", this::escolherPedidoPagamento);

        botaoSecundario("VOLTAR", this::menuAdmin);
    }

    // =========================================
    // SELEÇÃO DE PEDIDO PARA PAGAMENTO
    // =========================================

    private void escolherPedidoPagamento() {
        JSONArray pendentes = new JSONArray();

        for (int i = 0; i < pedidos.length(); i++) {
            JSONObject p = pedidos.optJSONObject(i);

            if (p != null
                    && !"CANCELADO".equals(p.optString("estado"))
                    && !"PAGO".equals(p.optString("pagamento_estado"))) {
                pendentes.put(p);
            }
        }

        if (pendentes.length() == 0) {
            aviso("Não existem pagamentos pendentes.");
            return;
        }

        String[] itens = new String[pendentes.length()];

        for (int i = 0; i < pendentes.length(); i++) {
            JSONObject p = pendentes.optJSONObject(i);

            itens[i] = p.optString("servico")
                    + " • " + formatar(p.optInt("preco")) + " Kz"
                    + " • " + p.optString("cliente");
        }

        new AlertDialog.Builder(this)
                .setTitle("Escolher pedido")
                .setItems(itens, (d, escolha) -> {

                    JSONObject escolhido = pendentes.optJSONObject(escolha);
                    if (escolhido == null) return;

                    long id = escolhido.optLong("id");
                    int indiceReal = -1;

                    for (int i = 0; i < pedidos.length(); i++) {
                        JSONObject p = pedidos.optJSONObject(i);

                        if (p != null && p.optLong("id") == id) {
                            indiceReal = i;
                            break;
                        }
                    }

                    if (indiceReal >= 0) {
                        registarPagamento(indiceReal);
                    }
                })
                .show();
    }

    // =========================================
    // PAGAMENTO
    // =========================================

    private void registarPagamento(int indice) {
        JSONObject p = pedidos.optJSONObject(indice);

        if (p == null) return;

        String[] metodos = {
                "Multicaixa Express",
                "PayPay",
                "United Money",
                "Presencial"
        };

        new AlertDialog.Builder(this)
                .setTitle("Método de pagamento")
                .setItems(metodos, (d, escolha) -> {

                    String metodo = metodos[escolha];

                    new AlertDialog.Builder(this)
                            .setTitle("Confirmar pagamento")
                            .setMessage("Método: " + metodo
                                    + "\nValor: " + formatar(p.optInt("preco")) + " Kz"
                                    + "\n\nConfirmas que recebeste o pagamento?")
                            .setPositiveButton("CONFIRMAR", (dialog, which) -> {

                                putJson(p, "pagamento_estado", "PAGO");
                                putJson(p, "pagamento_metodo", metodo);

                                String tipo = metodo.equals("Presencial")
                                        ? "Presencial"
                                        : "Digital";

                                putJson(p, "pagamento_tipo", tipo);

                                salvar();

                                aviso("Pagamento registado.");

                                financeiro();
                            })
                            .setNegativeButton("CANCELAR", null)
                            .show();
                })
                .show();
    }

    // =========================================
    // MOTORISTAS - ADMIN
    // =========================================

    private void listaMotoristas() {
        base();

        topo("GESTÃO DE MOTORISTAS", "Cadastros recebidos.");

        if (motoristas.length() == 0) {
            card("Não existem motoristas registados.");
        }

        for (int i = 0; i < motoristas.length(); i++) {
            final int indice = i;

            JSONObject m = motoristas.optJSONObject(i);
            if (m == null) continue;

            card("NOME: " + m.optString("nome")
                    + "\nTELEFONE: " + m.optString("telefone")
                    + "\nBI: " + m.optString("bi")
                    + "\nVEÍCULO: " + m.optString("veiculo")
                    + "\nMARCA: " + m.optString("marca")
                    + "\nMATRÍCULA: " + m.optString("matricula")
                    + "\nMUNICÍPIO: " + m.optString("municipio")
                    + "\nESTADO: " + m.optString("estado")
                    + "\nDISPONIBILIDADE: "
                    + (m.optBoolean("disponivel") ? "ATIVO" : "INATIVO"));

            botao("GERIR MOTORISTA", () -> gerirMotorista(indice));
        }

        botaoSecundario("VOLTAR", this::menuAdmin);
    }

    private void gerirMotorista(int indice) {
        JSONObject m = motoristas.optJSONObject(indice);

        if (m == null) return;

        String[] opcoes = {
                "Aprovar motorista",
                "Rejeitar motorista",
                "Ativar disponibilidade",
                "Desativar disponibilidade",
                "Contactar WhatsApp",
                "Voltar"
        };

        new AlertDialog.Builder(this)
                .setTitle(m.optString("nome"))
                .setItems(opcoes, (d, escolha) -> {

                    if (escolha == 0) {
                        putJson(m, "estado", "APROVADO");
                        putJson(m, "disponivel", true);
                        salvar();
                        aviso("Motorista aprovado.");
                        listaMotoristas();

                    } else if (escolha == 1) {
                        putJson(m, "estado", "REJEITADO");
                        putJson(m, "disponivel", false);
                        salvar();
                        aviso("Cadastro rejeitado.");
                        listaMotoristas();

                    } else if (escolha == 2) {
                        putJson(m, "disponivel", true);
                        salvar();
                        aviso("Motorista ativado.");
                        listaMotoristas();

                    } else if (escolha == 3) {
                        putJson(m, "disponivel", false);
                        salvar();
                        aviso("Motorista desativado.");
                        listaMotoristas();

                    } else if (escolha == 4) {
                        whatsapp("Olá " + m.optString("nome")
                                + ", estamos a contactar pela ÓTIMO ANGOLA.");
                    } else {
                        listaMotoristas();
                    }
                })
                .show();
    }

    // =========================================
    // WHATSAPP
    // =========================================

    private void whatsapp(String mensagem) {
        try {
            String url = "https://wa.me/" + WHATSAPP
                    + "?text=" + Uri.encode(mensagem);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

        } catch (Exception e) {
            aviso("Não foi possível abrir o WhatsApp.");
        }
    }

    // =========================================
    // DADOS / PERSISTÊNCIA
    // =========================================

    private JSONArray carregarArray(String chave) {
        try {
            return new JSONArray(prefs.getString(chave, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void salvar() {
        prefs.edit()
                .putString("pedidos", pedidos.toString())
                .putString("motoristas", motoristas.toString())
                .apply();
    }

    private void putJson(JSONObject obj, String chave, Object valor) {
        try {
            obj.put(chave, valor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // DESIGN
    // =========================================

    private void base() {
        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(FUNDO);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(22, 24, 22, 35);
        layout.setBackgroundColor(FUNDO);

        scroll.addView(layout);

        setContentView(scroll);
    }

    private void topo(String titulo, String subtitulo) {
        texto("ÓTIMO ANGOLA", 15, VERDE, true);

        espacamento(12);

        texto(titulo, 24, BRANCO, true);

        espacamento(5);

        texto(subtitulo, 14, CINZA, false);

        espacamento(20);
    }

    private void texto(String valor, int tamanho, int cor, boolean bold) {
        TextView t = new TextView(this);

        t.setText(valor);
        t.setTextSize(tamanho);
        t.setTextColor(cor);
        t.setGravity(Gravity.START);
        t.setPadding(0, 5, 0, 5);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        adicionar(t);
    }

    private void card(String valor) {
        TextView t = new TextView(this);

        t.setText(valor);
        t.setTextColor(BRANCO);
        t.setTextSize(15);
        t.setTypeface(null, Typeface.BOLD);
        t.setPadding(18, 20, 18, 20);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(18);

        t.setBackground(bg);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 8, 0, 8);

        layout.addView(t, p);
    }

    private void botao(String nome, Runnable acao) {
        Button b = new Button(this);

        b.setText(nome);
        b.setTextColor(Color.BLACK);
        b.setTextSize(14);
        b.setTypeface(null, Typeface.BOLD);
        b.setAllCaps(false);
        b.setPadding(12, 15, 12, 15);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(VERDE);
        bg.setCornerRadius(16);

        b.setBackground(bg);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);

        layout.addView(b, p);

        b.setOnClickListener(v -> acao.run());
    }

    private void botaoSecundario(String nome, Runnable acao) {
        Button b = new Button(this);

        b.setText(nome);
        b.setTextColor(BRANCO);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setPadding(12, 15, 12, 15);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(16);
        bg.setStroke(1, Color.rgb(65, 70, 76));

        b.setBackground(bg);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);

        layout.addView(b, p);

        b.setOnClickListener(v -> acao.run());
    }

    private EditText campo(String hint) {
        EditText e = new EditText(this);

        e.setHint(hint);
        e.setHintTextColor(CINZA);
        e.setTextColor(BRANCO);
        e.setTextSize(15);
        e.setSingleLine(true);
        e.setPadding(16, 14, 16, 14);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(14);
        bg.setStroke(1, Color.rgb(55, 60, 66));

        e.setBackground(bg);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);

        e.setLayoutParams(p);

        return e;
    }

    private Spinner spinner(String[] itens) {
        Spinner s = new Spinner(this);

        ArrayAdapter<String> a = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                itens
        );

        a.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        s.setAdapter(a);
        s.setBackgroundColor(CARD);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 8, 0, 8);

        layout.addView(s, p);

        return s;
    }

    private void adicionar(View v) {
        if (v.getParent() != null) return;

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);

        layout.addView(v, p);
    }

    private void espacamento(int altura) {
        View v = new View(this);

        layout.addView(v, new LinearLayout.LayoutParams(1, altura));
    }

    private void aviso(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private String formatar(int valor) {
        return String.format("%,d", valor).replace(",", ".");
    }
}