package th.co.svi.shopfloor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.fragment.SendFragment;
import th.co.svi.shopfloor.manager.ShareData;

public class SendActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ShareData shareMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Bundle extras = getIntent().getExtras();
        initInstances();
        if (savedInstanceState == null) {
            if (extras != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, SendFragment.newInstance(
                                extras.getString("work_order")
                        ))
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, SendFragment.newInstance(null))
                        .commit();
            }

        }
    }

    private void initInstances() {
        shareMember = new ShareData("MEMBER");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Send");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "SAVE").setShowAsAction(2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }
}
