package com.example.yiska.project_part2.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import com.example.yiska.project_part2.R;
import com.example.yiska.project_part2.model.backend.*;
import com.example.yiska.project_part2.model.datasource.DatabaseFb;
import com.example.yiska.project_part2.model.datasource.Action;
import com.example.yiska.project_part2.model.entities.Trip;
import com.example.yiska.project_part2.model.entities.Driver;

import android.preference.PreferenceManager;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText emailEditext;
    private EditText passwordEditext;
    private Button loginButton;
    private Button registerButton;
    private Toolbar toolbar;
    List<Driver> driverList;
    Backend instance;

    /**
     * Find the Views in the layout
     */
    private void findViews() {
        instance = BackendFactory.getInstance(getApplicationContext());
        emailEditext = (EditText) findViewById(R.id.email_editext);
        passwordEditext = (EditText) findViewById(R.id.password_edittext);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Taxi");
        setSupportActionBar(toolbar);
        //sign up the buttons to Listener event
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        loadSharedPreferences();
        getRegisterData();
        initTextChangeListener();
        addTrips();
    }


    private void addTrips() {


        try {
            Backend instance = BackendFactory.getInstance(getApplicationContext());
            for (int i = 0; i < 1; i++) {
                Trip ride = new Trip();
                ride.setDestination("Gan Hachayot Hatanachi , Jerusalem");
                ride.setCostumerTel("0545423200");
                ride.setPlaceBegin("Bilu Street 16, Jerusalem");
                ride.setHourBegin("12:10");
                ride.setCostumerName("Gadi choen");
                ride.setCostumerEmail("Gadi@gmail.com");
                ride.setCreditCard("1234123445341234");
                ride.setStatus(Trip.STATUS.AVAILABLE);

                ((DatabaseFb) instance).addTrip(ride, new Action<String>() {
                    @Override
                    public void onSuccess(String obj) {
                        Toast.makeText(getBaseContext(), "Succeeded" + obj, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(String status, double percent) {

                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * If the user just signed up, the function get the data from the RegisterActivity activity
     * and put it into the userName and password edit text
     */
    private void getRegisterData() {
        //get the intent that called this activity
        Intent intent = getIntent();
        //if extra data was sent from the calling activity
        if (this.getIntent().getExtras() != null) {
            //check if the data we are looking for was sent(email and password)
            if (this.getIntent().getExtras().containsKey("email") &&
                    this.getIntent().getExtras().containsKey("password")) {
                //assign into the text view the email and password
                emailEditext.setText(intent.getStringExtra("email"));
                passwordEditext.setText(intent.getStringExtra("password"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            //save the email and the password into shared preference
            saveSharedPreferences();
            //get driver list
            driverList = instance.getDrivers();
            //send to function to check if email and password are correct
            Driver d = checkLogin();
            if (d != null) {
                //call new intent with the navigation drawer
                Intent intent = new Intent(this, NavigationDrawerActivity.class);
                intent.putExtra("mDriver", (Serializable) d);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "incorrect email or password", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (v == registerButton) {
            //call new intent with the register activity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }

    }

    /**
     * check if the email and password are correct
     *
     * @return the driver entity if yes nad null if not
     */
    private Driver checkLogin() {
        for (Driver d : driverList) {
            if (d.getPassword().equals(passwordEditext.getText().toString()) &&
                    d.getEMail().equals(emailEditext.getText().toString())) {
                return d;
            }

        }
        return null;
    }

    /**
     * check if the focus has change in one of the edit text
     */
    private void initTextChangeListener() {
        //check if the focus has change in one of the edit text
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validate();
                }
            }
        };
        //set all edit text for onFocusChangeListener
        emailEditext.setOnFocusChangeListener(onFocusChangeListener);
        passwordEditext.setOnFocusChangeListener(onFocusChangeListener);

    }

    /**
     * check if the input in all text edits are valid, and if all are them are filled,
     * and enable/disable the login button
     **/
    private void validate() {
        boolean isAllValid = true;
        if (emailEditext.getText().length() == 0 || passwordEditext.getText().length() == 0) {
            isAllValid = false;
        }
        //Enable the button by the condition below
        loginButton.setEnabled(isAllValid);
    }

    /**
     * The function load the user saved userName and password to the UI.
     */
    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains("NAME")) {
            emailEditext.setText(sharedPreferences.getString("NAME", null));
        }
        if (sharedPreferences.contains("PASSWORD")) {
            //    String password = sharedPreferences.getInt("PASSWORD", 0);
            passwordEditext.setText(sharedPreferences.getString("PASSWORD", null));
        }
    }

    /**
     * The function save the userName+password of the user on the phone,
     * so next time the user try to login, his details will be shown
     */
    private void saveSharedPreferences() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String name = emailEditext.getText().toString();
            String password = passwordEditext.getText().toString();
            editor.putString("NAME", name);
            editor.putString("PASSWORD", password);
            editor.commit();
        } catch (Exception ex) {
        }
    }
}