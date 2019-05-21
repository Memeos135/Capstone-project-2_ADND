package com.example.macros;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ProfileFragmentTwo extends Fragment {

    public ProfileFragmentTwo(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_activity_fragment_two, container, false);

        setupRecycler(view);

        return view;
    }

    public void setupRecycler(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecordList(recyclerView);
    }

    public void setupRecordList(RecyclerView recyclerView){
        ArrayList<ProfileRecordSetter> recordList = new ArrayList<>();

        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());
        recordList.add(new ProfileRecordSetter());

        ProfileRecyclerAdapter profileRecyclerAdapter = new ProfileRecyclerAdapter(getContext(), recordList);
        recyclerView.setAdapter(profileRecyclerAdapter);

    }
}
