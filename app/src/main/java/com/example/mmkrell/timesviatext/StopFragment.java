package com.example.mmkrell.timesviatext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class StopFragment extends Fragment {

    public static boolean enableAnimations;

    private int stopId;
    private String stopName;
    private String stopDir;

    private boolean checked;
    private SharedPreferences sharedPreferences;
    private HashSet<String> favoritesSet;

    public StopFragment() {
        // Required empty public constructor
    }

    public static StopFragment newInstance(int stopId, String stopName, String stopDir) {
        StopFragment stopFragment = new StopFragment();
        Bundle args = new Bundle();
        args.putInt("stopId", stopId);
        args.putString("stopName", stopName);
        args.putString("stopDir", stopDir);
        stopFragment.setArguments(args);
        return stopFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stopId = getArguments().getInt("stopId");
            stopName = getArguments().getString("stopName");
            stopDir = getArguments().getString("stopDir");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stop, container, false);

        TextView textViewName = (TextView) v.findViewById(R.id.stop_fragment_text_view_name);
        TextView textViewDirection = (TextView) v.findViewById(R.id.stop_fragment_text_view_direction);
        final ImageView buttonFavorite = (ImageView) v.findViewById(R.id.stop_fragment_button_favorite);
        Button buttonWriteText = (Button) v.findViewById(R.id.stop_fragment_button_write_text);

        final VectorDrawableCompat uncheckedFavorite = VectorDrawableCompat.create(getResources(), R.drawable.ic_favorite_border_red_24dp, null);
        final VectorDrawableCompat checkedFavorite = VectorDrawableCompat.create(getResources(), R.drawable.ic_favorite_red_24dp, null);

        textViewName.setText(stopName);

        if (stopDir.isEmpty()) { // If stopDir is empty, hide the TextView that displays the direction
            textViewDirection.setVisibility(View.GONE);
        } else {
            textViewDirection.setText(stopDir);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        favoritesSet = new HashSet<>(sharedPreferences
                .getStringSet("favorites", new HashSet<String>()));

        // If this stop is a favorite, make the heart filled to represent that
        if (favoritesSet.contains(String.valueOf(stopId))) {
            checked = true;
            buttonFavorite.setImageDrawable(checkedFavorite);
        }

        buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Flip the "checked" boolean
                checked = ! checked;

                if (checked) {
                    // Set image to filled heart and add stop to favorites
                    buttonFavorite.setImageDrawable(checkedFavorite);
                    growAndShrink(buttonFavorite);
                    favoritesSet.add(String.valueOf(stopId));
                } else {
                    // Set image to empty heart and remove stop from favorites
                    buttonFavorite.setImageDrawable(uncheckedFavorite);
                    growAndShrink(buttonFavorite);
                    favoritesSet.remove(String.valueOf(stopId));
                }

                sharedPreferences.edit().putStringSet("favorites", favoritesSet).apply();

                FavoritesFragment favoritesFragment = (FavoritesFragment) getActivity()
                        .getSupportFragmentManager().findFragmentByTag("favorites_fragment");

                // Sort the array by stop name and direction
                ArrayList<Integer> favoritesArrayList = favoritesFragment
                        .sortStopIdsByNameAndDirection(favoritesSet);

                // Update the RecyclerView with the new data
                favoritesFragment.updateFavorites(favoritesArrayList);
            }
        });

        buttonWriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getStopTimesIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:41411"));
                getStopTimesIntent.putExtra("sms_body", "CTABUS " + stopId);
                if (getStopTimesIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(getStopTimesIntent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.no_sms_app_found, Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    // See http://stackoverflow.com/a/11253987
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enableAnimations) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation a = new Animation() {};
        a.setDuration(0);
        return a;
    }

    private static void growAndShrink(final View v) {
        v.animate().setDuration(250).scaleX(1.75f).scaleY(1.75f).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.animate().scaleX(1).scaleY(1);
                // The same ViewPropertyAnimator is being used,
                // so we don't need to call setDuration() again
            }
        });
    }
}
