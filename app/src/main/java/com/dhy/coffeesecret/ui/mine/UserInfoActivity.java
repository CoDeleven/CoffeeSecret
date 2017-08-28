package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dhy.coffeesecret.LoginActivity;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.User;
import com.dhy.coffeesecret.ui.mine.fragments.PasswordFragment;
import com.dhy.coffeesecret.ui.mine.fragments.TelFragment;
import com.dhy.coffeesecret.ui.mine.fragments.UsernameFragment;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bugtags.ui.view.rounded.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.dhy.coffeesecret.model.UniExtraKey.EXTRA_USER_INFO;

public class UserInfoActivity extends AppCompatActivity implements UsernameFragment.OnSuccessListener {

    public static final int DEFAULT = 0;
    public static final int UPDATE = 1;

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.head_img)
    CircleImageView headImg;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.phone)
    TextView phone;
    @Bind(R.id.pw)
    TextView pw;
    private User user;
    private int resultCode = DEFAULT;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        application = ((MyApplication) getApplication());
        user =  getIntent().getParcelableExtra(EXTRA_USER_INFO.getKey());
        setupUserInfo();
    }

    private void setupUserInfo() {
        username.setText(user.getUsername());
        String mobilePhone = user.getMobilePhone();
        phone.setText(mobilePhone.substring(0, 3) + "****" + mobilePhone.substring(7));
        String token = application.getToken();
        String avatar = UrlLogin.getAvatar(token);
        ImageLoader.getInstance().displayImage(avatar, headImg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_back, R.id.head_img, R.id.rl_username, R.id.rl_tel, R.id.rl_password, R.id.btn_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.head_img:
                selectImage();
                break;
            case R.id.rl_username:
                UsernameFragment usernameFragment = UsernameFragment.newInstance();
                usernameFragment.setOnSuccessListener(this);
                addFragment(usernameFragment);
                break;
            case R.id.rl_tel:
                TelFragment telFragment = TelFragment.newInstance(user.getMobilePhone());
                addFragment(telFragment);
                break;
            case R.id.rl_password:
                PasswordFragment passwordFragment = PasswordFragment.newInstance(user.getMobilePhone());
                addFragment(passwordFragment);
                break;
            case R.id.btn_exit:
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                application.setToken(null);
                finish();
                break;
        }
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_bottom, R.anim.in_from_right, R.anim.out_to_bottom)
                .add(R.id.layout, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get Image Path List
            final List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            String token = application.getToken();
            String url = UrlLogin.updateAvatar(token);
            Request request = HttpUtils.getRequest(url, new File(pathList.get(0)));
            HttpUtils.enqueue(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("xx", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String token = response.body().string();

                        if (token != null && !token.startsWith("error")) {
                            Log.d("xx", token);
                            application.setToken(token);
                            UserInfoActivity.this.resultCode = UPDATE;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(UserInfoActivity.this)
                                            .load(pathList.get(0))
                                            .into(headImg);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        setResult(resultCode);
        super.finish();
    }

    private void selectImage() {
        ImageConfig config = new ImageConfig.Builder(new com.yancy.imageselector.ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        }).steepToolBarColor(getResources().getColor(R.color.colorPrimary))
                .titleBgColor(getResources().getColor(R.color.colorPrimary))
                .singleSelect()
                .showCamera()
                .build();
        ImageSelector.open(this, config);
    }

    @Override
    public void onUserNameUpdate(String un) {
        resultCode = UPDATE;
        username.setText(un);
    }
}
