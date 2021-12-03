package edu.oakland;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyCustomAdapter1 extends ArrayAdapter
{
    private Activity context;
    private ArrayList<String> nImgArr;
    private ArrayList<String> nDateArr;
    private ArrayList<String> nNameArr;

    public MyCustomAdapter1(MyBookings myBookings, ArrayList nanny_img, ArrayList booking_date, ArrayList nanny_name) {
        super(myBookings, R.layout.list_item1, nanny_img);

        context = myBookings;
        nImgArr = nanny_img;
        nDateArr = booking_date;
        nNameArr = nanny_name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View rowView, @NonNull ViewGroup parent)
    {
        LayoutInflater myInflator = context.getLayoutInflater();
        rowView = myInflator.inflate(R.layout.list_item1, parent, false);

        ImageView nannyImg = rowView.findViewById(R.id.nannyImg);
        TextView bookingDate = rowView.findViewById(R.id.bookingDate);
        TextView nannyName = rowView.findViewById(R.id.nannyName);

        String extension = "";
        int i = nImgArr.get(position).lastIndexOf('.');
        if(i > 0)
        {
            extension = nImgArr.get(position).substring(i);
        }
        String nannyImage = nImgArr.get(position).replace(extension, "");

        //setting image into imageview after removing the extension.
        int info = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + nannyImage, null, null);
        nannyImg.setImageResource(info);
        nannyName.setText(nNameArr.get(position));
        bookingDate.setText(nDateArr.get(position));

        return rowView;
    }
}
