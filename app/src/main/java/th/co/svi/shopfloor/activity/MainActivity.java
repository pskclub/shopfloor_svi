package th.co.svi.shopfloor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.adapter.ViewPagerAdapter;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.fragment.PendingFragment;
import th.co.svi.shopfloor.fragment.PlanFragment;
import th.co.svi.shopfloor.manager.ShareData;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter pageAdapter;
    ShareData shareMember;

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu, fabSend, fabCreate;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();
        if (shareMember.getUserRoute().equals("CMC1") ||
                shareMember.getUserRoute().equals("CMS1") ||
                shareMember.getUserRoute().equals("SMT1")) {
            displayViewPendingAndPlan();
        } else {
            displayViewPending(savedInstanceState);
        }


    }

    private void displayViewPending(Bundle savedInstanceState) {
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, PendingFragment.newInstance())
                    .commit();
        }

        fabMenu.setOnClickListener(onFabClickListener);
        fabSend.setOnClickListener(onFabClickListener);
        fabCreate.setOnClickListener(onFabClickListener);
    }


    private void displayViewPendingAndPlan() {
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new PendingFragment(), "Pending");
        pageAdapter.addFragment(new PlanFragment(), "Planning");
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        fabMenu.setOnClickListener(onFabClickListener);
        fabSend.setOnClickListener(onFabClickListener);
        fabCreate.setOnClickListener(onFabClickListener);
    }


    private void initInstances() {
        shareMember = new ShareData("MEMBER");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ShopFloor");
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        fabMenu = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        fabCreate = (FloatingActionButton) findViewById(R.id.fabCreate);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFAB();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getString(R.string.confirm));
            builder.setMessage(getString(R.string.want_to_exit));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //คลิกใช่ ออกจากโปรแกรม
                    finish();
                    MainActivity.super.onBackPressed();
                }
            });//second parameter used for onclicklistener
            builder.setNegativeButton(getString(R.string.no), null);
            builder.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 4, shareMember.getUsername().toUpperCase() +
                " | " + shareMember.getUserRoute()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1, 2, 5, "Sign Out").setIcon(R.drawable.ic_power_settings_new_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1 || item.getItemId() == 9) {
            Toast.makeText(this, "ID : " + shareMember.getUserID() + " | Name : " + shareMember.getUsername() + " | Route : " + shareMember.getUserRoute(), Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == 2) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.confirm);
            builder.setMessage("Are you sure you want to sign out ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    shareMember.setMember(false, shareMember.getUsername(), "", "");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });//second parameter used for onclicklistener
            builder.setNegativeButton("No", null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void animateFAB() {

        if (isFabOpen) {

            fabMenu.startAnimation(rotate_backward);
            fabSend.startAnimation(fab_close);
            fabCreate.startAnimation(fab_close);
            fabCreate.setClickable(false);
            fabSend.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fabMenu.startAnimation(rotate_forward);
            fabSend.startAnimation(fab_open);
            fabCreate.startAnimation(fab_open);
            fabCreate.setClickable(true);
            fabSend.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
    }

    /***************
     * listener
     */

    View.OnClickListener onFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fabMenu:
                    animateFAB();
                    break;
                case R.id.fabSend:
                    animateFAB();
                    Intent iSend = new Intent(MainActivity.this, SendActivity.class);
                    startActivityForResult(iSend, 2);
                    break;
                case R.id.fabCreate:
                    animateFAB();
                    Intent i = new Intent(MainActivity.this, CreateActivity.class);
                    startActivityForResult(i, 1);

                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            ResultBus.getInstance().postQueue(
                    new ActivityResultEvent(requestCode, resultCode, data));
        }
    }


    /******************
     * inner class
     */

}
