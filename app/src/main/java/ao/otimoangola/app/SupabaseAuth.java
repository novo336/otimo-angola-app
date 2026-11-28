
package ao.otimoangola.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SupabaseAuth {

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private static final Handler MAIN =
            new Handler(Looper.getMainLooper());

    private SupabaseAuth() {}

    public interface Callback {
        void onResult(boolean sucesso, JSONObject resposta);
    }

    private static String authUrl() {
        return SupabaseConfig.SUPABASE_URL
                .replace("/rest/v1/", "/auth/v1/");
    }

    public static void registar(
            Context context,
            String email,
            String password,
            Callback callback) {

        JSONObject dados = new JSONObject();

        try {
            dados.put("email", email.trim());
            dados.put("password", password);
        } catch (Exception e) {
            entregar(callback, false, erro(e.getMessage()));
            return;
        }

        executar(context, "signup", dados, callback);
    }

    public static void entrar(
            Context context,
            String email,
            String password,
            Callback callback) {

        JSONObject dados = new JSONObject();

        try {
            dados.put("email", email.trim());
            dados.put("password", password);
        } catch (Exception e) {
            entregar(callback, false, erro(e.getMessage()));
            return;
        }

        executar(
                context,
                "token?grant_type=password",
                dados,
                callback
        );
    }

    private static void executar(
            Context context,
            String endpoint,
            JSONObject dados,
            Callback callback) {

        Context appContext = context.getApplicationContext();

        EXECUTOR.execute(() -> {
            HttpURLConnection conexao = null;

            try {
                URL url = new URL(authUrl() + endpoint);
                conexao = (HttpURLConnection) url.openConnection();

                conexao.setRequestMethod("POST");
                conexao.setConnectTimeout(15000);
                conexao.setReadTimeout(15000);
                conexao.setDoOutput(true);

                conexao.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY
                );

                conexao.setRequestProperty(
                        "Content-Type", "application/json"
                );

                conexao.setRequestProperty(
                        "Accept", "application/json"
                );

                byte[] corpo = dados.toString()
                        .getBytes(StandardCharsets.UTF_8);

                try (OutputStream saida = conexao.getOutputStream()) {
                    saida.write(corpo);
                }

                int codigo = conexao.getResponseCode();

                InputStream fluxo = codigo >= 200 && codigo < 300
                        ? conexao.getInputStream()
                        : conexao.getErrorStream();

                StringBuilder texto = new StringBuilder();

                if (fluxo != null) {
                    try (BufferedReader leitor =
                                 new BufferedReader(
                                         new InputStreamReader(
                                                 fluxo,
                                                 StandardCharsets.UTF_8))) {
                        String linha;
                        while ((linha = leitor.readLine()) != null) {
                            texto.append(linha);
                        }
                    }
                }

                JSONObject resposta =
                        texto.length() > 0
                                ? new JSONObject(texto.toString())
                                : new JSONObject();

                boolean sucesso = codigo >= 200 && codigo < 300;

                if (sucesso && resposta.has("access_token")) {
                    appContext.getSharedPreferences(
                            "OTIMO_AUTH",
                            Context.MODE_PRIVATE
                    ).edit()
                            .putString(
                                    "access_token",
                                    resposta.optString("access_token")
                            )
                            .putString(
                                    "refresh_token",
                                    resposta.optString("refresh_token")
                            )
                            .putString(
                                    "user_id",
                                    resposta.optJSONObject("user")
                                            .optString("id")
                            )
                            .apply();
                }

                entregar(callback, sucesso, resposta);

            } catch (Exception e) {
                entregar(callback, false, erro(e.getMessage()));

            } finally {
                if (conexao != null) {
                    conexao.disconnect();
                }
            }
        });
    }

    private static JSONObject erro(String mensagem) {
        JSONObject objeto = new JSONObject();

        try {
            objeto.put("message",
                    mensagem == null ? "Erro desconhecido" : mensagem);
        } catch (Exception ignored) {}

        return objeto;
    }

    private static void entregar(
            Callback callback,
            boolean sucesso,
            JSONObject resposta) {

        if (callback != null) {
            MAIN.post(() -> callback.onResult(sucesso, resposta));
        }
    }
}
