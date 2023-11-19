package com.gogit.gogit_app.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class FragmentHelper {
    public static boolean replaceFragment(FragmentManager fragmentManager,
                                       int containerId,
                                       Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return true;
    }

}
