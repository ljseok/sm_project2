package com.example.sm_project;

import android.renderscript.Element;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

public class SecondFragment extends androidx.fragment.app.Fragment {
    @Override
    public android.view.View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            android.os.Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@androidx.annotation.NonNull android.view.View view, android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}