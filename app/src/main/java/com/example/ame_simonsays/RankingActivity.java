package com.example.ame_simonsays;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {


    public void onClickBotoBack(View view){
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);
        String puntuacio = getIntent().getStringExtra("puntuacio");
    }


}
