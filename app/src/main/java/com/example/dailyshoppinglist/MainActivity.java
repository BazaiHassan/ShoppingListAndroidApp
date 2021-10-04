package com.example.dailyshoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements InsertDialog.InsertDataItem, AdapterItems.ItemCallBack, UpdateDialog.EditDataItem {

    private FloatingActionButton addBtn, removeAllBtn, infoBtn;
    private ItemDao dao;
    private RecyclerView rvItem;
    private List<ItemModel> itemModelList = new ArrayList<>();
    AdapterItems adapterItems = new AdapterItems(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dao = AppDatabase.getAppDatabase(this).getItemDao();
        findViews();
        listener();
    }

    private void listener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog insertDialog = new InsertDialog();
                insertDialog.show(getSupportFragmentManager(), null);
            }
        });
        removeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("آیا از حذف همه آیتم ها مطمئن هستید؟");
                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dao.deleteAll();
                        adapterItems.removeAllItems();
                        Toast.makeText(MainActivity.this, "همه آیتم ها حذف شدند", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoDialog infoDialog = new InfoDialog();
                infoDialog.show(getSupportFragmentManager(),null);
            }
        });
    }

    private void findViews() {
        addBtn = findViewById(R.id.add_btn);
        rvItem = findViewById(R.id.rv_items);
        removeAllBtn = findViewById(R.id.remove_all_btn);
        infoBtn = findViewById(R.id.btn_info);
        itemModelList = dao.getAllItems();
        adapterItems.showDataItem(itemModelList);
        rvItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvItem.setAdapter(adapterItems);


    }

    @Override
    public void insertItem(ItemModel itemModel) {
        long result = dao.addItem(itemModel);

        if (result != -1) {
            itemModel.setId(result);
            adapterItems.addItem(itemModel);
        } else {
            Toast.makeText(this, "خطای ثبت اطلاعات", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedItem(ItemModel model) {
        int result = dao.updateItem(model);
        if (result > 0) {
        } else {
            Toast.makeText(this, "آیتم باید خریداری شود", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDeleteItem(ItemModel model) {
        int result = dao.deleteItem(model);
        if (result > 0) {
            Toast.makeText(this, "آیتم حذف گردید", Toast.LENGTH_SHORT).show();
            adapterItems.deleteItemData(model);
        } else {
            Toast.makeText(this, "خطای حذف اطلاعات", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateItem(ItemModel model) {
        UpdateDialog updateDialogFragment = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", model);
        updateDialogFragment.setArguments(bundle);
        updateDialogFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void editItem(ItemModel itemModel) {
        int result = dao.updateItem(itemModel);
        if (result > 0) {
            adapterItems.editItem(itemModel);
        } else {
            Toast.makeText(this, "خطا در انجام ویرایش", Toast.LENGTH_SHORT).show();
        }


    }
}