package com.example.proyecto.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.proyecto.R;

public class MisEquiposFragment extends Fragment {

    public MisEquiposFragment() {
        // Constructor público vacío obligatorio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_equipos, container, false);
    }
}
