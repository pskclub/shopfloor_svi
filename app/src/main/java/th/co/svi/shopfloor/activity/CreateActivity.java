package th.co.svi.shopfloor.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.fragment.CreateFragment;
import th.co.svi.shopfloor.manager.ShareData;

public class CreateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ShareData shareMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        initInstances();
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, CreateFragment.newInstance())
                    .commit();
        }
    }

    private void initInstances() {
        shareMember = new ShareData("MEMBER");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setTitle("Start Job");
        setSupportActionBar(toolbar);
    }

}
