package com.goeuro.goeurotest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.goeuro.goeurotest.controller.HomeController;
import com.goeuro.goeurotest.dto.Place;
import com.goeuro.goeurotest.views.GoEuroAutoCompleteTextView;
import com.goeuro.goeurotest.views.PlaceAutoCompleteAdapter;
import com.percolate.caffeine.DialogUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.hoang8f.widget.FButton;

/**
 * A placeholder fragment containing a simple view.
 */
@EFragment(R.layout.fragment_main)
public class MainActivityFragment extends Fragment {

    private static final int INPUT_THRESHOLD = 3;

    @ViewById
    FrameLayout startLayout;
    @ViewById
    GoEuroAutoCompleteTextView startLocation;
    @ViewById
    ProgressBar startLoadingIndicator;
    @ViewById
    FrameLayout endLayout;
    @ViewById
    GoEuroAutoCompleteTextView endLocation;
    @ViewById
    ProgressBar endLoadingIndicator;
    @ViewById
    LinearLayout calendarView;
    @ViewById
    EditText dateTextField;
    @ViewById
    ImageButton pickDate;
    @ViewById
    FButton search;


    public MainActivityFragment() {
    }


    @Override
    public void onPause() {
        super.onPause();
        HomeController.getInstance(getActivity()).stopLocationUpdates();
    }

    @AfterViews
    public void onViewsLoaded() {
        startLocation.setThreshold(INPUT_THRESHOLD);
        startLocation.setAdapter(new PlaceAutoCompleteAdapter(getActivity()));
        startLocation.setLoadingIndicator(startLoadingIndicator);
        startLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                onPlaceSelected(startLocation, adapterView, position);
            }
        });

        endLocation.setThreshold(INPUT_THRESHOLD);
        endLocation.setAdapter(new PlaceAutoCompleteAdapter(getActivity()));
        endLocation.setLoadingIndicator(endLoadingIndicator);
        endLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                onPlaceSelected(endLocation, adapterView, position);
            }
        });
    }

    Date lastSelectedDate = null;

    @Click(R.id.pick_date)
    public void onPickDateClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View dialogView = getLayoutInflater(null).inflate(R.layout.dialog_date_picker, null);
        final MaterialCalendarView materialCalendarView = (MaterialCalendarView) dialogView.findViewById(R.id.calendarView);

        if (lastSelectedDate != null) {
            materialCalendarView.setSelectedDate(lastSelectedDate);
        }

        builder.setTitle(R.string.trip_date).setView(dialogView).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CalendarDay calendarDay = materialCalendarView.getSelectedDate();
                if (calendarDay != null) {
                    lastSelectedDate = calendarDay.getDate();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateTextField.setText(simpleDateFormat.format(calendarDay.getDate()));
                }
            }
        }).show();
    }

    @Click(R.id.search)
    public void onSearchClicked() {
        DialogUtils.quickDialog(getActivity(), getString(R.string.search_warning));
    }


    private void onPlaceSelected(EditText editText, AdapterView<?> adapterView, int position) {
        Place place = (Place) adapterView.getItemAtPosition(position);
        editText.setText(place.getName());
        editText.setSelection(editText.getText().length());
    }
}
