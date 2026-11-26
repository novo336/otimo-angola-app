package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    String servico = "Mudanças";
    String origem = "Morro Bento";
    String destino = "Talatona";
    int distanciaKm = 5;
    String detalhesCarga = "Mudança T2";
    int BASE = 35000;
    int POR_KM = 1050;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarInicio();
    }

    void mostrarInicio() {
        setContentView(R.layout.activity_inicio);
        findViewById(R.id.cardMudancas).setOnClickListener(v -> { servico="Mudanças"; mostrarDetalhes(); });
        findViewById(R.id.cardMercadorias).setOnClickListener(v -> { servico="Transportes de Mercadorias"; mostrarDetalhes(); });
        findViewById(R.id.cardExpressas).setOnClickListener(v -> { servico="Entregas Expressas"; mostrarDetalhes(); });
        findViewById(R.id.btnVerHorarios).setOnClickListener(v -> { servico="Mudanças"; mostrarDetalhes(); });
        findViewById(R.id.btnCriarConta).setOnClickListener(v -> { servico="Mudanças"; mostrarDetalhes(); });
    }

    void mostrarDetalhes() {
        setContentView(R.layout.activity_lista_viagens);
        TextView tvServico = findViewById(R.id.txtServicoEscolhido);
        if(tvServico!=null) tvServico.setText("Serviço: " + servico);

        findViewById(R.id.btnVoltarLista).setOnClickListener(v -> mostrarInicio());
        findViewById(R.id.btnCalcular).setOnClickListener(v -> {
            EditText eOrigem = findViewById(R.id.editOrigem);
            EditText eDestino = findViewById(R.id.editDestino);
            EditText eDist = findViewById(R.id.editDistancia);
            EditText eDet = findViewById(R.id.editDetalhes);
            if(eOrigem!=null && !eOrigem.getText().toString().isEmpty()) origem = eOrigem.getText().toString();
            if(eDestino!=null && !eDestino.getText().toString().isEmpty()) destino = eDestino.getText().toString();
            if(eDet!=null && !eDet.getText().toString().isEmpty()) detalhesCarga = eDet.getText().toString();
            try{
                if(eDist!=null && !eDist.getText().toString().isEmpty()) distanciaKm = Integer.parseInt(eDist.getText().toString());
            }catch(Exception e){ distanciaKm=5; }
            mostrarEstimativa();
        });
    }

    void mostrarEstimativa() {
        setContentView(R.layout.activity_reserva);
        int adicional = distanciaKm * POR_KM;
        int total = BASE + adicional;
        int comissao = (int)(total * 0.10);
        int motorista = total - comissao;

        TextView tvDetalhe = findViewById(R.id.txtDetalheViagem);
        if(tvDetalhe!=null) tvDetalhe.setText(servico + ": " + origem + " → " + destino + " (" + distanciaKm + "km)\n" + detalhesCarga);

        TextView tvCalc = findViewById(R.id.txtCalculo);
        if(tvCalc!=null) tvCalc.setText(
            "Base: " + BASE + " Kz\n" +
            "Adicional: " + distanciaKm + " x " + POR_KM + " = " + adicional + " Kz\n" +
            "TOTAL: " + total + " Kz\n\n" +
            "Comissão ÓTIMO (10%): " + comissao + " Kz\n" +
            "Motorista (90%): " + motorista + " Kz"
        );

        findViewById(R.id.btnVoltarConfirmar).setOnClickListener(v -> mostrarDetalhes());
        findViewById(R.id.btnPagar).setOnClickListener(v -> mostrarSucesso());
    }

    void mostrarSucesso() {
        setContentView(R.layout.activity_sucesso);
        TextView tv = findViewById(R.id.txtSucessoTitulo);
        // se não existir, ignora
        findViewById(R.id.btnVoltarInicio).setOnClickListener(v -> mostrarInicio());
        findViewById(R.id.btnRastrear).setOnClickListener(v -> mostrarRastreio());
        Toast.makeText(this,"Pedido registado! Motorista elegível será notificado.",Toast.LENGTH_LONG).show();
    }

    void mostrarRastreio() {
        setContentView(R.layout.activity_rastreamento);
        findViewById(R.id.btnVoltarRastreio).setOnClickListener(v -> mostrarSucesso());
    }
}