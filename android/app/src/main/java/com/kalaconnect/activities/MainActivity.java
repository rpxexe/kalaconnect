package com.kalaconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.kalaconnect.R;
import com.kalaconnect.auth.AuthManager;
import com.kalaconnect.models.UserRole;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AuthManager.AuthListener authListener;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.kalaconnect.utils.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        authListener = new AuthManager.AuthListener() {
            @Override
            public void onLoginSuccess(UserRole role) {
                // Handled in auth flow
            }

            @Override
            public void onLogout() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onTokenExpired() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, R.string.err_session_expired, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        };

        AuthManager.getInstance(this).addAuthListener(authListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authListener != null) {
            AuthManager.getInstance(this).removeAuthListener(authListener);
        }
    }

    public NavController getNavController() {
        return navController;
    }
}
