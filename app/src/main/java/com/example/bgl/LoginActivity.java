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

import com.example.bgl.controller.AuthController;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;

    private AuthController authController;

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

        authController = new AuthController(this);

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        TextView linkSignup = findViewById(R.id.link_signup);
        TextView linkForgot = findViewById(R.id.link_forgot);

        btnLogin.setOnClickListener(v -> fazerLogin());

        linkSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        linkForgot.setOnClickListener(v ->
                Toast.makeText(this, R.string.forgot_password, Toast.LENGTH_SHORT).show());
    }

    private void fazerLogin() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputPassword.getText().toString();

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
        if (senha.length() < 6) {
            inputPassword.setError(getString(R.string.error_password_short));
            inputPassword.requestFocus();
            return;
        }

        carregando(true);
        authController.login(email, senha, new AuthController.AuthCallback() {
            @Override
            public void onSucesso() {
                carregando(false);
                Toast.makeText(LoginActivity.this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
                irParaPrincipal();
            }

            @Override
            public void onErro(String mensagem) {
                carregando(false);
                Toast.makeText(LoginActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void irParaPrincipal() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void carregando(boolean ativo) {
        btnLogin.setEnabled(!ativo);
        btnLogin.setText(ativo ? "Entrando..." : getString(R.string.action_login));
    }
}
