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

public class SignupActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirm;
    private Button btnSignup;

    private AuthController authController;

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

        authController = new AuthController(this);

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirm = findViewById(R.id.input_confirm);
        btnSignup = findViewById(R.id.btn_signup);
        TextView linkLogin = findViewById(R.id.link_login);

        btnSignup.setOnClickListener(v -> fazerCadastro());

        // Volta para a tela de login.
        linkLogin.setOnClickListener(v -> finish());
    }

    private void fazerCadastro() {
        String nome = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String senha = inputPassword.getText().toString();
        String confirma = inputConfirm.getText().toString();

        if (TextUtils.isEmpty(nome)) {
            inputName.setError(getString(R.string.error_required));
            inputName.requestFocus();
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
        if (!senha.equals(confirma)) {
            inputConfirm.setError(getString(R.string.error_password_mismatch));
            inputConfirm.requestFocus();
            return;
        }

        carregando(true);
        authController.cadastrar(nome, email, senha, new AuthController.AuthCallback() {
            @Override
            public void onSucesso() {
                carregando(false);
                Toast.makeText(SignupActivity.this, R.string.msg_signup_success, Toast.LENGTH_SHORT).show();

                // Sem confirmação de e-mail: vai direto para o menu.
                Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                carregando(false);
                Toast.makeText(SignupActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void carregando(boolean ativo) {
        btnSignup.setEnabled(!ativo);
        btnSignup.setText(ativo ? "Cadastrando..." : getString(R.string.action_signup));
    }
}
