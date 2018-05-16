package com.coder.binauralbeats.activity.home;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.coder.binauralbeats.activity.BBeatActivity;
import com.coder.binauralbeats.adapter.GroupedListAdapter;
import com.coder.binauralbeats.base.BaseActivity;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.event.BusEvent;
import com.coder.binauralbeats.executor.NavigationMenuExecutor;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,HomeView {
    private static final String TAG = "HomeActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ArrayList<CategoryGroup> groups;
    private GroupedListAdapter adapter;
    private HomePresenter homePresenter;
    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }
    @Override
    protected MvpBasePresenter createPresenter() {
        return homePresenter==null ? homePresenter=new HomePresenter():homePresenter;
    }
    @Override
    protected MvpBaseView createView() {
        return this;
    }

    @Override
    protected void superInit(Intent intent) {
    }
    @Override
    protected void initEventAndData() {
        setDrawerToggle();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter=new GroupedListAdapter(this);
        recyclerview.setAdapter(adapter);
        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                Program program=groups.get(groupPosition).getObjets().get(childPosition).getProgram();
                EventBus.getDefault().postSticky(new BusEvent("key",program));
                Intent intent=new Intent(HomeActivity.this,BBeatActivity.class);
                startActivity(intent);

            }
        });
        if (homePresenter!=null){
            homePresenter.loadData();
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
        ColorStateList colorStateList=getBaseContext().getResources().getColorStateList(R.color.colorPrimary);
        navView.setItemIconTintList(colorStateList);
    }

    @Override
    public void showData(ArrayList<CategoryGroup> groupList) {
        groups=groupList;
        adapter.upData(groupList);
    }
}
