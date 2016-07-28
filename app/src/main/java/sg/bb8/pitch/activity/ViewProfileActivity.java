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
        String targetUserName = intent.getStringExtra("target_user_name");
        final String targetUserId = intent.getStringExtra("target_user_id");
        String targetPendingRequestId = intent.getStringExtra("target_user_pending_request_id");
        userName.setText("Name: " + targetUserName);

        if (Integer.parseInt(targetUserId) % 2 == 0) {
            userGender.setText("Gender: Male");
        } else {
            userGender.setText("Gender: Female");
        }

        if (currentUser.getPending_request_id().equals(targetUserId)) {
            userRequestStatus.setText("Requested: Yes");
        } else {
            userRequestStatus.setText("Requested: No");
        }

        if (targetPendingRequestId.equals(currentUser.getId())) {
            btnAccept.setVisibility(View.VISIBLE);
        } else {
            btnAccept.setVisibility(View.INVISIBLE);
        }

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(targetUserId);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest(targetUserId);
            }
        });
    }

    private void sendRequest(String targetUserId) {
        currentUser.setPending_request_id(targetUserId);

    }

    private void acceptRequest(String targetUserId) {

    }
}
