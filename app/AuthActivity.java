package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

public class AuthActivity extends Activity {

    EditText email, senha;
    TextView mensagem;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        construirTela();
    }

    private void construirTela() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(13, 17, 22));

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 70, 35, 35);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        scroll.addView(layout);

        TextView logo = new TextView(this);
        logo.setText("ÓTIMO ANGOLA");
        logo.setTextColor(Color.rgb(205, 255, 53));
        logo.setTextSize(30);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        layout.addView(logo);

        TextView titulo = new TextView(this);
        titulo.setText("Bem-vindo à sua próxima viagem");
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(20);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(0, 25, 0, 35);
        layout.addView(titulo);

        email = new EditText(this);
        email.setSingleLine(true);
        email.setHint("O seu e-mail");
        email.setHintTextColor(Color.GRAY);
        email.setTextColor(Color.WHITE);
        email.setInputType(33);
        campo(email);

        senha = new EditText(this);
        senha.setSingleLine(true);
        senha.setHint("Palavra-passe");
        senha.setHintTextColor(Color.GRAY);
        senha.setTextColor(Color.WHITE);
        senha.setInputType(129);
        campo(senha);

        Button entrar = botao("INICIAR SESSÃO");
        Button registar = botao("CRIAR CONTA");

        layout.addView(entrar);
        layout.addView(registar);

        mensagem = new TextView(this);
        mensagem.setTextColor(Color.WHITE);
        mensagem.setTextSize(14);
        mensagem.setGravity(Gravity.CENTER);
        mensagem.setPadding(0, 25, 0, 0);
        layout.addView(mensagem);

        entrar.setOnClickListener(v -> iniciarSessao());
        registar.setOnClickListener(v -> criarConta());

        setContentView(scroll);
    }

    private void campo(EditText campo) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 10, 0, 15);
        campo.setPadding(20, 18, 20, 18);
        campo.setBackgroundColor(Color.rgb(35, 42, 50));
        layout.addView(campo, params);
    }

    private Button botao(String texto) {
        Button b = new Button(this);
        b.setText(texto);
        b.setTextColor(Color.BLACK);
        b.setTextSize(15);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(205, 255, 53)
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 12, 0, 5);
        b.setLayoutParams(params);
        return b;
    }

    private void iniciarSessao() {
        String e = email.getText().toString().trim();
        String p = senha.getText().toString();

        if (e.isEmpty() || p.isEmpty()) {
            mensagem.setText("Preencha o e-mail e a palavra-passe.");
            return;
        }

        mensagem.setText("A verificar os seus dados...");

        SupabaseAuth.entrar(this, e, p, (sucesso, resposta) -> {
            if (sucesso) {
                mensagem.setText("Sessão iniciada!");
                startActivity(new android.content.Intent(
                        this, MainActivity.class));
                finish();
            } else {
                mensagem.setText(resposta.optString(
                        "message", "Não foi possível iniciar sessão."));
            }
        });
    }

    private void criarConta() {
        String e = email.getText().toString().trim();
        String p = senha.getText().toString();

        if (e.isEmpty() || p.length() < 8) {
            mensagem.setText(
                    "Introduza um e-mail e uma palavra-passe com pelo menos 8 caracteres.");
            return;
        }

        mensagem.setText("A criar a sua conta...");

        SupabaseAuth.registar(this, e, p, (sucesso, resposta) -> {
            if (sucesso) {
                if (resposta.has("access_token")) {
                    mensagem.setText("Conta criada com sucesso!");
                    startActivity(new android.content.Intent(
                            this, MainActivity.class));
                    finish();
                } else {
                    mensagem.setText(
                            "Conta criada! Confirme o seu e-mail antes de iniciar sessão.");
                }
            } else {
                mensagem.setText(resposta.optString(
                        "message", "Erro ao criar conta."));
            }
        });
    }
}
