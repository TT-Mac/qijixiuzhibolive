package com.qiji.live.xiaozhibo.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by weipeng on 16/11/12.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) % 2 != 0){
            outRect.left = 10;
            outRect.right = 30;
            outRect.bottom = 10;
            outRect.top = 10;

        }else {
            outRect.left = 30;
            outRect.right = 10;
            outRect.bottom = 10;
            outRect.top = 10;
        }


    }


}
