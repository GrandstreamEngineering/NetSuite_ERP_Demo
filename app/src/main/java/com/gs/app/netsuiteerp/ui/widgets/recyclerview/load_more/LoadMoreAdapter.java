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

package com.gs.app.netsuiteerp.ui.widgets.recyclerview.load_more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gs.app.netsuiteerp.R;

import java.util.List;

public abstract class LoadMoreAdapter<O, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    protected static final int VIEW_TYPE_ITEM = 0;
    protected static final int VIEW_TYPE_FOOTER = 1;

    protected List<O> objects;
    protected Context context;
    protected LayoutInflater layoutInflater;
    protected LoadMoreScrollerListener onScrollerListener;
    protected OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading = false;
    protected int pageSize = 10;
    protected int currentPage = 1;
    protected int totalPage = 1;

    public LoadMoreAdapter(@NonNull Context context, @NonNull List<O> objects) {
        this.context = context;
        this.objects = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        onScrollerListener = new LoadMoreScrollerListener(recyclerView) {
            @Override
            public void onLoadMore() {
                isLoading = true;
                onLoadMoreListener.onLoadMore(currentPage);
            }
        };
        recyclerView.addOnScrollListener(onScrollerListener);
        updateCanLoadMore();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (onScrollerListener != null) {
            recyclerView.removeOnScrollListener(onScrollerListener);
        }
        onScrollerListener = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return createItemViewHolder(parent);
        } else {
            return new FooterViewHolder(layoutInflater.inflate(R.layout.footer_load_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_FOOTER) {
            if (isCanLoadMore()) {
                ((FooterViewHolder) holder).progress_bar.setVisibility(View.VISIBLE);
                ((FooterViewHolder) holder).tv_more.setText(R.string.load_more);
            } else {
                ((FooterViewHolder) holder).progress_bar.setVisibility(View.GONE);
                ((FooterViewHolder) holder).tv_more.setText(R.string.load_more_none);
            }
        } else {
            bindItemViewHolder((V) holder, position);
        }
    }

    /*
     * 本例中所有数据加载完毕后还是保留footerView并显示已加载完全，所以footerView一直存在。
     * */
    @Override
    public int getItemCount() {
        return objects.size() + 1;
    }

    protected abstract V createItemViewHolder(@NonNull ViewGroup parent);
    protected abstract void bindItemViewHolder(V holder, int position);

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.totalPage = 1;
        updateCanLoadMore();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPage(int currentPage, int totalPage) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        LoadMoreScrollerListener scrollerListener = onScrollerListener;
        if (scrollerListener != null) {
            scrollerListener.setCanLoadMore(isCanLoadMore());
        }
        if (scrollerListener != null) {
            scrollerListener.setLoading(false);
        }
        isLoading = false;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    /*
     * 数据加载完毕时, 执行setCanLoadMore()，此时isLoading都置为false
     * */
    private void updateCanLoadMore() {
        LoadMoreScrollerListener scrollerListener = onScrollerListener;
        if (scrollerListener != null) {
            scrollerListener.setCanLoadMore(isCanLoadMore());
        }
        if (!isCanLoadMore()) {
            if (scrollerListener != null) {
                scrollerListener.setLoading(false);
            }
            isLoading = false;
        } else {
            if (scrollerListener != null) {
                scrollerListener.setLoading(isLoading);
            }
        }
    }

    public boolean isCanLoadMore() {
        return totalPage > 0 && currentPage > 0 && totalPage > currentPage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int currentPage);
    }

    // 底部的FooterView
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progress_bar;
        public TextView tv_more;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progress_bar = itemView.findViewById(R.id.progress_bar);
            tv_more = itemView.findViewById(R.id.tv_more);
        }
    }
}