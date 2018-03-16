package com.coder.binauralbeats.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by TUS on 2017/8/25.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private SparseArray<View> views=new SparseArray<>();
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.mView=itemView;
    }
    public <T extends View> T getView(int viewId){
        View view=views.get(viewId);
        if (view==null){
            view=itemView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T) view;
    }
    public View getRootView(){
        return itemView;
    }

}
