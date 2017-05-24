package com.vk.test.vkclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;

import java.io.InputStream;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    Account mAccount;
    Api mApi;

    User user;
    ImageView mImageProfile;
    TextView mOnlineStatus;
    TextView mName;
    TextView mUserStatus;
    Button mAddFriend;
    Button mRemoveFriend;
    Button mSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mAccount = Account.get(null);
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        mImageProfile = (ImageView) findViewById(R.id.image_profile);
        mOnlineStatus = (TextView) findViewById(R.id.online_status);
        mName = (TextView) findViewById(R.id.user_name);
        mUserStatus = (TextView) findViewById(R.id.user_status);
        mAddFriend = (Button) findViewById(R.id.add_friend);
        mRemoveFriend = (Button) findViewById(R.id.remove_friend);
        mSendMessage = (Button) findViewById(R.id.send_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        long id = (Long) intent.getSerializableExtra("ID");
        GetterProfileInfoTask getterProfileInfoTask = new GetterProfileInfoTask(id);
        getterProfileInfoTask.execute();
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MessagesActivity.class);
                intent.putExtra("ID", user.uid);
                intent.putExtra("NAME", user.first_name + " " + user.last_name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    class GetterProfileInfoTask extends AsyncTask<Void, Void, Void> {

        long id;

        public GetterProfileInfoTask(long id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Long> arrayList = new ArrayList<>();
            arrayList.add(id);
            try {
                user =  mApi.getProfiles(arrayList, null, "photo_200,online,status" , null, null, null).get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (user.online) {
                mOnlineStatus.setText("Online");
            } else {
                mOnlineStatus.setText("Offline");
            }
            mName.setText(user.first_name + " " + user.last_name);
            mUserStatus.setText(user.status);
            if (user.friend_status == 3) {
                mRemoveFriend.setVisibility(View.VISIBLE);
            } else if (user.friend_status == 0) {
                mRemoveFriend.setVisibility(View.VISIBLE);
            }
            new DownloadImageTask(mImageProfile).execute(user.photo_200);

            super.onPostExecute(result);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
