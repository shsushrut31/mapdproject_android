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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.activity.MainActivity;
import remotedoorway.byteme.com.models.ChatAuthentication;
import remotedoorway.byteme.com.models.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    List<UserInfo> userInfoList =new ArrayList<UserInfo>();
    private List<ChatAuthentication> chatAuthenticationList=new ArrayList<ChatAuthentication>();
    ArrayAdapter<UserInfo> adapter;
    ListView list;






    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        populateUserList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentView view = inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        adapter=new MyAdapter();
        list=(ListView) view.findViewById(R.id.lvfragfriendlist);
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

    private void populateUserList()
    {


        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        DatabaseReference chatAuthentication= FirebaseDatabase.getInstance().getReference().child("ChatAuthentication");


        chatAuthentication.runTransaction(new Transaction.Handler()
        {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                chatAuthenticationList.clear();
                for (DataSnapshot chatAuthenticationrows: dataSnapshot.getChildren()) {

                    ChatAuthentication obj1 = chatAuthenticationrows.getValue(ChatAuthentication.class);

                    //in future, change below userid with sharedpreference
                    if((obj1.getUserID1().equals(userid) && obj1.getValidate1().equals("1") && obj1.getValidate2().equals("1")) ){
                        obj1.setChatAuthenticationID(chatAuthenticationrows.getKey().toString());
                        chatAuthenticationList.add(obj1);
                    }

                    if((obj1.getUserID2().equals(userid) && obj1.getValidate1().equals("1") && obj1.getValidate2().equals("1"))) {
                        obj1.setChatAuthenticationID(chatAuthenticationrows.getKey().toString());
                        chatAuthenticationList.add(obj1);
                    }
                    //chatAuthentication.removeEventListener(this);
                }




                // get friends information
                DatabaseReference UserInfoTable = FirebaseDatabase.getInstance().getReference().child("UserInfo");


                Log.e("db ref:",UserInfoTable.toString());
                //Toast.makeText(ShowFriendsActivity.this,"See:" + UserInfoTable.toString(), Toast.LENGTH_LONG).show();

                UserInfoTable.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        //userInfoList.clear();
                        userInfoList.clear();
                        for (DataSnapshot userinforows: dataSnapshot.getChildren()) {

                            UserInfo obj1=userinforows.getValue(UserInfo.class);
                            obj1.setUserId(userinforows.getKey().toString());
                            Log.e("db key:",userinforows.getKey());


                            for(int i=0;i<chatAuthenticationList.size();i++)
                            {
                                if(userinforows.getKey().toString().equals(chatAuthenticationList.get(i).getUserID2()))
                                {
                                    if(!chatAuthenticationList.get(i).getUserID2().equals(userid)) {
                                        obj1.setUserId(chatAuthenticationList.get(i).getChatAuthenticationID());
                                        userInfoList.add(obj1);
                                    }
                                }

                                if(userinforows.getKey().toString().equals(chatAuthenticationList.get(i).getUserID1()))
                                {

                                    if(!chatAuthenticationList.get(i).getUserID1().equals(userid)) {
                                        obj1.setUserId(chatAuthenticationList.get(i).getChatAuthenticationID());
                                        userInfoList.add(obj1);
                                    }
                                }


                            }
                        }
                        //UserInfoTable.removeEventListener(this);

                        adapter=new MyAdapter();
                        list.setAdapter(adapter);

                    }


                });


            }


        });

    }



    private class MyAdapter extends ArrayAdapter<UserInfo>
    {
        public MyAdapter() {
            super(getActivity().getBaseContext(),R.layout.friendlistlistrowview, userInfoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.friendlistlistrowview,parent,false);
            }

            final UserInfo currentUser= userInfoList.get(position);
            TextView tvusername=(TextView) itemview.findViewById(R.id.tvfriendlistusername);
            ImageView imguserdp=(ImageView) itemview.findViewById(R.id.imgvfriendlistuserdp);
            RelativeLayout friendlistviewcell=(RelativeLayout) itemview.findViewById(R.id.friendlistviewcell);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://away-obscure-2.appspot.com");

            StorageReference dpref = storageRef.child(currentUser.getDPURL());

            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(dpref)
                    .into(imguserdp);

            tvusername.setText(currentUser.getFullName());
            friendlistviewcell.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Bundle bundle=new Bundle();
                    bundle.putString("currentChatAuthID", currentUser.getUserId());
                    bundle.putSerializable("friendobj",currentUser);

                    Fragment fragment = new ChatFragment();
                    fragment.setArguments(bundle);

                    android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, "");
                    fragmentTransaction.commit();
                    MainActivity.navItemIndex=6;

                }
            });

            return itemview;
        }
    }






    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
