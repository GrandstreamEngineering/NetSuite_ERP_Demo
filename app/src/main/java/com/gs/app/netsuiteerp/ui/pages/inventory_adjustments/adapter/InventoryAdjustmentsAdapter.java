// Copyright 2025 Grandstream
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gs.app.netsuiteerp.ui.pages.inventory_adjustments.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gs.app.netsuiteerp.R;
import com.gs.app.netsuiteerp.beans.InventoryAdjustmentShow;
import com.gs.app.netsuiteerp.ui.widgets.recyclerview.load_more.LoadMoreAdapter;
import com.gs.app.netsuiteerp.utils.NumberUtil;

import java.util.List;
import java.util.Objects;

public class InventoryAdjustmentsAdapter extends LoadMoreAdapter<InventoryAdjustmentShow, InventoryAdjustmentsAdapter.ViewHolderBean> implements View.OnClickListener {
    private OnItemClickListener listener;

    public InventoryAdjustmentsAdapter(@NonNull Context context, @NonNull List<InventoryAdjustmentShow> objects) {
        super(context, objects);
    }

    @Override
    protected ViewHolderBean createItemViewHolder(@NonNull ViewGroup parent) {
        return new ViewHolderBean(layoutInflater.inflate(R.layout.item_inventory_adjustment, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindItemViewHolder(ViewHolderBean holder, int position) {
        InventoryAdjustmentShow itemObject = objects.get(position);
        holder.itemView.setTag(position);
        holder.tv_document_number.setText(context.getString(R.string.document_number_title) + " " + itemObject.getInventoryAdjustment().getTranId());
        holder.tv_account.setText(context.getString(R.string.account_title) + " " + itemObject.getInventoryAdjustment().getAccount().getRefName());
        holder.tv_memo.setText(context.getString(R.string.memo_title) + " " + itemObject.getInventoryAdjustment().getMemo());
        holder.tv_currency.setText(context.getString(R.string.currency_title) + " " + itemObject.getCurrency().getName());
        holder.tv_amount_foreign.setText(context.getString(R.string.amount_foreign_title) + " " + itemObject.getCurrency().getDisplaySymbol() + NumberUtil.INSTANCE.formatWithCommas(Math.abs(itemObject.getInventoryAdjustment().getEstimatedTotalValue())));
        holder.tv_amount.setText(context.getString(R.string.amount_title) + " " + NumberUtil.INSTANCE.formatWithCommas(itemObject.getInventoryAdjustment().getEstimatedTotalValue()));
        holder.tv_create_date.setText(context.getString(R.string.create_date_title) + " " + itemObject.getInventoryAdjustment().getCreatedDate());
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (listener != null) {
            listener.onItemClicked(position);
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public static class ViewHolderBean extends RecyclerView.ViewHolder {
        public TextView tv_document_number;
        public TextView tv_account;
        public TextView tv_memo;
        public TextView tv_currency;
        public TextView tv_amount_foreign;
        public TextView tv_amount;
        public TextView tv_create_date;

        public ViewHolderBean(@NonNull View itemView) {
            super(itemView);
            tv_document_number = itemView.findViewById(R.id.tv_document_number);
            tv_account = itemView.findViewById(R.id.tv_account);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_currency = itemView.findViewById(R.id.tv_currency);
            tv_amount_foreign = itemView.findViewById(R.id.tv_amount_foreign);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_create_date = itemView.findViewById(R.id.tv_create_date);
        }
    }
}