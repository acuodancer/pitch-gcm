package sg.bb8.pitch.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.bb8.pitch.R;
import sg.bb8.pitch.adapter.ChatRoomThreadAdapter;
import sg.bb8.pitch.app.Config;
import sg.bb8.pitch.app.EndPoints;
import sg.bb8.pitch.app.MyApplication;
import sg.bb8.pitch.gcm.NotificationUtils;
import sg.bb8.pitch.model.Message;
import sg.bb8.pitch.model.User;

public class ViewProfileActivity extends AppCompatActivity {

    private String TAG = ViewProfileActivity.class.getSimpleName();

    private TextView userName, userGender, userRequestStatus;
    private Button btnRequest, btnAccept;
    private User currentUser;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        userName = (TextView) findViewById(R.id.user_name_text);
        userGender = (TextView) findViewById(R.id.user_gender_text);
        userRequestStatus = (TextView) findViewById(R.id.user_requested_status_text);
        btnAccept = (Button) findViewById(R.id.accept_btn);
        btnRequest = (Button) findViewById(R.id.request_btn);
        currentUser = MyApplication.getInstance().getPrefManager().getUser();
        intent = getIntent();
        final String targetUserName = intent.getStringExtra("target_user_name");
        final String targetUserId = intent.getStringExtra("target_user_id");
        final String targetPendingRequestId = intent.getStringExtra("target_user_pending_request_id");
        userName.setText("Name: " + targetUserName);
        // Get gender randomly for now
        if (Integer.parseInt(targetUserId) % 2 == 0) {
            userGender.setText("Gender: Male");
        } else {
            userGender.setText("Gender: Female");
        }
        // Show requested status if user has requested target user
        if (currentUser.getPending_request_id().equals(targetUserId)) {
            userRequestStatus.setText("Requested: Yes");
        } else {
            userRequestStatus.setText("Requested: No");
        }
        // show accept button if target user has requested user
        if (targetPendingRequestId.equals(currentUser.getId())) {
            btnAccept.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "This user has requested for you!" , Toast.LENGTH_LONG).show();
        } else {
            btnAccept.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "This user has not requested for you!" , Toast.LENGTH_LONG).show();
        }

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(targetUserId);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest(targetUserId, targetUserName);
            }
        });
    }

    private void sendRequest(String targetUserId) {
        currentUser.setPending_request_id(targetUserId);
        updateUserPendingRequestId(currentUser.getId().toString(), targetUserId);
    }

    private void acceptRequest(String targetUserId, String targetUserName) {
        int currentPrivateId = MyApplication.getInstance().getPrefManager().getCurrentPrivateId();
        currentPrivateId++;
        updateUserPrivateRoomId(currentUser.getId().toString(), Integer.toString(currentPrivateId));
        updateUserPrivateRoomId(targetUserId, Integer.toString(currentPrivateId));
        String roomName = "pcr_" + currentUser.getName() + "_" + targetUserName;
        createPrivateChatRoom(Integer.toString(currentPrivateId), roomName);
        MyApplication.getInstance().getPrefManager().setCurrentPrivateId(++currentPrivateId);
    }

    private void updateUserPrivateRoomId(String userId, String roomId) {

        final String privateRoomId = roomId;

        String endPoint = EndPoints.USER_PRIVATE_ROOM.replace("_ID_", userId);

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(getApplicationContext(), "Private Room ID Updated Successfully!" , Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to update private room id to selected user. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("private_room_id", privateRoomId);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void updateUserPendingRequestId(String userId, String requestId) {

        final String pendingRequestId = requestId;

        String endPoint = EndPoints.USER_PENDING_REQUEST.replace("_ID_", userId);

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(getApplicationContext(), "Pending Request ID Updated Successfully!" , Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to update private room id to selected user. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pending_request_id", pendingRequestId);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void createPrivateChatRoom(String roomId, String roomName) {

        final String chatRoomId = roomId;
        final String chatRoomName = roomName;

        String endPoint = EndPoints.CHAT_ROOM_CREATE;

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(getApplicationContext(), "Private Chat Room Created Succesfully!" , Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to create private chat room. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("chat_room_id", chatRoomId);
                params.put("chat_room_name", chatRoomName);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
