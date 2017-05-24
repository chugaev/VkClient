package com.vk.test.vkclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;

import java.io.InputStream;
import java.util.ArrayList;


public class MyProfileFragment extends Fragment {

    Account mAccount;
    Api mApi;

    User user;
    ImageView mImageProfile;
    TextView mOnlineStatus;
    TextView mName;
    TextView mUserStatus;
    TextView mNameNav;
    TextView mUserStatusNav;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount = Account.get(getContext());
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        GetterProfileInfoTask getterProfileInfoTask = new GetterProfileInfoTask();
        getterProfileInfoTask.execute();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Профиль");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile, container, false);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        mNameNav = (TextView) header.findViewById(R.id.text_nav);
        mUserStatusNav = (TextView) header.findViewById(R.id.text_status);
        mImageProfile = (ImageView) view.findViewById(R.id.image_profile);
        mOnlineStatus = (TextView) view.findViewById(R.id.online_status);
        mName = (TextView) view.findViewById(R.id.user_name);
        mUserStatus = (TextView) view.findViewById(R.id.user_status);
        Button friend_button = (Button) view.findViewById(R.id.add_friend);
        friend_button.setVisibility(View.GONE);
        Button message_Button = (Button) view.findViewById(R.id.send_message);
        message_Button.setVisibility(View.GONE);
        return view;
    }

    class GetterProfileInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Long> arrayList = new ArrayList<>();
            arrayList.add(mAccount.getUserId());
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
            mNameNav.setText(user.first_name + " " + user.last_name);
            mUserStatus.setText(user.status);
            mUserStatusNav.setText(user.status);

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
