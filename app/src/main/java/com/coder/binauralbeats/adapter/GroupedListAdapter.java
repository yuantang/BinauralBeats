package com.coder.binauralbeats.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.coder.binauralbeats.ConsIntent;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.activity.BBeatActivity;
import com.coder.binauralbeats.activity.home.HomeActivity;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.beats.ProgramMeta;
import com.coder.binauralbeats.utils.ColorUtils;
import com.coder.binauralbeats.utils.StringUtils;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    private ArrayList<CategoryGroup> mGroups;
    private Context context;
    public GroupedListAdapter(Context context) {
        super(context);
        this.context=context;
    }
    public void upData(ArrayList<CategoryGroup> groups){
        this.mGroups = groups;
        notifyDataSetChanged();
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
        holder.setBackgroundColor(R.id.group_title_lly, ColorUtils.getColorPrimary(context));
    }

    @Override
    public void onBindChildViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, final int groupPosition, final int childPosition) {
        ProgramMeta entity = mGroups.get(groupPosition).getObjets().get(childPosition);
        final Program p = entity.getProgram();
        holder.setText(R.id.child_name_txt, p.getName());
        holder.setText(R.id.child_author_txt,p.getAuthor());
        holder.setText(R.id.child_length_txt, StringUtils.formatDuration(context,p.getLength()*1000));
        holder.setText(R.id.child_desc_txt,p.getDescription());
        holder.get(R.id.child_item_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BBeatActivity.class)
                        .putExtra(ConsIntent.groupId,groupPosition)
                        .putExtra(ConsIntent.childId,childPosition)
                        .putExtra(ConsIntent.programName,p.getName());
                context.startActivity(intent);
            }
        });
    }
}
