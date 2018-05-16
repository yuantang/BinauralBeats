package com.coder.binauralbeats.activity.home;

import android.util.Log;

import com.coder.binauralbeats.MyApp;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.DefaultProgramsBuilder;
import com.coder.binauralbeats.beats.ProgramMeta;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author TUS
 * @date 2018/3/20
 */

public class HomePresenter extends MvpBasePresenter<HomeView> {
    Map<String,ProgramMeta> programs;
    ArrayList<CategoryGroup> groups;
    public void loadData(){
        programs = DefaultProgramsBuilder.getProgramMethods(MyApp.getInstance());
        groups = new ArrayList<>();
        for (String pname: programs.keySet()) {
            ProgramMeta pm = programs.get(pname);
            String catname = pm.getCat().toString();
            CategoryGroup g = null;
            for (CategoryGroup g2: groups) {
                if (g2.getName().equals(catname)) {
                    g = g2;
                }
            }
            if (g == null) {
                g = new CategoryGroup(catname);
                try {
                    Log.e("TAG", catname.toLowerCase());
                    g.setNiceName(MyApp.getInstance().getString(R.string.class.getField("group_"+catname.toLowerCase()).getInt(null)));
                } catch (Exception e) {

                }
                groups.add(g);
            }
            pm.setProgram(DefaultProgramsBuilder.getProgram(pm));
            g.add(pm);
        }

        getView().showData(groups);

    }
}
