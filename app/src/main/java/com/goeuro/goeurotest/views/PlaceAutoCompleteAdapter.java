package com.goeuro.goeurotest.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.goeuro.goeurotest.R;
import com.goeuro.goeurotest.controller.HomeController;
import com.goeuro.goeurotest.dto.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    HomeController homeController;
    private List<Place> resultList = new ArrayList<>();

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
        homeController = HomeController.getInstance(context);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Place getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item, parent, false);
        }

        String text = String.format("%s (%s)", getItem(position).getName(), getItem(position).getCountry());
        ((TextView) convertView.findViewById(R.id.place_name)).setText(text);

        LayoutAnimationController controller = new LayoutAnimationController(AnimationUtils.loadAnimation(mContext, R.anim.enter_animation));
        parent.setLayoutAnimation(controller);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Place> places = findPlaces(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = places;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Place>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Returns a search result for the given place name.
     */
    private List<Place> findPlaces(Context context, String placeName) {

        List<Place> results = homeController.findPlaces(placeName);

        return results;
    }
}