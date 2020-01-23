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

    // BUTTONS
    private Button botoBlau; // Botons de llum
    private Button botoVermell;
    private Button botoVerd;
    private Button botoGroc;

    private Button nivell1; // Botons de madness
    private Button nivell2;
    private Button nivell3;
    private Button madness;

    // LAYOUTS
    private LinearLayout botonera; // Botonera amb els botons d'inici de joc
    private LinearLayout radioGrup; // Botonera nivell madness

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

    // DATA STREAMS
    private OutputStream outputStream;
    private InputStream inStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        inicialitzacions();
    }

    // MADNESS
    public void onClickJugarMadness(View view) {
        modeJoc = 1;
        int seleccionat = view.getId();
        Button rb = (Button) findViewById(seleccionat);
        dificultat = (int) rb.getTag();
        inicialitzacions();
        madness.setBackgroundDrawable(nivell1.getBackground());

    }

    // INICIALITZACIONS DELS COMPONENTS
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

    // DISPLAY DE LA BOTONERA DE MADNESS
    public void onClickSelectLevelMadness(View view) {
        radioGrup.setVisibility(View.VISIBLE);
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
                write("1");
                break;
            case 1: // verd
                botoVerd.setBackgroundColor(
                        getResources().getColor((canvi) ? R.color.colorVerdClar : R.color.colorVerdFosc));
                write("2");
                break;
            case 2: // groc
                botoGroc.setBackgroundColor(
                        getResources().getColor((canvi) ? R.color.colorGrocClar : R.color.colorGrocFosc));
                write("3");
                break;
            case 3: // blau
                botoBlau.setBackgroundColor(
                        getResources().getColor((canvi) ? R.color.colorBlauClar : R.color.colorBlauFosc));
                write("4");
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
    }

    public void amagarComponents() {
        botonera.setVisibility(View.GONE);
        radioGrup.setVisibility(View.GONE);
        textNomJugador.setVisibility(View.INVISIBLE);
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

}
