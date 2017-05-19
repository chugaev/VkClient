package com.vk.test.vkclient;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lenka on 16.04.17.
 */

public class PagerForFriends extends Fragment {


    ViewPager pager;
    PagerAdapter pagerAdapter;
    TabLayout tabLayout;

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_for_friends, container, false);
        Log.i("Menu 1", "onCreateView");
        pager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new PagerForFriends.MyFragmentPagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
//        pager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        Log.i("", "onCreateView: " + (tabLayout == null));
        tabLayout.setupWithViewPager(pager);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Друзья");
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ListFriendsFragment.newInstance(position);
            } else {
                return ListOnlineFriendsFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ДРУЗЕЙ";
                case 1:
                    return "В СЕТИ";
            }
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setVisibility(View.VISIBLE);
    }
}
