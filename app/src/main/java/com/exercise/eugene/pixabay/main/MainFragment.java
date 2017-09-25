package com.exercise.eugene.pixabay.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exercise.eugene.pixabay.R;
import com.exercise.eugene.pixabay.adapters.CategoryRecyclerAdapter;
import com.exercise.eugene.pixabay.adapters.PixabayRecyclerAdapter;
import com.exercise.eugene.pixabay.client.Filter;
import com.exercise.eugene.pixabay.data.PixabayPrefs;
import com.exercise.eugene.pixabay.model.Hit;
import com.exercise.eugene.pixabay.util.EndlessParentScrollListener;
import com.exercise.eugene.pixabay.util.PixabayImageView;
import com.exercise.eugene.pixabay.util.Prefs;
import com.exercise.eugene.pixabay.viewimage.ViewImageActivity;

import java.util.List;

public class MainFragment extends Fragment implements MainContract.View {

    private Activity mHost;
    private MainContract.Presenter mPresenter;
    private int mLayoutManagerType = PixabayPrefs.PREF_LAYOUT_MANAGER_LINEAR;
    private Filter.TYPE mType = Filter.TYPE.ALL;

    public MainFragment() {
        // Requires empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManagerType = Prefs.getInt(PixabayPrefs.PREF_LAYOUT_MANAGER, PixabayPrefs.PREF_LAYOUT_MANAGER_GRID);
        setHasOptionsMenu(true);
        mHost = getActivity();
        mCategoryAdapter = new CategoryRecyclerAdapter(mHost);
        mPixabayAdapter = new PixabayRecyclerAdapter(mHost);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private NestedScrollView mNestedScrollView;
    private PixabayRecyclerAdapter mPixabayAdapter;
    private RecyclerView mRecyclerPixabay;
    private RecyclerView.LayoutManager mLayoutManager;
    private EndlessParentScrollListener mEndlessScrollListener;
    private CategoryRecyclerAdapter mCategoryAdapter;
    private RecyclerView mRecyclerCategories;

    private ProgressBar mProgressBar;
    private LinearLayout mErrorView;
    private TextView mTextError;
    private Button mButtonReset;

    protected String mCategory = null;
    protected String mQuery = null;

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mHost.setTitle("Editor's Choice Popular");
        mNestedScrollView = v.findViewById(R.id.nestedScrollView);
        // Category Adapter
        mRecyclerCategories = v.findViewById(R.id.recycler_categories);
        mRecyclerCategories.setNestedScrollingEnabled(false);
        mRecyclerCategories.setLayoutManager(new LinearLayoutManager(mHost, LinearLayoutManager.HORIZONTAL, false));
        mCategoryAdapter.setListener(mCategoryListener);
        mRecyclerCategories.setAdapter(mCategoryAdapter);
        // Pixabay Adapter
        mRecyclerPixabay = v.findViewById(R.id.recycler_pixabay);
        mRecyclerPixabay.setNestedScrollingEnabled(false);
        mRecyclerPixabay.setAdapter(mPixabayAdapter);
        mPixabayAdapter.setListener(mPixabayListener);
        setRecyclerViewLayoutManager(mLayoutManagerType);
        // Views
        mProgressBar = v.findViewById(R.id.progressBar);
        mErrorView = v.findViewById(R.id.errorView);
        mTextError = v.findViewById(R.id.textError);
        mButtonReset = v.findViewById(R.id.buttonReset);
        // Init Api
        mPresenter.start();
    }

    @Override
    public void setActionbar(boolean showNavUp, String title) {
        mListener.setActionbar(showNavUp, title);
    }

    @Override
    public void setType(Filter.TYPE type) {
        mType = type;
    }

    @Override
    public void setCategoryString(String category) {
        mCategory = category;
    }

    @Override
    public void setSearchString(String search) {
        mQuery = search;
    }

