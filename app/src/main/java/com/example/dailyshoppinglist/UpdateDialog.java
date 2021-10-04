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

public class UpdateDialog extends DialogFragment {

    private ItemModel model = new ItemModel();
    private EditDataItem editDataItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        editDataItem = (EditDataItem) context;
        model = getArguments().getParcelable("item");
        if (model == null) //New
            dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog, null, false);
        final EditText editText = view.findViewById(R.id.edt_item_update);
        editText.setText(model.getItemName());
        Button btnEdit = view.findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.length()>0){
                    String inputItem = editText.getText().toString();
                    model.setItemName(inputItem);
                    editDataItem.editItem(model);
                    dismiss(); // New
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
        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    public interface EditDataItem{
        void editItem(ItemModel itemModel);
    }
}
