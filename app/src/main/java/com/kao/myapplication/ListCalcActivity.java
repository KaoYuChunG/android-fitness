package com.kao.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                    ListCalcAdapter adapter = new ListCalcAdapter(registers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                });
            }).start();
        }
    }

    private class ListCalcAdapter extends RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder> implements OnAdapterItemClickListener {

        private List<Register> datas;

        //      cria variavel e construtor para pegar items
        public ListCalcAdapter(List<Register> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public ListCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//          aqui relaciona ao layout filho, simple_list_item_1 é do kit do android
            return new ListCalcViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ListCalcViewHolder holder, int position) {
            Register data = datas.get(position);
//          cria um funcao bind para pegar position
            holder.bind(data, this);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void onClick(int id, String type) {
            // verificar qual tipo de dado deve ser EDITADO na tela seguinte
            switch (type) {
                case "imc":
                    Intent intent = new Intent(ListCalcActivity.this, ImcActivity.class);
                    intent.putExtra("updateId", id);
                    startActivity(intent);
                    break;
                case "tmb":
                    Intent i = new Intent(ListCalcActivity.this, TmbActivity.class);
                    i.putExtra("updateId", id);
                    startActivity(i);
                    break;
            }
        }

        @Override
        public void onLongClick(int position, String type, int id) {
            // evento de exclusão (PERGUNTAR ANTES PARA O USUARIO)
            AlertDialog alertDialog = new AlertDialog.Builder(ListCalcActivity.this)
                    .setMessage(getString(R.string.delete_message))
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                        new Thread(() -> {
                            SqlHelper sqlHelper = SqlHelper.getInstance(ListCalcActivity.this);
                            long calcId = sqlHelper.removeItem(type, id);

                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    Toast.makeText(ListCalcActivity.this, R.string.calc_removed, Toast.LENGTH_LONG).show();
                                    datas.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                        }).start();


                    })
                    .create();

            alertDialog.show();
        }

        private class ListCalcViewHolder extends  RecyclerView.ViewHolder {

            public ListCalcViewHolder(@NonNull View itemView) {
                super(itemView);

            }

            //      cria um funcao para pegar position, ou passa informacao para tela
            public void bind( Register item, final OnAdapterItemClickListener onItemClickListener) {
                String formatted= "";
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));
                    Date dateSaved = sdf.parse(item.createdDate);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
                    formatted = dateFormat.format(dateSaved);
                } catch (ParseException e ) {
                    Log.e("List", e.getMessage(), e);
                }
                ((TextView) itemView).setText(
                    getString(R.string.list_response, item.response, formatted)
                );

                // listener para ouvir evento de click (ABRIR EDIÇAO)
                itemView.setOnClickListener(view -> {
                    onItemClickListener.onClick(item.id, item.type);
                });

                // listener para ouvir evento de long-click (segurar touch - EXCLUIR)
                itemView.setOnLongClickListener(view -> {
                    onItemClickListener.onLongClick(getAdapterPosition(), item.type, item.id);
                    return false;
                });
            }
        }
    }
}