package ao.otimoangola.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("ÓTIMO ANGOLA");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(20, 110, 60));
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("Mudanças • Entregas • Transporte");
        subtitle.setTextSize(18);
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 30, 0, 30);

        TextView price = new TextView(this);
        price.setText("Transportes a partir de 35.000 Kz");
        price.setTextSize(20);
        price.setTextColor(Color.BLACK);
        price.setGravity(Gravity.CENTER);

        TextView info = new TextView(this);
        info.setText("Peça o seu transporte com facilidade.\\n\\nÓTIMO ANGOLA — A sua mudança, o nosso compromisso.");
        info.setTextSize(17);
        info.setTextColor(Color.DKGRAY);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 40, 0, 0);

        layout.addView(title);
        layout.addView(subtitle);
        layout.addView(price);
        layout.addView(info);

        setContentView(layout);
    }
}