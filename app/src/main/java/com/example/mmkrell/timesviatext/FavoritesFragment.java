package com.example.mmkrell.timesviatext;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class FavoritesFragment extends Fragment {

    private MyAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        HashSet<String> favoritesSet = new HashSet<>(sharedPreferences.getStringSet("favorites", new HashSet<String>()));

        String[] favoritesArray = favoritesSet.toArray(new String[] {});
        // TODO: sort by stop name instead of stop code
        Arrays.sort(favoritesArray, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            }
        });

        adapter = new MyAdapter(getContext(), favoritesArray);
        recyclerView.setAdapter(adapter);

        return v;
    }

    public MyAdapter getAdapter() {
        return adapter;
    }
}