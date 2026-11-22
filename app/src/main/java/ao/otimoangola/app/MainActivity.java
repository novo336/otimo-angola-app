
package ao.otimoangola.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout box;
    private SharedPreferences storage;

    private JSONArray pedidos;
    private JSONArray motoristas;

    private final int BASE = 35000;
    private final int POR_KM = 1050;

    private final String WHATSAPP = "244900000000";
    private final String PIN_ADMIN = "2026";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = getSharedPreferences("OTIMO_DATA", MODE_PRIVATE);

        try {
            pedidos = new JSONArray(storage.getString("pedidos", "[]"));
            motoristas = new JSONArray(storage.getString("motoristas", "[]"));
        } catch (Exception e) {
            pedidos = new JSONArray();
            motoristas = new JSONArray();
        }

        inicio();
    }

    private void inicio() {
        base("ÓTIMO ANGOLA", "Mudanças • Entregas • Transporte");

        botao("Solicitar transporte", this::cliente);
        botao("Cadastrar motorista", this::cadastroMotorista);
        botao("Painel administrativo", this::loginAdmin);

        texto("Transportes a partir de 35.000 Kz");
        texto("Mudanças, mercadorias e entregas.");
    }

    private void base(String titulo, String subtitulo) {
        box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(35, 35, 35, 35);
        box.setBackgroundColor(Color.WHITE);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(box);

        setContentView(scroll);

        TextView t = new TextView(this);
        t.setText(titulo);
        t.setTextSize(28);
        t.setTypeface(null, Typeface.BOLD);
        t.setTextColor(Color.rgb(20, 90, 40));
        t.setGravity(Gravity.CENTER);
        box.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitulo);
        s.setTextSize(16);
        s.setGravity(Gravity.CENTER);
        s.setPadding(0, 10, 0, 30);
        box.addView(s);
    }

    private void texto(String value) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(16);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(0, 12, 0, 12);
        box.addView(t);
    }

    private EditText campo(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(16);
        box.addView(e);
        return e;
    }

    private void botao(String label, Runnable acao) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setBackgroundColor(Color.rgb(20, 110, 50));
        b.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, -2
        );
        lp.setMargins(0, 10, 0, 10);
        b.setLayoutParams(lp);

        b.setOnClickListener(v -> acao.run());
        box.addView(b);
    }

    private void voltar() {
        botao("Voltar ao início", this::inicio);
    }

    private String dinheiro(int valor) {
        return NumberFormat.getInstance(new Locale("pt", "AO"))
                .format(valor) + " Kz";
    }

    private int calcular(int km) {
        return BASE + (km * POR_KM);
    }

    // ================= CLIENTE =================

    private void cliente() {
        base("Solicitar transporte", "Preencha os dados da sua viagem.");

        EditText origem = campo("Local de recolha");
        EditText destino = campo("Local de entrega");

        Spinner servico = spinner(new String[]{
                "Mudança residencial",
                "Transporte de mercadorias",
                "Entrega"
        });

        Spinner veiculo = spinner(new String[]{
                "Carrinha pequena",
                "Carrinha grande",
                "Caminhão",
                "Outro"
        });

        EditText kmCampo = campo("Distância estimada (km)");
        EditText telefone = campo("Seu número de telefone");
        EditText detalhes = campo("Detalhes da carga");

        kmCampo.setInputType(2);

        botao("Calcular preço", () -> {
            try {
                int km = Integer.parseInt(kmCampo.getText().toString());

                if (km <= 0) {
                    aviso("Insira uma distância válida.");
                    return;
                }

                int preco = calcular(km);

                new AlertDialog.Builder(this)
                        .setTitle("Estimativa do transporte")
                        .setMessage("Distância: " + km + " km\n"
                                + "Preço estimado: " + dinheiro(preco)
                                + "\n\nO valor final pode ser ajustado.")
                        .setPositiveButton("Continuar", (d, w) ->
                                guardarPedido(
                                        origem.getText().toString(),
                                        destino.getText().toString(),
                                        servico.getSelectedItem().toString(),
                                        veiculo.getSelectedItem().toString(),
                                        km,
                                        preco,
                                        telefone.getText().toString(),
                                        detalhes.getText().toString()
                                ))
                        .setNegativeButton("Cancelar", null)
                        .show();

            } catch (Exception e) {
                aviso("Introduza a distância em km.");
            }
        });

        voltar();
    }

    private void guardarPedido(
            String origem,
            String destino,
            String servico,
            String veiculo,
            int km,
            int preco,
            String telefone,
            String detalhes
    ) {
        try {
            JSONObject p = new JSONObject();

            String id = "OA-" + System.currentTimeMillis();

            p.put("id", id);
            p.put("origem", origem);
            p.put("destino", destino);
            p.put("servico", servico);
            p.put("veiculo", veiculo);
            p.put("km", km);
            p.put("preco", preco);
            p.put("telefone", telefone);
            p.put("detalhes", detalhes);
            p.put("estado", "PENDENTE");

            pedidos.put(p);
            salvar();

            String msg = "NOVO PEDIDO - ÓTIMO ANGOLA\n"
                    + "Código: " + id + "\n"
                    + "Serviço: " + servico + "\n"
                    + "Origem: " + origem + "\n"
                    + "Destino: " + destino + "\n"
                    + "Veículo: " + veiculo + "\n"
                    + "Distância: " + km + " km\n"
                    + "Preço: " + dinheiro(preco) + "\n"
                    + "Cliente: " + telefone + "\n"
                    + "Detalhes: " + detalhes;

            sucessoPedido(msg);

        } catch (Exception e) {
            aviso("Não foi possível guardar o pedido.");
        }
    }

    private void sucessoPedido(String msg) {
        base("Pedido registado", "O seu pedido foi guardado.");

        texto("A ÓTIMO ANGOLA recebeu os dados do seu transporte.");

        botao("Enviar pedido por WhatsApp", () -> whatsapp(msg));

        voltar();
    }

    // ================= MOTORISTA =================

    private void cadastroMotorista() {
        base("Cadastro de motorista", "Registe os seus dados para trabalhar.");

        EditText nome = campo("Nome completo");
        EditText telefone = campo("Número de telefone");
        EditText matricula = campo("Matrícula da viatura");
        EditText municipio = campo("Município");
        EditText extra = campo("Marca e modelo da viatura");

        Spinner tipo = spinner(new String[]{
                "Carrinha pequena",
                "Carrinha grande",
                "Caminhão",
                "Outro"
        });

        botao("Enviar cadastro", () -> {
            if (nome.getText().toString().trim().isEmpty()
                    || telefone.getText().toString().trim().isEmpty()
                    || matricula.getText().toString().trim().isEmpty()) {
                aviso("Preencha nome, telefone e matrícula.");
                return;
            }

            try {
                JSONObject m = new JSONObject();

                m.put("id", "M-" + System.currentTimeMillis());
                m.put("nome", nome.getText().toString());
                m.put("telefone", telefone.getText().toString());
                m.put("matricula", matricula.getText().toString());
                m.put("municipio", municipio.getText().toString());
                m.put("veiculo", tipo.getSelectedItem().toString());
                m.put("extra", extra.getText().toString());
                m.put("estado", "PENDENTE");
                m.put("disponivel", false);

                motoristas.put(m);
                salvar();

                base("Cadastro recebido", "A sua candidatura foi registada.");
                texto("Estado: PENDENTE DE APROVAÇÃO.");
                texto("A administração irá verificar os seus dados.");

                voltar();

            } catch (Exception e) {
                aviso("Erro ao guardar cadastro.");
            }
        });

        voltar();
    }

    // ================= ADMIN =================

    private void loginAdmin() {
        EditText pin = new EditText(this);
        pin.setHint("PIN de administrador");
        pin.setInputType(2);

        new AlertDialog.Builder(this)
                .setTitle("Área administrativa")
                .setView(pin)
                .setPositiveButton("Entrar", (d, w) -> {
                    if (pin.getText().toString().equals(PIN_ADMIN)) {
                        admin();
                    } else {
                        aviso("PIN incorreto.");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void admin() {
        base("Painel administrativo", "Gestão da ÓTIMO ANGOLA");

        botao("Pedidos (" + pedidos.length() + ")", this::listaPedidos);
        botao("Motoristas (" + motoristas.length() + ")", this::listaMotoristas);

        botao("Atualizar dados", this::admin);
        voltar();
    }

    // ================= GESTÃO PEDIDOS =================

    private void listaPedidos() {
        base("Pedidos", "Gestão das solicitações");

        if (pedidos.length() == 0) {
            texto("Ainda não existem pedidos.");
        }

        for (int i = pedidos.length() - 1; i >= 0; i--) {
            JSONObject p = pedidos.optJSONObject(i);
            if (p == null) continue;

            String id = p.optString("id");
            String estado = p.optString("estado", "PENDENTE");

            texto(
                    "Código: " + id
                            + "\nOrigem: " + p.optString("origem")
                            + "\nDestino: " + p.optString("destino")
                            + "\nPreço: " + dinheiro(p.optInt("preco"))
                            + "\nEstado: " + estado
            );

            botao("Alterar estado: " + id, () -> mudarEstadoPedido(id));
            botao("Enviar pedido ao WhatsApp", () -> whatsapp(
                    "PEDIDO " + id
                            + "\nOrigem: " + p.optString("origem")
                            + "\nDestino: " + p.optString("destino")
                            + "\nCliente: " + p.optString("telefone")
                            + "\nPreço: " + dinheiro(p.optInt("preco"))
                            + "\nEstado: " + p.optString("estado")
            ));
        }

        voltar();
    }

    private void mudarEstadoPedido(String id) {
        String[] estados = {
                "PENDENTE",
                "ATRIBUÍDO",
                "CONCLUÍDO",
                "CANCELADO"
        };

        new AlertDialog.Builder(this)
                .setTitle("Estado do pedido")
                .setItems(estados, (dialog, escolha) -> {
                    for (int i = 0; i < pedidos.length(); i++) {
                        JSONObject p = pedidos.optJSONObject(i);

                        if (p != null && p.optString("id").equals(id)) {
                            safePut(p, "estado", estados[escolha]);
                        }
                    }

                    salvar();
                    listaPedidos();
                })
                .show();
    }

    // ================= GESTÃO MOTORISTAS =================

    private void listaMotoristas() {
        base("Motoristas", "Gestão dos motoristas cadastrados");

        if (motoristas.length() == 0) {
            texto("Ainda não existem motoristas.");
        }

        for (int i = 0; i < motoristas.length(); i++) {
            JSONObject m = motoristas.optJSONObject(i);
            if (m == null) continue;

            String id = m.optString("id");

            texto(
                    "Nome: " + m.optString("nome")
                            + "\nTelefone: " + m.optString("telefone")
                            + "\nViatura: " + m.optString("veiculo")
                            + "\nMatrícula: " + m.optString("matricula")
                            + "\nMunicípio: " + m.optString("municipio")
                            + "\nEstado: " + m.optString("estado")
                            + "\nDisponível: " + m.optBoolean("disponivel")
            );

            botao("Gerir motorista", () -> gerirMotorista(id));
        }

        voltar();
    }

    private void gerirMotorista(String id) {
        String[] opcoes = {
                "APROVAR",
                "REJEITAR",
                "ATIVAR/DESATIVAR DISPONIBILIDADE"
        };

        new AlertDialog.Builder(this)
                .setTitle("Gestão do motorista")
                .setItems(opcoes, (d, escolha) -> {

                    for (int i = 0; i < motoristas.length(); i++) {
                        JSONObject m = motoristas.optJSONObject(i);

                        if (m == null || !m.optString("id").equals(id)) {
                            continue;
                        }

                        if (escolha == 0) {
                            safePut(m, "estado", "APROVADO");
                        }

                        if (escolha == 1) {
                            safePut(m, "estado", "REJEITADO");
                            safePut(m, "disponivel", false);
                        }

                        if (escolha == 2) {
                            boolean disp = m.optBoolean("disponivel");
                            safePut(m, "disponivel", !disp);
                        }
                    }

                    salvar();
                    listaMotoristas();
                })
                .show();
    }

    // ================= UTILIDADES =================

    private Spinner spinner(String[] opcoes) {
        Spinner s = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                opcoes
        );

        s.setAdapter(adapter);
        box.addView(s);

        return s;
    }

    private void salvar() {
        storage.edit()
                .putString("pedidos", pedidos.toString())
                .putString("motoristas", motoristas.toString())
                .apply();
    }

    private void safePut(JSONObject obj, String key, Object value) {
        try {
            obj.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void whatsapp(String mensagem) {
        try {
            String url = "https://wa.me/" + WHATSAPP
                    + "?text=" + Uri.encode(mensagem);

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);

        } catch (Exception e) {
            aviso("Não foi possível abrir o WhatsApp.");
        }
    }

    private void aviso(String mensagem) {
        new AlertDialog.Builder(this)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}
