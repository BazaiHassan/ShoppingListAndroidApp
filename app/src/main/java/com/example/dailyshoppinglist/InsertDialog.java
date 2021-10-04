package com.example.dailyshoppinglist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InsertDialog extends DialogFragment {

    InsertDataItem insertDataItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        insertDataItem = (InsertDataItem) context;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.insert_dialog,null,false);
        EditText editText = view.findViewById(R.id.edt_item);
        Button btnInsert = view.findViewById(R.id.btn_insert);
        builder.setCancelable(true);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.length()>0){
                    String inputItem = editText.getText().toString();
                    ItemModel itemModel = new ItemModel();
                    itemModel.setItemName(inputItem);
                    itemModel.setChecked(false);
                    insertDataItem.insertItem(itemModel);
                    editText.setText("");
                }else {
                    editText.setError("لطفا یک ایتم را وارد کنید");
                }

            }
        });
        Button btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public interface InsertDataItem{
        void insertItem(ItemModel itemModel);
    }
}
