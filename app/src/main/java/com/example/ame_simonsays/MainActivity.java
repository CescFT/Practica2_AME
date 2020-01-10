package com.example.ame_simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();
        List<colors> sequencia  = new ArrayList<colors>();
        List<colors> colorsDisponibles = new ArrayList<colors>();
        colorsDisponibles.add(colors.BLAU);
        colorsDisponibles.add(colors.GROC);
        colorsDisponibles.add(colors.VERD);
        colorsDisponibles.add(colors.VERMELL);
        int sequenceLength = 5 + (int)(Math.random() * ((10 - 5) + 1));  // [5,10]

        //inicialització de la seqüència que tindrà el joc.
        for(int i=0; i<sequenceLength; i++)
        {
            int rand = (int) ((Math.random() * ((4 - 0) + 1)) + 0);
            sequencia.add(colorsDisponibles.get(rand));
        }

        if(!sequencia.isEmpty())
        {
            //anar agafant els valors de la llista, pintarlos al textview i esperar a que apreti boto, comprovar q esta be i incrementar la variable índex.
            int indexSeq=0;
            boolean botoBlauApretat = false;
            boolean botoVermellApretat = false;
            boolean botoVerdApretat = false;
            boolean botoGrocApretat = false;
            TextView mTextView = (TextView) findViewById(R.id.seg_color);
            Button botoBlau = (Button) findViewById(R.id.botoBlau);
            Button botoVermell = (Button) findViewById(R.id.botoVermell);
            Button botoVerd = (Button) findViewById(R.id.botoVerd);
            Button botoGroc = (Button) findViewById(R.id.botoGroc);



            


            while(indexSeq < sequenceLength)
            {
                //mentre hi ha joc

                mTextView.setText(sequencia.get(indexSeq).toString());

            }
        }

    }
}
