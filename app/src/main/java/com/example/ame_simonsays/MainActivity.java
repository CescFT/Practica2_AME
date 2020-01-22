package com.example.ame_simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /**
     * Elements que necessitem per a executar el codi
     */
    private TextView textResultat;
    private TextView textFase;
    private TextView labelFase;

    private int indexSeq;
    private Button botoBlau;
    private Button botoVermell;
    private Button botoVerd;
    private Button botoGroc;

    private LinearLayout botonera;

    private List<Integer> sequencia  = new ArrayList<Integer>();
    private int index = 0;
    private int valorEscollit=5;
    private int sequenceLength;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    private Handler handler = new Handler();
    private int temps=0;

    private LinearLayout radioGrup;
    private Button nivell1;
    private Button nivell2;
    private Button nivell3;
    private Button madness;


    private int nivell;
    private int dificultat;
    private int punts = 0;
    private int modeJoc;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        indexSeq =0;
        botoBlau =  (Button) findViewById(R.id.botoBlau);
        botoVermell = (Button) findViewById(R.id.botoVermell);
        botoVerd = (Button) findViewById(R.id.botoVerd);
        botoGroc = (Button) findViewById(R.id.botoGroc);
        textResultat = (TextView) findViewById(R.id.textResultat);
        textFase = (TextView) findViewById(R.id.fase);
        labelFase = (TextView) findViewById(R.id.labelFase);
        botonera = (LinearLayout) findViewById(R.id.botonera);

        radioGrup = (LinearLayout) findViewById(R.id.layoutNivell);
        nivell1 = (Button) findViewById(R.id.madness1);
        nivell2 = (Button) findViewById(R.id.madness2);
        nivell3 = (Button) findViewById(R.id.madness3);
        madness = (Button) findViewById(R.id.bmadness);

        botoVermell.setEnabled(false);
        botoVerd.setEnabled(false);
        botoGroc.setEnabled(false);
        botoBlau.setEnabled(false);
        botoVermell.setTag(0);
        botoVerd.setTag(1);
        botoGroc.setTag(2);
        botoBlau.setTag(3);

        nivell1.setTag(1);
        nivell2.setTag(2);
        nivell3.setTag(3);
        radioGrup.setVisibility(View.INVISIBLE);

        sharedPrefs = getPreferences(MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();




    }

    public void clicaBoto(final int c){
        canviColorClar(c, true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                canviColorClar(c, false);
            }
        }, 250);
    }
    public void finalJoc(){
        textFase.setText("Cesc Says!");
        textResultat.setText("Game Over");
        botoVermell.setEnabled(false);
        botoVerd.setEnabled(false);
        botoGroc.setEnabled(false);
        botoBlau.setEnabled(false);
        mostrarComponents();
        Intent intent = new Intent(MainActivity.this,RankingActivity.class);
        intent.putExtra("puntuacio", punts);
        startActivity(intent);
    }

    public void onClickSelectLevelMadness(View view){
        radioGrup.setVisibility(View.VISIBLE);
        madness.setBackgroundColor(getResources().getColor(R.color.colorBotoPitjat));
    }

    public void comprovaBoto(int c){
        if(c!=sequencia.get(index)) finalJoc();
        else{
            punts += nivell*10;
            textResultat.setText(String.valueOf(punts));
            index++;
            if(index==nivell){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seguentNivell();
                    }
                }, 2000);
            }
        }
    }

    public void seguentNivell(){
        nivell++;
        iniciarNivell();

    }

    public void onClickBoto(View view){
        int tag = (int)view.getTag();
        clicaBoto(tag);
        comprovaBoto(tag);
    }

    private void canviColorClar(int tag, boolean canvi){
        switch(tag){
            case 0: //vermell
                botoVermell.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorVermellClar : R.color.colorVermellFosc));
                break;
            case 1: //verd
                botoVerd.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorVerdClar : R.color.colorVerdFosc));
                break;
            case 2: //groc
                botoGroc.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorGrocClar : R.color.colorGrocFosc));
                break;
            case 3: //blau
                botoBlau.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorBlauClar : R.color.colorBlauFosc));
                break;
        }
    }


    public void bloquejarPantalla(boolean b){

        //activar boto
        botoVermell.setEnabled(!b);
        botoBlau.setEnabled(!b);
        botoGroc.setEnabled(!b);
        botoVerd.setEnabled(!b);


    }

    public void mostrarComponents(){
        botonera.setVisibility(View.VISIBLE);
        labelFase.setVisibility(View.INVISIBLE);
        radioGrup.setVisibility(View.VISIBLE);
        radioGrup.setVisibility(View.INVISIBLE);
    }

    public void amagarComponents(){
        botonera.setVisibility(View.GONE);
        labelFase.setVisibility(View.VISIBLE);
        radioGrup.setVisibility(View.GONE);

    }

    public void onClickJugarClassic(View view) {
        modeJoc=0;
        sequencia = new ArrayList<Integer>();
        sequenceLength=0;
        inicialitzacions();
    }

    public void onClickJugarMadness(View view) {
        modeJoc=1;
        int seleccionat = view.getId();
        Button rb = (Button) findViewById(seleccionat);
        dificultat = (int) rb.getTag();
        inicialitzacions();
        madness.setBackgroundDrawable(nivell1.getBackground());

    }

    public void inicialitzacions(){
        amagarComponents();
        textResultat.setText("");
        punts=0;
        textResultat.setText("");
        nivell=1;

        botoVermell.setEnabled(true);
        botoVerd.setEnabled(true);
        botoGroc.setEnabled(true);
        botoBlau.setEnabled(true);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarNivell();
            }
        }, 2000);

    }

    public void iniciarNivell(){
        textFase.setText(String.valueOf(nivell));
        Random rand = new Random();
        if(modeJoc==1) {
            sequencia = new ArrayList<Integer>();
            sequenceLength = nivell*dificultat;
            //inicialització de la seqüència que tindrà el joc.
            for(int i=0; i<sequenceLength; i++)
            {

                sequencia.add(rand.nextInt(4));
            }
        }else{
            sequencia.add(rand.nextInt(4));
            sequenceLength++;
        }
        index = 0;


        bloquejarPantalla(true);
        for(final Integer c : sequencia){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColorClar(c, true);
                }
            }, temps);
            temps+=500;
            final int tempsFinal = temps;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColorClar(c, false);
                    if(tempsFinal == (500*sequenceLength*2)-500){
                        bloquejarPantalla(false);
                    }
                }
            }, temps);
            temps+=500;

        }
        temps = 0;
    }
}
