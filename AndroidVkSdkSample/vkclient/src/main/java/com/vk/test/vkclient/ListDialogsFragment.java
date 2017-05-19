package com.vk.test.vkclient;

import android.content.Context;
import android.content.Intent;
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
import de.hdodenhof.circleimageview.CircleImageView;

import com.perm.kate.api.Api;
import com.perm.kate.api.Message;
import com.perm.kate.api.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lenka on 19.04.17.
 */

public class ListDialogsFragment extends ListFragment {
    Account mAccount;
    Api mApi;

    ArrayList<Long> mIds = new ArrayList<>();
    ArrayList<String> mStrings = new ArrayList<>();
    ArrayList<Message> mDialogs = null;
    HashMap<Long, User> mUsers = new HashMap<>();



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount = Account.get(getContext());
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        GetterDialogsTask getterDialogsTask = new GetterDialogsTask();
        getterDialogsTask.execute();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Сообщения");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_dialog, container, false);
        return inflater.inflate(R.layout.list_dialog, container, false);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra("ID", mIds.get(position));
        if (mDialogs.get(position).title.equals("...")) {
            User u = mUsers.get(mIds.get(position));
            intent.putExtra("NAME", u.first_name + " " + u.last_name);
        } else {
            intent.putExtra("NAME", mDialogs.get(position).title);
        }
        startActivity(intent);
    }

    class GetterDialogsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDialogs = getListDialog();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            MyListAdapter myListAdapter = new MyListAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, mStrings);
            setListAdapter(myListAdapter);
        }
    }

    private ArrayList<Message> getListDialog() {
        ArrayList<Message> dialogs = null;
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        try {
            dialogs = mApi.getMessagesDialogs(0, 20, null, null);
            for (Message message : dialogs) {
                if (message.title.equals("...")) {
                    ids.add(message.uid);
                }
            }
            users = mApi.getProfiles(ids, null, "photo_100", null, null, null);
            for (int i = 0; i < ids.size(); i++) {
                mUsers.put(ids.get(i), users.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Message message : dialogs) {
            mIds.add(message.uid);
            mStrings.add(message.body);
        }
        return dialogs;
    }

    public class MyListAdapter extends ArrayAdapter<String> {

        private Context mContext;

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public MyListAdapter(Context context, int textViewResourceId,
                             ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            super.getView(position, convertView, parent);

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.dialog_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.name_dialog);
            TextView content = (TextView) row.findViewById(R.id.content_dialog);
            CircleImageView imageView = (CircleImageView)  row.findViewById(R.id.imageViewIcon);
            imageView.setImageResource(R.drawable.ic_menu_camera);
            Message message = mDialogs.get(position);
            if (message.title.equals("...")) {
                name.setText(mUsers.get(message.uid).first_name + " " + mUsers.get(message.uid).last_name);
                if (mUsers.get(message.uid).photo_medium_rec != null) {
                new DownloadImageTask(imageView).execute(mUsers.get(message.uid).photo_medium_rec);
                }
            } else {
                name.setText(message.title);
                if (message.photo100 != null) {
                    new DownloadImageTask(imageView).execute(message.photo100);
                }
            }
            content.setText(mDialogs.get(position).body);

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
