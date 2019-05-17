package com.coder.binauralbeats.activity.home;

import android.os.AsyncTask;

import com.coder.binauralbeats.MyApp;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.DefaultProgramsBuilder;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.beats.ProgramMeta;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author TUS
 * @date 2018/3/20
 */

public class HomePresenter extends MvpBasePresenter<HomeView> {
    ArrayList<CategoryGroup>  groups;
    Map<String,ProgramMeta> programs;
    Program mProgram;
    public void loadData(){
        mAsyncTask.execute();
    }
    public void getProgram(int groupPosition,int childPosition){
        ArrayList<CategoryGroup> categoryGroups=MyApp.getInstance().groups;
        if (categoryGroups!=null){
            mProgram=categoryGroups.get(groupPosition).getObjets().get(childPosition).getProgram();
            getView().showProgramData(mProgram);
        }
    };
    private ArrayList<CategoryGroup> getData() {
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
                    g.setNiceName(MyApp.getInstance().getString(R.string.class.getField("group_"+catname.toLowerCase()).getInt(null)));
                } catch (Exception e) {

                }
                groups.add(g);
            }
            pm.setProgram(DefaultProgramsBuilder.getProgram(pm));
            g.add(pm);
        }
        return groups;
    }

    AsyncTask mAsyncTask=new AsyncTask() {
        @Override
        protected ArrayList<CategoryGroup> doInBackground(Object[] objects) {
            return getData();
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            getView().showData(groups);
        }
    };

}
