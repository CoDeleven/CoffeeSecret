package com.dhy.coffeesecret.ui.mine;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.mine.adapter.AccountAdapter;
import com.dhy.coffeesecret.utils.UIUtils;
import com.dhy.coffeesecret.views.DividerDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText usernameView;
    private EditText passwordView;
    private ImageButton clearButton;
    private ImageButton showButton;
    private PopupWindow accountWindow;
    private Set<String> accountSet;
    private ArrayList<String> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UIUtils.steepToolBar(this);

        usernameView = (EditText) findViewById(R.id.et_username);
        passwordView = (EditText) findViewById(R.id.et_password);
        clearButton = (ImageButton) findViewById(R.id.iv_clear);
        showButton = (ImageButton) findViewById(R.id.iv_show_account);
        init();
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
        clearButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
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
        String username = usernameView.getText().toString().trim();
        accountSet.add(username);
        SharedPreferences.Editor edit = getSharedPreferences("account", MODE_PRIVATE).edit();
        edit.putStringSet("account", accountSet);
        edit.putString("last_user",username);
        edit.commit();
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
        }
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
}
