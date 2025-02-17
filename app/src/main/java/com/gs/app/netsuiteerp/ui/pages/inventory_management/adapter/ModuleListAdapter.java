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

package com.gs.app.netsuiteerp.ui.pages.inventory_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gs.app.netsuiteerp.R;

import java.util.List;

public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ViewHolderBean> implements View.OnClickListener {
    private final LayoutInflater layoutInflater;
    private final List<String> objects;
    private OnItemClickListener listener;
    public ModuleListAdapter(@NonNull Context context, @NonNull List<String> objects) {
        this.objects = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolderBean onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderBean(layoutInflater.inflate(R.layout.item_prefrence, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBean holder, int position) {
        holder.itemView.setTag(position);
        String itemObject = objects.get(position);
        holder.tv_title.setText(itemObject);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return objects.size();
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
        public TextView tv_title;

        public ViewHolderBean(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}