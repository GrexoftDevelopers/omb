package com.oneminutebefore.workout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener,RegisterFragment.RegisterInteractionListener{

    private ViewPager vpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ViewPager vpSlide = (ViewPager)findViewById(R.id.vp_slide_show);
        ViewPagerAdapter sliderAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_1));
        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_2));
        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_3));
        vpSlide.setAdapter(sliderAdapter);

        vpLogin = (ViewPager) findViewById(R.id.vp_forms);
        ViewPagerAdapter formsAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        formsAdapter.addItem(new LoginFragment());
        formsAdapter.addItem(new RegisterFragment());
        vpLogin.setAdapter(formsAdapter);

    }

    @Override
    public void onLoginSuccessFul() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.login_successful), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSignUpClicked() {
        vpLogin.setCurrentItem(1);
    }

    @Override
    public void onRegisterSuccessFul() {
        vpLogin.setCurrentItem(0);

    }

    @Override
    public void onSignInClicked() {
        vpLogin.setCurrentItem(0);

    }


    private class ViewPagerAdapter extends FragmentPagerAdapter{


        private ArrayList<Fragment> fragments = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addItem(Fragment fragment){
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments != null && fragments.size() > position ? fragments.get(position) : null;
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }

}
