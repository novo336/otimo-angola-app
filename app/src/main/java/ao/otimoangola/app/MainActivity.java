
package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    String servico = "Mudanças";
    String origem = "";
    String destino = "";
    String telefoneCliente = "";

    String nomeMotorista = "";
    String telefoneMotorista = "";
    String numeroBI = "";
    String matricula = "";
    String tipoViatura = "";

    int d = 0;

    final int PRECO_MINIMO = 35000;
    final int ACRESCIMO_KM = 1050;

    int id(String n) {
        return getResources().getIdentifier(
                n, "id", getPackageName()
        );
    }

    void on(String n, Runnable r) {
        int i = id(n);

        if (i != 0) {
            View v = findViewById(i);

            if (v != null) {
                v.setOnClickListener(x -> r.run());
            }
        }
    }

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        inicio();
    }

    // ECRÃ INICIAL
    void inicio() {
        setContentView(R.layout.activity_inicio);

        on("cardMudancas", () -> {
            servico = "Mudanças";
            form();
        });

        on("cardMercadorias", () -> {
            servico = "Transporte de mercadorias";
            form();
        });

        on("cardExpressas", () -> {
            servico = "Entregas";
            form();
        });

        on("btnVerHorarios", () -> {
            servico = "Mudanças";
            form();
        });

        on("btnMotorista", this::motorista);
    }

    // FORMULÁRIO DO CLIENTE
    void form() {
        setContentView(R.layout.activity_lista_viagens);

        destino = "";

        TextView t = findViewById(id("txtServicoEscolhido"));

        if (t != null) {
            t.setText("Serviço: " + servico);
        }

        on("btnVoltarLista", this::inicio);

        on("btnMapa", () -> {
            destino = "Kilamba (mapa)";

            TextView tm = findViewById(id("txtDestinoMapa"));

            if (tm != null) {
                tm.setText("Destino: " + destino + " ✅");
            }
        });

        on("btnCalcular", this::ler);
    }

    // LER DADOS DO CLIENTE
    void ler() {

        EditText et = findViewById(id("editTelefoneCliente"));
        EditText eo = findViewById(id("editOrigem"));
        EditText edest = findViewById(id("editDestino"));
        EditText ed = findViewById(id("editDistancia"));

        if (et == null || eo == null ||
                edest == null || ed == null) {

            Toast.makeText(this,
                    "Erro ao carregar os campos.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        telefoneCliente = et.getText().toString().trim();
        origem = eo.getText().toString().trim();

        destino = edest.getText().toString().trim();

        String distanciaTexto =
                ed.getText().toString().trim();

        if (telefoneCliente.isEmpty()) {
            et.setError("Introduza o seu telefone");
            et.requestFocus();
            return;
        }

        if (origem.isEmpty()) {
            eo.setError("Introduza a origem");
            eo.requestFocus();
            return;
        }

        if (destino.isEmpty()) {
            edest.setError("Introduza o destino");
            edest.requestFocus();
            return;
        }

        if (distanciaTexto.isEmpty()) {
            ed.setError("Introduza a distância");
            ed.requestFocus();
            return;
        }

        try {
            double distancia = Double.parseDouble(
                    distanciaTexto.replace(",", ".")
            );

            if (distancia <= 0) {
                ed.setError("Distância inválida");
                ed.requestFocus();
                return;
            }

            d = (int) Math.ceil(distancia);

        } catch (Exception e) {
            ed.setError("Introduza uma distância válida");
            ed.requestFocus();
            return;
        }

        estimativa();
    }

    // CALCULAR PREÇO
    void estimativa() {

        setContentView(R.layout.activity_reserva);

        int total = PRECO_MINIMO + ACRESCIMO_KM * d;
        int comissao = total * 10 / 100;
        int motorista = total - comissao;

        TextView a = findViewById(id("txtDetalheViagem"));

        if (a != null) {
            a.setText(
                    "Serviço: " + servico +
                    "\nTelefone: " + telefoneCliente +
                    "\nOrigem: " + origem +
                    "\nDestino: " + destino +
                    "\nDistância: " + d + " km"
            );
        }

        TextView b = findViewById(id("txtCalculo"));

        if (b != null) {
            b.setText(
                    "CÁLCULO DA VIAGEM\n\n" +
                    "Base: " + PRECO_MINIMO + " Kz\n" +
                    d + " km x " + ACRESCIMO_KM +
                    " = " + (d * ACRESCIMO_KM) + " Kz\n\n" +
                    "TOTAL: " + total + " Kz\n\n" +
                    "Cliente paga: " + total + " Kz\n" +
                    "Motorista recebe 90%: " +
                    motorista + " Kz\n" +
                    "ÓTIMO recebe 10%: " +
                    comissao + " Kz"
            );
        }

        on("btnVoltarConfirmar", this::form);
        on("btnPagar", this::sucesso);
    }

    // SERVIÇO CONCLUÍDO
    void sucesso() {
        setContentView(R.layout.activity_sucesso);

        on("btnVoltarInicio", this::inicio);
        on("btnRastrear", this::rastreio);
    }

    // RASTREAMENTO
    void rastreio() {
        setContentView(R.layout.activity_rastreamento);

        on("btnVoltarRastreio", this::sucesso);
    }

    // ABRIR CADASTRO DO MOTORISTA
    void motorista() {
        setContentView(R.layout.activity_motorista);

        on("btnVoltarMotorista", this::inicio);
        on("btnEnviarKyc", this::validarMotorista);
    }

    // VALIDAR CADASTRO DO MOTORISTA
    void validarMotorista() {

        EditText nome = findViewById(id("editNomeMotorista"));
        EditText telefone = findViewById(id("editTelefoneMotorista"));
        EditText bi = findViewById(id("editNumeroBI"));
        EditText placa = findViewById(id("editMatricula"));
        EditText viatura = findViewById(id("editTipoViatura"));

        if (nome == null || telefone == null ||
                bi == null || placa == null || viatura == null) {

            Toast.makeText(this,
                    "Erro ao carregar o formulário.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        nomeMotorista = nome.getText().toString().trim();
        telefoneMotorista = telefone.getText().toString().trim();
        numeroBI = bi.getText().toString().trim();
        matricula = placa.getText().toString().trim();
        tipoViatura = viatura.getText().toString().trim();

        if (nomeMotorista.isEmpty()) {
            nome.setError("Introduza o nome completo");
            nome.requestFocus();
            return;
        }

        if (telefoneMotorista.isEmpty()) {
            telefone.setError("Introduza o telefone");
            telefone.requestFocus();
            return;
        }

        if (numeroBI.isEmpty()) {
            bi.setError("Introduza o número do BI");
            bi.requestFocus();
            return;
        }

        if (matricula.isEmpty()) {
            placa.setError("Introduza a matrícula");
            placa.requestFocus();
            return;
        }

        if (tipoViatura.isEmpty()) {
            viatura.setError("Introduza o tipo de viatura");
            viatura.requestFocus();
            return;
        }

        Toast.makeText(this,
                "Dados preenchidos com sucesso! Próxima etapa: envio seguro para verificação KYC.",
                Toast.LENGTH_LONG).show();
    }
}
