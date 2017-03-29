package remotedoorway.byteme.com.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.Doors;

public class SharedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    List<Doors> OwnerDoorsList =new ArrayList<Doors>();


    Spinner doorSelector;
    ListView doorUserList;
    Button addUser;
    ArrayAdapter<Doors> myadapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SharedFragment() {
        // Required empty public constructor
    }


    public static SharedFragment newInstance(String param1, String param2) {
        SharedFragment fragment = new SharedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateFriendRequets();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_shared, container, false);

       // String[] myStringArray = {"Alpha", "Beta", "Charlie"};

         doorSelector = (Spinner) view.findViewById(R.id.array_list1);
         doorUserList = (ListView) view.findViewById(R.id.doorUserList);
         addUser = (Button) view.findViewById(R.id.btnAddUser);
         myadapter = new ArrayAdapter<Doors>(getActivity(),
                                                                android.R.layout.simple_list_item_1,
                                                                OwnerDoorsList);
       // doorSelector.setAdapter(myadapter);


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

    }











    private void populateFriendRequets()
    {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        DatabaseReference Doors = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userid).child("Doors");

        // now lets get all his friends id
        Doors.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // now seprating DataSnapshot according Others and Owner

                DataSnapshot DSOwnerDoors = dataSnapshot.child("Owner");
                OwnerDoorsList.clear();




                for (DataSnapshot doorRows : DSOwnerDoors.getChildren()) {
                    final Doors doors=doorRows.getValue(Doors.class);
                    doors.setDoorId(doorRows.getKey());
                    OwnerDoorsList.add(doors);
                    Log.v("Owners got:",doors.toString());
                }

                doorSelector.setAdapter(myadapter);
               // ownerlistview.setAdapter(owneradaptor);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

/*

    private class OwnerDoorAdaptor extends ArrayAdapter<Doors>
    {
        public OwnerDoorAdaptor() {
            super(getActivity().getBaseContext(),R.layout.ownerdoorlistlistrowview, OwnerDoorsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.ownerdoorlistlistrowview,parent,false);
            }


            final Doors currentDoor= OwnerDoorsList.get(position);
            TextView tvdoorname=(TextView) itemview.findViewById(R.id.tvownerdoorlistdoorname);
            tvdoorname.setText(currentDoor.getDoorName());
            return itemview;
        }
    }

*/


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
