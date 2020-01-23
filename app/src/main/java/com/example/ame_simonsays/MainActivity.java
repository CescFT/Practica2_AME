package com.example.ame_simonsays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Vibrator;
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
import android.widget.Switch;
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

    // TEXT VIEWS
    private TextView textResultat;
    private TextView textFase;
    private TextView labelFase;

    // INTS
    private int index = 0; // Valor de pressió de l'usuari
    private int valorEscollit = 5; // Boto premut (DEFAULT 5)
    private int llargadaSequencia; // Llargada de la sequencia
    private int temps = 0; // Temps d'espera pels handlers
    private int nivell; // Nivell en el que es troba
    private int dificultat; // Dificultat de l'usuari
    private int punts = 0;
    private int modeJoc; // Classic -> 0; Madness -> 1

    //SWITCH
    private Switch dispositiuE;

    // BUTTONS
    private Button botoBlau; // Botons de llum
    private Button botoVermell;
    private Button botoVerd;
    private Button botoGroc;
    private Button botoClassic;

    private Button nivell1; // Botons de madness
    private Button nivell2;
    private Button nivell3;
    private Button madness;

    // LAYOUTS
    private LinearLayout botonera; // Botonera amb els botons d'inici de joc
    private LinearLayout radioGrup; // Botonera nivell madness
    private LinearLayout labelBluetooth;

    // LISTS
    private List<Integer> sequencia = new ArrayList<Integer>();
    private static List<Guanyador> llistaRanking = new ArrayList<Guanyador>();

    // SHARED PREFERENCES
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    // HANDLER
    private Handler handler = new Handler();

    // INPUT TEXT
    private EditText textNomJugador;

    private Switch jugarPC;

    // DATA STREAMS
    private OutputStream outputStream;
    private InputStream inStream;

    //STRINGS
    private String dadesRebudes="";

    //BOOLEANS
    private boolean dispositiuExtern=false;
    private boolean escoltar = false;

    private MediaPlayer mi, re, si, sol, ok, canviFase, entroRanking;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mi = MediaPlayer.create(this, R.raw.mi);
        re = MediaPlayer.create(this, R.raw.re);
        si = MediaPlayer.create(this, R.raw.si);
        sol = MediaPlayer.create(this, R.raw.sol);
        ok = MediaPlayer.create(this, R.raw.ok);
        canviFase = MediaPlayer.create(this, R.raw.bell);
        entroRanking = MediaPlayer.create(this, R.raw.cheer);
        canviFase.setVolume(0.50f,0.50f);

        // DECLARACIO BOTONS
        botoVermell = (Button) findViewById(R.id.botoVermell);
        botoVermell.setEnabled(false);
        botoVermell.setTag(0);

        botoVerd = (Button) findViewById(R.id.botoVerd);
        botoVerd.setEnabled(false);
        botoVerd.setTag(1);

        botoGroc = (Button) findViewById(R.id.botoGroc);
        botoGroc.setEnabled(false);
        botoGroc.setTag(2);

        botoBlau = (Button) findViewById(R.id.botoBlau);
        botoBlau.setEnabled(false);
        botoBlau.setTag(3);
        botoClassic = (Button) findViewById(R.id.bclassic);

        jugarPC = (Switch) findViewById(R.id.bluetoothSwitch);

        botonera = (LinearLayout) findViewById(R.id.botonera);

        // DECLARACIO MADNESS
        madness = (Button) findViewById(R.id.bmadness);

        radioGrup = (LinearLayout) findViewById(R.id.layoutNivell);
        radioGrup.setVisibility(View.INVISIBLE);

        nivell1 = (Button) findViewById(R.id.madness1);
        nivell1.setTag(1);

        nivell2 = (Button) findViewById(R.id.madness2);
        nivell2.setTag(2);

        nivell3 = (Button) findViewById(R.id.madness3);
        nivell3.setTag(3);

        // DECLARACIO NOM JUGADOR
        textNomJugador = (EditText) findViewById(R.id.editText);
        textNomJugador.setVisibility(View.VISIBLE);

        // ALTRES DECLARACIONS
        textResultat = (TextView) findViewById(R.id.textResultat);
        textFase = (TextView) findViewById(R.id.fase);
        labelFase = (TextView) findViewById(R.id.labelFase);
        labelBluetooth = (LinearLayout) findViewById(R.id.layoutBluetooth);
        sharedPrefs = getPreferences(MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();

        Gson gson = new Gson();
        String rankJSON = sharedPrefs.getString("ranking", "");
        llistaRanking = rankJSON.equals("") ? new ArrayList<Guanyador>()
                : (List<Guanyador>) gson.fromJson(rankJSON, new TypeToken<List<Guanyador>>() {
        }.getType());

        try {
            init();
        } catch (Exception e) {

        }

    }

    // CLASSIC
    public void onClickJugarClassic(View view) {
        modeJoc = 0;
        sequencia = new ArrayList<Integer>();
        llargadaSequencia = 0;
        ok.start();
        inicialitzacions();
    }

    // MADNESS
    public void onClickJugarMadness(View view) {
        modeJoc = 1;
        int seleccionat = view.getId();
        Button rb = (Button) findViewById(seleccionat);
        dificultat = (int) rb.getTag();
        inicialitzacions();
        ok.start();
        madness.setBackgroundDrawable(nivell1.getBackground());

    }

    // INICIALITZACIONS DELS COMPONENTS
    public void inicialitzacions() {
        amagarComponents();
        dispositiuExtern = jugarPC.isChecked();
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

    // INICIAR NIVELL
    public void iniciarNivell() {
        labelFase.setVisibility(View.VISIBLE);
        textFase.setTextSize(44);
        textFase.setText(String.valueOf(nivell));

        Random rand = new Random();
        if (modeJoc == 1) {
            sequencia = new ArrayList<Integer>();
            llargadaSequencia = nivell * dificultat;
            // inicialització de la seqüència que tindrà el joc.
            for (int i = 0; i < llargadaSequencia; i++) {
                sequencia.add(rand.nextInt(4));
            }
        } else {
            sequencia.add(rand.nextInt(4));
            llargadaSequencia++;
        }
        index = 0;

        // BLOQUEJAR PANTALLA
        bloquejarPantalla(true);
        for (final Integer c : sequencia) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColor(c, true);
                }
            }, temps);
            temps += 500;
            final int tempsFinal = temps;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canviColor(c, false);
                    if (tempsFinal == (500 * llargadaSequencia * 2) - 500) {
                        bloquejarPantalla(false);
                    }
                }
            }, temps);
            temps += 500;

        }
        try{
            write("5");
        }catch(Exception e){}
        escoltar = true;
        temps = 0;
    }

    // CANVI LLUM BOTO CLICK USUARI
    public void clicaBoto(final int c) {
        canviColor(c, true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                canviColor(c, false);
            }
        }, 250);
    }

    // GAME OVER FINAL JOC
    public void finalJoc() {
        textFase.setText("Cesc Says!");
        textResultat.setText("Game Over");
        botoVermell.setEnabled(false);
        botoVerd.setEnabled(false);
        botoGroc.setEnabled(false);
        botoBlau.setEnabled(false);
        mostrarComponents();
        vibrator.vibrate(1000);
        try{
            write("8");
        }catch(Exception e) {}

        if (modeJoc == 0) {
            Intent intent = new Intent(MainActivity.this, RankingActivity.class);


            llistaRanking.add(new Guanyador(textNomJugador.getText().toString(), punts));
            Collections.sort(llistaRanking);
            Collections.reverse(llistaRanking);
            List<Guanyador> subRanking = new ArrayList();
            try {
                subRanking = llistaRanking.subList(0, 4);
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

    // DISPLAY DE LA BOTONERA DE MADNESS
    public void onClickSelectLevelMadness(View view) {
        radioGrup.setVisibility(View.VISIBLE);
        ok.start();
        madness.setBackgroundColor(getResources().getColor(R.color.colorBotoPitjat));
    }

    // COMPROVA RESULTAT DEL CLICK DE L'USUARI
    public void comprovaBoto(int c) {
        if (c != sequencia.get(index))
            finalJoc();
        else {
            punts += nivell * 10;
            textResultat.setText(String.valueOf(punts));
            index++;
            boolean finalNivell = false;
            if (modeJoc == 0 && index == nivell) {
                finalNivell = true;

                canviFase.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seguentNivell();
                    }
                }, 2000);
                try{
                    write("7");
                }catch(Exception e){

                }
            } else if (modeJoc == 1 && ((int) (index / dificultat)) == nivell) {
                finalNivell = true;

                canviFase.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seguentNivell();
                    }
                }, 2000);

                try{
                    write("7");
                }catch(Exception e){}

            }
            if(dispositiuExtern && !finalNivell){
                try{
                    write("0");
                }catch(Exception e){

                }
            }

            escoltar = true;
        }
    }

    // PASSAR DE NIVELL
    public void seguentNivell() {
        nivell++;
        iniciarNivell();


    }

    // EXECUCIO RUTINA CLICAR BOTO
    public void onClickBoto(View view) {
        int tag = (int) view.getTag();
        clicaBoto(tag);
        comprovaBoto(tag);
    }

    // CANVI A CLAR/FOSC DELS BOTONS
    private void canviColor(int tag, boolean canvi) {
        try {
            switch (tag) {
                case 0: // vermell
                    botoVermell.setBackgroundColor(
                            getResources().getColor((canvi) ? R.color.colorVermellClar : R.color.colorVermellFosc));
                    if(dispositiuExtern) write("1");
                    mi.start();
                    break;
                case 1: // verd
                    botoVerd.setBackgroundColor(
                            getResources().getColor((canvi) ? R.color.colorVerdClar : R.color.colorVerdFosc));
                    if(dispositiuExtern) write("2");
                    re.start();
                    break;
                case 2: // groc
                    botoGroc.setBackgroundColor(
                            getResources().getColor((canvi) ? R.color.colorGrocClar : R.color.colorGrocFosc));
                    if(dispositiuExtern) write("3");
                    si.start();
                    break;
                case 3: // blau
                    botoBlau.setBackgroundColor(
                            getResources().getColor((canvi) ? R.color.colorBlauClar : R.color.colorBlauFosc));
                    if(dispositiuExtern) write("4");
                    sol.start();
                    break;
            }
        } catch (Exception e) {

        }

    }

    public void bloquejarPantalla(boolean b) {

        // activar boto
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
        labelBluetooth.setVisibility(View.VISIBLE);
    }

    public void amagarComponents() {
        botonera.setVisibility(View.GONE);
        radioGrup.setVisibility(View.GONE);
        textNomJugador.setVisibility(View.INVISIBLE);
        labelBluetooth.setVisibility(View.INVISIBLE);
    }

    // BLUETOOTH
    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if (bondedDevices.size() > 0) {
                    Object[] devices = (Object[]) bondedDevices.toArray();
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

    /*
    public void comunicar() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;
        while (escoltar) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
                if(bytes > 0){
                    switch(new String(buffer)){
                        case "r":
                            comprovaBoto(0);
                            escoltar = false;
                            break;
                        case "g":
                            comprovaBoto(1);
                            escoltar = false;
                            break;
                        case "b":
                            comprovaBoto(3);
                            escoltar = false;
                            break;
                        case "y":
                            comprovaBoto(2);
                            escoltar = false;
                            break;
                        default:
                            try{
                                write("9");
                            }catch(Exception e){

                            }
                            escoltar = false;
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }*/

}

