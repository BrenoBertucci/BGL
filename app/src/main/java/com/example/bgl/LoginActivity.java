package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);

        Button btnLogin = findViewById(R.id.btn_login);
        TextView linkSignup = findViewById(R.id.link_signup);
        TextView linkForgot = findViewById(R.id.link_forgot);

        btnLogin.setOnClickListener(v -> attemptLogin());

        linkSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        linkForgot.setOnClickListener(v ->
                Toast.makeText(this, R.string.forgot_password, Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_required));
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.error_email_invalid));
            inputEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            inputPassword.setError(getString(R.string.error_password_short));
            inputPassword.requestFocus();
            return;
        }

        // TODO: hook real authentication here.
        Toast.makeText(this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
    }
}
