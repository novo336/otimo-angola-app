package ao.otimoangola.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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

    // ===== CORES ÓTIMO ANGOLA =====
    private final int FUNDO = Color.rgb(13, 17, 22);
    private final int CARD = Color.rgb(25, 32, 40);
    private final int VERDE = Color.rgb(205, 255, 53);
    private final int BRANCO = Color.WHITE;
    private final int CINZA = Color.rgb(155, 166, 178);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(13, 17, 22));
        getWindow().setNavigationBarColor(Color.rgb(13, 17, 22));

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

    // ================= INÍCIO =================

    private void inicio() {

        base("ÓTIMO ANGOLA", "TRANSPORTES • LOGÍSTICA • MUDANÇAS");

        // Cartão principal
        LinearLayout hero = card();

        TextView tag = new TextView(this);
        tag.setText("  LOGÍSTICA À TUA MANEIRA  ");
        tag.setTextColor(Color.BLACK);
        tag.setTextSize(11);
        tag.setTypeface(null, Typeface.BOLD);
        tag.setPadding(dp(10), dp(8), dp(10), dp(8));
        tag.setBackground(fundo(VERDE, 30));
        hero.addView(tag);

        TextView slogan = new TextView(this);
        slogan.setText("Movemos\n");
        slogan.append("o que importa.");
        slogan.setTextSize(32);
        slogan.setTypeface(null, Typeface.BOLD);
        slogan.setTextColor(BRANCO);
        slogan.setPadding(0, dp(20), 0, dp(8));
        hero.addView(slogan);

        TextView desc = new TextView(this);
        desc.setText(
                "Mudanças, mercadorias e entregas.\n"
                        + "O teu transporte começa aqui."
        );
        desc.setTextSize(15);
        desc.setTextColor(CINZA);
        desc.setPadding(0, 0, 0, dp(20));
        hero.addView(desc);

        TextView preco = new TextView(this);
        preco.setText("A partir de 35.000 Kz");
        preco.setTextColor(VERDE);
        preco.setTextSize(18);
        preco.setTypeface(null, Typeface.BOLD);
        hero.addView(preco);

        botao("Solicitar transporte", this::cliente);

        // Serviços
        tituloSecao("OS NOSSOS SERVIÇOS");

        servicoCard("01", "Mudanças", "Transportamos os teus bens.");
        servicoCard("02", "Mercadorias", "Transporte de cargas e produtos.");
        servicoCard("03", "Entregas", "Movimentação e entrega de encomendas.");

        tituloSecao("FAZ PARTE DA ÓTIMO");

        botaoSecundario("Cadastrar motorista", this::cadastroMotorista);
        botaoSecundario("Painel administrativo", this::loginAdmin);

        textoRodape("ÓTIMO ANGOLA\nMovemos o que importa.");
    }

    private void servicoCard(String numero, String nome, String descricao) {
        LinearLayout c = card();
        c.setOrientation(LinearLayout.HORIZONTAL);
        c.setGravity(Gravity.CENTER_VERTICAL);

        TextView n = new TextView(this);
        n.setText(numero);
        n.setTextColor(VERDE);
        n.setTextSize(20);
        n.setTypeface(null, Typeface.BOLD);
        n.setPadding(0, 0, dp(18), 0);
        c.addView(n);

        LinearLayout coluna = new LinearLayout(this);
        coluna.setOrientation(LinearLayout.VERTICAL);

        TextView t = new TextView(this);
        t.setText(nome);
        t.setTextColor(BRANCO);
        t.setTextSize(17);
        t.setTypeface(null, Typeface.BOLD);

        TextView d = new TextView(this);
        d.setText(descricao);
        d.setTextColor(CINZA);
        d.setTextSize(13);
        d.setPadding(0, dp(5), 0, 0);

        coluna.addView(t);
        coluna.addView(d);

        c.addView(coluna);
    }

    // ================= BASE VISUAL =================

    private void base(String titulo, String subtitulo) {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(FUNDO);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);

        box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(22), dp(24), dp(22), dp(35));
        box.setBackgroundColor(FUNDO);

        scroll.addView(box);
        root.addView(scroll);

        setContentView(root);

        // Cabeçalho / marca
        TextView logo = new TextView(this);
        logo.setText("ÓTIMO  /  ANGOLA");
        logo.setTextColor(VERDE);
        logo.setTextSize(14);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setLetterSpacing(0.08f);
        logo.setPadding(0, dp(8), 0, dp(18));
        box.addView(logo);

        TextView t = new TextView(this);
        t.setText(titulo);
        t.setTextSize(27);
        t.setTypeface(null, Typeface.BOLD);
        t.setTextColor(BRANCO);
        t.setGravity(Gravity.START);
        box.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitulo);
        s.setTextSize(13);
        s.setTextColor(CINZA);
        s.setPadding(0, dp(8), 0, dp(22));
        box.addView(s);

        animarEntrada(logo, 0);
        animarEntrada(t, 1);
        animarEntrada(s, 2);
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(18), dp(18), dp(18), dp(18));
        c.setBackground(fundo(CARD, 20));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, -2
        );
        lp.setMargins(0, dp(8), 0, dp(12));
        c.setLayoutParams(lp);

        box.addView(c);
        animarEntrada(c, box.getChildCount());
        return c;
    }

    private void tituloSecao(String texto) {
        TextView t = new TextView(this);
        t.setText(texto);
        t.setTextColor(VERDE);
        t.setTextSize(12);
        t.setTypeface(null, Typeface.BOLD);
        t.setLetterSpacing(0.08f);
        t.setPadding(0, dp(22), 0, dp(8));
        box.addView(t);
        animarEntrada(t, box.getChildCount());
    }

    private void texto(String value) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(15);
        t.setTextColor(BRANCO);
        t.setPadding(dp(16), dp(14), dp(16), dp(14));
        t.setBackground(fundo(CARD, 16));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, -2
        );
        lp.setMargins(0, dp(5), 0, dp(5));
        t.setLayoutParams(lp);

        box.addView(t);
        animarEntrada(t, box.getChildCount());
    }

    private void textoRodape(String value) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextColor(CINZA);
        t.setTextSize(12);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, dp(30), 0, dp(10));
        box.addView(t);
    }

    // ================= CAMPOS =================

    private EditText campo(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(15);
        e.setSingleLine(false);
        e.setTextColor(BRANCO);
        e.setHintTextColor(Color.rgb(135, 145, 155));
        e.setPadding(dp(16), dp(13), dp(16), dp(13));
        e.setBackground(fundo(CARD, 15));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, -2
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        e.setLayoutParams(lp);

        box.addView(e);
        animarEntrada(e, box.getChildCount());

        return e;
    }

    // ================= BOTÕES =================

    private void botao(String label, Runnable acao) {

        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(15);
        b.setTypeface(null, Typeface.BOLD);
        b.setAllCaps(false);
        b.setTextColor(Color.rgb(15, 18, 20));
        b.setPadding(dp(18), dp(14), dp(18), dp(14));
        b.setBackground(fundo(VERDE, 18));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, dp(58)
        );
        lp.setMargins(0, dp(8), 0, dp(8));
        b.setLayoutParams(lp);

        b.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(80)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        acao.run();
                    })
                    .start();
        });

        box.addView(b);
        animarEntrada(b, box.getChildCount());
    }

    private void botaoSecundario(String label, Runnable acao) {

        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setTextColor(VERDE);
        b.setPadding(dp(15), dp(12), dp(15), dp(12));
        b.setBackground(fundo(CARD, 18));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, dp(55)
        );
        lp.setMargins(0, dp(7), 0, dp(7));
        b.setLayoutParams(lp);

        b.setOnClickListener(v -> acao.run());

        box.addView(b);
        animarEntrada(b, box.getChildCount());
    }

    private void voltar() {
        botaoSecundario("←  Voltar ao início", this::inicio);
    }

    // ================= ANIMAÇÕES =================

    private void animarEntrada(View v, int ordem) {
        v.setAlpha(0f);
        v.setTranslationY(dp(18));

        v.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(Math.min(ordem * 35, 240))
                .setDuration(380)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    // ================= UTILIDADES VISUAIS =================

    private GradientDrawable fundo(int cor, int raio) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(cor);
        gd.setCornerRadius(dp(raio));
        return gd;
    }

    private int dp(float valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private String dinheiro(int valor) {
        return NumberFormat.getInstance(new Locale("pt", "AO"))
                .format(valor) + " Kz";
    }

    private int calcular(int km) {
        return BASE + (km * POR_KM);
    }

    private Spinner spinner(String[] opcoes) {
        Spinner s = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        s.setAdapter(adapter);
        s.setBackground(fundo(CARD, 15));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                -1, dp(55)
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        s.setLayoutParams(lp);

        box.addView(s);
        animarEntrada(s, box.getChildCount());

        return s;
    }

    // ================= CLIENTE =================

    private void cliente() {
        base("Solicitar transporte", "Diz-nos o que precisas transportar.");

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
        telefone.setInputType(3);

        botao("CALCULAR PREÇO  →", () -> {
            try {
                if (origem.getText().toString().trim().isEmpty()
                        || destino.getText().toString().trim().isEmpty()
                        || telefone.getText().toString().trim().isEmpty()) {
                    aviso("Preencha origem, destino e telefone.");
                    return;
                }

                int km = Integer.parseInt(
                        kmCampo.getText().toString().trim()
                );

                if (km <= 0) {
                    aviso("Insira uma distância válida.");
                    return;
                }

                int preco = calcular(km);

                new AlertDialog.Builder(this)
                        .setTitle("Estimativa do transporte")
                        .setMessage("Distância: " + km + " km\n\n"
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
        base("Pedido registado ✓", "O teu pedido foi preparado.");

        LinearLayout c = card();

        TextView t = new TextView(this);
        t.setText("PEDIDO RECEBIDO");
        t.setTextColor(VERDE);
        t.setTextSize(21);
        t.setTypeface(null, Typeface.BOLD);
        c.addView(t);

        TextView d = new TextView(this);
        d.setText("Agora envia os dados para a nossa equipa confirmar o transporte.");
        d.setTextColor(CINZA);
        d.setTextSize(14);
        d.setPadding(0, dp(10), 0, dp(10));
        c.addView(d);

        botao("ENVIAR PELO WHATSAPP", () -> whatsapp(msg));

        voltar();
    }

    // ================= MOTORISTA =================

    private void cadastroMotorista() {
        base("Cadastro de motorista", "Junta-te à rede de transportes ÓTIMO.");

        EditText nome = campo("Nome completo");
        EditText telefone = campo("Número de telefone");
        EditText matricula = campo("Matrícula da viatura");
        EditText municipio = campo("Município");
        EditText extra = campo("Marca e modelo da viatura");

        telefone.setInputType(3);

        Spinner tipo = spinner(new String[]{
                "Carrinha pequena",
                "Carrinha grande",
                "Caminhão",
                "Outro"
        });

        botao("ENVIAR CADASTRO  →", () -> {
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

                base("Cadastro recebido ✓", "Candidatura registada");

                LinearLayout c = card();

                TextView t = new TextView(this);
                t.setText("EM ANÁLISE");
                t.setTextColor(VERDE);
                t.setTextSize(20);
                t.setTypeface(null, Typeface.BOLD);
                c.addView(t);

                TextView d = new TextView(this);
                d.setText("Os teus dados foram guardados. A administração irá analisar o teu cadastro.");
                d.setTextColor(CINZA);
                d.setTextSize(14);
                d.setPadding(0, dp(12), 0, 0);
                c.addView(d);

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
        pin.setPadding(dp(15), dp(12), dp(15), dp(12));

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
        base("Painel administrativo", "CENTRO DE CONTROLO");

        LinearLayout estatisticas = card();

        TextView e1 = new TextView(this);
        e1.setText("PEDIDOS REGISTADOS");
        e1.setTextColor(CINZA);
        e1.setTextSize(12);
        estatisticas.addView(e1);

        TextView e2 = new TextView(this);
        e2.setText(String.valueOf(pedidos.length()));
        e2.setTextColor(VERDE);
        e2.setTextSize(36);
        e2.setTypeface(null, Typeface.BOLD);
        estatisticas.addView(e2);

        TextView e3 = new TextView(this);
        e3.setText("MOTORISTAS REGISTADOS: " + motoristas.length());
        e3.setTextColor(BRANCO);
        e3.setTextSize(14);
        e3.setPadding(0, dp(8), 0, 0);
        estatisticas.addView(e3);

        botao("Pedidos (" + pedidos.length() + ")  →", this::listaPedidos);
        botaoSecundario("Motoristas (" + motoristas.length() + ")", this::listaMotoristas);
        botaoSecundario("Actualizar dados", this::admin);

        voltar();
    }

    // ================= PEDIDOS =================

    private void listaPedidos() {
        base("Pedidos", "GESTÃO DE SOLICITAÇÕES");

        if (pedidos.length() == 0) {
            texto("Ainda não existem pedidos.");
        }

        for (int i = pedidos.length() - 1; i >= 0; i--) {
            JSONObject p = pedidos.optJSONObject(i);
            if (p == null) continue;

            String id = p.optString("id");
            String estado = p.optString("estado", "PENDENTE");

            LinearLayout c = card();

            TextView codigo = new TextView(this);
            codigo.setText(id);
            codigo.setTextColor(VERDE);
            codigo.setTextSize(13);
            codigo.setTypeface(null, Typeface.BOLD);
            c.addView(codigo);

            TextView info = new TextView(this);
            info.setText(
                    "\n" + p.optString("servico")
                            + "\n\nOrigem: " + p.optString("origem")
                            + "\nDestino: " + p.optString("destino")
                            + "\nTelefone: " + p.optString("telefone")
                            + "\nVeículo: " + p.optString("veiculo")
                            + "\nDistância: " + p.optInt("km") + " km"
                            + "\nPreço: " + dinheiro(p.optInt("preco"))
            );
            info.setTextColor(BRANCO);
            info.setTextSize(14);
            c.addView(info);

            TextView st = new TextView(this);
            st.setText("\n●  " + estado);
            st.setTextColor(VERDE);
            st.setTextSize(13);
            st.setTypeface(null, Typeface.BOLD);
            c.addView(st);

            botaoSecundario("Alterar estado", () -> mudarEstadoPedido(id));

            botaoSecundario("Enviar pedido ao WhatsApp", () -> whatsapp(
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

    // ================= MOTORISTAS =================

    private void listaMotoristas() {
        base("Motoristas", "GESTÃO DA REDE DE TRANSPORTE");

        if (motoristas.length() == 0) {
            texto("Ainda não existem motoristas.");
        }

        for (int i = 0; i < motoristas.length(); i++) {
            JSONObject m = motoristas.optJSONObject(i);
            if (m == null) continue;

            String id = m.optString("id");

            LinearLayout c = card();

            TextView nome = new TextView(this);
            nome.setText(m.optString("nome"));
            nome.setTextColor(BRANCO);
            nome.setTextSize(18);
            nome.setTypeface(null, Typeface.BOLD);
            c.addView(nome);

            TextView info = new TextView(this);
            info.setText(
                    "\nTelefone: " + m.optString("telefone")
                            + "\nViatura: " + m.optString("veiculo")
                            + "\nMatrícula: " + m.optString("matricula")
                            + "\nMunicípio: " + m.optString("municipio")
                            + "\nModelo: " + m.optString("extra")
            );
            info.setTextColor(CINZA);
            info.setTextSize(14);
            c.addView(info);

            TextView estado = new TextView(this);
            estado.setText(
                    "\nESTADO: " + m.optString("estado")
                            + "\nDISPONÍVEL: "
                            + (m.optBoolean("disponivel") ? "SIM" : "NÃO")
            );
            estado.setTextColor(VERDE);
            estado.setTextSize(13);
            estado.setTypeface(null, Typeface.BOLD);
            c.addView(estado);

            botaoSecundario("Gerir motorista", () -> gerirMotorista(id));
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

    // ================= ARMAZENAMENTO =================

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

    // ================= WHATSAPP =================

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

    // ================= AVISOS =================

    private void aviso(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("ÓTIMO ANGOLA")
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}