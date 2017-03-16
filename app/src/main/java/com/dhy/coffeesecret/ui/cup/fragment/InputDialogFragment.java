package com.dhy.coffeesecret.ui.cup.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.dhy.coffeesecret.R;

import static com.dhy.coffeesecret.R.string.acidity;
import static com.dhy.coffeesecret.R.string.after_taste;
import static com.dhy.coffeesecret.R.string.baked;
import static com.dhy.coffeesecret.R.string.balance;
import static com.dhy.coffeesecret.R.string.dry_and_frag;
import static com.dhy.coffeesecret.R.string.faced;
import static com.dhy.coffeesecret.R.string.flavor;
import static com.dhy.coffeesecret.R.string.overall;
import static com.dhy.coffeesecret.R.string.overdev;
import static com.dhy.coffeesecret.R.string.scorched;
import static com.dhy.coffeesecret.R.string.sweet;
import static com.dhy.coffeesecret.R.string.taste;
import static com.dhy.coffeesecret.R.string.tipped;
import static com.dhy.coffeesecret.R.string.underdev;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnValueChangeListener} interface
 * to handle interaction events.
 * Use the {@link InputDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputDialogFragment extends DialogFragment
        implements View.OnClickListener, ItemInputFragment.OnItemValueChangeListener {

    private static final String CURRENT_ITEM = "currentItem";
    private static final String DEFAULT_VALUE = "defaultValue";


    private String[] itemName;

    private final static String IS_SLIDABLE = "isSlidable";

    private String[] defaultValue;
    private int currentItem;

    private OnValueChangeListener mListener;

    private View mContentView;
    private ImageButton mLeftButton;
    private ImageButton mRightButton;
    private ViewPager mViewPager;
    private ItemPageAdapter mAdapter;
    private Button mButton;
    private boolean isSlidable;

    public InputDialogFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param defaultValue defaultValue
     * @return A new instance of fragment InputDialogFragment.
     */
    public static InputDialogFragment newInstance(boolean isSlidable, float[] defaultValue) {
        InputDialogFragment fragment = new InputDialogFragment();
        Bundle args = new Bundle();
        String[] values = null;
        if (defaultValue != null) {
            values = new String[defaultValue.length];
            for (int i = 0; i < defaultValue.length; i++) {
                values[i] = defaultValue[i] + "";
            }
        }
        args.putStringArray(DEFAULT_VALUE, values);
        args.putBoolean(IS_SLIDABLE, isSlidable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemName = new String[]{getString(dry_and_frag), getString(flavor), getString(after_taste), getString(acidity),
                getString(taste), getString(sweet), getString(balance), getString(overall), getString(underdev), getString(overdev),
                getString(baked), getString(scorched), getString(tipped), getString(faced)};

        if (getArguments() != null) {
            defaultValue = getArguments().getStringArray(DEFAULT_VALUE);
            isSlidable = getArguments().getBoolean(IS_SLIDABLE);
        }
        if (defaultValue == null) {
            defaultValue = new String[]{"0", "0", "0", "0",
                    "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onResume() {

        if (mViewPager != null) {
            if (isSlidable) {
                if (!mLeftButton.isEnabled()) {
                    mLeftButton.setEnabled(true);
                }
                if (!mRightButton.isEnabled()) {
                    mRightButton.setEnabled(true);
                }
            }
            mViewPager.setCurrentItem(currentItem);
        }
        super.onResume();
    }

    public void show(int currentItem, FragmentManager manager, String tag) {
        this.currentItem = currentItem;
        this.show(manager, tag);
    }

    /**
     * @see InputDialogFragment#show(int, FragmentManager, String)
     */
    @Deprecated
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (isSlidable) {
            mContentView = inflater.inflate(R.layout.fragment_input_slidable, container, false);
        } else {
            mContentView = inflater.inflate(R.layout.fragment_input, container, false);
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setCanceledOnTouchOutside(false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        mViewPager = (ViewPager) mContentView.findViewById(R.id.vp);
        mAdapter = new ItemPageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(currentItem);
        if (isSlidable) {
            mLeftButton = (ImageButton) mContentView.findViewById(R.id.ib_left);
            mRightButton = (ImageButton) mContentView.findViewById(R.id.ib_right);


            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                    if (position == 0) {
                        mLeftButton.setEnabled(false);
                    } else if (position == 1) {
                        mLeftButton.setEnabled(true);
                    }
                    if (position == mAdapter.getCount() - 1) {
                        mRightButton.setEnabled(false);
                    } else if (position == mAdapter.getCount() - 2) {
                        mRightButton.setEnabled(true);
                    }
                }

            });
            mLeftButton.setOnClickListener(this);
            mRightButton.setOnClickListener(this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnValueChangeListener) {
            mListener = (OnValueChangeListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnValueChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        currentItem = mViewPager.getCurrentItem();
        if (mLeftButton.equals(view)) {
            if (currentItem > 0) {
                mViewPager.setCurrentItem(--currentItem);
            }
        } else {
            if (currentItem < mAdapter.getCount() - 1) {
                mViewPager.setCurrentItem(++currentItem);
            }
        }
    }

    @Override
    public void onItemValueChange(int position, float value) {
        defaultValue[position] = String.valueOf(value);
        if (mListener != null) {
            mListener.onValueChange(position, value);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnValueChangeListener {
        void onValueChange(int position, float value);
    }

    class ItemPageAdapter extends FragmentPagerAdapter {

        public ItemPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ItemInputFragment fragment =
                    ItemInputFragment.newInstance(position, itemName[position], defaultValue[position]);
            return fragment;
        }

        @Override
        public int getCount() {
            return itemName.length;
        }
    }
}
