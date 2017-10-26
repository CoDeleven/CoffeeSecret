package com.dhy.coffeesecret;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.dhy.coffeesecret.ui.MainActivity;
import com.dhy.coffeesecret.ui.common.views.DividerDecoration;
import com.dhy.coffeesecret.ui.mine.adapter.AccountAdapter;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SystemStatusBarUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, RegFragment.OnSuccessListener, LoginByTelFragment.OnSuccessListener {

    private static final int WAIT = 0;
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private static final String TAG = "LoginActivity";

    private EditText usernameView;
    private EditText passwordView;
    private ImageButton clearButton;
    private ImageButton showButton;
    private PopupWindow accountWindow;
    private Set<String> accountSet;
    private ArrayList<String> accounts;
    private MyApplication myApplication;
    private Button regButton;

    private Handler mHandler = new Handler(){

        private ProgressDialog progressDialog;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WAIT:
                    progressDialog = ProgressDialog.show(LoginActivity.this, null, "登录中...");
                    break;
                case SUCCESS:
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    break;
                case ERROR:
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    Snackbar.make(usernameView,"登录失败，请检测用户名密码。",Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SystemStatusBarUtils.steepToolBar(this);
        myApplication = (MyApplication) getApplication();
        new Thread(){
            @Override
            public void run() {
                try {
                    loginByToken();
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(SUCCESS);
                    e.printStackTrace();
                }
            }
        }.start();

        usernameView = (EditText) findViewById(R.id.et_username);
        passwordView = (EditText) findViewById(R.id.et_password);
        clearButton = (ImageButton) findViewById(R.id.iv_clear);
        showButton = (ImageButton) findViewById(R.id.iv_show_account);
        regButton = (Button) findViewById(R.id.btn_reg);
        init();
    }


    private void loginByToken() throws IOException {
        String token = myApplication.getToken();
        if(token != null){
            // 请求登录 登录成功则将返回的token存入
            mHandler.sendEmptyMessage(WAIT);
            String url = UrlLogin.loginByToken(token);
            String result = HttpUtils.getStringFromServer(url,null,null);
            if(result!= null && !result.startsWith("error")){
                loginSuccess(result);
            }else {
                mHandler.sendEmptyMessage(SUCCESS);
            }
        }
    }

    private void loginByTel(String tel) throws IOException {
        String result = HttpUtils.getStringFromServer(tel,null,null);
        if(result!= null && !result.startsWith("error")){
            loginSuccess(result);
        }else {
            mHandler.sendEmptyMessage(ERROR);
        }
    }

    private void loginSuccess(String result) {
        mHandler.sendEmptyMessage(SUCCESS);
        myApplication.setToken(result);
        Intent intent ;
        // 不需要跳到第一次连接的界面
        intent= new Intent(this,MainActivity.class);

        startActivity(intent);
        finish();
    }

    private void loginByPw(String tel,String password) throws IOException {
        mHandler.sendEmptyMessage(WAIT);
        String url = UrlLogin.loginByPw(tel, password);
        Log.d(TAG,"url:"+url);
        String result = HttpUtils.getStringFromServer(url,null,null);
        if(result!= null && !result.startsWith("error")){
            loginSuccess(result);
        }else {
            mHandler.sendEmptyMessage(ERROR);
        }
    }

    private void init() {
        accountSet = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        accountSet = sharedPreferences.getStringSet("account", accountSet);
        String lastUser = sharedPreferences.getString("last_user", "");
        usernameView.setText(lastUser);

        accounts = new ArrayList<>(accountSet);
        AccountAdapter adapter = new AccountAdapter(accounts, this, new AccountAdapter.OnItemDelete() {
            @Override
            public void onItemDelete(int position) {
                String s = accounts.get(position);
                accountSet.remove(s);
                accounts.clear();
                accounts.addAll(accountSet);
                SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                if(s.equals(preferences.getString("last_user",""))){
                    edit.remove("last_user");
                }
                if(accountSet.size() == 0){
                    edit.clear();
                }else {
                    edit.putStringSet("account", accountSet);
                }
                edit.commit();
                accountWindow.dismiss();
            }
        });
        initPopWindow(adapter);
        findViewById(R.id.btn_tel).setOnClickListener(this);
        clearButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
        regButton.setOnClickListener(this);
        usernameView.addTextChangedListener(this);
        adapter.setOnItemClick(new AccountAdapter.OnItemClick() {
            @Override
            public void onItemClick(String str) {
                usernameView.setText(str);
                accountWindow.dismiss();
            }
        });
    }

    private void initPopWindow(AccountAdapter adapter) {
        View content = getLayoutInflater().inflate(R.layout.ppw_account, null);
        RecyclerView rv = (RecyclerView) content.findViewById(R.id.rv_account);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerDecoration(this));
        accountWindow = new PopupWindow(content, WindowManager.LayoutParams.MATCH_PARENT, WRAP_CONTENT);
        accountWindow.setBackgroundDrawable(new ColorDrawable());
        accountWindow.setOutsideTouchable(true);
        accountWindow.setFocusable(true);
    }

    public void login(View view) {
        final String username = usernameView.getText().toString().trim();
        final String password = passwordView.getText().toString().trim();
        accountSet.add(username);
        SharedPreferences.Editor edit = getSharedPreferences("account", MODE_PRIVATE).edit();
        edit.putStringSet("account", accountSet);
        edit.putString("last_user",username);
        edit.commit();
        new Thread(){
            @Override
            public void run() {
                try {
                    loginByPw(username,password);
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(ERROR);
                    e.printStackTrace();
                }
            }
        } .start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear:
                usernameView.setText("");
                break;
            case R.id.iv_show_account:
                accountWindow.showAsDropDown(usernameView);
                break;
            case R.id.btn_reg:
                showRegFragment();
                break;
            case R.id.btn_tel:
                showTelFragment();
                break;
        }
    }

    private void showTelFragment() {
        LoginByTelFragment telFragment = LoginByTelFragment.newInstance();
        telFragment.setOnSuccessListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.in_from_bottom,R.anim.out_to_bottom,R.anim.in_from_bottom,R.anim.out_to_bottom)
                .add(R.id.layout,telFragment)
                .addToBackStack(telFragment.getClass().getSimpleName())
                .commit();
    }

    private void showRegFragment() {
        RegFragment regFragment = RegFragment.newInstance();
        regFragment.setOnSuccessListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.in_from_bottom,R.anim.out_to_bottom,R.anim.in_from_bottom,R.anim.out_to_bottom)
                .add(R.id.layout,regFragment)
                .addToBackStack("regFragment")
                .commit();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        passwordView.setText("");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.length() == 0){
            clearButton.setVisibility(View.GONE);
        }else {
            clearButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRegSuccess(String result,String tel) {
        accountSet.add(tel);
        SharedPreferences.Editor edit = getSharedPreferences("account", MODE_PRIVATE).edit();
        edit.putStringSet("account", accountSet);
        edit.putString("last_user",tel);
        edit.commit();
        loginSuccess(result);
    }

    @Override
    public void onLoginSuccess(String result, String tel) {
        accountSet.add(tel);
        SharedPreferences.Editor edit = getSharedPreferences("account", MODE_PRIVATE).edit();
        edit.putStringSet("account", accountSet);
        edit.putString("last_user",tel);
        edit.commit();
        loginSuccess(result);
    }
}
