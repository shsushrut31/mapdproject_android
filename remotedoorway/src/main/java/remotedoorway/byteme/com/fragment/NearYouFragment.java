package remotedoorway.byteme.com.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.ChatAuthentication;
import remotedoorway.byteme.com.models.UserInfo;
import remotedoorway.byteme.com.services.GPSTracker;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearYouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearYouFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    List<UserInfo> userInfoList =new ArrayList<UserInfo>();
    List<UserInfo> tempuserInfoList =new ArrayList<UserInfo>();

    private List<ChatAuthentication> chatAuthenticationList=new ArrayList<ChatAuthentication>();
    ArrayAdapter<UserInfo> adapter;
    ListView list;


    private ProgressDialog progressDialog;


    List<String> alreadyFriendList = new ArrayList<String>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    GPSTracker gps;

    Location currentLocation;
    private OnFragmentInteractionListener mListener;

    public NearYouFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearYouFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearYouFragment newInstance(String param1, String param2) {
        NearYouFragment fragment = new NearYouFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity(),R.style.progressbarstyle); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined

        progressDialog.setMessage("Loading..");
        progressDialog.setTitle("Retrieving Nearer Users");

        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();


        currentLocation= new Location("");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        populateUserList();

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        int cnt=0;
        @Override
        public void onReceive(Context context, Intent intent) {

            Location l2=(Location) intent.getExtras().get("newlocation");

            if(l2.distanceTo(currentLocation)>1000)
            {
                uploadLocation();
                //Toast.makeText(getActivity(),"Yes it is updated",Toast.LENGTH_SHORT).show();
                populateUserList();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        populateUserList();


        adapter=new MyAdapter();
        list=(ListView) view.findViewById(R.id.lvfraghomenearusers);
        list.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                // No explanation needed, we can request the permission.


        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);


        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:

            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Please allow us to access your location to show nearer friends!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
                case 2: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        uploadLocation();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getActivity(), "Please allow us to access your location to show nearer friends!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request

    }


    @Override
    public void onStart() {
        super.onStart();




        adapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(),"Total users are:" + userInfoList.size(),Toast.LENGTH_LONG).show();
        //list.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(receiver,new IntentFilter("update"));


        adapter=new MyAdapter();
        list.setAdapter(adapter);
        uploadLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private class MyAdapter extends ArrayAdapter<UserInfo>
    {
        public MyAdapter() {
            super(getActivity().getBaseContext(),R.layout.nearyoufriendlistrowview, userInfoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.nearyoufriendlistrowview,parent,false);
            }


            final UserInfo currentUser= userInfoList.get(position);
            TextView tvusername=(TextView) itemview.findViewById(R.id.tvnearyouusername);
            TextView tvnearyouuserdevice=(TextView) itemview.findViewById(R.id.tvnearyouuserdevice);
            TextView tvnearyouusersex=(TextView) itemview.findViewById(R.id.tvnearyouusersex);
            ImageView imguserdp=(ImageView) itemview.findViewById(R.id.imgvnearyouuserdp);
            Button btnSendRequest=(Button)itemview.findViewById(R.id.imgbtnnearyousendreq);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.firebasestorageurl));

            StorageReference dpref = storageRef.child(currentUser.getDPURL());

            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(dpref)
                    .into(imguserdp);

            tvusername.setText(currentUser.getFullName());
            tvnearyouuserdevice.setText(currentUser.getDeviceInfo());
            tvnearyouusersex.setText(currentUser.getGender());
            btnSendRequest.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    FirebaseAuth auth= FirebaseAuth.getInstance();
                    DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("ChatAuthentication");
                    Map<String,Object> map = new HashMap<String, Object>();
                    String temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String,Object> map2 = new HashMap<String, Object>();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    String timestamp = (dateFormat.format(date));

                    map2.put("Timestamp",timestamp);
                    map2.put("UserID1",auth.getCurrentUser().getUid());
                    map2.put("UserID2",currentUser.getUserId());
                    map2.put("Validate1","1");
                    map2.put("Validate2","0");
                    message_root.updateChildren(map2);

                    populateUserList();
                }
            });


            return itemview;
        }
    }


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
                alreadyFriendList.clear();
                tempuserInfoList.clear();
                userInfoList.clear();

                if (dataSnapshot != null) {
                    for (DataSnapshot chatAuthenticationrows : dataSnapshot.getChildren()) {
                        ChatAuthentication obj1 = chatAuthenticationrows.getValue(ChatAuthentication.class);

                        //in future, change below userid with sharedpreference
                        if (obj1.getUserID1().equals(userid)) {
                            if(alreadyFriendList.indexOf(obj1.getUserID2())==-1) {
                                alreadyFriendList.add(obj1.getUserID2());
                            }
                        }
                    }
                    for (DataSnapshot chatAuthenticationrows : dataSnapshot.getChildren()) {
                        ChatAuthentication obj1 = chatAuthenticationrows.getValue(ChatAuthentication.class);

                        if (obj1.getUserID2().equals(userid)) {
                            if(alreadyFriendList.indexOf(obj1.getUserID1())==-1) {
                                alreadyFriendList.add(obj1.getUserID1());
                            }
                        }
                    }

                }

                Set<String> s = new HashSet<String>(alreadyFriendList);

                // to remove unnecessary repeating items
                alreadyFriendList.clear();
                alreadyFriendList=null;
                alreadyFriendList=new ArrayList<String>(s);



                // now lets load its friends
                // get friends information
                DatabaseReference UserInfoTable = FirebaseDatabase.getInstance().getReference().child("UserInfo");

                UserInfoTable.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return Transaction.success(mutableData);
                    }



                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        tempuserInfoList.clear();
                        if (dataSnapshot != null) {
                            for (DataSnapshot userinforows : dataSnapshot.getChildren()) {

                                UserInfo currentUser = userinforows.getValue(UserInfo.class);
                                currentUser.setUserId(userinforows.getKey().toString());

                                final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                                if(!currentUser.getUserId().equals(userid)) {


                                    // location based filtering
                                    double latitude=0;
                                    double longitude=0;
                                    gps = new GPSTracker(getActivity());


                                    if(gps.canGetLocation()) {
                                        latitude = gps.getLatitude();
                                        longitude = gps.getLongitude();
                                        Location loggedinUser = new Location("");
                                        loggedinUser.setLatitude(latitude);
                                        loggedinUser.setLongitude(longitude);

                                        Location currentUserLocation = new Location("");
                                        currentUserLocation.setLatitude(Double.parseDouble(currentUser.getLat()));
                                        currentUserLocation.setLongitude(Double.parseDouble(currentUser.getLong()));
                                        if(currentUserLocation.distanceTo(loggedinUser)<1000)
                                        {
                                            tempuserInfoList.add(currentUser);
                                        }
                                    }
                                }
                            }
                        }

                        Set<UserInfo> s = new HashSet<UserInfo>(tempuserInfoList);

                        // to remove unnecessary repeating items
                        tempuserInfoList.clear();
                        tempuserInfoList=null;
                        tempuserInfoList=new ArrayList<UserInfo>(s);



                        /*
                        for(int i=0;i<tempuserInfoList.size();i++)
                        {
                            Toast.makeText(getActivity(),tempuserInfoList.get(i).toString(),Toast.LENGTH_LONG).show();
                        }
                        */

                        // now filtering only non friendlist
                        userInfoList.clear();
                        for(int i=0;i<tempuserInfoList.size();i++)
                        {
                            int temp=0;
                            for(int j=0;j<alreadyFriendList.size();j++)
                            {
                                Log.e("SEE",tempuserInfoList.get(i).getUserId().toString()+ " " + alreadyFriendList.get(j).toString());
                                if(tempuserInfoList.get(i).getUserId().toString().equals(alreadyFriendList.get(j).toString()))
                                {
                                    temp=1;
                                    break;
                                }
                            }
                            if(temp==0)
                            {
                                //Toast.makeText(getActivity(),"I added:" + tempuserInfoList.get(i).toString(),Toast.LENGTH_LONG).show();
                                userInfoList.add(tempuserInfoList.get(i));
                            }
                        }

                        progressDialog.dismiss();
                        adapter = new MyAdapter();
                        list.setAdapter(adapter);
                        if(!(userInfoList.size()<=0)){
                            ((ImageView)getActivity().findViewById(R.id.ivfraghomenearusers)).setVisibility(View.GONE);
                            ((TextView)getActivity().findViewById(R.id.tvfraghomenearusers)).setVisibility(View.GONE);

                        }
                        else
                        {
                            ((TextView)getActivity().findViewById(R.id.tvfraghomenearusers)).setVisibility(View.VISIBLE);
                            ((ImageView)getActivity().findViewById(R.id.ivfraghomenearusers)).setVisibility(View.VISIBLE);
                        }
                    }
                });

            }


        });


    }


    public void uploadLocation()
    {
        double latitude=0;
        double longitude=0;
        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()) {


            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(uid);
            Map<String, Object> map = new HashMap<String, Object>();

            Map<String, Object> map2 = new HashMap<String, Object>();

            map2.put("Lat", "" + latitude);
            map2.put("Long", "" + longitude);

            root.updateChildren(map2);



            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);

            //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
