package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.CaptureActivity;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class ContrainerSearchFragment extends Fragment {
    private EditText txtId;
    private ImageButton btnQrcode, btnSearch;

    public ContrainerSearchFragment() {
        super();
    }

    public static ContrainerSearchFragment newInstance() {
        ContrainerSearchFragment fragment = new ContrainerSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contrainer_search, container, false);
        initInstances(rootView, savedInstanceState);
        btnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(i,5);
            }
        });
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        txtId = (EditText) rootView.findViewById(R.id.txtID);
        btnQrcode = (ImageButton) rootView.findViewById(R.id.btnQrcode);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

}
