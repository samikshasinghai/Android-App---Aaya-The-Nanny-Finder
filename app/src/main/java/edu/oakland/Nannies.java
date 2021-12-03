package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Nannies extends AppCompatActivity {

    DBHelper db;
    JsonParse jp;
    MyCustomAdapter myCustomAdapter;
    ListView nannyList;
    ImageView menu_icon;

    ArrayList<String> nannylist_phone;
    ArrayList<String> nannylist_image;
    ArrayList<String> nannylist_name;
    ArrayList<Float> nannylist_experience;
    ArrayList<String> nannylist_city;
    ArrayList<Integer> nannylist_rating;

    String listViewQuery = "";
    String phnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nannies);
        nannyList = findViewById(R.id.nannyList);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        phnNo = getIntent().getStringExtra("phoneNumber");
        String zipSearch = getIntent().getStringExtra("zip_search");
        String zipcodes = null;
        try {
            zipcodes = getZipCodes(zipSearch,"25");
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = new DBHelper(this);
        menu_icon.bringToFront();
        hideSoftKeyboard();
        Cursor cursor = db.extractNannies(phnNo,zipcodes);
        cursor.moveToFirst();
        nannylist_phone = new ArrayList<String>();
        nannylist_image = new ArrayList<String>();
        nannylist_name = new ArrayList<String>();
        nannylist_experience = new ArrayList<Float>();
        nannylist_city = new ArrayList<String>();
        nannylist_rating = new ArrayList<Integer>();

        if (cursor.getCount() >= 1)
        {
            //nannyList.setVisibility(View.VISIBLE);
            do{
                nannylist_phone.add(cursor.getString(0));
                nannylist_image.add(cursor.getString(1));
                nannylist_name.add(cursor.getString(2) + " " + cursor.getString(3));
                nannylist_experience.add(cursor.getFloat(4));
                nannylist_city.add(cursor.getString(5));
                nannylist_rating.add(cursor.getInt(6));
            }while (cursor.moveToNext());

            for(int i= 0; i< nannylist_name.size(); i++)
            {
                if(nannylist_image.get(i) == null)
                {
                    nannylist_image.set(i, "img_no.png");
                }

                myCustomAdapter = new MyCustomAdapter(Nannies.this, nannylist_name, nannylist_image, nannylist_experience, nannylist_city, nannylist_rating);
                nannyList.setAdapter(myCustomAdapter);
            }
        }else{
            Toast.makeText(Nannies.this, "No nannies available according to your preferences", Toast.LENGTH_SHORT).show();
            System.out.println("nannyList: " + nannyList);
        }

        nannyList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), NannyDetails.class);
                intent.putExtra("user_phoneNumber", phnNo);
                intent.putExtra("nanny_phoneNumber", nannylist_phone.get(position));
                startActivity(intent);

            }
        });

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Menu Icon Clicked");
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),menu_icon);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()){

                            case R.id.myBookings:
                                intent = new Intent(Nannies.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(Nannies.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(Nannies.this, NannyPreferences.class);
                                intent.putExtra("phoneNumber", phnNo);
                                startActivity(intent);
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    };



    private void hideSoftKeyboard()
    {
        if(getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    private String getZipCodes(String zipCode, String radius) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.zipcodeapi.com/rest/oIbyNely4dYIosjEbWuTBFSNKoJ0yRgHa7F8fiVbbu341wNFJhwGzl0fQHGza2kY/radius.json/"+zipCode+"/"+radius+"/mile?minimal";
        String zip_codes = null;
        String foundZips = "";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            zip_codes =  response.body().string();
            System.out.println("API RESPONSE " + zip_codes);
            String[] response_1 = zip_codes.split(":");
            String[] response_2 = response_1[1].split("\"");

            for(int i = 0; i < response_2.length; i++){

                if(response_2[i].length() == 5){
                    foundZips = foundZips + "'" + response_2[i] +"',";
                }

            }



        }
        catch (IOException e)
        { e.printStackTrace(); }
        foundZips = foundZips.substring(0,foundZips.length()-1);
        return foundZips;
    }
}