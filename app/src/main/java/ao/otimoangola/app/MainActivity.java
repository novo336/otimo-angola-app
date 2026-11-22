package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    private EditText origem, destino, detalhes, telefone;
    private Spinner servico, viatura;

    private final int verde = Color.rgb(20, 110, 60);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 35, 35, 35);
        layout.setBackgroundColor(Color.rgb(245, 248, 245));

        scroll.addView(layout);

        TextView logo = new TextView(this);
        logo.setText("ÓTIMO ANGOLA");
        logo.setTextSize(29);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setTextColor(verde);
        logo.setGravity(Gravity.CENTER);
        layout.addView(logo);

        TextView subtitulo = new TextView(this);
        subtitulo.setText("MUDANÇAS • TRANSPORTES • ENTREGAS");
        subtitulo.setTextSize(14);
        subtitulo.setTextColor(Color.DKGRAY);
        subtitulo.setGravity(Gravity.CENTER);
        subtitulo.setPadding(0, 10, 0, 30);
        layout.addView(subtitulo);

        TextView titulo = new TextView(this);
        titulo.setText("Solicitar transporte");
        titulo.setTextSize(23);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.BLACK);
        layout.addView(titulo);

        servico = criarSpinner(layout, "Tipo de serviço",
                new String[]{
                        "Mudança residencial",
                        "Transporte de mercadorias",
                        "Entrega"
                });

        origem = criarCampo(layout, "Local de recolha / Origem");

        destino = criarCampo(layout, "Local de entrega / Destino");

        viatura = criarSpinner(layout, "Tipo de viatura",
                new String[]{
                        "Carrinha pequena",
                        "Carrinha grande",
                        "Camião",
                        "Não sei qual preciso"
                });

        telefone = criarCampo(layout, "O teu número de telefone");

        detalhes = criarCampo(layout, "Detalhes da carga ou mudança");

        Button enviar = new Button(this);
        enviar.setText("PEDIR ORÇAMENTO PELO WHATSAPP");
        enviar.setTextColor(Color.WHITE);
        enviar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(verde)
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.setMargins(0, 30, 0, 20);
        layout.addView(enviar, buttonParams);

        enviar.setOnClickListener(v -> enviarPedido());

        TextView rodape = new TextView(this);
        rodape.setText("ÓTIMO ANGOLA\nA sua mudança, o nosso compromisso.");
        rodape.setTextColor(verde);
        rodape.setGravity(Gravity.CENTER);
        rodape.setPadding(0, 20, 0, 30);
        layout.addView(rodape);

        setContentView(scroll);
    }

    private EditText criarCampo(LinearLayout layout, String hint) {
        EditText campo = new EditText(this);
        campo.setHint(hint);
        campo.setTextSize(16);
        campo.setSingleLine(false);
        campo.setPadding(20, 20, 20, 20);
        campo.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 12, 0, 12);
        layout.addView(campo, params);

        return campo;
    }

    private Spinner criarSpinner(LinearLayout layout,
                              String titulo,
                              String[] opcoes) {

        TextView label = new TextView(this);
        label.setText(titulo);
        label.setTextSize(15);
        label.setTextColor(verde);
        label.setTypeface(null, Typeface.BOLD);
        label.setPadding(0, 15, 0, 5);
        layout.addView(label);

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 15);
        layout.addView(spinner, params);

        return spinner;
    }

    private void enviarPedido() {

        String o = origem.getText().toString().trim();
        String d = destino.getText().toString().trim();
        String t = telefone.getText().toString().trim();
        String det = detalhes.getText().toString().trim();

        if (o.isEmpty() || d.isEmpty() || t.isEmpty()) {
            Toast.makeText(
                    this,
                    "Preenche a origem, destino e telefone.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        String tipo = servico.getSelectedItem().toString();
        String carro = viatura.getSelectedItem().toString();

        String mensagem =
                "🚚 *NOVO PEDIDO - ÓTIMO ANGOLA*%0A" +
                "Serviço: " + tipo + "%0A" +
                "Origem: " + o + "%0A" +
                "Destino: " + d + "%0A" +
                "Viatura: " + carro + "%0A" +
                "Telefone do cliente: " + t + "%0A" +
                "Detalhes: " + det;

        abrirWhatsApp(mensagem);
    }

    private void abrirWhatsApp(String mensagem) {

        // SUBSTITUI PELO NÚMERO REAL DA ÓTIMO ANGOLA
        String numero = "244900000000";

        String texto =
                "🚚 NOVO PEDIDO - ÓTIMO ANGOLA\n" +
                "Serviço: " + servico.getSelectedItem() + "\n" +
                "Origem: " + origem.getText().toString() + "\n" +
                "Destino: " + destino.getText().toString() + "\n" +
                "Viatura: " + viatura.getSelectedItem() + "\n" +
                "Telefone: " + telefone.getText().toString() + "\n" +
                "Detalhes: " + detalhes.getText().toString();

        String url = "https://wa.me/" + numero +
                "?text=" + Uri.encode(texto);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Não foi possível abrir o WhatsApp.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}