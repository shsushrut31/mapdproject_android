package remotedoorway.byteme.com.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import remotedoorway.byteme.com.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoorListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DoorListFragment#newInstance} factory method to
 * create an instance of this fragmen
 */
public class DoorListFragment extends Fragment {
    public DoorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doorlist, container, false);
    }

}
