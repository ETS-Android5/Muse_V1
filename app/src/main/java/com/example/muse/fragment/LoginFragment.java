package com.example.muse.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.muse.R;
import com.example.muse.StartActivity;
import com.example.muse.utility.SaveState;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView tv_forgot, tv_signUp;
    private Button btn_login;
    private String email, password;
    private ProgressDialog progressDialog;
    private TextInputEditText et_email, et_password;
    private TextInputLayout itl_email, itl_password;
    private NavController navController;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        navController=Navigation.findNavController(requireActivity(),R.id.start_fragment);
        if (StartActivity.mAuth.getCurrentUser() != null)
            navController.navigate(R.id.action_loginFragment_to_mainFragment);

        //StatusBar color
        if (SaveState.getDarkModeState())
            StartActivity.setupBackgroundStatusBar(getResources().getColor(R.color.nice_black, null));
        else
            StartActivity.setupLightStatusBar(getResources().getColor(R.color.white_muse, null));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_email = view.findViewById(R.id.login_et_email);
        et_password = view.findViewById(R.id.login_et_password);
        itl_email = view.findViewById(R.id.itl_email);
        itl_password = view.findViewById(R.id.itl_password);
        progressDialog = new ProgressDialog(getContext());

        tv_forgot = view.findViewById(R.id.login_tv_forgot);
        tv_signUp = view.findViewById(R.id.login_tv_signUp);
        btn_login = view.findViewById(R.id.login_btn_login);

        tv_forgot.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_tv_forgot:
                setupForgotPassword();
                break;

            case R.id.login_tv_signUp:
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.login_btn_login:
                setupLogin();
                break;
        }
    }

    public void setupForgotPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.DialogStyle);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final TextInputEditText et_resetEmail = view.findViewById(R.id.dialog_et_email);
        final TextView tv_submit = view.findViewById(R.id.dialog_tv_submit);
        final TextView tv_cancel = view.findViewById(R.id.dialog_tv_cancel);
        final ProgressBar pb = view.findViewById(R.id.dialog_pb);

        tv_submit.setOnClickListener(v1 -> {
            String reset_email = et_resetEmail.getText().toString();
            if (TextUtils.isEmpty(reset_email)) {
                Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (SaveState.checkConnection(getContext())) {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            } else {

                pb.setVisibility(View.VISIBLE);
                tv_submit.setVisibility(View.INVISIBLE);
                tv_cancel.setVisibility(View.INVISIBLE);

                StartActivity.mAuth.fetchSignInMethodsForEmail(reset_email).addOnSuccessListener(task -> StartActivity.mAuth.sendPasswordResetEmail(reset_email)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            tv_submit.setVisibility(View.VISIBLE);
                            tv_cancel.setVisibility(View.VISIBLE);
                        })).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    tv_submit.setVisibility(View.VISIBLE);
                    tv_cancel.setVisibility(View.VISIBLE);
                });
            }
        });
        tv_cancel.setOnClickListener(v12 -> alertDialog.dismiss());
    }

    public void setupLogin() {
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            itl_email.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            itl_password.setError("Password is required");
            itl_email.setError(null);
            return;
        }

        if (SaveState.checkConnection(getContext())) {
            itl_email.setError(null);
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        StartActivity.mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(task -> {
            StartActivity.mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(task1 -> navController.navigate(R.id.action_loginFragment_to_mainFragment)).addOnFailureListener(e -> {
                //email does not exist
                if (e instanceof FirebaseAuthInvalidUserException) {
                    itl_email.setError("Invalid email");
                    itl_password.setError(null);
                }

                //wrong password
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    itl_password.setError("Password is wrong");
                    itl_email.setError(null);
                }
            });
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                itl_email.setError("Badly format");
                itl_password.setError(null);
            } else
                Toast.makeText(getContext(), "Error! " + e.getCause(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }
}