package remotedoorway.byteme.com.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.Doors;
import remotedoorway.byteme.com.models.UserInfo;

public class SharedFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    List<Doors> DoorsList =new ArrayList<Doors>();
    List<UserInfo> UsersList =new ArrayList<UserInfo>();


    Spinner doorSelector;
    ListView doorUserList;
    EditText txtUserId;
    Button addUser;
    ArrayAdapter<Doors> myadapter;
    ArrayAdapter<UserInfo> usersadapter;

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
         txtUserId = (EditText) view.findViewById(R.id.txtEmail);
         addUser = (Button) view.findViewById(R.id.btnAddUser);
         myadapter = new ArrayAdapter<Doors>(getActivity(),
                                                                android.R.layout.simple_list_item_1,
                 DoorsList);
        usersadapter = new ArrayAdapter<UserInfo>(getActivity(),
                                                            android.R.layout.simple_list_item_1,
                                                             UsersList);


        doorSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String switchuserid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                final String txtAddUser = parent.getItemAtPosition(position).toString();

                FirebaseDatabase database= FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                databaseReference.child("UserInfo").child(switchuserid).child("Doors").child("Owner").child(switchuserid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot DSOwnerDoors = dataSnapshot.child("SharedWith");
                                UsersList.clear();

                                for (DataSnapshot userRows : DSOwnerDoors.getChildren()) {
                                    final UserInfo users=userRows.getValue(UserInfo.class);
                                    users.setUserId(userRows.getKey());
                                    UsersList.add(users);
                                    Log.v("Permitted to:",users.toString());
                                }

                                doorUserList.setAdapter(usersadapter);
                            }


                            @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    private void populateFriendRequets()
    {
        //Fill Spinner with all doors
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference Doors = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(uid).child("Doors");

        Doors.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot DSOwnerDoors = dataSnapshot.child("Owner");
                DoorsList.clear();

                for (DataSnapshot doorRows : DSOwnerDoors.getChildren()) {
                    final Doors doors=doorRows.getValue(Doors.class);

                    doors.setDoorId(doorRows.getKey());
                    DoorsList.add(doors);
                    Log.v("Owners got:",doors.toString());
                }

                doorSelector.setAdapter(myadapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        //Fill listview with users for door selected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        databaseReference. child("UserInfo").child(uid).child("Doors").child("Owner").child("SharedWith")
                .addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               DataSnapshot contactSnapshot = dataSnapshot.child("Owner");
                                               Iterable<DataSnapshot> contactChildren = contactSnapshot.getChildren();
                                               for (DataSnapshot contact : contactChildren) {
                                                   UserInfo c = contact.getValue(UserInfo.class);
                                                   Log.d("contact:: ", c.getFullName());
                                                   UsersList.add(c);
                                               }


                                               doorUserList.setAdapter(usersadapter);
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       }

                );


    }

    //To give access to new user for door [Button click event]
    public void addUser(View v){

        final String newuserid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        final String txtAddUser = txtUserId.getText().toString();

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("UserInfo").child(txtAddUser);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo user = dataSnapshot.getValue(UserInfo.class);

                myRef.child("UserInfo").child(newuserid).child("Doors").child("Owner").child(newuserid).push().setValue(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }





    private class UserListAdaptor extends ArrayAdapter<UserInfo>
    {
        public UserListAdaptor() {
            super(getActivity().getBaseContext(),R.layout.ownerdoorlistlistrowview, UsersList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.ownerdoorlistlistrowview,parent,false);
            }


            final UserInfo users= UsersList.get(position);
            TextView tvdoorname=(TextView) itemview.findViewById(R.id.tvownerdoorlistdoorname);
            tvdoorname.setText(users.getFullName());
            return itemview;
        }
    }




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
