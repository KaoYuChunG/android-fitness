package com.kao.myapplication.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kao.myapplication.OnAdapterItemClickListener;
import com.kao.myapplication.R;
import com.kao.myapplication.model.Register;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListCalcViewHolder extends  RecyclerView.ViewHolder {
    public ListCalcViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected final Context getContext() {
        return itemView.getContext();
    }
    //      cria um funcao para pegar position, ou passa informacao para tela
    public void bind(Register item, final OnAdapterItemClickListener onItemClickListener) {
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
                getContext().getResources().getString(R.string.list_response, item.response, formatted)
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
