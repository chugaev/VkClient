package com.perm.kate.api.sample;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends Activity {
    
    private final int REQUEST_LOGIN = 1;
    
    Button authorizeButton;
    Button logoutButton;
    Button postButton;
    EditText messageEditText;
    Button showMyFriends;
    EditText mEditText;
    
    Account account=new Account();
    Api api;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupUI();
        
        //Восстановление сохранённой сессии
        account.restore(this);
        
        //Если сессия есть создаём API для обращения к серверу
        if(account.access_token != null) {
            api = new Api(account.access_token, Constants.API_ID);
        }
        showButtons();
    }

    private void setupUI() {
        authorizeButton=(Button)findViewById(R.id.authorize);
        logoutButton=(Button)findViewById(R.id.logout);
        showMyFriends = (Button) findViewById(R.id.show_friends);
        showMyFriends.setOnClickListener(friendsClick);
        postButton=(Button)findViewById(R.id.post);
        messageEditText=(EditText)findViewById(R.id.message);
        authorizeButton.setOnClickListener(authorizeClick);
        logoutButton.setOnClickListener(logoutClick);

        postButton.setOnClickListener(postClick);

        mEditText = (EditText) findViewById(R.id.list_friends);

    }
    
    private OnClickListener authorizeClick=new OnClickListener(){
        @Override
        public void onClick(View v) {
            startLoginActivity();
        }
    };
    
    private OnClickListener logoutClick=new OnClickListener(){
        @Override
        public void onClick(View v) {
            logOut();
        }
    };

    private OnClickListener friendsClick=new OnClickListener() {
        @Override
        public void onClick(View view) {
            getListFriends();
        }
    };
    
    private OnClickListener postClick=new OnClickListener(){
        @Override
        public void onClick(View v) {
            postToWall();
        }
    };
    
    private void startLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                //авторизовались успешно 
                account.access_token=data.getStringExtra("token");
                account.user_id=data.getLongExtra("user_id", 0);
                account.save(MainActivity.this);
                api=new Api(account.access_token, Constants.API_ID);
                showButtons();
            }
        }
    }

    private void getListFriends() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(new Callable<String>() {
           public String call() {
               String string = null;
               ArrayList<User> users = null;
               try {
                   users = api.getFriends(57583039L, "domain,sex", null, null, null);
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
               for (User user : users) {
                   string += user.first_name + " ";
               }
               return string;
           }
        });
        try {
            mEditText.setText(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void postToWall() {
        //Общение с сервером в отдельном потоке чтобы не блокировать UI поток
        new Thread(){
            @Override
            public void run() {
                try {
                    String text = messageEditText.getText().toString();
                    api.createWallPost(account.user_id, text, null, null, false, false, false, null, null, null, 0L, null, null);
                    //Показать сообщение в UI потоке
                    runOnUiThread(successRunnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    Runnable successRunnable=new Runnable(){
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Запись успешно добавлена", Toast.LENGTH_LONG).show();
        }
    };
    
    private void logOut() {
        api=null;
        account.access_token=null;
        account.user_id=0;
        account.save(MainActivity.this);
        showButtons();
    }
    
    void showButtons(){
        if(api != null){
            authorizeButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            postButton.setVisibility(View.VISIBLE);
            messageEditText.setVisibility(View.VISIBLE);
            showMyFriends.setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.VISIBLE);
        } else {
            authorizeButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            postButton.setVisibility(View.GONE);
            messageEditText.setVisibility(View.GONE);
            showMyFriends.setVisibility(View.GONE);
            mEditText.setVisibility(View.GONE);
        }
    }
}