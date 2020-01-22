package com.example.ame_simonsays;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private TextView text1, text2, text3, text4, text5;
    private Button btnUpdateRanking;

    public void onClickBotoBack(View view){
        this.onBackPressed();
    }

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);
        text1 = (TextView) findViewById(R.id.rankText1);
        text2 = (TextView) findViewById(R.id.rankText2);
        text3 = (TextView) findViewById(R.id.rankText3);
        text4 = (TextView) findViewById(R.id.rankText4);
        text5 = (TextView) findViewById(R.id.rankText5);
        btnUpdateRanking = (Button) findViewById(R.id.updateRanking);
        mPreferences = getPreferences(MODE_PRIVATE);



    }

    public void onClickButtonUpdate(View view){
        String data = getIntent().getStringExtra("Puntuacio");
        String nomJugadorActual = getIntent().getStringExtra("nomJugador");
        int puntuacioActual = Integer.valueOf(data);
        List<Guanyador> guanyadors = new ArrayList<Guanyador>();

        for(String jugador : mPreferences.getAll().keySet()){
            String puntsJugadorEmmagatzemat = String.valueOf(mPreferences.getAll().get(jugador));
            int integerPuntsJugadorEmmagatzemat = Integer.valueOf(puntsJugadorEmmagatzemat);

            guanyadors.add(new Guanyador(jugador, integerPuntsJugadorEmmagatzemat));
        }

        guanyadors.add(new Guanyador(nomJugadorActual, puntuacioActual));

        Collections.sort(guanyadors);

        for(int i = 0; i<guanyadors.size(); i++)
        {
            String textToPrint="";
            switch (i){
                case 0:
                    textToPrint = guanyadors.get(0).getNomJugador()+"punts:"+String.valueOf(guanyadors.get(0).getPuntuacioObtinguda());
                    text1.setText(textToPrint);
                    break;
                case 1:
                    textToPrint = guanyadors.get(1).getNomJugador()+"punts:"+String.valueOf(guanyadors.get(1).getPuntuacioObtinguda());
                    text2.setText(textToPrint);
                    break;
                case 2:
                    textToPrint = guanyadors.get(2).getNomJugador()+"punts:"+String.valueOf(guanyadors.get(2).getPuntuacioObtinguda());
                    text3.setText(textToPrint);
                    break;
                case 3:
                    textToPrint = guanyadors.get(3).getNomJugador()+"punts:"+String.valueOf(guanyadors.get(3).getPuntuacioObtinguda());
                    text4.setText(textToPrint);
                    break;
                case 4:
                    textToPrint = guanyadors.get(4).getNomJugador()+"punts:"+String.valueOf(guanyadors.get(4).getPuntuacioObtinguda());
                    text5.setText(textToPrint);
                    break;
            }
        }

    }

}
