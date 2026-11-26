package ao.otimoangola.app;
import android.app.Activity; import android.os.Bundle; import android.view.View; import android.widget.TextView; import android.widget.EditText;
public class MainActivity extends Activity {
    String servico="Mudanças"; String origem="Nova Vida"; String destino="Talatona"; int d=5;
    final int PRECO_MINIMO=35000; final int ACRESCIMO_KM=1050;
    int id(String n){return getResources().getIdentifier(n,"id",getPackageName());}
    void on(String n,Runnable r){int i=id(n); if(i!=0){View v=findViewById(i); if(v!=null) v.setOnClickListener(x->r.run());}}
    @Override protected void onCreate(Bundle s){super.onCreate(s); inicio();}
    void inicio(){setContentView(R.layout.activity_inicio); on("cardMudancas",()->{servico="Mudanças"; form();}); on("cardMercadorias",()->{servico="Transporte de mercadorias"; form();}); on("cardExpressas",()->{servico="Entregas"; form();}); on("btnVerHorarios",()->{servico="Mudanças"; form();}); on("btnMotorista",this::motorista);}
    void form(){setContentView(R.layout.activity_lista_viagens); TextView t=findViewById(id("txtServicoEscolhido")); if(t!=null) t.setText("Serviço: "+servico); on("btnVoltarLista",this::inicio); on("btnMapa",()->{destino="Kilamba (mapa)"; TextView tm=findViewById(id("txtDestinoMapa")); if(tm!=null) tm.setText("Destino: "+destino+" ✅");}); on("btnCalcular",this::ler);}
    void ler(){EditText eo=findViewById(id("editOrigem")); EditText ed=findViewById(id("editDistancia")); if(eo!=null&&!eo.getText().toString().isEmpty()) origem=eo.getText().toString(); try{if(ed!=null&&!ed.getText().toString().isEmpty()) d=Integer.parseInt(ed.getText().toString());}catch(Exception e){d=5;} estimativa();}
    void estimativa(){setContentView(R.layout.activity_reserva); int total=PRECO_MINIMO+ACRESCIMO_KM*d; int com=total*10/100; int mot=total*90/100; TextView a=findViewById(id("txtDetalheViagem")); if(a!=null) a.setText(servico+": "+origem+" → "+destino+" ("+d+"km)"); TextView b=findViewById(id("txtCalculo")); if(b!=null) b.setText("Ponto 8 conceito: P(d)=35.000+1.050xd\n\nBase: "+PRECO_MINIMO+" Kz\n"+d+"km x "+ACRESCIMO_KM+" = "+(d*ACRESCIMO_KM)+" Kz\nTOTAL: "+total+" Kz\n\nCliente paga: "+total+" Kz\nMotorista 90%: "+mot+" Kz\nÓTIMO 10%: "+com+" Kz"); on("btnVoltarConfirmar",this::form); on("btnPagar",this::sucesso);}
    void sucesso(){setContentView(R.layout.activity_sucesso); on("btnVoltarInicio",this::inicio); on("btnRastrear",this::rastreio);}
    void rastreio(){setContentView(R.layout.activity_rastreamento); on("btnVoltarRastreio",this::sucesso);}
    void motorista(){setContentView(R.layout.activity_motorista); on("btnVoltarMotorista",this::inicio);}
}