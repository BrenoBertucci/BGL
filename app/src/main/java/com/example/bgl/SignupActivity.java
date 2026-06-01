package com.example.bgl;

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

public class SignupActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirm = findViewById(R.id.input_confirm);

        Button btnSignup = findViewById(R.id.btn_signup);
        TextView linkLogin = findViewById(R.id.link_login);

        btnSignup.setOnClickListener(v -> attemptSignup());

        // Return to the login screen.
        linkLogin.setOnClickListener(v -> finish());
    }

    private void attemptSignup() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String confirm = inputConfirm.getText().toString();

        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.error_required));
            inputName.requestFocus();
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
        if (!password.equals(confirm)) {
            inputConfirm.setError(getString(R.string.error_password_mismatch));
            inputConfirm.requestFocus();
            return;
        }

        // TODO: hook real account creation here.
        Toast.makeText(this, R.string.msg_signup_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}
