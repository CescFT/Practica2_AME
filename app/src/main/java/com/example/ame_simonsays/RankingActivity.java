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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private TextView text1, text2, text3, text4, text5;
    private Button btnUpdateRanking;

    public void onClickBotoBack(View view){
        this.onBackPressed();
    }

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

        updateRanking();



    }

    public void updateRanking(){
        ArrayList<String> data = getIntent().getStringArrayListExtra("RankingActual");
        List<Guanyador> guanyadors = new ArrayList<Guanyador>();

        for(String s : data)
        {
            String [] sTallat = s.split(",");
            guanyadors.add(new Guanyador(sTallat[0], Integer.parseInt(sTallat[1])));
        }

        Collections.sort(guanyadors);
        Collections.reverse(guanyadors);
        for(int i = 0; i<guanyadors.size(); i++)
        {
            String textToPrint="";
            try{
                Guanyador winner = guanyadors.get(i);
                switch (i){
                    case 0:
                        textToPrint = guanyadors.get(i).getNomJugador()+"  "+String.valueOf(guanyadors.get(i).getPuntuacioObtinguda());
                        text1.setText(textToPrint);
                        break;
                    case 1:
                        textToPrint = guanyadors.get(i).getNomJugador()+"  "+String.valueOf(guanyadors.get(i).getPuntuacioObtinguda());
                        text2.setText(textToPrint);
                        break;
                    case 2:
                        textToPrint = guanyadors.get(i).getNomJugador()+"  "+String.valueOf(guanyadors.get(i).getPuntuacioObtinguda());
                        text3.setText(textToPrint);
                        break;
                    case 3:
                        textToPrint = guanyadors.get(i).getNomJugador()+"  "+String.valueOf(guanyadors.get(i).getPuntuacioObtinguda());
                        text4.setText(textToPrint);
                        break;
                    case 4:
                        textToPrint = guanyadors.get(i).getNomJugador()+"  "+String.valueOf(guanyadors.get(i).getPuntuacioObtinguda());
                        text5.setText(textToPrint);
                        break;
                }
            } catch(Exception e){
                break;
            }

        }

    }

}
