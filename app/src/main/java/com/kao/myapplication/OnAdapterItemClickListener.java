package com.kao.myapplication;

//para update e delete elemento
public interface OnAdapterItemClickListener {
    void onClick(int id, String type);
    void onLongClick(int position, String type, int id);
}
