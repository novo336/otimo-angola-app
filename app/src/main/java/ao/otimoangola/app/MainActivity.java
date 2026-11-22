
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

    private EditText origem, destino, detalhes, telefone, distancia;
    private Spinner servico, viatura;
    private TextView precoTexto;

    private final int verde = Color.rgb(20, 110, 60);
    private final int precoBase = 35000;
    private final int precoKm = 1050;

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

        distancia = criarCampo(layout, "Distância estimada em km");
        distancia.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        telefone = criarCampo(layout, "O teu número de telefone");
        telefone.setInputType(InputType.TYPE_CLASS_PHONE);

        detalhes = criarCampo(layout, "Detalhes da carga ou mudança");

        TextView regra = new TextView(this);
        regra.setText("Preço mínimo: 35.000 Kz\\n" +
                "Adicional: 1.050 Kz por km");
        regra.setTextColor(verde);
        regra.setTextSize(16);
        regra.setTypeface(null, Typeface.BOLD);
        regra.setPadding(0, 20, 0, 10);
        layout.addView(regra);

        Button calcular = new Button(this);
        calcular.setText("CALCULAR PREÇO");
        calcular.setTextColor(Color.WHITE);
        calcular.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(verde)
        );
        layout.addView(calcular);

        precoTexto = new TextView(this);
        precoTexto.setText("Estimativa: -- Kz");
        precoTexto.setTextSize(24);
        precoTexto.setTypeface(null, Typeface.BOLD);
        precoTexto.setTextColor(verde);
        precoTexto.setGravity(Gravity.CENTER);
        precoTexto.setPadding(0, 25, 0, 25);
        layout.addView(precoTexto);

        calcular.setOnClickListener(v -> calcularPreco());

        Button enviar = new Button(this);
        enviar.setText("PEDIR ORÇAMENTO PELO WHATSAPP");
        enviar.setTextColor(Color.WHITE);
        enviar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(verde)
        );

        layout.addView(enviar);
        enviar.setOnClickListener(v -> enviarPedido());

        TextView rodape = new TextView(this);
        rodape.setText("ÓTIMO ANGOLA\\nA sua mudança, o nosso compromisso.");
        rodape.setTextColor(verde);
        rodape.setGravity(Gravity.CENTER);
        rodape.setPadding(0, 30, 0, 30);
        layout.addView(rodape);

        setContentView(scroll);
    }

    private EditText criarCampo(LinearLayout layout, String hint) {
        EditText campo = new EditText(this);
        campo.setHint(hint);
        campo.setTextSize(16);
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
        layout.addView(spinner);

        return spinner;
    }

    private long calcularValor() {
        String textoKm = distancia.getText().toString().trim()
                .replace(",", ".");

        if (textoKm.isEmpty()) {
            return -1;
        }

        try {
            double km = Double.parseDouble(textoKm);

            if (km <= 0 || km > 10000) {
                return -1;
            }

            return Math.round(precoBase + (km * precoKm));

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void calcularPreco() {
        long preco = calcularValor();

        if (preco < 0) {
            Toast.makeText(this,
                    "Introduz uma distância válida em quilómetros.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        NumberFormat formato = NumberFormat.getInstance(
                new Locale("pt", "AO")
        );

        precoTexto.setText("Estimativa: " +
                formato.format(preco) + " Kz");
    }

    private void enviarPedido() {

        String o = origem.getText().toString().trim();
        String d = destino.getText().toString().trim();
        String t = telefone.getText().toString().trim();
        String det = detalhes.getText().toString().trim();

        if (o.isEmpty() || d.isEmpty() || t.isEmpty()) {
            Toast.makeText(this,
                    "Preenche origem, destino e telefone.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        long preco = calcularValor();

        if (preco < 0) {
            Toast.makeText(this,
                    "Introduz uma distância válida antes de continuar.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        NumberFormat formato = NumberFormat.getInstance(
                new Locale("pt", "AO")
        );

        String texto =
                "NOVO PEDIDO - OTIMO ANGOLA\\n" +
                "Serviço: " + servico.getSelectedItem() + "\\n" +
                "Origem: " + o + "\\n" +
                "Destino: " + d + "\\n" +
                "Viatura: " + viatura.getSelectedItem() + "\\n" +
                "Distância: " + distancia.getText() + " km\\n" +
                "Preço estimado: " + formato.format(preco) + " Kz\\n" +
                "Telefone: " + t + "\\n" +
                "Detalhes: " + det;

        abrirWhatsApp(texto);
    }

    private void abrirWhatsApp(String texto) {

        String numero = "244900000000";

        String url = "https://wa.me/" + numero +
                "?text=" + Uri.encode(texto);

        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
        );

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Não foi possível abrir o WhatsApp.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
