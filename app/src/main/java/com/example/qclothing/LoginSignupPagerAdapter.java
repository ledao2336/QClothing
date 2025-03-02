package com.example.qclothing;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class LoginSignupPagerAdapter extends FragmentStateAdapter {

    public LoginSignupPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LoginFormFragment();
            case 1:
                return new SignupFormFragment();
            default:
                return new LoginFormFragment(); // Default to Login
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Login and Signup tabs
    }
}