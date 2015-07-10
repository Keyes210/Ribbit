package com.teamtreehouse.ribbit.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.RibbitApplication;

public class LoginActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected ProgressBar mProgressBar;

    protected TextView mSignUpTextView; //1. create variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); //initiates? progress bar
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mProgressBar = (ProgressBar) findViewById(R.id.pBar);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText); //2. link layout element to variable
        mSignUpTextView.setOnClickListener(new View.OnClickListener() { //3. code onclick listener
            @Override
            public void onClick(View view) {
                //use LoginActivity.this b/c were in this ocl method, so need to specify that this is the activity, not the ocl method
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);

        mLoginButton = (Button) findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();


                username = username.trim(); //gets rid of whitespace user may enter
                password = password.trim();


                if (username.isEmpty() || password.isEmpty()) {
                    //create dialog for user, better than toast b/c it doesnt go away
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message);  //can also get rid of semicolons and have just one builder statement
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok, null); //null just dismissed dialog if button is clicked

                    //this is to actually show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //Login
                    mProgressBar.setVisibility(View.VISIBLE);//makes progess bar visible

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            mProgressBar.setVisibility(View.INVISIBLE); //when process is done, this hides the visibility bar
                            if (e == null) {
                                //success

                                RibbitApplication.updateParseInstallation(user); //method declared in ribbit application b/c it's available everywhere and this is needed elsewhere

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                //create dialog for user, better than toast b/c it doesnt go away
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());  //gets string from the exception
                                builder.setTitle(R.string.login_error_title);
                                builder.setPositiveButton(android.R.string.ok, null); //null just dismissed dialog if button is clicked

                                //this is to actually show the dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });

                }
            }
        });
    }


}
