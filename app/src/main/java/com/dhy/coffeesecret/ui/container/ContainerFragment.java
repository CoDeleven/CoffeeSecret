package com.dhy.coffeesecret.ui.container;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.astuetz.PagerSlidingTabStrip;
import com.dhy.coffeesecret.MainActivity;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.container.fragments.BeanListFragment;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ContainerFragment extends Fragment {

    private static final String TAG = "ContainerFragment";
    private final String[] TITLES = {"全部", "中美", "南美", "大洋", "亚洲", "非洲", "其它"};

    private OnContainerInteractionListener mListener;
    private View mContent;
    private ViewPager containerPager = null;
    private PagerSlidingTabStrip containerTabs = null;
    private PopupWindow mSortPopupWindow;
    private List<Fragment> fragments = null;
    private Context context;
    private boolean isSortWindowShowing = false;

    public ContainerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContent = inflater.inflate(R.layout.fragment_container, container, false);
        context = getActivity();
        Log.d(TAG, "onCreateView: " + getActivity());

        initView();
        initPager();

        return mContent;
    }

    public void initView() {
        containerPager = (ViewPager) mContent.findViewById(R.id.container_pager);
        containerPager.setAdapter(new MyPagerAdapter(((MainActivity) context).getSupportFragmentManager()));
        containerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentPage = containerPager.getCurrentItem();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Bind the tabs to the ViewPager
        containerTabs = (PagerSlidingTabStrip) mContent.findViewById(R.id.container_tabs);
        containerTabs.setTextColor(getResources().getColor(R.color.white));
        containerTabs.setViewPager(containerPager);
    }

    private void initPager() {

        fragments = new ArrayList<>();

        for (int i = 0; i < TITLES.length; i++) {
            BeanListFragment fragment = new BeanListFragment();
            fragment.setTitle(TITLES[i]);
            fragment.setContext(context);
            fragments.add(fragment);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void initPopupWindow() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final View contentView = inflater.inflate(R.layout.ppw_conta_screen, null);

        mSortPopupWindow = new PopupWindow(contentView, width * 2 / 3, WRAP_CONTENT);
        mSortPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mSortPopupWindow.setOutsideTouchable(true);
        mSortPopupWindow.setFocusable(true);
//        mScreenButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isSortWindowShowing) {
//                    mSortPopupWindow.dismiss();
//                } else {
//                    mSortPopupWindow.showAsDropDown(mScreenButton);
//                    mShade.setVisibility(View.VISIBLE);
//                }
//                isSortWindowShowing = !isSortWindowShowing;
//            }
//        });
//
//        mSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mShade.setVisibility(View.GONE);
//            }
//        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onContainerInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContainerInteractionListener) {
            mListener = (OnContainerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContainerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnContainerInteractionListener {
        void onContainerInteraction(Uri uri);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

    }
}
