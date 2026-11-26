package com.luandabenguelabooking.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    String precoEscolhido = "15.000 Kz";
    String viagemEscolhida = "MACON - 08:00";
    String assentoEscolhido = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarInicio();
    }

    void mostrarInicio() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnVerHorarios).setOnClickListener(v -> mostrarLista());
    }

    void mostrarLista() {
        setContentView(R.layout.activity_lista_viagens);
        findViewById(R.id.btnVoltarLista).setOnClickListener(v -> mostrarInicio());

        findViewById(R.id.cardViagem1).setOnClickListener(v -> {
            viagemEscolhida = "MACON - 08:00";
            precoEscolhido = "15.000 Kz";
            mostrarAssentos();
        });
        findViewById(R.id.cardViagem2).setOnClickListener(v -> {
            viagemEscolhida = "ANGO-REAL - 10:30";
            precoEscolhido = "18.500 Kz";
            mostrarAssentos();
        });
        findViewById(R.id.cardViagem3).setOnClickListener(v -> {
            viagemEscolhida = "SGO - 14:00";
            precoEscolhido = "16.000 Kz";
            mostrarAssentos();
        });
    }

    void mostrarAssentos() {
        setContentView(R.layout.activity_assentos);
        findViewById(R.id.btnVoltarAssentos).setOnClickListener(v -> mostrarLista());

        TextView a1 = findViewById(R.id.assento1);
        TextView a2 = findViewById(R.id.assento2);
        TextView a3 = findViewById(R.id.assento3);
        TextView a4 = findViewById(R.id.assento4);
        TextView a6 = findViewById(R.id.assento6);

        a1.setOnClickListener(v -> { assentoEscolhido="1"; Toast.makeText(this,"Assento 1 escolhido",Toast.LENGTH_SHORT).show(); });
        a2.setOnClickListener(v -> { assentoEscolhido="2"; Toast.makeText(this,"Assento 2 escolhido",Toast.LENGTH_SHORT).show(); });
        a3.setOnClickListener(v -> { assentoEscolhido="3"; Toast.makeText(this,"Assento 3 escolhido",Toast.LENGTH_SHORT).show(); });
        a4.setOnClickListener(v -> { assentoEscolhido="4"; Toast.makeText(this,"Assento 4 escolhido",Toast.LENGTH_SHORT).show(); });
        a6.setOnClickListener(v -> { assentoEscolhido="6"; Toast.makeText(this,"Assento 6 escolhido",Toast.LENGTH_SHORT).show(); });

        findViewById(R.id.assento5).setOnClickListener(v -> Toast.makeText(this,"Ocupado!",Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnConfirmarAssento).setOnClickListener(v -> mostrarConfirmar());
    }

    void mostrarConfirmar() {
        setContentView(R.layout.activity_confirmar);
        TextView tv = findViewById(R.id.txtDetalheViagem);
        if(tv!=null) tv.setText(viagemEscolhida + " | Assento " + assentoEscolhido + " | " + precoEscolhido);
        
        findViewById(R.id.btnVoltarConfirmar).setOnClickListener(v -> mostrarAssentos());
        findViewById(R.id.btnPagar).setOnClickListener(v -> mostrarSucesso());
    }

    void mostrarSucesso() {
        setContentView(R.layout.activity_sucesso);
        findViewById(R.id.btnVoltarInicio).setOnClickListener(v -> mostrarInicio());
        findViewById(R.id.btnRastrear).setOnClickListener(v -> mostrarRastreio());
    }

    void mostrarRastreio() {
        setContentView(R.layout.activity_rastreamento);
        findViewById(R.id.btnVoltarRastreio).setOnClickListener(v -> mostrarSucesso());
    }
}