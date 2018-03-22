package com.coder.binauralbeats.activity.home;

import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.beats.CategoryGroup;

import java.util.ArrayList;

/**
 * Created by TUS on 2018/3/20.
 */

public interface HomeView extends MvpBaseView {

    void showData(ArrayList<CategoryGroup> categoryGroups);
}
