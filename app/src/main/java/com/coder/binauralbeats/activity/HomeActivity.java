package com.coder.binauralbeats.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.adapter.GroupedListAdapter;
import com.coder.binauralbeats.base.BaseActivity;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.DefaultProgramsBuilder;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.beats.ProgramMeta;
import com.coder.binauralbeats.executor.NavigationMenuExecutor;
import com.coder.binauralbeats.utils.Preferences;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    Map<String,ProgramMeta> programs;
    ArrayList<CategoryGroup> groups;
    private GroupedListAdapter adapter;
    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected MvpBaseView createView() {
        return null;
    }

    @Override
    protected void initEventAndData() {
        setDrawerToggle();
        initData();

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter=new GroupedListAdapter(this,groups);
        recyclerview.setAdapter(adapter);
        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                Program program=groups.get(groupPosition).getObjets().get(childPosition).getProgram();
                Intent intent=new Intent(HomeActivity.this,BBeatActivity.class);
                startActivity(intent);
                EventBus.getDefault().postSticky(new BusEvent("key",program));

            }
        });
    }

    private void initData() {
        programs = DefaultProgramsBuilder.getProgramMethods(this);
        groups = new ArrayList<>();

        for (String pname: programs.keySet()) {
            ProgramMeta pm = programs.get(pname);
            String catname = pm.getCat().toString();
            CategoryGroup g = null;
			/* Check if I already have a group with that name */
            for (CategoryGroup g2: groups) {
                if (g2.getName().equals(catname)) {
                    g = g2;
                }
            }
            if (g == null) {
                g = new CategoryGroup(catname);
                try {
                    g.setNiceName(getString(R.string.class.getField("group_"+catname.toLowerCase()).getInt(null)));
                } catch (Exception e) {
                    // pass
                }
                groups.add(g);
            }
            pm.setProgram(DefaultProgramsBuilder.getProgram(pm));
            g.add(pm);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
//        menu.getItem(R.id.nav_theme).setTitle(Preferences.isNightMode()?"白天模式":"夜间模式");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return NavigationMenuExecutor.onNavigationItemSelected(item, this);
    }
    private void setDrawerToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
    }
}
