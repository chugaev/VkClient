package com.vk.test.vkclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.perm.kate.api.Api;
import com.perm.kate.api.Message;
import com.perm.kate.api.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class DialogStatsFragment extends ListFragment {

    Account mAccount;
    Api mApi;

    ArrayList<Long> mIds = new ArrayList<>();
    ArrayList<String> mStrings = new ArrayList<>();
    ArrayList<Message> mDialogs = null;
    HashMap<Long, User> mUsers = new HashMap<>();
    TreeMap<Integer, String> mCountMessages = new TreeMap<>();

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
        getActivity().setTitle("Статистика по диалогам");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_stats_dialog, null);
        return inflater.inflate(R.layout.list_stats_dialog, null);
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

            int time = 0;
            for (long key : mUsers.keySet()) {
                int count = mApi.getCountMessagesHistory(key, 0);
                User user = mUsers.get(key);
                mCountMessages.put(count, user.first_name + " " + user.last_name);
                time++;
                if (time % 3 == 0) {
                    synchronized(this) {
                        wait(1000);
                    }
                }
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
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row_stats_dialog, parent, false);
            Set<Integer> set = mCountMessages.keySet();
            int i = 0;
            int count = 0;
            for (int s : set) {
                if (i == mCountMessages.size() - position) {
                    count = s;
                    break;
                }
                i++;
            }
            String string = mCountMessages.get(count);
            TextView name = (TextView) row.findViewById(R.id.name);
            name.setText(string + "");
            TextView countName = (TextView) row.findViewById(R.id.count_mes);
            countName.setText(count + "");
            return row;
        }
    }
}
