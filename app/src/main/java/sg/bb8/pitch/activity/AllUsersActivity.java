package sg.bb8.pitch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import sg.bb8.pitch.R;
import sg.bb8.pitch.adapter.ChatRoomsAdapter;
import sg.bb8.pitch.app.Config;
import sg.bb8.pitch.app.EndPoints;
import sg.bb8.pitch.app.MyApplication;
import sg.bb8.pitch.gcm.GcmIntentService;
import sg.bb8.pitch.gcm.NotificationUtils;
import sg.bb8.pitch.helper.SimpleDividerItemDecoration;
import sg.bb8.pitch.model.ChatRoom;
import sg.bb8.pitch.model.Message;
import sg.bb8.pitch.model.User;

public class AllUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
    }
}
