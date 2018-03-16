package com.coder.binauralbeats.adapter;

import android.content.Context;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.beats.ProgramMeta;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    private ArrayList<CategoryGroup> mGroups;

    public GroupedListAdapter(Context context, ArrayList<CategoryGroup> groups) {
        super(context);
        mGroups = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ProgramMeta> children = mGroups.get(groupPosition).getObjets();
        return children == null ? 0 : children.size();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.bb_group_item;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.bb_child_item;
    }

    @Override
    public void onBindFooterViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition) {

    }
    @Override
    public void onBindHeaderViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition) {
        CategoryGroup entity = mGroups.get(groupPosition);
        holder.setText(R.id.group_title_txt, entity.getNiceName());
    }

    @Override
    public void onBindChildViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition, int childPosition) {

        ProgramMeta entity = mGroups.get(groupPosition).getObjets().get(childPosition);
        Program p = entity.getProgram();
        holder.setText(R.id.child_name_txt, p.getName()+"\n"+p.getAuthor()+"\n"+p.getLength()+"\n"+ p.getNumPeriods());
        holder.setText(R.id.child_desc_txt,p.getDescription());


//        String.format(" %sh%smin.",
//                formatTimeNumberwithLeadingZero(length / 60 / 60),
//                formatTimeNumberwithLeadingZero((length / 60) % 60))
    }
}
