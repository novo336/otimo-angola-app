package ao.otimoangola.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    // CORES NOVAS - IGUAL AS IMAGENS
    private final int FUNDO = Color.WHITE;
    private final int FUNDO_CINZA = Color.rgb(245, 245, 247);
    private final int VERMELHO = Color.rgb(212, 25, 25); // #D41919
    private final int VERMELHO_CLARO = Color.rgb(253, 236, 236);
    private final int ROSA_CARD = Color.rgb(248, 215, 215); // cards serviços
    private final int CARD_BRANCO = Color.WHITE;
    private final int PRETO = Color.rgb(17, 17, 17);
    private final int CINZA = Color.rgb(138, 138, 138);
    private final int CINZA_ESCURO = Color.rgb(80, 80, 80);
    private final int VERDE = Color.rgb(34, 197, 94);
    
    // CONFIGURAÇÕES TUAS - MANTIDAS
    private final int BASE = 35000;
    private final int POR_KM = 1050;
    private final int COMISSAO = 10;
    private final String WHATSAPP = "244962670068";
    private final String PIN_ADMIN = "2026";

    // DADOS
    private SharedPreferences prefs;
    private JSONArray pedidos;
    private JSONArray motoristas;
    private String telefoneCliente = "";
    private LinearLayout layout;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(FUNDO);
        getWindow().setNavigationBarColor(FUNDO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        
        prefs = getSharedPreferences("OTIMO_DATA", MODE_PRIVATE);
        pedidos = carregarArray("pedidos");
        motoristas = carregarArray("motoristas");
        telefoneCliente = prefs.getString("user_phone", "");

        if (telefoneCliente.isEmpty()) boasVindas();
        else inicio();
    }

    // =========================================
    // BOAS-VINDAS - NOVO DESIGN
    // =========================================
    private void boasVindas() {
        base();
        espacamento(35);
        logoCentral();
        espacamento(20);
        texto("Movemos o que importa.", 16, CINZA, false, Gravity.CENTER);
        espacamento(30);
        bannerVermelho("Mudança rápida e segura", "Serviço de mudança em Luanda • Cotação em minutos", true);
        espacamento(20);
        botaoVermelho("COMEÇAR", this::cadastroCliente);
        botaoSecundario("Já tenho conta", this::entrarCliente);
        espacamento(10);
        botaoSecundario("Sou motorista", this::cadastroMotorista);
        botaoLink("Área administrativa", this::administrador);
    }

    // =========================================
    // INICIO - IGUAL IMAGEM 1
    // =========================================
    private void inicio() {
        base();

        // TOPO: Logo + Perfil João Viegas
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams topP = new LinearLayout.LayoutParams(-1, -2);
        topP.setMargins(0,0,0,16);
        layout.addView(top, topP);
        
        // Logo
        TextView logo = new TextView(this);
        logo.setText("📍 ÓTIMO\nANGOLA");
        logo.setTextColor(VERMELHO);
        logo.setTextSize(18);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        top.addView(logo);

        // Avatar + nome
        LinearLayout perfil = new LinearLayout(this);
        perfil.setOrientation(LinearLayout.VERTICAL);
        perfil.setGravity(Gravity.END);
        TextView nome = new TextView(this);
        nome.setText("Olá, João Viegas");
        nome.setTextColor(PRETO);
        nome.setTypeface(null, Typeface.BOLD);
        nome.setTextSize(14);
        nome.setGravity(Gravity.END);
        TextView local = new TextView(this);
        local.setText("📍 Luanda, Angola");
        local.setTextColor(CINZA);
        local.setTextSize(12);
        local.setGravity(Gravity.END);
        perfil.addView(nome);
        perfil.addView(local);
        top.addView(perfil);

        // BARRA PESQUISA
        EditText busca = campoPesquisa("Para onde vamos mudar?");
        adicionar(busca);

        // BANNER VERMELHO
        bannerVermelho("Mudança rápida e segura", "Serviço de mudança em Luanda • Cotação em minutos", false);

        // SERVIÇOS
        espacamento(16);
        cabecalhoSessao("Serviços", "Ver todos →");
        LinearLayout servicos = new LinearLayout(this);
        servicos.setOrientation(LinearLayout.HORIZONTAL);
        servicos.setGravity(Gravity.CENTER);
        layout.addView(servicos, new LinearLayout.LayoutParams(-1, -2));

        servicos.addView(cardServico("🏠\n📦", "Mudança\nResidência", "Casas e apartamentos"));
        servicos.addView(cardServico("🏢\n💼", "Escritório", "Empresas e escritórios"));
        servicos.addView(cardServico("📦\n⚖️", "Carga Pesada", "Mercadorias e\nequipamentos"));

        // AGENDAMENTOS RECENTES
        espacamento(16);
        texto("Agendamentos recentes", 18, PRETO, true, Gravity.START);
        cardVazioAgendamento();

        espacamento(16);
        bottomNav(0);

        texto("ÓTIMO ANGOLA • Luanda", 11, CINZA, false, Gravity.CENTER);
    }

    // =========================================
    // CLIENTE - ESTILO IMAGEM 2 e 3
    // =========================================
    private void cliente(String servico) {
        base();
        topoVoltar("Reserva de Mudança", "Mudança Residencial • Luanda");
        cardCliente("João Viegas");

        // De -> Para
        cardTrajeto("Talatona", "Kilamba");

        texto("Data da Mudança", 14, PRETO, true, Gravity.START);
        cardData("21 Out 2024 • Terça-feira");

        texto("Veículo", 14, PRETO, true, Gravity.START);
        cardVeiculoSelecionado("Carrinha Grande 5T", "Capacidade: 5 toneladas • Equipe de 3 ajudantes • 25-30 m³");

        // Estimativa
        cardPreco(85000);

        texto("Apenas serviço Luanda • Cobrimos toda Luanda: Talatona, Kilamba, Belas, Viana e arredores", 12, CINZA_ESCURO, false, Gravity.START);

        EditText origem = campo("Local de partida");
        EditText destino = campo("Local de destino");
        EditText distancia = campo("Distância estimada em km");
        EditText descricao = campo("Descrição da carga (opcional)");
        adicionar(origem); adicionar(destino); adicionar(distancia); adicionar(descricao);

        botaoVermelho("CALCULAR PREÇO", () -> {
            String o = origem.getText().toString().trim();
            String d = destino.getText().toString().trim();
            String k = distancia.getText().toString().trim();
            if (o.isEmpty() || d.isEmpty() || k.isEmpty()) { aviso("Preencha partida, destino e distância."); return; }
            double km;
            try { km = Double.parseDouble(k.replace(",", ".")); } catch (Exception e) { aviso("Distância inválida."); return; }
            int preco = BASE + (int) Math.round(km * POR_KM);
            confirmarPedido(servico, o, d, km, preco, "Carrinha grande", descricao.getText().toString().trim());
        });
        botaoSecundario("VOLTAR", this::inicio);
    }

    private void confirmarPedido(String servico, String origem, String destino, double km, int preco, String veiculo, String descricao) {
        base();
        topoVoltar("Confirmar Pedido", "Revise os dados.");
        cardDetalhe("SERVIÇO: " + servico + "\nPARTIDA: " + origem + "\nDESTINO: " + destino + "\nDISTÂNCIA: " + km + " km\nVEÍCULO: " + veiculo + "\nVALOR: " + formatar(preco) + " Kz");
        botaoVermelho("Confirmar Reserva ✓", () -> {
            try {
                JSONObject p = new JSONObject();
                p.put("id", System.currentTimeMillis());
                p.put("cliente", telefoneCliente); p.put("servico", servico);
                p.put("origem", origem); p.put("destino", destino);
                p.put("km", km); p.put("preco", preco); p.put("veiculo", veiculo);
                p.put("descricao", descricao); p.put("estado", "PENDENTE");
                p.put("data", System.currentTimeMillis()); p.put("motorista", "");
                int comissao = (int) Math.round(preco * COMISSAO / 100.0);
                p.put("comissao", comissao); p.put("motorista_valor", preco - comissao);
                p.put("pagamento_estado", "PENDENTE");
                pedidos.put(p); salvar();
                sucessoPedido(servico, origem, destino, preco, veiculo, descricao);
            } catch (Exception e) { aviso("Erro ao registar."); }
        });
        botaoSecundario("EDITAR", () -> cliente(servico));
    }

    private void sucessoPedido(String servico, String origem, String destino, int preco, String veiculo, String descricao) {
        base();
        logoCentral();
        checkVerde();
        texto("Mudança concluída com sucesso!", 20, PRETO, true, Gravity.CENTER);
        texto("O serviço foi finalizado com sucesso", 13, CINZA, false, Gravity.CENTER);
        cardDetalhe("Cliente: João Viegas\nServiço: " + servico + "\nLocal: Luanda • Talatona\nConcluído em: 25 Out 2024 • 16:42\nAvaliação: ★★★★★ (5.0)");
        cardRecibo("12,3 km", "2h 15min", preco);
        texto("Obrigado por confiar na ÓTIMO ANGOLA!", 14, VERMELHO, true, Gravity.CENTER);
        botaoVermelho("Baixar Recibo", this::meusPedidos);
        botaoOutline("Fazer Nova Mudança", this::inicio);
        botaoSecundario("Contactar no WhatsApp", () -> whatsapp("Olá ÓTIMO! Pedido de " + servico + " " + origem + " > " + destino + " Valor: " + formatar(preco) + " Kz Cliente: " + telefoneCliente));
    }

    // ... MANTÉM TODOS OS TEUS MÉTODOS DE ADMIN, MOTORISTA, FINANCEIRO, ETC IGUAIS ...
    // (copia do teu código original - não alterei lógica)
    private void cadastroCliente() {
        EditText telefone = campo("Número de telefone");
        new AlertDialog.Builder(this).setTitle("Criar conta").setMessage("Introduza o seu número.").setView(telefone)
        .setPositiveButton("CONTINUAR", (d, w) -> {
            String numero = telefone.getText().toString().trim();
            if (numero.length() < 9) { aviso("Número inválido."); return; }
            telefoneCliente = numero;
            prefs.edit().putString("user_phone", numero).apply();
            inicio();
        }).setNegativeButton("Cancelar", null).show();
    }
    private void entrarCliente() {
        EditText telefone = campo("Número de telefone");
        telefone.setText(telefoneCliente);
        new AlertDialog.Builder(this).setTitle("Entrar").setView(telefone)
        .setPositiveButton("ENTRAR", (d, w) -> {
            String numero = telefone.getText().toString().trim();
            if (numero.length() < 9) { aviso("Inválido."); return; }
            telefoneCliente = numero;
            prefs.edit().putString("user_phone", numero).apply();
            inicio();
        }).setNegativeButton("Cancelar", null).show();
    }
    private void meusPedidos() {
        base(); topoVoltar("Minhas Solicitações", telefoneCliente);
        boolean encontrou = false;
        for (int i = pedidos.length() - 1; i >= 0; i--) {
            JSONObject p = pedidos.optJSONObject(i);
            if (p != null && telefoneCliente.equals(p.optString("cliente"))) {
                encontrou = true;
                cardDetalhe("SERVIÇO: " + p.optString("servico") + "\nPARTIDA: " + p.optString("origem") + "\nDESTINO: " + p.optString("destino") + "\nVALOR: " + formatar(p.optInt("preco")) + " Kz\nESTADO: " + p.optString("estado"));
            }
        }
        if (!encontrou) cardDetalhe("Ainda não tem pedidos registados.");
        botaoSecundario("VOLTAR", this::inicio);
    }
    private void cadastroMotorista() { /* igual ao teu original */ base(); topoVoltar("Cadastro Motorista", "Registe os seus dados."); EditText nome = campo("Nome completo"); EditText telefone = campo("Telefone"); EditText bi = campo("Número do BI"); EditText matricula = campo("Matrícula"); EditText municipio = campo("Município"); EditText marca = campo("Marca / modelo"); adicionar(nome); adicionar(telefone); adicionar(bi); adicionar(matricula); adicionar(municipio); adicionar(marca); botaoVermelho("ENVIAR CADASTRO", () -> { try { JSONObject motorista = new JSONObject(); motorista.put("id", System.currentTimeMillis()); motorista.put("nome", nome.getText().toString().trim()); motorista.put("telefone", telefone.getText().toString().trim()); motorista.put("bi", bi.getText().toString().trim()); motorista.put("matricula", matricula.getText().toString().trim()); motorista.put("municipio", municipio.getText().toString().trim()); motorista.put("marca", marca.getText().toString().trim()); motorista.put("estado", "PENDENTE"); motorista.put("disponivel", false); motoristas.put(motorista); salvar(); new AlertDialog.Builder(this).setTitle("Cadastro enviado").setMessage("Aguarda aprovação.").setPositiveButton("OK", (d,w)->boasVindas()).show(); } catch (Exception e) { aviso("Erro."); } }); botaoSecundario("VOLTAR", this::boasVindas); }
    private void administrador() { EditText pin = campo("PIN"); pin.setInputType(2); new AlertDialog.Builder(this).setTitle("Área administrativa").setMessage("PIN:").setView(pin).setPositiveButton("ENTRAR", (d,w)->{ if(PIN_ADMIN.equals(pin.getText().toString().trim())) menuAdmin(); else aviso("PIN incorreto."); }).setNegativeButton("CANCELAR", null).show(); }
    private void menuAdmin() { base(); topoVoltar("PAINEL ADMINISTRATIVO", "Gestão"); cardDetalhe("PEDIDOS: " + pedidos.length() + "\nMOTORISTAS: " + motoristas.length()); botaoVermelho("GESTÃO DE PEDIDOS", this::listaPedidos); botaoVermelho("GESTÃO DE MOTORISTAS", this::listaMotoristas); botaoVermelho("FINANCEIRO", this::financeiro); botaoSecundario("VOLTAR", this::boasVindas); }
    private void listaPedidos() { base(); topoVoltar("Gestão Pedidos", "Todos"); for(int i=pedidos.length()-1;i>=0;i--){ final int indice=i; JSONObject p=pedidos.optJSONObject(i); if(p==null) continue; cardDetalhe("CLIENTE: "+p.optString("cliente")+"\nSERVIÇO: "+p.optString("servico")+"\n"+p.optString("origem")+" > "+p.optString("destino")+"\nVALOR: "+formatar(p.optInt("preco"))+" Kz\nESTADO: "+p.optString("estado")); botaoVermelho("GERIR PEDIDO", ()->gerirPedido(indice)); } botaoSecundario("VOLTAR", this::menuAdmin); }
    private void gerirPedido(int indice){ JSONObject p=pedidos.optJSONObject(indice); if(p==null) return; String[] opcoes={"Atribuir motorista","Marcar concluído","Cancelar","Voltar"}; new AlertDialog.Builder(this).setTitle("Gestão").setItems(opcoes,(d,escolha)->{ if(escolha==0) atribuirMotorista(indice); else if(escolha==1) mudarEstadoPedido(indice,"CONCLUÍDO"); else if(escolha==2) mudarEstadoPedido(indice,"CANCELADO"); else listaPedidos(); }).show(); }
    private void atribuirMotorista(int indicePedido){ JSONArray aprovados=new JSONArray(); for(int i=0;i<motoristas.length();i++){ JSONObject m=motoristas.optJSONObject(i); if(m!=null&&"APROVADO".equals(m.optString("estado"))) aprovados.put(m); } if(aprovados.length()==0){ aviso("Sem motoristas aprovados."); return; } String[] nomes=new String[aprovados.length()]; for(int i=0;i<aprovados.length();i++){ JSONObject m=aprovados.optJSONObject(i); nomes[i]=m.optString("nome")+" • "+m.optString("matricula"); } new AlertDialog.Builder(this).setTitle("Selecionar motorista").setItems(nomes,(d,escolha)->{ JSONObject m=aprovados.optJSONObject(escolha); JSONObject p=pedidos.optJSONObject(indicePedido); if(m==null||p==null) return; putJson(p,"motorista",m.optString("nome")); putJson(p,"motorista_telefone",m.optString("telefone")); putJson(p,"estado","ATRIBUÍDO"); salvar(); aviso("Atribuído."); listaPedidos(); }).show(); }
    private void mudarEstadoPedido(int indice,String estado){ JSONObject p=pedidos.optJSONObject(indice); if(p==null) return; putJson(p,"estado",estado); salvar(); aviso("Atualizado: "+estado); listaPedidos(); }
    private void financeiro(){ base(); topoVoltar("FINANCEIRO", "Comissões"); int total=0,com=0,rep=0; for(int i=0;i<pedidos.length();i++){ JSONObject p=pedidos.optJSONObject(i); if(p!=null&&"CONCLUÍDO".equals(p.optString("estado"))){ total+=p.optInt("preco"); com+=p.optInt("comissao"); rep+=p.optInt("motorista_valor"); } } cardDetalhe("TOTAL CONCLUÍDO\n"+formatar(total)+" Kz"); cardDetalhe("COMISSÃO 10%\n"+formatar(com)+" Kz"); cardDetalhe("REPASSE MOTORISTAS\n"+formatar(rep)+" Kz"); botaoSecundario("VOLTAR", this::menuAdmin); }
    private void listaMotoristas(){ base(); topoVoltar("Motoristas", "Cadastros"); for(int i=0;i<motoristas.length();i++){ final int indice=i; JSONObject m=motoristas.optJSONObject(i); if(m==null) continue; cardDetalhe("NOME: "+m.optString("nome")+"\nTEL: "+m.optString("telefone")+"\nBI: "+m.optString("bi")+"\nVEÍCULO: "+m.optString("veiculo")+"\nESTADO: "+m.optString("estado")); botaoVermelho("GERIR", ()->gerirMotorista(indice)); } botaoSecundario("VOLTAR", this::menuAdmin); }
    private void gerirMotorista(int indice){ JSONObject m=motoristas.optJSONObject(indice); if(m==null) return; String[] opcoes={"Aprovar","Rejeitar","Ativar","Desativar","WhatsApp","Voltar"}; new AlertDialog.Builder(this).setTitle(m.optString("nome")).setItems(opcoes,(d,escolha)->{ if(escolha==0){ putJson(m,"estado","APROVADO"); putJson(m,"disponivel",true); salvar(); aviso("Aprovado."); listaMotoristas(); } else if(escolha==1){ putJson(m,"estado","REJEITADO"); putJson(m,"disponivel",false); salvar(); aviso("Rejeitado."); listaMotoristas(); } else if(escolha==2){ putJson(m,"disponivel",true); salvar(); aviso("Ativado."); listaMotoristas(); } else if(escolha==3){ putJson(m,"disponivel",false); salvar(); aviso("Desativado."); listaMotoristas(); } else if(escolha==4){ whatsapp("Olá "+m.optString("nome")+", ÓTIMO ANGOLA."); } else listaMotoristas(); }).show(); }
    private void whatsapp(String msg){ try{ String url="https://wa.me/"+WHATSAPP+"?text="+Uri.encode(msg); startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }catch(Exception e){ aviso("Sem WhatsApp."); } }
    private JSONArray carregarArray(String chave){ try{ return new JSONArray(prefs.getString(chave,"[]")); }catch(Exception e){ return new JSONArray(); } }
    private void salvar(){ prefs.edit().putString("pedidos",pedidos.toString()).putString("motoristas",motoristas.toString()).apply(); }
    private void putJson(JSONObject obj,String chave,Object valor){ try{ obj.put(chave,valor); }catch(Exception e){ e.printStackTrace(); } }

    // =========================================
    // DESIGN NOVO - COMPONENTES
    // =========================================
    private void base(){ scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(FUNDO); layout=new LinearLayout(this); layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(24,20,24,30); layout.setBackgroundColor(FUNDO); scroll.addView(layout); setContentView(scroll); }
    private void logoCentral(){ TextView t=new TextView(this); t.setText("📍 ÓTIMO ANGOLA"); t.setTextColor(VERMELHO); t.setTextSize(26); t.setTypeface(null,Typeface.BOLD); t.setGravity(Gravity.CENTER); layout.addView(t, new LinearLayout.LayoutParams(-1,-2)); }
    private void checkVerde(){ TextView t=new TextView(this); t.setText("✓"); t.setTextColor(Color.WHITE); t.setTextSize(36); t.setGravity(Gravity.CENTER); GradientDrawable bg=new GradientDrawable(); bg.setColor(VERDE); bg.setShape(GradientDrawable.OVAL); bg.setSize(80,80); t.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(120,120); p.gravity=Gravity.CENTER; p.setMargins(0,20,0,10); layout.addView(t,p); }
    private void topoVoltar(String titulo,String subtitulo){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); TextView back=new TextView(this); back.setText("← "); back.setTextSize(22); back.setTextColor(PRETO); back.setOnClickListener(v->inicio()); l.addView(back); TextView tit=new TextView(this); tit.setText(titulo); tit.setTextSize(20); tit.setTextColor(PRETO); tit.setTypeface(null,Typeface.BOLD); tit.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); l.addView(tit); layout.addView(l); texto(subtitulo,13,CINZA,false,Gravity.START); espacamento(16); }
    private void cabecalhoSessao(String titulo,String acao){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); TextView t1=new TextView(this); t1.setText(titulo); t1.setTextSize(18); t1.setTypeface(null,Typeface.BOLD); t1.setTextColor(PRETO); t1.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); TextView t2=new TextView(this); t2.setText(acao); t2.setTextSize(14); t2.setTextColor(VERMELHO); t2.setTypeface(null,Typeface.BOLD); l.addView(t1); l.addView(t2); layout.addView(l); }
    private void bannerVermelho(String titulo,String subtitulo,boolean botao){ TextView card=new TextView(this); card.setText(titulo+"\n"+subtitulo+(botao?"\n\n[Solicitar agora]":"")); card.setTextColor(Color.WHITE); card.setTextSize(15); card.setTypeface(null,Typeface.BOLD); card.setPadding(22,22,22,22); GradientDrawable bg=new GradientDrawable(); bg.setColor(VERMELHO); bg.setCornerRadius(20); card.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,12,0,12); layout.addView(card,p); card.setOnClickListener(v->cliente("Mudança residencial")); }
    private View cardServico(String emoji,String titulo,String sub){ TextView t=new TextView(this); t.setText(emoji+"\n\n"+titulo+"\n"+sub); t.setTextColor(PRETO); t.setTextSize(12); t.setGravity(Gravity.CENTER); t.setPadding(14,18,14,18); GradientDrawable bg=new GradientDrawable(); bg.setColor(ROSA_CARD); bg.setCornerRadius(18); t.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(6,6,6,6); t.setLayoutParams(p); t.setOnClickListener(v->cliente(titulo)); return t; }
    private void cardVazioAgendamento(){ TextView t=new TextView(this); t.setText("📅 Nenhum agendamento ainda\nQuando solicitar uma mudança, ela aparecerá aqui"); t.setTextColor(CINZA_ESCURO); t.setTextSize(13); t.setPadding(20,20,20,20); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(16); t.setBackground(bg); layout.addView(t,new LinearLayout.LayoutParams(-1,-2)); }
    private void cardCliente(String nome){ TextView t=new TextView(this); t.setText("👤 Cliente\n"+nome); t.setTextColor(PRETO); t.setTextSize(14); t.setTypeface(null,Typeface.BOLD); t.setPadding(18,16,18,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(14); t.setBackground(bg); layout.addView(t); }
    private void cardTrajeto(String de,String para){ TextView t=new TextView(this); t.setText("🟢 De: "+de+"\n\n🔴 Para: "+para); t.setTextColor(PRETO); t.setTextSize(16); t.setTypeface(null,Typeface.BOLD); t.setPadding(18,16,18,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(CARD_BRANCO); bg.setCornerRadius(14); bg.setStroke(1, Color.rgb(230,230,230)); t.setBackground(bg); layout.addView(t); }
    private void cardData(String data){ TextView t=new TextView(this); t.setText("📅 "+data); t.setTextColor(PRETO); t.setPadding(16,14,16,14); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(12); t.setBackground(bg); layout.addView(t); }
    private void cardVeiculoSelecionado(String nome,String detalhe){ TextView t=new TextView(this); t.setText("🚚 "+nome+"\n"+detalhe); t.setTextColor(PRETO); t.setTextSize(13); t.setPadding(18,16,18,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(VERMELHO_CLARO); bg.setCornerRadius(14); bg.setStroke(2, VERMELHO); t.setBackground(bg); layout.addView(t); }
    private void cardPreco(int preco){ TextView t=new TextView(this); t.setText("Estimativa de Preço\n"+formatar(preco)+" KZ\n• Transporte + 3h: 65.000 KZ\n• Taxa Luanda: 20.000 KZ"); t.setTextColor(VERMELHO); t.setTextSize(18); t.setTypeface(null,Typeface.BOLD); t.setPadding(18,16,18,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(CARD_BRANCO); bg.setCornerRadius(14); bg.setStroke(1, Color.rgb(230,230,230)); t.setBackground(bg); layout.addView(t); }
    private void cardDetalhe(String valor){ TextView t=new TextView(this); t.setText(valor); t.setTextColor(PRETO); t.setTextSize(14); t.setPadding(18,18,18,18); GradientDrawable bg=new GradientDrawable(); bg.setColor(CARD_BRANCO); bg.setCornerRadius(16); bg.setStroke(1, Color.rgb(235,235,235)); t.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); layout.addView(t,p); }
    private void cardRecibo(String dist,String dur,int total){ TextView t=new TextView(this); t.setText("Recibo\nDistância: "+dist+"\nDuração: "+dur+"\nValor total: "+formatar(total)+" Kz PAGO"); t.setTextColor(PRETO); t.setTextSize(14); t.setPadding(18,18,18,18); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(16); t.setBackground(bg); layout.addView(t); }
    private void bottomNav(int ativo){ LinearLayout nav=new LinearLayout(this); nav.setOrientation(LinearLayout.HORIZONTAL); nav.setGravity(Gravity.CENTER); nav.setPadding(0,16,0,0); String[] itens={"Início","Solicitar","Viagens","Perfil"}; for(int i=0;i<itens.length;i++){ TextView b=new TextView(this); b.setText(itens[i]); b.setTextSize(12); b.setGravity(Gravity.CENTER); b.setPadding(12,10,12,10); b.setTextColor(i==ativo?VERMELHO:CINZA); b.setTypeface(null, i==ativo?Typeface.BOLD:Typeface.NORMAL); b.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); int idx=i; b.setOnClickListener(v->{ if(idx==0) inicio(); else if(idx==1) cliente("Mudança residencial"); else if(idx==2) meusPedidos(); else boasVindas(); }); nav.addView(b); } layout.addView(nav); }
    private void texto(String valor,int tamanho,int cor,boolean bold,int gravity){ TextView t=new TextView(this); t.setText(valor); t.setTextSize(tamanho); t.setTextColor(cor); t.setGravity(gravity); t.setPadding(0,4,0,4); if(bold) t.setTypeface(null,Typeface.BOLD); adicionar(t); }
    private void botaoVermelho(String nome,Runnable acao){ Button b=new Button(this); b.setText(nome); b.setTextColor(Color.WHITE); b.setTextSize(15); b.setTypeface(null,Typeface.BOLD); b.setAllCaps(false); b.setPadding(12,18,12,18); GradientDrawable bg=new GradientDrawable(); bg.setColor(VERMELHO); bg.setCornerRadius(16); b.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); layout.addView(b,p); b.setOnClickListener(v->acao.run()); }
    private void botaoOutline(String nome,Runnable acao){ Button b=new Button(this); b.setText(nome); b.setTextColor(VERMELHO); b.setTextSize(14); b.setTypeface(null,Typeface.BOLD); b.setAllCaps(false); b.setPadding(12,16,12,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(16); bg.setStroke(2, VERMELHO); b.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); layout.addView(b,p); b.setOnClickListener(v->acao.run()); }
    private void botaoSecundario(String nome,Runnable acao){ Button b=new Button(this); b.setText(nome); b.setTextColor(PRETO); b.setTextSize(14); b.setAllCaps(false); b.setPadding(12,16,12,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(16); b.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); layout.addView(b,p); b.setOnClickListener(v->acao.run()); }
    private void botaoLink(String nome,Runnable acao){ TextView t=new TextView(this); t.setText(nome); t.setTextColor(CINZA); t.setTextSize(13); t.setGravity(Gravity.CENTER); t.setPadding(0,10,0,10); t.setOnClickListener(v->acao.run()); layout.addView(t); }
    private EditText campo(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(CINZA); e.setTextColor(PRETO); e.setTextSize(15); e.setPadding(18,16,18,16); GradientDrawable bg=new GradientDrawable(); bg.setColor(FUNDO_CINZA); bg.setCornerRadius(14); bg.setStroke(1, Color.rgb(230,230,230)); e.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); e.setLayoutParams(p); return e; }
    private EditText campoPesquisa(String hint){ EditText e=campo(hint); e.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_search_category_default,0,0,0); return e; }
    private void adicionar(View v){ if(v.getParent()!=null) return; LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,6,0,6); layout.addView(v,p); }
    private void espacamento(int altura){ View v=new View(this); layout.addView(v, new LinearLayout.LayoutParams(1, altura)); }
    private void aviso(String mensagem){ Toast.makeText(this,mensagem,Toast.LENGTH_LONG).show(); }
    private String formatar(int valor){ return String.format("%,d", valor).replace(",", "."); }
}