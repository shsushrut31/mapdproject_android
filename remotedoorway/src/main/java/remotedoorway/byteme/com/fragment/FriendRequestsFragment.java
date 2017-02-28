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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.ChatAuthentication;
import remotedoorway.byteme.com.models.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendRequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendRequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatabaseReference chatAuthentication;
    DatabaseReference UserInfoTable;


    ValueEventListener v1;
    ValueEventListener v2;

    List<UserInfo> userInfoList =new ArrayList<UserInfo>();
    List<ChatAuthentication> chatAuthenticationList=new ArrayList<ChatAuthentication>();

    ListView list;
    ArrayAdapter<UserInfo> adapter;

    private OnFragmentInteractionListener mListener;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendRequestsFragment newInstance(String param1, String param2) {
        FriendRequestsFragment fragment = new FriendRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        adapter=new MyAdapter();
        list=(ListView) view.findViewById(R.id.lvfragfriendrequests);
        list.setAdapter(adapter);
        return view;
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onResume() {
        super.onResume();
        populateFriendRequets();
    }

    @Override
    public void onPause() {
        super.onPause();
        chatAuthentication.removeEventListener(v1);
        UserInfoTable.removeEventListener(v2);
    }

    private class MyAdapter extends ArrayAdapter<UserInfo>
    {
        public MyAdapter() {
            super(getActivity().getBaseContext(),R.layout.friendrequestslistviewrow, userInfoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.friendrequestslistviewrow,parent,false);
            }


            final UserInfo currentUser= userInfoList.get(position);
            TextView tvusername=(TextView) itemview.findViewById(R.id.tvfriendrequestsusername);
            TextView tvnearyouuserdevice=(TextView) itemview.findViewById(R.id.tvfriendrequestsuserdevice);
            TextView tvnearyouusersex=(TextView) itemview.findViewById(R.id.tvfriendrequestsusersex);
            ImageView imguserdp=(ImageView) itemview.findViewById(R.id.imgvfriendrequestuserdp);
            Button btnAccceptRequest=(Button)itemview.findViewById(R.id.btnfriendrequestsaccept);
            Button btnRejectRequest=(Button)itemview.findViewById(R.id.btnfriendrequestsreject);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://away-obscure-2.appspot.com");

            StorageReference dpref = storageRef.child(currentUser.getDPURL());

            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(dpref)
                    .into(imguserdp);

            tvusername.setText(currentUser.getFullName());
            tvnearyouuserdevice.setText(currentUser.getDeviceInfo());
            tvnearyouusersex.setText(currentUser.getGender());
            btnAccceptRequest.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    FirebaseAuth auth= FirebaseAuth.getInstance();
                    DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("ChatAuthentication").child(currentUser.getUserId());
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("Validate2", "1");
                    root.updateChildren(map);
                }
            });

            btnRejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth= FirebaseAuth.getInstance();
                    DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("ChatAuthentication").child(currentUser.getUserId());
                    root.removeValue();
                }
            });


            return itemview;
        }
    }


    private void populateFriendRequets()
    {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        chatAuthentication= FirebaseDatabase.getInstance().getReference().child("ChatAuthentication");

        // now lets get all his friends id
        chatAuthentication.addValueEventListener(v1=new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatAuthenticationList.clear();
                userInfoList.clear();
                for (DataSnapshot chatAuthenticationrows: dataSnapshot.getChildren()) {

                    ChatAuthentication obj1 = chatAuthenticationrows.getValue(ChatAuthentication.class);

                    //in future, change below userid with sharedpreference


                    if(obj1.getUserID2().equals(userid) && obj1.getValidate1().equals("1") && obj1.getValidate2().equals("0")) {
                        obj1.setChatAuthenticationID(chatAuthenticationrows.getKey().toString());

                        chatAuthenticationList.add(obj1);
                    }


                }

                // get friends information
                UserInfoTable = FirebaseDatabase.getInstance().getReference().child("UserInfo");

                Log.e("db ref:",UserInfoTable.toString());
                //Toast.makeText(ShowFriendsActivity.this,"See:" + UserInfoTable.toString(), Toast.LENGTH_LONG).show();

                UserInfoTable.addValueEventListener(v2=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userinforows: dataSnapshot.getChildren()) {
                            UserInfo obj1=userinforows.getValue(UserInfo.class);
                            obj1.setUserId(userinforows.getKey().toString());
                            Log.e("db key:",userinforows.getKey());

                            for(int i=0;i<chatAuthenticationList.size();i++)
                            {
                                if(obj1.getUserId().toString().equals(chatAuthenticationList.get(i).getUserID1()))
                                {
                                    obj1.setUserId(chatAuthenticationList.get(i).getChatAuthenticationID());
                                    userInfoList.add(obj1);
                                }
                                Log.e("get info of:",obj1.getUserId());
                            }
                        }

                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);

                        if(userInfoList.size()<=0)
                        {
                            ((TextView)getActivity().findViewById(R.id.tvfragnofriendrequests)).setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            ((TextView)getActivity().findViewById(R.id.tvfragnofriendrequests)).setVisibility(View.GONE);
                        }
                        UserInfoTable.removeEventListener(this);
                        chatAuthentication.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
