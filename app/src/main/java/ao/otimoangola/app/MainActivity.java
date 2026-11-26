package ao.otimoangola.app;

import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarBoasVindas();
    }

    private void animarEntrada(View view) {
        view.setAlpha(0f);
        view.setTranslationY(80f);
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(350)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    private void mostrarBoasVindas() {
        setContentView(R.layout.activity_boas_vindas);
        currentView = findViewById(android.R.id.content);
        animarEntrada(currentView);
        findViewById(R.id.btnComecar).setOnClickListener(v -> mostrarInicio());
    }

    private void mostrarInicio() {
        setContentView(R.layout.activity_inicio);
        currentView = findViewById(android.R.id.content);
        animarEntrada(currentView);
        findViewById(R.id.btnBuscar).setOnClickListener(v -> mostrarReserva());
        findViewById(R.id.btnMinhasReservas).setOnClickListener(v -> mostrarRastreamento());
    }

    private void mostrarReserva() {
        setContentView(R.layout.activity_reserva);
        currentView = findViewById(android.R.id.content);
        animarEntrada(currentView);
        findViewById(R.id.btnVoltar1).setOnClickListener(v -> mostrarInicio());
        findViewById(R.id.btnConfirmar).setOnClickListener(v -> mostrarSucesso());
    }

    private void mostrarSucesso() {
        setContentView(R.layout.activity_sucesso);
        currentView = findViewById(android.R.id.content);
        animarEntrada(currentView);
        findViewById(R.id.btnRastrear).setOnClickListener(v -> mostrarRastreamento());
        findViewById(R.id.btnVoltarInicio).setOnClickListener(v -> mostrarInicio());
    }

    private void mostrarRastreamento() {
        setContentView(R.layout.activity_rastreamento);
        currentView = findViewById(android.R.id.content);
        animarEntrada(currentView);
        findViewById(R.id.btnVoltar2).setOnClickListener(v -> mostrarInicio());
    }
}