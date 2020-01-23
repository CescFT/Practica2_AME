package com.example.ame_simonsays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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

    private List<Integer> sequencia = new ArrayList<Integer>();
    private int index = 0;
    private int valorEscollit = 5;
    private int sequenceLength;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    private Handler handler = new Handler();
    private int temps = 0;
    private static List<Guanyador> llistaRanking = new ArrayList<Guanyador>();
    private LinearLayout radioGrup;
    private Button nivell1;
    private Button nivell2;
    private Button nivell3;
    private Button madness;

    private EditText textNomJugador;

    private int nivell;
    private int dificultat;
    private int punts = 0;
    private int modeJoc;

    private OutputStream outputStream;
    private InputStream inStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        indexSeq = 0;
        botoBlau = (Button) findViewById(R.id.botoBlau);
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
        textNomJugador = (EditText) findViewById(R.id.editText);

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
        textNomJugador.setVisibility(View.VISIBLE);

        sharedPrefs = getPreferences(MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        Gson gson = new Gson();
        String rankJSON = sharedPrefs.getString("ranking", "");
        llistaRanking = rankJSON.equals("") ? new ArrayList<Guanyador>() : (List<Guanyador>) gson.fromJson(rankJSON, new TypeToken<List<Guanyador>>() {
        }.getType());

        try{
            init();
        }catch(Exception e){

        }

    }


    public void clicaBoto(final int c) {
        canviColorClar(c, true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                canviColorClar(c, false);
            }
        }, 250);
    }

    public void finalJoc() {
        textFase.setText("Cesc Says!");
        textResultat.setText("Game Over");
        botoVermell.setEnabled(false);
        botoVerd.setEnabled(false);
        botoGroc.setEnabled(false);
        botoBlau.setEnabled(false);
        mostrarComponents();

        if (modeJoc == 0) {
            Intent intent = new Intent(MainActivity.this, RankingActivity.class);

            llistaRanking.add(new Guanyador(textNomJugador.getText().toString(), punts));
            Collections.sort(llistaRanking);
            Collections.reverse(llistaRanking);
            List<Guanyador> subRanking = new ArrayList();
            try {
                subRanking = llistaRanking.subList(0, 5);
            } catch (Exception e) {
                subRanking = llistaRanking;
            }
            Gson gson = new Gson();
            String info = gson.toJson(subRanking);
            sharedPrefsEditor.putString("ranking", info);
            sharedPrefsEditor.commit();
            ArrayList<String> rankingString = new ArrayList<String>();
            for (Guanyador o : llistaRanking) {
                rankingString.add(o.toString());
            }
            intent.putStringArrayListExtra("RankingActual", rankingString);
            startActivity(intent);
        }
    }

    public void onClickSelectLevelMadness(View view) {
        radioGrup.setVisibility(View.VISIBLE);
        madness.setBackgroundColor(getResources().getColor(R.color.colorBotoPitjat));
    }

    public void comprovaBoto(int c) {
        if (c != sequencia.get(index)) finalJoc();
        else {
            punts += nivell * 10;
            textResultat.setText(String.valueOf(punts));
            index++;
            if (modeJoc == 0 && index == nivell) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seguentNivell();
                    }
                }, 2000);
            } else if (modeJoc == 1 && ((int) (index / dificultat)) == nivell) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seguentNivell();
                    }
                }, 2000);
            }
        }
    }

    public void seguentNivell() {
        nivell++;
        iniciarNivell();

    }

    public void onClickBoto(View view) {
        int tag = (int) view.getTag();
        clicaBoto(tag);
        comprovaBoto(tag);
    }

    private void canviColorClar(int tag, boolean canvi) {
        try{
            switch (tag) {
                case 0: //vermell
                    botoVermell.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorVermellClar : R.color.colorVermellFosc));
                    write("1");
                    break;
                case 1: //verd
                    botoVerd.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorVerdClar : R.color.colorVerdFosc));
                    write("2");
                    break;
                case 2: //groc
                    botoGroc.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorGrocClar : R.color.colorGrocFosc));
                    write("3");
                    break;
                case 3: //blau
                    botoBlau.setBackgroundColor(getResources().getColor((canvi) ? R.color.colorBlauClar : R.color.colorBlauFosc));
                    write("4");
                    break;
            }
        }catch(Exception e){

        }

    }


    public void bloquejarPantalla(boolean b) {

        //activar boto
        botoVermell.setEnabled(!b);
        botoBlau.setEnabled(!b);
        botoGroc.setEnabled(!b);
        botoVerd.setEnabled(!b);


    }

    public void mostrarComponents() {
        botonera.setVisibility(View.VISIBLE);
        labelFase.setVisibility(View.INVISIBLE);
        radioGrup.setVisibility(View.VISIBLE);
        radioGrup.setVisibility(View.INVISIBLE);
    }

    public void amagarComponents() {
        botonera.setVisibility(View.GONE);
        radioGrup.setVisibility(View.GONE);
        textNomJugador.setVisibility(View.INVISIBLE);
    }

    public void onClickJugarClassic(View view) {
        modeJoc = 0;
        sequencia = new ArrayList<Integer>();
        sequenceLength = 0;
        inicialitzacions();
    }

    public void onClickJugarMadness(View view) {
        modeJoc = 1;
        int seleccionat = view.getId();
        Button rb = (Button) findViewById(seleccionat);
        dificultat = (int) rb.getTag();
        inicialitzacions();
        madness.setBackgroundDrawable(nivell1.getBackground());

    }

    public void inicialitzacions() {
        amagarComponents();
        textResultat.setText("");
        punts = 0;
        textResultat.setText("");
        nivell = 1;
        textFase.setTextSize(32);
        textFase.setText("Prepara't!");
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

    public void iniciarNivell() {
        labelFase.setVisibility(View.VISIBLE);
        textFase.setTextSize(44);
        textFase.setText(String.valueOf(nivell));

        Random rand = new Random();
        if (modeJoc == 1) {
            sequencia = new ArrayList<Integer>();
            sequenceLength = nivell * dificultat;
            //inicialització de la seqüència que tindrà el joc.
            for (int i = 0; i < sequenceLength; i++) {
                sequencia.add(rand.nextInt(4));
            }
        } else {
            sequencia.add(rand.nextInt(4));
            sequenceLength++;
        }
        index = 0;


        bloquejarPantalla(true);
        for (final Integer c : sequencia) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColorClar(c, true);
                }
            }, temps);
            temps += 500;
            final int tempsFinal = temps;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColorClar(c, false);
                    if (tempsFinal == (500 * sequenceLength * 2) - 500) {
                        bloquejarPantalla(false);
                    }
                }
            }, temps);
            temps += 500;

        }
        temps = 0;
    }

    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];
                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

}
