package com.kao.myapplication;

public interface OnAdapterItemClickListener {
	public void onClick(int id, String type);
	public void onLongClick(int position, String type, int id);
}
