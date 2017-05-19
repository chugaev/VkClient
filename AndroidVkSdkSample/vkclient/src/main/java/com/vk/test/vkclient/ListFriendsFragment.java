package com.vk.test.vkclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListFriendsFragment extends ListFragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    ArrayList<String> mListNameFriends = null;
    ArrayList<String> mListPhotoFriends = new ArrayList<>();
    ArrayList<Long> mIds = new ArrayList<>();

    Account mAccount;
    Api mApi;

    static ListFriendsFragment newInstance(int page) {
        ListFriendsFragment pageFragment = new ListFriendsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount = Account.get(getContext());
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        GetterFriendsTask getterFriendsTask = new GetterFriendsTask();
        getterFriendsTask.execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_friends, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("ID", mIds.get(position));
        startActivity(intent);
    }

    class GetterFriendsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mListNameFriends = getListFriends();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mListNameFriends != null) {
                MyListAdapter myListAdapter = new MyListAdapter(getActivity(),
                        R.layout.list_friends, mListNameFriends);
                setListAdapter(myListAdapter);
            }
        }
    }

    private ArrayList<String> getListFriends() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<User> users = null;
        try {
            users = mApi.getFriends(mAccount.getUserId(), "sex,photo_100", null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.i("aasdd", (users == null) + "");
        for (User user : users) {
            arrayList.add(user.first_name + " " + user.last_name);
            mIds.add(user.uid);
            mListPhotoFriends.add(user.photo_medium_rec);
        }
        return arrayList;
    }

    public class MyListAdapter extends ArrayAdapter<String> {

        private Context mContext;

        public MyListAdapter(Context context, int textViewResourceId,
                             ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.friends_row, parent, false);
            TextView catNameTextView = (TextView) row.findViewById(R.id.textViewName);
            catNameTextView.setText(mListNameFriends.get(position));
            CircleImageView iconImageView = (CircleImageView) row.findViewById(R.id.imageViewIcon);
            String url = mListPhotoFriends.get(position);
            iconImageView.setImageResource(R.drawable.ic_menu_camera);
            new ListFriendsFragment.DownloadImageTask(iconImageView).execute(url);
            // Присваиваем значок
            return row;
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
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
