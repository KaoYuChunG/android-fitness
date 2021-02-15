package com.kao.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.kao.myapplication.adapter.ListCalcAdapter;
import java.util.List;


public class ListCalcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calc);

        Bundle extras = getIntent().getExtras();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_list);

        if (extras != null) {
            String type = extras.getString("type");
            new Thread(() -> {
                List<Register> registers = SqlHelper.getInstance(this).getRegisterBy(type);
                runOnUiThread(() -> {
                    Log.d("TESTE", registers.toString());
                    ListCalcAdapter adapter = new ListCalcAdapter(registers, this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                });
            }).start();
        }
    }
}