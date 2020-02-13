package com.example.jonas.areafoliar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;


public class ActConfigGeral extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_geral);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        validaCheckBox();
    }

    public void validaCheckBox(){
        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(getString(R.string.pref_text), "");

        CheckBox checkbox3 = findViewById(R.id.checkbox3);
        CheckBox checkbox4 = findViewById(R.id.checkbox4);
        CheckBox checkbox5 = findViewById(R.id.checkbox5);

        if(result.equals("3")){
            checkbox3.setChecked(true);
        }else if(result.equals("4")){
            checkbox4.setChecked(true);
        }else{
            checkbox5.setChecked(true);
            editor = sharedPreferences.edit();
            editor.putString(getString(R.string.pref_text), "5");
            editor.apply();
        }
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox3:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "3");
                    editor.apply();
                }
                break;
            case R.id.checkbox4:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "4");
                    editor.apply();
                }
                break;
            case R.id.checkbox5:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "3");
                    editor.apply();
                }
                break;
        }
    }

}
