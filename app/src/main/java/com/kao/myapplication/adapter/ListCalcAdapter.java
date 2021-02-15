package com.kao.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kao.myapplication.ImcActivity;
import com.kao.myapplication.ListCalcActivity;
import com.kao.myapplication.OnAdapterItemClickListener;
import com.kao.myapplication.R;
import com.kao.myapplication.Register;
import com.kao.myapplication.SqlHelper;
import com.kao.myapplication.TmbActivity;
import com.kao.myapplication.viewholder.ListCalcViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ListCalcAdapter extends RecyclerView.Adapter<ListCalcViewHolder> implements OnAdapterItemClickListener {

    private List<Register> datas;
    private Context context;
    private Activity activity;

    //      cria variavel e construtor para pegar items
    public ListCalcAdapter(List<Register> datas, Context context) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ListCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//          aqui relaciona ao layout filho, simple_list_item_1 é do kit do android

        return new ListCalcViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListCalcViewHolder holder, int position) {
        Register data = datas.get(position);
//          cria um funcao bind para pegar position
        holder.bind(data, this);
    }

    @Override
    public int getItemCount() {
        return  datas != null ? datas.size() : 0 ;
    }

    @Override
    public void onClick(int id, String type) {
        // verificar qual tipo de dado deve ser EDITADO na tela seguinte
        switch (type) {
            case "imc":
                Intent intent = new Intent(context, ImcActivity.class);
                intent.putExtra("updateId", id);
                context.startActivity(intent);
                break;
            case "tmb":
                Intent i = new Intent(context, TmbActivity.class);
                i.putExtra("updateId", id);
                context.startActivity(i);
                break;
        }
    }

    @Override
    public void onLongClick(int position, String type, int id) {
        // evento de exclusão (PERGUNTAR ANTES PARA O USUARIO)
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.delete_message))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    new Thread(() -> {
                        SqlHelper sqlHelper = SqlHelper.getInstance(context);
                        long calcId = sqlHelper.removeItem(type, id);

                        activity.runOnUiThread(() -> {
                            if (calcId > 0) {
                                Toast.makeText(context, R.string.calc_removed, Toast.LENGTH_LONG).show();
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }).start();


                })
                .create();

        alertDialog.show();
    }
}
