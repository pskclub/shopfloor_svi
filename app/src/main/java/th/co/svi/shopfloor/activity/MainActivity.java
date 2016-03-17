package th.co.svi.shopfloor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.adapter.ViewPagerAdapter;
import th.co.svi.shopfloor.fragment.MainFragment;
import th.co.svi.shopfloor.fragment.PendingFragment;
import th.co.svi.shopfloor.manager.ShareData;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    boolean plan = true;
    public static String txtDate = (DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString());
    ViewPagerAdapter pageAdapter;
    ShareData shareMember;
    int[] tabIcons = {
            R.drawable.ic_list_white_36dp,
            R.drawable.ic_shopping_cart_white_24dp,
            R.drawable.ic_schedule_white_36dp
    };

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu, fabSend, fabCreate;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();
        viewPager.addOnPageChangeListener(onPageChangeListener);
        if (shareMember.getUserRoute().equals("CMC1")) {
            displayView_CMC();
        } else if (shareMember.getUserRoute().equals("CMS1")) {
            displayView_CMS();
        } else {
            displayView();
        }

       /* if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MainFragment.newInstance())
                    .commit();
        }*/
    }

    private void displayView() {
    }

    private void displayView_CMS() {
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new MainFragment(), "Show Plan");
//        pageAdapter.addFragment(new ReturnCartFragment(), "Return Cart");
        pageAdapter.addFragment(new PendingFragment(), "Pending Job");
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[1]);


        fabMenu.setImageResource(R.drawable.ic_send_white_36dp);
        fabMenu.setOnClickListener(cmcFabOnClickListener);
    }

    private void displayView_CMC() {
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new MainFragment(), "Show Plan");
        pageAdapter.addFragment(new PendingFragment(), "Pending Job");
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[2]);


        fabMenu.setOnClickListener(onFabClickListener);
        fabSend.setOnClickListener(onFabClickListener);
        fabCreate.setOnClickListener(onFabClickListener);
    }


    private void initInstances() {
        shareMember = new ShareData("MEMBER");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Show Plan");
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (plan) {
//            menu.add(1, 3, 2, "Find Date").setIcon(R.drawable.ic_date_range_white_24dp).setShowAsAction(2);?
            menu.add(1, 3, 3, txtDate).setShowAsAction(1);

        }
        menu.add(1, 1, 1, "User : " + shareMember.getUsername().toUpperCase() +
                " (" + shareMember.getUserRoute() + ")");
        menu.add(1, 2, 2, "Sign Out");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Toast.makeText(this, "Username : " + shareMember.getUsername() + "| Route : " + shareMember.getUserRoute(), Toast.LENGTH_SHORT).show();
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
            if (shareMember.getUserRoute().equals("CMC1")) {
                fabCreate.startAnimation(fab_close);
                fabCreate.setClickable(false);
            }

            fabSend.setClickable(false);

            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fabMenu.startAnimation(rotate_forward);
            fabSend.startAnimation(fab_open);
            if (shareMember.getUserRoute().equals("CMC1")) {
                fabCreate.startAnimation(fab_open);
                fabCreate.setClickable(true);
            }
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
                    startActivity(iSend);
                    break;
                case R.id.fabCreate:
                    animateFAB();
                    Intent i = new Intent(MainActivity.this, CreateActivity.class);
                    startActivity(i);

                    break;
            }
        }
    };

    View.OnClickListener cmcFabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SendActivity.class);
            startActivity(i);

        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            toolbar.setTitle(pageAdapter.getMyPageTitle(position));
            setSupportActionBar(toolbar);
            if (position == 0)
                plan = true;
            else
                plan = false;
            invalidateOptionsMenu();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /******************
     * inner class
     */

}
