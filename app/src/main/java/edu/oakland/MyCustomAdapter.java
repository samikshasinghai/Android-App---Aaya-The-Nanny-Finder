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

public class MyCustomAdapter extends ArrayAdapter
{
    private Activity context;
    private ArrayList<String> fImgArr;
    private ArrayList<String> fNameArr;
    private ArrayList<Float> fExperArr;
    private ArrayList<String> fCityArr;
    private ArrayList<Integer> fRatingArr;


    public MyCustomAdapter(Nannies nannies, ArrayList nannylist_name, ArrayList nannylist_image, ArrayList nannylist_experience, ArrayList nannylist_city, ArrayList nannylist_rating)
    {
        super(nannies, R.layout.list_item, nannylist_name);
        context = nannies;
        fImgArr = nannylist_image;
        fNameArr = nannylist_name;
        fExperArr = nannylist_experience;
        fCityArr = nannylist_city;
        fRatingArr = nannylist_rating;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View rowView, @NonNull ViewGroup parent)
    {
        LayoutInflater myInflator = context.getLayoutInflater();
        rowView = myInflator.inflate(R.layout.list_item, parent, false);

        ImageView nannyImg = rowView.findViewById(R.id.nannyImg);
        TextView nannyName = rowView.findViewById(R.id.nannyName);
        RatingBar nannyRating = rowView.findViewById(R.id.nannyRating);
        TextView nannyLoc = rowView.findViewById(R.id.nannyLoc);
        TextView nannyExp = rowView.findViewById(R.id.nannyExp);

        String extension = "";
        int i = fImgArr.get(position).lastIndexOf('.');
        if(i > 0)
        {
            extension = fImgArr.get(position).substring(i);
        }
        String nannyImage = fImgArr.get(position).replace(extension, "");

        //setting image into imageview after removing the extension.
        int info = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + nannyImage, null, null);
        nannyImg.setImageResource(info);
        nannyName.setText(fNameArr.get(position));
        nannyExp.setText(fExperArr.get(position).toString() + " yrs");
        nannyLoc.setText(fCityArr.get(position));
        nannyRating.setRating(fRatingArr.get(position));

        return rowView;
    }
}
