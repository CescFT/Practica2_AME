package com.example.ame_simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Elements que necessitem per a executar el codi
     */
    private TextView mTextView;
    private int indexSeq;
    private Button botoBlau;
    private Button botoVermell;
    private Button botoVerd;
    private Button botoGroc;
    private List<colors> colorsDisponibles = new ArrayList<colors>();
    private List<colors> sequencia  = new ArrayList<colors>();
    int sequenceLength;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;


    /**
     * Classe privada per als colors
     */
    private enum colors{
        BLAU,
        GROC,
        VERD,
        VERMELL
    }

    /**
     * Mètode que inicialitza la llista de colors disponibles
     */
    private void iniLlista(){
        colorsDisponibles.add(colors.BLAU);
        colorsDisponibles.add(colors.GROC);
        colorsDisponibles.add(colors.VERD);
        colorsDisponibles.add(colors.VERMELL);
    }

    private boolean comprovarValor(TextView t, String text){
        return t.getText().toString().equals(text);
    }

    public void bttnOnClick(View view)
    {
        //bttn handler???
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*mTextView =(TextView) findViewById(R.id.seg_color);
        indexSeq =0;
        botoBlau =  (Button) findViewById(R.id.botoBlau);
        botoVermell = (Button) findViewById(R.id.botoVermell);
        botoVerd = (Button) findViewById(R.id.botoVerd);
        botoGroc = (Button) findViewById(R.id.botoGroc);

        sharedPrefs = getPreferences(MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        this.iniLlista();

        sequenceLength = 5 + (int)(Math.random() * ((10 - 5) + 1));  // [5,10]

        //inicialització de la seqüència que tindrà el joc.
        for(int i=0; i<sequenceLength; i++)
        {
            int rand = (int) ((Math.random() * ((4 - 0) + 1)) + 0);
            sequencia.add(colorsDisponibles.get(rand));
        }

        if(!sequencia.isEmpty())
        {
            //anar agafant els valors de la llista, pintarlos al textview i esperar a que apreti boto, comprovar q esta be i incrementar la variable índex.


            botoBlau.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });


            botoGroc.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                }
            });

            botoVerd.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                }
            });

            botoVermell.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                }
            });


            while(indexSeq < sequenceLength)
            {
                //mentre hi ha joc
                mTextView.setText(sequencia.get(indexSeq).toString());

            }
        }*/
    }
}
