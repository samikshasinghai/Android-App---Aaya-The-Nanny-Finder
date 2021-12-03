package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    AutoCompleteTextView zipCity;
    EditText firstName, lastName, email, address;
    Button btnSignUp;
    CheckBox agreeCB;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        String[] zipCitiesList = getResources().getStringArray(R.array.zip_City);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, zipCitiesList);
        firstName = findViewById(R.id.firstNameET);
        lastName = findViewById(R.id.lastNameET);
        email = findViewById(R.id.emailET);
        address = findViewById(R.id.addressET);
        zipCity = findViewById(R.id.zipCityAC);
        btnSignUp = findViewById(R.id.signUp);
        agreeCB = findViewById(R.id.agreeCB);
        zipCity.setAdapter(adapter);
        db = new DBHelper(this);

        String phnNo = getIntent().getStringExtra("phoneNumber");

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(validateFirstName() && validateLastName() && validateEmail() && validateAddress() && validateZip_City() && validateTermsandCondition())
                {
                    String fName = firstName.getText().toString();
                    String lName = lastName.getText().toString();
                    String emailID = email.getText().toString();
                    String addr = address.getText().toString();
                    String[] zipCodeandCity = zipCity.getText().toString().split("\\|");
                    String state = "Michigan";
                    boolean signUpsuccess = db.signUp(fName, lName, emailID, phnNo, addr, zipCodeandCity[0], state , zipCodeandCity[1]);
                    if (signUpsuccess == true){
                        Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), NannyPreferences.class);
                        intent.putExtra("phoneNumber", phnNo);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Registration Unsuccessful.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //Validation for first name
    private boolean validateFirstName()
    {
        String fName = firstName.getText().toString();
        String fPattern = "[a-zA-Z]+";

        if(fName.isEmpty())
        {
            firstName.setError("Field cannot be empty");
            return false;
        }else if(!fName.matches(fPattern)) {
            firstName.setError("Invalid first name");
            return false;
        }else{
            firstName.setError(null);
            return true;
        }

    }

    //Validation for last name
    private boolean validateLastName()
    {
        String lName = lastName.getText().toString();
        String lPattern = "[a-zA-Z]+";

        if(lName.isEmpty())
        {
            lastName.setError("Field cannot be empty");
            return false;
        }else if(!lName.matches(lPattern)) {
            lastName.setError("Invalid last name");
            return false;
        }else{
            lastName.setError(null);
            return true;
        }
    }

    //Validation for email address
    private boolean validateEmail()
    {
        String emailID = email.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(emailID.isEmpty())
        {
            email.setError("Field cannot be empty");
            return false;
        }
        else if(!emailID.matches(emailPattern)){
            email.setError("Invalid email address");
            return false;

    }else{
        email.setError(null);
        return true;
    }
    }

    //Validation for address
    private boolean validateAddress()
    {
        String addr = address.getText().toString();
        String addrPattern = "^[#.0-9a-zA-Z\\s,-]+$";

        if(addr.isEmpty())
        {
            address.setError("Field cannot be empty");
            return false;
        }else if(!addr.matches(addrPattern))
        {
            address.setError("Invalid address");
            return false;
        }else{
            address.setError(null);
            return true;
        }
    }
    //Validation for zip code
    private boolean validateZip_City()
    {
        String zipCode = zipCity.getText().toString();

        if (zipCode.isEmpty()) {
            zipCity.setError("Field cannot be empty");
            return false;
        } else {
            zipCity.setError(null);
            return true;
        }
    }

    private boolean validateTermsandCondition()
    {
        if (agreeCB.isChecked()){
            return true;
    }
        else{
            agreeCB.setError("Please agree terms & condition");
            return false;
        }
    }


}