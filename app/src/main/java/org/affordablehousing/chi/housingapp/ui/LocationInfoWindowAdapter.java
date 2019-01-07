package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import org.affordablehousing.chi.housingapp.R;

import static androidx.core.content.ContextCompat.getColor;
import static com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import static org.affordablehousing.chi.housingapp.R.color.colorSecondaryDark;

/**
 * Marker window presentation.
 */
class LocationInfoWindowAdapter implements InfoWindowAdapter {

    private final View mWindow;
    private final View mContents;
    private Context mContext;

    LocationInfoWindowAdapter(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }


    @Override
    public View getInfoWindow(Marker marker) {
//            render(marker, mWindow);
//            return mWindow;
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mContents);
        return mContents;
    }

    private void render(Marker marker, View view) {

        String title = marker.getTitle();
        TextView titleUi = view.findViewById(R.id.title);
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null && snippet.length() > 0) {
            SpannableString snippetText = new SpannableString(snippet);
            int typeStartIndex = snippetText.toString().indexOf("Type:");
            int typeEndIndex = snippetText.toString().length();
            int typeColor = getColor(mContext, colorSecondaryDark);
            snippetText.setSpan(new ForegroundColorSpan(typeColor), typeStartIndex, typeEndIndex, 0);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

}
