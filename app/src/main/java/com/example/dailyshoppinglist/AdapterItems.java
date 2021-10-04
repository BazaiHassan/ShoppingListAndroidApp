package com.example.dailyshoppinglist;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ItemViewHolder>  {
    private ItemCallBack itemCallBack;
    private List<ItemModel> itemModelList = new ArrayList<>();

    public AdapterItems(ItemCallBack itemCallBack) {
        this.itemCallBack = itemCallBack;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterItems.ItemViewHolder holder, int position) {
        holder.bindItem(itemModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public void addItem(ItemModel items){
        itemModelList.add(items);
        notifyDataSetChanged();
    }

    public void showDataItem(List<ItemModel> itemModels){
        this.itemModelList.addAll(itemModels);
        notifyDataSetChanged();
    }

    public void deleteItemData(ItemModel items){
        for (int i = 0; i <itemModelList.size() ; i++) {
            if (itemModelList.get(i).getId() == items.getId()){
                itemModelList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void removeAllItems(){
        itemModelList.clear();
        notifyDataSetChanged();
    }

    public void editItem(ItemModel items){
        for (int i = 0; i <itemModelList.size() ; i++) {
            if (items.getId() == itemModelList.get(i).getId()){
                itemModelList.set(i,items);
                notifyItemChanged(i);
            }
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtItem;
        private CheckBox cbItem;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.txt_item);
            cbItem = itemView.findViewById(R.id.cb_item);


        }
        public void bindItem(ItemModel itemModel){
            txtItem.setText(itemModel.getItemName());
            if (itemModel.isChecked()){
                txtItem.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                txtItem.setTextColor(itemView.getResources().getColor(R.color.gray));
            }else {
                txtItem.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                txtItem.setTextColor(itemView.getResources().getColor(R.color.black));
            }
            cbItem.setChecked(itemModel.isChecked());

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemCallBack.onDeleteItem(itemModel);
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemCallBack.onUpdateItem(itemModel);
                }
            });

            cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    itemModel.setChecked(b);
                    itemCallBack.onCheckedItem(itemModel);
                    if (b){
                        txtItem.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        txtItem.setTextColor(itemView.getResources().getColor(R.color.gray));
                    }else {
                        txtItem.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                        txtItem.setTextColor(itemView.getResources().getColor(R.color.black));;
                    }
                }
            });
        }
    }



    public interface ItemCallBack{
        void onCheckedItem(ItemModel model);
        void onDeleteItem(ItemModel model);
        void onUpdateItem(ItemModel model);
    }

}
