package com.vk.test.vkclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.perm.kate.api.Group;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListGroupsFragment extends ListFragment {

    Account mAccount;
    Api mApi;

    ArrayList<String> mListNameGroups = null;
    ArrayList<String> mListPhotoGroups = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount = Account.get(getContext());
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        ListGroupsFragment.GetterGroupsTask getterFriendsTask = new ListGroupsFragment.GetterGroupsTask();
        getterFriendsTask.execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Группы");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_groups, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
    }

    class GetterGroupsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mListNameGroups = getListGroup();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            MyListAdapter myListAdapter = new MyListAdapter(getActivity(),
                    R.layout.list_groups, mListNameGroups);
            setListAdapter(myListAdapter);
        }
    }

    private ArrayList<String> getListGroup() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Group> groups = null;
        try {
            groups = mApi.getUserGroups(mAccount.getUserId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Group group : groups) {
            arrayList.add(group.name);
            if (group.photo != null) {
                mListPhotoGroups.add(group.photo_medium);
            }
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
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.friends_row, parent, false);
            TextView catNameTextView = (TextView) row.findViewById(R.id.textViewName);
            catNameTextView.setText(mListNameGroups.get(position));
            CircleImageView iconImageView = (CircleImageView) row.findViewById(R.id.imageViewIcon);
            String url = mListPhotoGroups.get(position);
            iconImageView.setImageResource(R.drawable.ic_menu_camera);
            new DownloadImageTask(iconImageView).execute(url);
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
