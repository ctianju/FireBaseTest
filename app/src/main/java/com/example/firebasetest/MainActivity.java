package com.example.firebasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()){
                            Log.e("TAG", "Fetching FCM registration token failed");
                            return;
                        }

                        String token=task.getResult();
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("TAG:", msg);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        askNotificationPermission();
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                XXPermissions.with(this)
                        // 申请单个权限
                        .permission(Permission.POST_NOTIFICATIONS)
                        // 申请多个权限
                        // 设置权限请求拦截器（局部设置）
                        //.interceptor(new PermissionInterceptor())
                        // 设置不触发错误检测机制（局部设置）
                        //.unchecked()
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean allGranted) {
                                if (!allGranted) {
                                    toast("获取部分权限成功，但部分权限未正常授予");
                                    return;
                                }
                                toast("获取通知成功");
                                FirebaseAnalytics.getInstance(MainActivity.this).setAnalyticsCollectionEnabled(true);

                                FirebaseMessaging.getInstance().isAutoInitEnabled();
                            }

                            @Override
                            public void onDenied( List<String> permissions, boolean doNotAskAgain) {
                                if (doNotAskAgain) {
                                    toast("被永久拒绝授权，请手动授予通知权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                                } else {
                                    toast("获取通知权限失败");
                                }
                            }
                        });
            }
        }

    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}