
package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.content.Intent;
import android.view.Gravity;
import android.widget.*;
import android.text.InputType;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout layout;
    private EditText origem, destino, telefone, detalhes, distancia;
    private EditText nomeMotorista, telefoneMotorista, matricula, municipio;
    private Spinner servico, viatura, tipoViatura, provincia;
    private TextView precoTexto;

    private final int verde = Color.rgb(20, 110, 60);
    private final int base = 35000;
    private final int porKm = 1050;

    // SUBSTITUIR PELO WHATSAPP REAL DA ADMINISTRAÇÃO
    private final String whatsappAdmin = "244900000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarInicio();
    }

    private void prepararLayout() {
        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.rgb(245, 248, 245));
        scroll.addView(layout);
        setContentView(scroll);
    }

    private void cabecalho(String subtitulo) {
        TextView logo = new TextView(this);
        logo.setText("ÓTIMO ANGOLA");
        logo.setTextColor(verde);
        logo.setTextSize(29);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        layout.addView(logo);

        TextView sub = new TextView(this);
        sub.setText(subtitulo);
        sub.setTextColor(Color.DKGRAY);
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 10, 0, 25);
        layout.addView(sub);
    }

    private void mostrarInicio() {
        prepararLayout();
        cabecalho("MUDANÇAS • TRANSPORTES • ENTREGAS");

        TextView titulo = texto("Bem-vindo à ÓTIMO ANGOLA", 22, Color.BLACK, true);
        layout.addView(titulo);

        Button cliente = botao("SOU CLIENTE — PEDIR TRANSPORTE");
        layout.addView(cliente);
        cliente.setOnClickListener(v -> mostrarCliente());

        Button motorista = botao("SOU MOTORISTA — CADASTRAR VIATURA");
        layout.addView(motorista);
        motorista.setOnClickListener(v -> mostrarMotorista());

        TextView info = texto(
                "Ligamos clientes a motoristas de mudanças e transportes.",
                16, Color.DKGRAY, false
        );
        info.setPadding(0, 30, 0, 0);
        layout.addView(info);
    }

    private void mostrarCliente() {
        prepararLayout();
        cabecalho("PEDIDO DE TRANSPORTE");

        Button voltar = botao("← VOLTAR AO INÍCIO");
        layout.addView(voltar);
        voltar.setOnClickListener(v -> mostrarInicio());

        servico = spinner("Tipo de serviço",
                new String[]{"Mudança residencial",
                "Transporte de mercadorias", "Entrega"});

        origem = campo("Origem / local de recolha");
        destino = campo("Destino / local de entrega");

        viatura = spinner("Tipo de viatura",
                new String[]{"Carrinha pequena", "Carrinha grande",
                "Camião", "Não sei qual preciso"});

        distancia = campo("Distância aproximada em km");
        distancia.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        telefone = campo("Número de telefone");
        telefone.setInputType(InputType.TYPE_CLASS_PHONE);

        detalhes = campo("Detalhes da carga ou mudança");

        TextView regra = texto("Mínimo: 35.000 Kz  |  1.050 Kz/km",
                16, verde, true);
        layout.addView(regra);

        Button calcular = botao("CALCULAR PREÇO");
        layout.addView(calcular);

        precoTexto = texto("Estimativa: —", 23, verde, true);
        layout.addView(precoTexto);

        calcular.setOnClickListener(v -> calcularPreco());

        Button enviar = botao("ENVIAR PEDIDO PELO WHATSAPP");
        layout.addView(enviar);
        enviar.setOnClickListener(v -> enviarPedidoCliente());
    }

    private void mostrarMotorista() {
        prepararLayout();
        cabecalho("REGISTO DE MOTORISTAS");

        Button voltar = botao("← VOLTAR AO INÍCIO");
        layout.addView(voltar);
        voltar.setOnClickListener(v -> mostrarInicio());

        TextView aviso = texto(
                "Preenche os teus dados para te candidatares como motorista.",
                16, Color.DKGRAY, false);
        layout.addView(aviso);

        nomeMotorista = campo("Nome completo");
        telefoneMotorista = campo("Número de telefone / WhatsApp");
        telefoneMotorista.setInputType(InputType.TYPE_CLASS_PHONE);

        tipoViatura = spinner("Tipo de viatura",
                new String[]{"Carrinha pequena", "Carrinha grande",
                "Camião", "Outro"});

        matricula = campo("Matrícula da viatura");
        provincia = spinner("Província",
                new String[]{"Luanda", "Bengo", "Benguela",
                "Huambo", "Huíla", "Malanje", "Cuanza Norte",
                "Cuanza Sul", "Namibe", "Cabinda", "Zaire",
                "Uíge", "Lunda Norte", "Lunda Sul", "Moxico",
                "Cuando", "Cubango", "Bié"});
        municipio = campo("Município onde trabalhas");
        detalhes = campo("Informações adicionais (opcional)");

        Button enviar = botao("ENVIAR CANDIDATURA");
        layout.addView(enviar);
        enviar.setOnClickListener(v -> enviarCadastroMotorista());

        TextView nota = texto(
                "A tua candidatura será enviada para a administração e aguarda aprovação.",
                14, Color.DKGRAY, false);
        nota.setPadding(0, 20, 0, 0);
        layout.addView(nota);
    }

    private EditText campo(String dica) {
        EditText e = new EditText(this);
        e.setHint(dica);
        e.setTextSize(16);
        e.setPadding(20, 18, 20, 18);
        e.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 10, 0, 10);
        layout.addView(e, p);
        return e;
    }

    private Spinner spinner(String titulo, String[] itens) {
        TextView label = texto(titulo, 15, verde, true);
        label.setGravity(Gravity.START);
        label.setPadding(0, 12, 0, 4);
        layout.addView(label);

        Spinner s = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, itens);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        layout.addView(s);
        return s;
    }

    private TextView texto(String t, int tamanho, int cor, boolean negrito) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(tamanho);
        v.setTextColor(cor);
        if (negrito) v.setTypeface(null, Typeface.BOLD);
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
        p.setMargins(0, 12, 0, 12);
        b.setLayoutParams(p);
        return b;
    }

    private long valorCalculado() {
        try {
            String kmTexto = distancia.getText().toString()
                    .trim().replace(",", ".");
            if (kmTexto.isEmpty()) return -1;
            double km = Double.parseDouble(kmTexto);
            if (km <= 0 || km > 10000) return -1;
            return Math.round(base + km * porKm);
        } catch (Exception e) {
            return -1;
        }
    }

    private String formatar(long valor) {
        return NumberFormat.getInstance(new Locale("pt", "AO"))
                .format(valor);
    }

    private void calcularPreco() {
        long valor = valorCalculado();
        if (valor < 0) {
            Toast.makeText(this, "Introduz uma distância válida.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        precoTexto.setText("Estimativa: " + formatar(valor) + " Kz");
    }

    private void enviarPedidoCliente() {
        String o = origem.getText().toString().trim();
        String d = destino.getText().toString().trim();
        String t = telefone.getText().toString().trim();
        long valor = valorCalculado();

        if (o.isEmpty() || d.isEmpty() || t.isEmpty() || valor < 0) {
            Toast.makeText(this,
                    "Preenche origem, destino, telefone e distância válida.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String msg = "NOVO PEDIDO - OTIMO ANGOLA\\n" +
                "Serviço: " + servico.getSelectedItem() + "\\n" +
                "Origem: " + o + "\\nDestino: " + d +
                "\\nViatura: " + viatura.getSelectedItem() +
                "\\nDistância: " + distancia.getText() + " km" +
                "\\nEstimativa: " + formatar(valor) + " Kz" +
                "\\nTelefone: " + t +
                "\\nDetalhes: " + detalhes.getText();

        abrirWhatsApp(msg);
    }

    private void enviarCadastroMotorista() {
        String nome = nomeMotorista.getText().toString().trim();
        String tel = telefoneMotorista.getText().toString().trim();
        String mat = matricula.getText().toString().trim();
        String mun = municipio.getText().toString().trim();

        if (nome.isEmpty() || tel.isEmpty() || mat.isEmpty() || mun.isEmpty()) {
            Toast.makeText(this,
                    "Preenche nome, telefone, matrícula e município.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String msg = "CANDIDATURA DE MOTORISTA - OTIMO ANGOLA\\n" +
                "Nome: " + nome +
                "\\nTelefone: " + tel +
                "\\nViatura: " + tipoViatura.getSelectedItem() +
                "\\nMatrícula: " + mat +
                "\\nProvíncia: " + provincia.getSelectedItem() +
                "\\nMunicípio: " + mun +
                "\\nInformações adicionais: " + detalhes.getText() +
                "\\nEstado: AGUARDA APROVAÇÃO";

        abrirWhatsApp(msg);
    }

    private void abrirWhatsApp(String msg) {
        String url = "https://wa.me/" + whatsappAdmin +
                "?text=" + Uri.encode(msg);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o WhatsApp.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