    @Override
    public void showCategoryRecycler(boolean show) {
        mRecyclerCategories.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPixabayImageAdapter(List<Hit> hitList) {
        mPixabayAdapter.setHitList(hitList);
    }

    @Override
    public void resetPixabayAdapter() {
        mPixabayAdapter.resetHitList();
    }

    @Override
    public void resetEndlessScrollListener() {
        if (mEndlessScrollListener != null) {
            mEndlessScrollListener.resetState();
        }
    }

    @Override
    public void showNoItems() {
        mProgressBar.setVisibility(View.GONE);
        mButtonReset.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mTextError.setText(R.string.no_results);
    }

    @Override
    public void showErrorView(String errorMessage, final String category, final int page) {
        mTextError.setText(errorMessage);
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mButtonReset.setVisibility(View.VISIBLE);
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mType) {
                    case ALL:
                        mPresenter.loadFeaturedImages(Filter.ORDER.POPULAR, true);
                        break;
                    case CATEGORY:
                        mPresenter.loadCategoryImages(Filter.ORDER.POPULAR, mCategory, page);
                        break;
                    case SEARCH:
                        mPresenter.loadSearchImages(Filter.ORDER.POPULAR, mQuery, page);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void resetErrorView() {
        if (mProgressBar.getVisibility() == View.GONE) {
            mTextError.setText(null);
            mProgressBar.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
            mButtonReset.setVisibility(View.VISIBLE);
            mButtonReset.setOnClickListener(null);
        }
    }

    protected void scrollToTop() {
        mNestedScrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Adapter Listeners
     */
    private CategoryRecyclerAdapter.CategoryAdapterListener mCategoryListener = new CategoryRecyclerAdapter.CategoryAdapterListener() {
        @Override
        public void onItemClicked(String categoryName) {
            mPresenter.loadCategoryImages(Filter.ORDER.POPULAR, categoryName, 1);
        }
    };

    private PixabayRecyclerAdapter.PixabayAdapterListener mPixabayListener = new PixabayRecyclerAdapter.PixabayAdapterListener() {
        @Override
        public void onItemClicked(Hit hit, PixabayImageView mImage) {
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            int location[] = new int[2];
            mImage.requestLayout();
            mImage.getLocationOnScreen(location);
            intent.putExtra("left", location[0]);
            intent.putExtra("top", location[1]);
            intent.putExtra("height", mImage.getHeight());
            intent.putExtra("width", mImage.getWidth());
            intent.putExtra("hit", hit);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }
    };

    /**
     * Menu
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        switch (mLayoutManagerType) {
            case PixabayPrefs.PREF_LAYOUT_MANAGER_LINEAR:
                inflater.inflate(R.menu.menu_grid, menu);
                break;
            case PixabayPrefs.PREF_LAYOUT_MANAGER_GRID:
                inflater.inflate(R.menu.menu_list, menu);
                break;
            default:
                inflater.inflate(R.menu.menu_grid, menu);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_grid:
            case R.id.action_show_list:
                if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                    setRecyclerViewLayoutManager(PixabayPrefs.PREF_LAYOUT_MANAGER_GRID);
                } else if (mLayoutManager instanceof LinearLayoutManager) {
                    setRecyclerViewLayoutManager(PixabayPrefs.PREF_LAYOUT_MANAGER_LINEAR);
                }
                return false;
            default:
                break;
        }
        return false;
    }

    /**
     * Handle Layout Manager
     */
    public void setRecyclerViewLayoutManager(int layoutManagerType) {
        if (mLayoutManagerType != layoutManagerType) {
            Prefs.putInt(PixabayPrefs.PREF_LAYOUT_MANAGER, layoutManagerType);
        }
        mLayoutManagerType = layoutManagerType;
        switch (layoutManagerType) {
            case PixabayPrefs.PREF_LAYOUT_MANAGER_LINEAR:
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                break;
            case PixabayPrefs.PREF_LAYOUT_MANAGER_GRID:
                mLayoutManager = new LinearLayoutManager(mHost);
                break;
            default:
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
        mRecyclerPixabay.setLayoutManager(mLayoutManager);
        mEndlessScrollListener = new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                switch (mType) {
                    case ALL:
                        mPresenter.loadFeaturedImages(Filter.ORDER.POPULAR, true);
                        break;
                    case CATEGORY:
                        mPresenter.loadCategoryImages(Filter.ORDER.POPULAR, mCategory, page);
                        break;
                    case SEARCH:
                        mPresenter.loadSearchImages(Filter.ORDER.POPULAR, mQuery, page);
                        break;
                    default:
                        break;
                }
            }
        };
        mNestedScrollView.setOnScrollChangeListener(mEndlessScrollListener);
        mHost.invalidateOptionsMenu();
    }

    /**
     * Interface between fragment and activity
     */
    private OnFragmentInteractionListener mListener;

    interface OnFragmentInteractionListener {
        void setActionbar(boolean showNavUp, String title);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
