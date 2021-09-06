package com.myginee.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.model.Conversation;
import com.myginee.customer.model.Message;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.CommonMethod;
import com.myginee.customer.utils.GineePref;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog mProgressDialog;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private ListMessageAdapter adapter;
    private String roomId, idFriend;
    //    private ArrayList<CharSequence> idFriend;
    private Conversation conversation;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;
    ImageView imgCallToAdmin, imgBack;
    private RecyclerView rvChat;
    JSONArray chatArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mProgressDialog = new ProgressDialog(ChatActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Intent intentData = getIntent();
        idFriend = intentData.getStringExtra("agent_id");
//        idFriend = GineePref.getAgent(ChatActivity.this);
        roomId = intentData.getStringExtra("_id");

        conversation = new Conversation();

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        imgCallToAdmin = (ImageView) findViewById(R.id.imgCallToAdmin);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        btnSend.setOnClickListener(this);
        imgCallToAdmin.setOnClickListener(this);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
        if (idFriend != null) {
//            getSupportActionBar().setTitle(nameFriend);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvChat = (RecyclerView) findViewById(R.id.rvChat);
            rvChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, conversation, bitmapAvataFriend, bitmapAvataUser);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        if (!GineePref.getAccessToken(ChatActivity.this).equals("")) {
                            retrieveChatAPICall(roomId);
                            HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                            Message newMessage = new Message();
                            newMessage.from_user = (String) mapMessage.get("from_user");
                            newMessage.to_user = (String) mapMessage.get("to_user");
                            if (newMessage.from_user.equals(GineePref.getUSERID(ChatActivity.this)) || newMessage.to_user.equals(GineePref.getUSERID(ChatActivity.this))) {
                                newMessage.message = (String) mapMessage.get("message");
                                newMessage.sent_at = (long) mapMessage.get("sent_at");
                                conversation.getListMessageData().add(newMessage);
                                adapter.notifyDataSetChanged();
                                linearLayoutManager.scrollToPosition(conversation.getListMessageData().size() - 1);
                            }
                        }

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            rvChat.setAdapter(adapter);
        }


    }

    private JSONArray createMsgArray(String msg, String sentTime) {

        JSONObject jsonService = new JSONObject();
        try {
            jsonService.put("message", msg);
            jsonService.put("sent_at", sentTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatArray.put(jsonService);
    }

    private void callAPIForSendChat(String msg, String time) {
        createMsgArray(msg, time);
        GineePref.getSharedPreferences(ChatActivity.this);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("order_id", roomId);
            paramObject.put("agent_id", idFriend);
            paramObject.put("messages", chatArray);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveChat(GineePref.getAccessToken(ChatActivity.this),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {

                            } else {
                                Toast.makeText(ChatActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void retrieveChatAPICall(String id) {
        GineePref.getSharedPreferences(ChatActivity.this);
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().retrieveChatByOrderId(GineePref.getAccessToken(ChatActivity.this),
                id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray item = jsonObject.getJSONArray("data");
                            if (item.length() > 0) {

                            }

                        } else {
                            Toast.makeText(ChatActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ChatActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                if (!GineePref.getAccessToken(ChatActivity.this).equals("")) {
                    editWriteMessage.setText("");
                    callAPIForSendChat(content, String.valueOf(System.currentTimeMillis()));
                    Message newMessage = new Message();
                    newMessage.message = content;
                    newMessage.from_user = GineePref.getUSERID(ChatActivity.this);
                    newMessage.to_user = idFriend;
                    newMessage.sent_at = System.currentTimeMillis();
                    FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                    chatArray =  new JSONArray();
//                FirebaseDatabase.getInstance().getReference().child("message/" + idFriend+"_"+roomId).push().setValue(newMessage);
                }


            }
        } else if (view.getId() == R.id.imgCallToAdmin) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + "+919893778304"));
            startActivity(callIntent);
        }
    }

    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private Conversation consersation;
        private HashMap<String, Bitmap> bitmapAvata;
        private HashMap<String, DatabaseReference> bitmapAvataDB;
        private Bitmap bitmapAvataUser;

        public ListMessageAdapter(Context context, Conversation consersation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser) {
            this.context = context;
            this.consersation = consersation;
            this.bitmapAvata = bitmapAvata;
            this.bitmapAvataUser = bitmapAvataUser;
            bitmapAvataDB = new HashMap<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_raw_agent, parent, false);
                return new ItemMessageFriendHolder(view);
            } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_raw_customer, parent, false);
                return new ItemMessageUserHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemMessageFriendHolder) {
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).message);
                ((ItemMessageFriendHolder) holder).textTime.setText(CommonMethod.getDate(consersation.getListMessageData().get(position).sent_at, "hh:mm a"));

            } else if (holder instanceof ItemMessageUserHolder) {
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).message);
                ((ItemMessageUserHolder) holder).textTime.setText(CommonMethod.getDate(consersation.getListMessageData().get(position).sent_at, "hh:mm a"));

                if (bitmapAvataUser != null) {
//                    ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return consersation.getListMessageData().get(position).from_user.equals(GineePref.getUSERID(ChatActivity.this)) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
        }

        @Override
        public int getItemCount() {
            return consersation.getListMessageData().size();
        }
    }

    class ItemMessageUserHolder extends RecyclerView.ViewHolder {
        public TextView txtContent, textTime;

        public ItemMessageUserHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
        }
    }

    class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
        public TextView txtContent, textTime;

        public ItemMessageFriendHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentAgent);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
        }
    }
}
