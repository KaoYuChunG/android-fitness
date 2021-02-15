package com.kao.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ImcActivity extends AppCompatActivity {

    private EditText editHeight;
    private EditText editWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        editHeight = findViewById(R.id.edit_imc_height);
        editWeight = findViewById(R.id.edit_imc_weight);

        Button btnSend = findViewById(R.id.btn_imc_send);
        buttonSend(btnSend);
    }

    private void buttonSend(Button btnSend) {
        //btnSend.setOnClickListener((view) -> {});  mesmo coisa da baixo
        btnSend.setOnClickListener(v -> {
            if(!validate()) {
                Toast.makeText(ImcActivity.this, R.string.fields_message, Toast.LENGTH_LONG).show();
                return;
            }

            String sHeight = editHeight.getText().toString();
            String sWeight = editWeight.getText().toString();

            int height = Integer.parseInt(sHeight);
            int weigth = Integer.parseInt(sWeight);

            double result = calculateImc(height, weigth);

            Log.d("TESTE", "resultado: " + result);

            int imcResponseId = imcResponse(result);

            getDialog(result, imcResponseId);

            //controle para teclato ... esconde teclato
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
        });
    }

    private void getDialog(double result, int imcResponseId) {
        AlertDialog dialog = new AlertDialog.Builder(ImcActivity.this)
                .setTitle(getString(R.string.imc_response,  result))
                .setMessage(imcResponseId)
                .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton(android.R.string.ok, ((dialog1, which) -> {
                    SqlHelper sqlHelper = SqlHelper.getInstance(ImcActivity.this);
//                      Thread serve para trabalha com service paralelo
                    new Thread(() -> {
                        int updateId = 0;

                        // verifica se tem ID vindo da tela anterior quando é UPDATE
                        if (getIntent().getExtras() != null)
                            updateId = getIntent().getExtras().getInt("updateId", 0);

                        long calcId;
                        // verifica se é update ou create
                        if (updateId > 0) {
                            calcId = sqlHelper.updateItem("imc", result, updateId);
                        } else {
                            calcId = sqlHelper.addItem("imc", result);
                        }

                        runOnUiThread(() -> {
                            Log.d("Thread","no Thread");
                            if(calcId > 0) {
                                Toast.makeText(ImcActivity.this, R.string.saved,Toast.LENGTH_SHORT).show();
                                openListCalcActivity();
                            }
                        });
                    }).start();
                }))
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list:
                openListCalcActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openListCalcActivity() {
        Intent intent = new Intent(ImcActivity.this, ListCalcActivity.class);
        intent.putExtra("type", "imc");
        startActivity(intent);
    }

    //@StringRes  serve para quando usa elemento de arquivo string.xml de função
    // sempre combina ao R.string, garanta para nao escreve code errado.
    @StringRes
    private int imcResponse(double result) {

        if(result < 15) {
            return R.string.imc_severely_low_weight;
        } else if(result < 16) {
            return R.string.imc_very_low_weight;
        }else if(result < 18.5) {
            return R.string.imc_low_weight;
        }else if(result < 25) {
            return R.string.normal;
        }else if(result < 30) {
            return R.string.imc_high_weight;
        }else if(result < 35) {
            return R.string.imc_so_high_weight;
        }else if(result < 40) {
            return R.string.imc_severely_high_weight;
        }else {
            return R.string.imc_extreme_weight;
        }
    }

    private double calculateImc(int height, int weight) {
        return weight / (((double) height /100) * ((double) height / 100));
    }

    private boolean validate() {
        return (!editWeight.getText().toString().startsWith("0")
                && !editWeight.getText().toString().isEmpty()
                && !editHeight.getText().toString().startsWith("0")
                && !editHeight.getText().toString().isEmpty()
        );
    }

}