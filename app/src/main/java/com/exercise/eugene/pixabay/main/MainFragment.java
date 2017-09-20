package com.exercise.eugene.pixabay.main;

import android.app.Activity;
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

import com.exercise.eugene.pixabay.R;
import com.exercise.eugene.pixabay.adapters.CategoryAdapter;
import com.exercise.eugene.pixabay.adapters.PixabayAdapter;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Hit;
import com.exercise.eugene.pixabay.util.EndlessParentScrollListener;

import java.util.List;

public class MainFragment extends Fragment implements MainContract.View {

    private Activity mHost;
    private MainContract.Presenter mPresenter;

    public MainFragment() {
        // Requires empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mHost = getActivity();
        mPixabayAdapter = new PixabayAdapter(mHost);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private NestedScrollView mNestedScrollView;
    private PixabayAdapter mPixabayAdapter;
    private RecyclerView mRecyclerPixabay;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutManagerType mCurrentLayoutManagerType;
    private EndlessParentScrollListener mEndlessScrollListener;

    protected String mCategory = null;

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mHost.setTitle("Editor's Choice Popular");
        mNestedScrollView = v.findViewById(R.id.nestedScrollView);
        // Category Adapter
        RecyclerView mRecyclerCategories = v.findViewById(R.id.recycler_categories);
        mRecyclerCategories.setNestedScrollingEnabled(false);
        mRecyclerCategories.setLayoutManager(new LinearLayoutManager(mHost, LinearLayoutManager.HORIZONTAL, false));
        CategoryAdapter mCategoryAdapter = new CategoryAdapter(mHost);
        mCategoryAdapter.setListener(categoryAdapterListener);
        mRecyclerCategories.setAdapter(mCategoryAdapter);
        // Pixabay Adapter
        mRecyclerPixabay = v.findViewById(R.id.recycler_pixabay);
        mRecyclerPixabay.setNestedScrollingEnabled(false);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        mRecyclerPixabay.setAdapter(mPixabayAdapter);
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
    }

    CategoryAdapter.CategoryAdapterListener categoryAdapterListener = new CategoryAdapter.CategoryAdapterListener() {
        @Override
        public void onItemClicked(String categoryName) {
            resetData(false);
            ((MainActivity) mHost).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCategory = categoryName;
            mPresenter.loadPixabayImages(categoryName, PixabayService.ORDER.popular, 1);
            mHost.setTitle(categoryName);
        }
    };

    protected void resetData(boolean refreshOriginalList) {
        mCategory = null;
        if (mEndlessScrollListener != null) {
            mEndlessScrollListener.resetState();
        }
        mPixabayAdapter.setFreshList();
        if (refreshOriginalList) {
            mHost.setTitle("Editor's Choice Popular");
            mPresenter.start();
        }
    }

    protected void scrollToTop() {
        mNestedScrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPixabayImageAdapter(List<Hit> hitList) {
        mPixabayAdapter.setHitList(hitList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mRecyclerPixabay.getLayoutManager() == null) {
            inflater.inflate(R.menu.menu_list, menu);
        } else {
            if (mRecyclerPixabay.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                inflater.inflate(R.menu.menu_grid, menu);
            } else if (mRecyclerPixabay.getLayoutManager() instanceof LinearLayoutManager) {
                inflater.inflate(R.menu.menu_list, menu);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_grid:
            case R.id.action_show_list:
                if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                    setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                } else if (mLayoutManager instanceof LinearLayoutManager) {
                    setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
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
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(mHost);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        }
        mRecyclerPixabay.setLayoutManager(mLayoutManager);
        mEndlessScrollListener = new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mPresenter.loadPixabayImages(mCategory, PixabayService.ORDER.popular, page);
            }
        };
        mNestedScrollView.setOnScrollChangeListener(mEndlessScrollListener);
        mHost.invalidateOptionsMenu();
    }
}
