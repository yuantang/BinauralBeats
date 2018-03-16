package com.coder.binauralbeats.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TUS on 2017/8/25.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>{

    private Context mContext;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private List<T> mDatas=new ArrayList<>();

    protected OnItemClickListner onItemClickListner;//单击事件
    protected OnItemLongClickListner onItemLongClickListner;//长按单击事件

    public void setData(List<T> datas){
        if (this.mDatas!=null){
            this.mDatas=datas;
        }
        notifyDataSetChanged();
    }
    public void upData(List<T> datas){
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    public BaseRecyclerViewAdapter(Context context, int LayoutId){
        this.mContext=context;
        this.mLayoutId=LayoutId;
        this.mInflater= LayoutInflater.from(context);
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(mInflater.inflate(mLayoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        bindView(holder,mDatas.get(position),position);
    }

    protected abstract void bindView(BaseViewHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        if (mDatas==null)
            return 0;
        return mDatas.size();
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public void setOnItemLongClickListner(OnItemLongClickListner onItemLongClickListner) {
        this.onItemLongClickListner = onItemLongClickListner;
    }
    public interface OnItemClickListner {
        void onItemClickListner(View v, int position);
    }
    public interface OnItemLongClickListner {
        void onItemLongClickListner(View v, int position);
    }
}
