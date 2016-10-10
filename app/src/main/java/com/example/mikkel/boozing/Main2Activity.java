package com.example.mikkel.boozing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Main2Activity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "Ved stadig ikke hvad den her gør";
    public final static String KEY_MESSAGE = "Hallo";
    private String ownerKey="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        ownerKey = intent.getStringExtra(MapsActivity.keyMessage);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        Bundle extras = new Bundle();
        EditText editText = (EditText) findViewById(R.id.editText2);
        String message = editText.getText().toString();
        extras.putString(KEY_MESSAGE, ownerKey);
        extras.putString(EXTRA_MESSAGE, message);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
