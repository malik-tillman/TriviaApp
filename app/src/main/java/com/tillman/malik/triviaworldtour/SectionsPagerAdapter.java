package com.tillman.malik.triviaworldtour;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount(){
     return mFragmentList.size();
    }


}
