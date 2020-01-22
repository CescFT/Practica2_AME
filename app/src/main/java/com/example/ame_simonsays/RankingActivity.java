package com.example.ame_simonsays;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {

    private TextView text1;
    private Button btnUpdateRanking;

    public void onClickBotoBack(View view){
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);
        text1 = (TextView) findViewById(R.id.rankText1);
        btnUpdateRanking = (Button) findViewById(R.id.updateRanking);




    }

    public void onClickButtonUpdate(View view){
        String data = getIntent().getStringExtra("Puntuacio");
        text1.setText(data);

    }

}
