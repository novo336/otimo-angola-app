package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;

public class MainActivity extends Activity {

    String servico = "Mudanças";
    String origem = "Morro Bento";
    String destino = "Talatona";
    int distanciaKm = 5;
    String detalhesCarga = "Mudança T2";
    int BASE = 35000;
    int POR_KM = 1050;

    // Método que evita erro "cannot find symbol"
    int getID(String nome){
        return getResources().getIdentifier(nome, "id", getPackageName());
    }
    void click(String nome, Runnable acao){
        int id = getID(nome);
        if(id!=0){
            View v = findViewById(id);
            if(v!=null) v.setOnClickListener(x->acao.run());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarInicio();
    }

    void mostrarInicio() {
        setContentView(R.layout.activity_inicio);
        click("cardMudancas", this::mostrarDetalhes);
        click("cardMercadorias", this::mostrarDetalhesMercadorias);
        click("cardExpressas", this::mostrarDetalhesExpressas);
        click("cardMudancaResidencial", this::mostrarDetalhes);
        click("cardMudancas", ()->{ servico="Mudanças"; });
        click("btnVerHorarios", ()->{ servico="Mudanças"; mostrarDetalhes(); });
        click("btnCriarConta", ()->{ servico="Mudanças"; mostrarDetalhes(); });
        click("btnVerHorario", ()->{ servico="Mudanças"; mostrarDetalhes(); });
    }

    void mostrarDetalhes(){ servico="Mudanças"; mostrarFormulario(); }
    void mostrarDetalhesMercadorias(){ servico="Transportes de Mercadorias"; mostrarFormulario(); }
    void mostrarDetalhesExpressas(){ servico="Entregas Expressas"; mostrarFormulario(); }

    void mostrarFormulario(){
        setContentView(R.layout.activity_lista_viagens);
        TextView tvServico = (TextView) findViewById(getID("txtServicoEscolhido"));
        if(tvServico!=null) tvServico.setText("Serviço: " + servico);
        click("btnVoltarLista", this::mostrarInicio);
        click("btnCalcular", this::lerEAvancar);
    }

    void lerEAvancar(){
        EditText eOrigem = (EditText) findViewById(getID("editOrigem"));
        EditText eDestino = (EditText) findViewById(getID("editDestino"));
        EditText eDist = (EditText) findViewById(getID("editDistancia"));
        EditText eDet = (EditText) findViewById(getID("editDetalhes"));
        if(eOrigem!=null && !eOrigem.getText().toString().isEmpty()) origem = eOrigem.getText().toString();
        if(eDestino!=null && !eDestino.getText().toString().isEmpty()) destino = eDestino.getText().toString();
        if(eDet!=null && !eDet.getText().toString().isEmpty()) detalhesCarga = eDet.getText().toString();
        try{ if(eDist!=null && !eDist.getText().toString().isEmpty()) distanciaKm = Integer.parseInt(eDist.getText().toString()); }catch(Exception e){ distanciaKm=5; }
        mostrarEstimativa();
    }

    void mostrarEstimativa() {
        setContentView(R.layout.activity_reserva);
        int adicional = distanciaKm * POR_KM;
        int total = BASE + adicional;
        int comissao = (int)(total * 0.10);
        int motorista = total - comissao;

        TextView tvDetalhe = (TextView) findViewById(getID("txtDetalheViagem"));
        if(tvDetalhe!=null) tvDetalhe.setText(servico + ": " + origem + " → " + destino + " (" + distanciaKm + "km)\n" + detalhesCarga);

        TextView tvCalc = (TextView) findViewById(getID("txtCalculo"));
        if(tvCalc!=null) tvCalc.setText(
            "Base: " + BASE + " Kz\n" +
            "Adicional: " + distanciaKm + " x " + POR_KM + " = " + adicional + " Kz\n" +
            "TOTAL: " + total + " Kz\n\n" +
            "Comissão ÓTIMO (10%): " + comissao + " Kz\n" +
            "Motorista (90%): " + motorista + " Kz"
        );
        click("btnVoltarConfirmar", this::mostrarFormulario);
        click("btnPagar", this::mostrarSucesso);
    }

    void mostrarSucesso() {
        setContentView(R.layout.activity_sucesso);
        click("btnVoltarInicio", this::mostrarInicio);
        click("btnRastrear", this::mostrarRastreio);
    }

    void mostrarRastreio() {
        setContentView(R.layout.activity_rastreamento);
        click("btnVoltarRastreio", this::mostrarSucesso);
        click("btnVoltarRastreamento", this::mostrarSucesso);
    }
}