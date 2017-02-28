package remotedoorway.byteme.com.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.Messages;
import remotedoorway.byteme.com.models.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;

    private String user_name,room_name;
    private DatabaseReference root;
    private String temp_key;

    String currentLoginUserId;
    String currentChatAuthID;

    private List<Messages> messagesList=new ArrayList<Messages>();
    UserInfo chatFriendUserInfo;


    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);



        btn_send_msg = (Button) view.findViewById(R.id.btn_send);
        input_msg = (EditText) view.findViewById(R.id.msg_input);
        chat_conversation = (TextView) view.findViewById(R.id.textView);

        //user_name = getIntent().getExtras().get("user_name").toString();
        //room_name = getIntent().getExtras().get("room_name").toString();
        user_name= "";
        room_name = "";

        chatFriendUserInfo =(UserInfo)getArguments().getSerializable("friendobj");
        currentChatAuthID=getArguments().get("currentChatAuthID").toString();

        currentLoginUserId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        //Toast.makeText(this, currentChatAuthID,Toast.LENGTH_LONG).show();


        root = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentChatAuthID);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String timestamp = (dateFormat.format(date));
                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("UserId",currentLoginUserId);
                map2.put("Content",input_msg.getText().toString());

                map2.put("Timestamp",timestamp);
                message_root.updateChildren(map2);


                input_msg.setText("");
            }
        });

        //root=root.getParent();
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });











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
       // if (context instanceof OnFragmentInteractionListener) {
       //     mListener = (OnFragmentInteractionListener) context;
       // } else {
       //     throw new RuntimeException(context.toString()
       //             + " must implement OnFragmentInteractionListener");
       // }
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

    private String chat_msg,chat_user_name;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        /*
        for (DataSnapshot messagesrows: dataSnapshot.getChildren()) {

            Log.e("f",messagesrows.toString());
            Messages obj1 = messagesrows.getValue(Messages.class);
            messagesList.add(obj1);
            chat_msg = (String) obj1.getContent();
            chat_conversation.append(chat_msg + "\n");
        }*/



        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){
            Messages m1=new Messages();

            String content= (String) ((DataSnapshot)i.next()).getValue();
            String ts=(String) ((DataSnapshot)i.next()).getValue();
            String userid = (String) ((DataSnapshot)i.next()).getValue();

            m1=new Messages(content,userid,ts);
            messagesList.add(m1);
            Log.e("CHECKER", userid + " and " + currentLoginUserId);
            if(userid.equals(currentLoginUserId)) {

                chat_conversation.append("YOU: " + content + " \n");
            }
            else {
                chat_conversation.append(chatFriendUserInfo.getFullName() + ": " + content + " \n");
            }
        }

    }

}
