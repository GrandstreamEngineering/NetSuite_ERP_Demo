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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class LoadMoreScrollerListener extends RecyclerView.OnScrollListener {
    //是否正在加载
    private boolean isLoading = false;
    //是否能够加载更多
    private boolean isCanLoadMore = true;
    private final RecyclerView recyclerView;

    public LoadMoreScrollerListener(@NonNull RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 能够加载更多
        if (isCanLoadMore) {
            //已滑动到最底部
            if (isSlideToBottom()) {
                isLoading = true;
                onLoadMore();
            }
        }
    }

    /*
     * 是否滑动到最底部
     *
     * !isLoading, 不在加载过程中
     * lastVisibleItemPosition == totalItemCount - 1, 当前可见的最后一个item是列表的最后一个
     * */
    private boolean isSlideToBottom() {
        if (recyclerView  == null) {
            return false;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            return !isLoading && visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1;
        }
        return false;
    }

    //抽象方法，用来传递加载更多事件
    public abstract void onLoadMore();

    public void setCanLoadMore(boolean load) {
        isCanLoadMore = load;
    }

    public void setLoading(boolean load) {
        isLoading = load;
    }
}