package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyBookings extends AppCompatActivity
{
    DBHelper db;

    ListView bookings;
    MyCustomAdapter1 myCustomAdapter1;

    ArrayList<Integer> bookingID;
    ArrayList<String> bookedNannyName;
    ArrayList<String> bookedNannyImage;
    ArrayList<String> bookingDate;

    SharedPreferences savedValues;
    String userPhone;
    ImageView menu_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        bookings = findViewById(R.id.bookings);
        db = new DBHelper(this);
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);
        userPhone = savedValues.getString("userPhnNo","");

        bookingID = new ArrayList<Integer>();
        bookedNannyName = new ArrayList<String>();
        bookedNannyImage = new ArrayList<String>();
        bookingDate= new ArrayList<String>();

        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        menu_icon.bringToFront();

        Cursor cursor = db.extractBookings(userPhone);
        cursor.moveToFirst();

        if (cursor.getCount() >= 1)
        {
            //nannyList.setVisibility(View.VISIBLE);
            do{
                bookingID.add(cursor.getInt(0));
                bookedNannyName.add(cursor.getString(1) + " " + cursor.getString(2));
                bookedNannyImage.add(cursor.getString(3));
                bookingDate.add(cursor.getString(4));

            }while (cursor.moveToNext());

            for(int i= 0; i< bookingID.size(); i++)
            {
                if(bookedNannyImage.get(i) == null)
                {
                    bookedNannyImage.set(i, "img_no.png");
                }

                myCustomAdapter1 = new MyCustomAdapter1(MyBookings.this, bookedNannyImage, bookedNannyName, bookingDate);
                bookings.setAdapter(myCustomAdapter1);
            }
        }else{
            Toast.makeText(MyBookings.this, "No Bookings available to show.", Toast.LENGTH_SHORT).show();

        }

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
                                intent = new Intent(MyBookings.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(MyBookings.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(MyBookings.this, NannyPreferences.class);
                                intent.putExtra("phoneNumber", savedValues.getString("userPhnNo",""));
                                startActivity(intent);
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        bookings.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), BookingConfirmation.class);
                intent.putExtra("BookingID", bookingID.get(position));
                startActivity(intent);

            }
        });


    }


};