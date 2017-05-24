package com.vk.test.vkclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.perm.kate.api.Api;
import com.perm.kate.api.Message;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessagesActivity extends AppCompatActivity {

    Account mAccount;
    Api mApi;

    EmojiconEditText emojiconEditText;
    ImageView emojiButton;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Message> messages = new ArrayList<>();
    private long Id;
    private String name = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);
        mAccount = Account.get(this);
        mApi = new Api(mAccount.getAccessToken(), mAccount.getUserId() + "");
        Intent intent = getIntent();
        Id = (Long) intent.getSerializableExtra("ID");
        name = intent.getStringExtra("NAME");
        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetterMessagesTask getterMessagesTask = new GetterMessagesTask();
        getterMessagesTask.execute();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layoutManager);
        setTitle(name);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = emojiconEditText.getText().toString();
                SenderMesssages senderMesssages = new SenderMesssages(string);
                senderMesssages.execute();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getBaseContext(), DialogStatsActivity.class);
                startActivity(intent);
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    class GetterMessagesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                messages = mApi.getMessagesHistory(Id, 0, mAccount.getUserId(), 0L, 20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter = new RecyclerAdapter(messages);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    class SenderMesssages extends AsyncTask<Void, Void, Void> {

        String mString;

        SenderMesssages(String string) {
            mString = string;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mApi.sendMessage(Id, 0, mString, null, null, null, null, null, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.body = mString;
            messages.add(0, message);
            emojiconEditText.setText("");
            mAdapter.notifyDataSetChanged();
        }
    }
}
