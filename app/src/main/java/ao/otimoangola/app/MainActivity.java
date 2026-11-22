
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
import android.text.InputType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout box;
    private EditText origem, destino, km, clienteTel, detalhes;
    private EditText nome, motoristaTel, matricula, municipio, extra;
    private Spinner servico, viatura, provincia;
    private TextView precoView;

    private SharedPreferences storage;
    private JSONArray pedidos, motoristas;

    private final int verde = Color.rgb(20, 110, 60);
    private final int base = 35000;
    private final int porKm = 1050;

    // PIN ADMINISTRATIVO PROVISÓRIO: 2026
    // Número WhatsApp provisório
    private final String whatsapp = "244900000000";
    private final String PIN_ADMIN = "2026";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = getSharedPreferences("OTIMO_DATA", MODE_PRIVATE);
        pedidos = carregar("pedidos");
        motoristas = carregar("motoristas");

        inicio();
    }

    private JSONArray carregar(String chave) {
        try {
            return new JSONArray(storage.getString(chave, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void guardar() {
        storage.edit()
                .putString("pedidos", pedidos.toString())
                .putString("motoristas", motoristas.toString())
                .apply();
    }

    private void layoutBase() {
        ScrollView scroll = new ScrollView(this);
        box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(32, 30, 32, 30);
        box.setBackgroundColor(Color.rgb(245, 248, 245));
        scroll.addView(box);
        setContentView(scroll);
    }

    private void cabecalho(String sub) {
        TextView logo = texto("ÓTIMO ANGOLA", 29, verde, true);
        logo.setGravity(Gravity.CENTER);
        box.addView(logo);

        TextView subtitle = texto(sub, 14, Color.DKGRAY, false);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 25);
        box.addView(subtitle);
    }

    private void inicio() {
        layoutBase();
        cabecalho("MUDANÇAS • TRANSPORTES • ENTREGAS");

        box.addView(texto("Bem-vindo!", 24, Color.BLACK, true));

        Button c = botao("SOLICITAR TRANSPORTE");
        box.addView(c);
        c.setOnClickListener(v -> telaCliente());

        Button m = botao("CADASTRAR COMO MOTORISTA");
        box.addView(m);
        m.setOnClickListener(v -> telaMotorista());

        Button a = botao("ADMINISTRAÇÃO");
        box.addView(a);
        a.setOnClickListener(v -> loginAdmin());

        box.addView(texto(
                "ÓTIMO ANGOLA — A sua mudança, o nosso compromisso.",
                15, verde, false));
    }

    private void telaCliente() {
        layoutBase();
        cabecalho("NOVO PEDIDO DE TRANSPORTE");

        box.addView(botaoVoltar());

        servico = spinner("Serviço", new String[]{
                "Mudança residencial", "Transporte de mercadorias", "Entrega"
        });

        origem = campo("Origem / local de recolha");
        destino = campo("Destino / local de entrega");

        viatura = spinner("Viatura", new String[]{
                "Carrinha pequena", "Carrinha grande",
                "Camião", "Não sei qual preciso"
        });

        km = campo("Distância aproximada (km)");
        km.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        clienteTel = campo("Telefone do cliente");
        clienteTel.setInputType(InputType.TYPE_CLASS_PHONE);

        detalhes = campo("Detalhes da carga ou mudança");

        box.addView(texto(
                "Mínimo: 35.000 Kz + 1.050 Kz por km",
                16, verde, true));

        Button calc = botao("CALCULAR ESTIMATIVA");
        box.addView(calc);

        precoView = texto("Estimativa: —", 23, verde, true);
        precoView.setGravity(Gravity.CENTER);
        box.addView(precoView);

        calc.setOnClickListener(v -> calcular());

        Button enviar = botao("GUARDAR PEDIDO E ENVIAR");
        box.addView(enviar);
        enviar.setOnClickListener(v -> criarPedido());
    }

    private void telaMotorista() {
        layoutBase();
        cabecalho("CANDIDATURA DE MOTORISTA");
        box.addView(botaoVoltar());

        nome = campo("Nome completo");
        motoristaTel = campo("Telefone / WhatsApp");
        motoristaTel.setInputType(InputType.TYPE_CLASS_PHONE);

        viatura = spinner("Tipo de viatura", new String[]{
                "Carrinha pequena", "Carrinha grande", "Camião", "Outro"
        });

        matricula = campo("Matrícula");
        provincia = spinner("Província", new String[]{
                "Luanda", "Bengo", "Benguela", "Huambo", "Huíla",
                "Malanje", "Cuanza Norte", "Cuanza Sul", "Namibe",
                "Cabinda", "Zaire", "Uíge", "Lunda Norte",
                "Lunda Sul", "Moxico", "Bié", "Cuando", "Cubango"
        });
        municipio = campo("Município");
        extra = campo("Informações adicionais (opcional)");

        Button registar = botao("GUARDAR CANDIDATURA");
        box.addView(registar);
        registar.setOnClickListener(v -> guardarMotorista());
    }

    private void guardarMotorista() {
        String n = nome.getText().toString().trim();
        String t = motoristaTel.getText().toString().trim();
        String mat = matricula.getText().toString().trim();
        String mun = municipio.getText().toString().trim();

        if (n.isEmpty() || t.isEmpty() || mat.isEmpty() || mun.isEmpty()) {
            aviso("Preenche nome, telefone, matrícula e município.");
            return;
        }

        try {
            JSONObject j = new JSONObject();
            j.put("id", "M-" + System.currentTimeMillis());
            j.put("nome", n);
            j.put("telefone", t);
            j.put("viatura", viatura.getSelectedItem().toString());
            j.put("matricula", mat);
            j.put("provincia", provincia.getSelectedItem().toString());
            j.put("municipio", mun);
            j.put("extra", extra.getText().toString());
            j.put("estado", "PENDENTE");
            j.put("disponivel", false);

            motoristas.put(j);
            guardar();

            aviso("Candidatura guardada! Aguarda aprovação.");
            inicio();

        } catch (Exception e) {
            aviso("Erro ao guardar candidatura.");
        }
    }

    private void calcular() {
        long valor = valorKm();
        if (valor < 0) {
            aviso("Introduz uma distância válida.");
            return;
        }
        precoView.setText("Estimativa: " + formatar(valor) + " Kz");
    }

    private long valorKm() {
        try {
            double distancia = Double.parseDouble(
                    km.getText().toString().trim().replace(",", "."));
            if (distancia <= 0 || distancia > 10000) return -1;
            return Math.round(base + (distancia * porKm));
        } catch (Exception e) {
            return -1;
        }
    }

    private void criarPedido() {
        String o = origem.getText().toString().trim();
        String d = destino.getText().toString().trim();
        String t = clienteTel.getText().toString().trim();
        long preco = valorKm();

        if (o.isEmpty() || d.isEmpty() || t.isEmpty() || preco < 0) {
            aviso("Preenche origem, destino, telefone e distância válida.");
            return;
        }

        try {
            String id = "OA-" + (pedidos.length() + 1) + "-" +
                    (System.currentTimeMillis() % 100000);

            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("servico", servico.getSelectedItem().toString());
            j.put("origem", o);
            j.put("destino", d);
            j.put("viatura", viatura.getSelectedItem().toString());
            j.put("km", km.getText().toString());
            j.put("preco", preco);
            j.put("telefone", t);
            j.put("detalhes", detalhes.getText().toString());
            j.put("estado", "PENDENTE");

            pedidos.put(j);
            guardar();

            String msg = "NOVO PEDIDO - OTIMO ANGOLA\\n" +
                    "Código: " + id + "\\n" +
                    "Serviço: " + j.getString("servico") + "\\n" +
                    "Origem: " + o + "\\nDestino: " + d + "\\n" +
                    "Viatura: " + j.getString("viatura") + "\\n" +
                    "Distância: " + km.getText() + " km\\n" +
                    "Preço: " + formatar(preco) + " Kz\\n" +
                    "Cliente: " + t + "\\n" +
                    "Detalhes: " + detalhes.getText() + "\\n" +
                    "Estado: PENDENTE";

            aviso("Pedido " + id + " guardado!");
            whatsappMsg(msg);
            inicio();

        } catch (Exception e) {
            aviso("Não foi possível guardar pedido.");
        }
    }

    private void loginAdmin() {
        EditText pin = new EditText(this);
        pin.setHint("PIN de administrador");
        pin.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Administração ÓTIMO ANGOLA")
                .setView(pin)
                .setPositiveButton("Entrar", (dialog, which) -> {
                    if (PIN_ADMIN.equals(pin.getText().toString())) {
                        telaAdmin();
                    } else {
                        aviso("PIN incorreto.");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void telaAdmin() {
        layoutBase();
        cabecalho("PAINEL DE ADMINISTRAÇÃO");

        box.addView(botaoVoltar());

        box.addView(texto("PEDIDOS (" + pedidos.length() + ")",
                21, verde, true));

        if (pedidos.length() == 0) {
            box.addView(texto("Ainda não há pedidos guardados.",
                    16, Color.DKGRAY, false));
        }

        for (int i = pedidos.length() - 1; i >= 0; i--) {
            final int idx = i;
            JSONObject p = pedidos.optJSONObject(i);
            if (p == null) continue;

            String resumo = "Código: " + p.optString("id") +
                    "\n" + p.optString("origem") + " → " +
                    p.optString("destino") +
                    "\nPreço: " + formatar(p.optLong("preco")) + " Kz" +
                    "\nEstado: " + p.optString("estado");

            Button b = botao(resumo + "\nALTERAR ESTADO");
            box.addView(b);
            b.setOnClickListener(v -> mudarEstadoPedido(idx));
        }

        box.addView(texto("MOTORISTAS (" + motoristas.length() + ")",
                21, verde, true));

        if (motoristas.length() == 0) {
            box.addView(texto("Ainda não há motoristas registados.",
                    16, Color.DKGRAY, false));
        }

        for (int i = motoristas.length() - 1; i >= 0; i--) {
            final int idx = i;
            JSONObject m = motoristas.optJSONObject(i);
            if (m == null) continue;

            String resumo = m.optString("nome") +
                    "\n" + m.optString("telefone") +
                    "\nViatura: " + m.optString("viatura") +
                    "\nMatrícula: " + m.optString("matricula") +
                    "\nLocal: " + m.optString("municipio") +
                    ", " + m.optString("provincia") +
                    "\nAprovação: " + m.optString("estado") +
                    "\nDisponível: " + (m.optBoolean("disponivel") ?
                    "SIM" : "NÃO");

            Button b = botao(resumo + "\nGERIR MOTORISTA");
            box.addView(b);
            b.setOnClickListener(v -> gerirMotorista(idx));
        }
    }

    private void mudarEstadoPedido(int idx) {
        String[] estados = {"PENDENTE", "ATRIBUÍDO", "CONCLUÍDO", "CANCELADO"};

        new AlertDialog.Builder(this)
                .setTitle("Estado do pedido")
                .setItems(estados, (dialog, escolha) -> {
                    JSONObject p = pedidos.optJSONObject(idx);
                    if (p == null) return;
                    p.putSafe("estado", estados[escolha]);
                    guardar();
                    telaAdmin();
                }).show();
    }

    private void gerirMotorista(int idx) {
        JSONObject m = motoristas.optJSONObject(idx);
        if (m == null) return;

        String estado = m.optString("estado", "PENDENTE");
        boolean disp = m.optBoolean("disponivel", false);

        String[] opcoes = {
                "APROVAR MOTORISTA",
                "REJEITAR CANDIDATURA",
                " " + (disp ? "MARCAR INDISPONÍVEL" : "MARCAR DISPONÍVEL")
        };

        new AlertDialog.Builder(this)
                .setTitle(m.optString("nome"))
                .setItems(opcoes, (dialog, escolha) -> {
                    if (escolha == 0) {
                        m.putSafe("estado", "APROVADO");
                    } else if (escolha == 1) {
                        m.putSafe("estado", "REJEITADO");
                        m.putSafe("disponivel", false);
                    } else {
                        if (!"APROVADO".equals(estado)) {
                            aviso("Aprova o motorista primeiro.");
                            return;
                        }
                        m.putSafe("disponivel", !disp);
                    }
                    guardar();
                    telaAdmin();
                }).show();
    }

    private void whatsappMsg(String msg) {
        String url = "https://wa.me/" + whatsapp +
                "?text=" + Uri.encode(msg);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            aviso("WhatsApp não abriu.");
        }
    }

    private String formatar(long valor) {
        return NumberFormat.getInstance(new Locale("pt", "AO"))
                .format(valor);
    }

    private EditText campo(String dica) {
        EditText e = new EditText(this);
        e.setHint(dica);
        e.setTextSize(16);
        e.setPadding(20, 18, 20, 18);
        e.setBackgroundColor(Color.WHITE);
        box.addView(e);
        return e;
    }

    private Spinner spinner(String titulo, String[] itens) {
        box.addView(texto(titulo, 15, verde, true));
        Spinner s = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, itens);
        a.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
        box.addView(s);
        return s;
    }

    private TextView texto(String t, int size, int cor, boolean bold) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(size);
        v.setTextColor(cor);
        if (bold) v.setTypeface(null, Typeface.BOLD);
        v.setPadding(0, 12, 0, 12);
        return v;
    }

    private Button botao(String t) {
        Button b = new Button(this);
        b.setText(t);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(verde));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 10, 0, 10);
        b.setLayoutParams(p);
        return b;
    }

    private Button botaoVoltar() {
        Button b = botao("← VOLTAR");
        b.setOnClickListener(v -> inicio());
        return b;
    }

    private void aviso(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
