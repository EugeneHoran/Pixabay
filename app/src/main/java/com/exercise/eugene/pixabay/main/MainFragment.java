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
import com.exercise.eugene.pixabay.adapters.CategoryAdapter;
import com.exercise.eugene.pixabay.adapters.PixabayAdapter;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Hit;
import com.exercise.eugene.pixabay.util.EndlessParentScrollListener;
import com.exercise.eugene.pixabay.util.PixabayImageView;
import com.exercise.eugene.pixabay.viewimage.ViewImageActivity;

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
    private PixabayAdapter mPixabayAdapter;
    private RecyclerView mRecyclerPixabay;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutManagerType mCurrentLayoutManagerType;
    private EndlessParentScrollListener mEndlessScrollListener;
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
        CategoryAdapter mCategoryAdapter = new CategoryAdapter(mHost);
        mCategoryAdapter.setListener(mCategoryListener);
        mRecyclerCategories.setAdapter(mCategoryAdapter);
        // Pixabay Adapter
        mRecyclerPixabay = v.findViewById(R.id.recycler_pixabay);
        mRecyclerPixabay.setNestedScrollingEnabled(false);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        mRecyclerPixabay.setAdapter(mPixabayAdapter);
        mPixabayAdapter.setListener(mPixabayListener);
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
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
                mPresenter.loadPixabayImages(category, PixabayService.ORDER.popular, mQuery, page);
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
    private CategoryAdapter.CategoryAdapterListener mCategoryListener = new CategoryAdapter.CategoryAdapterListener() {
        @Override
        public void onItemClicked(String categoryName) {
            mPresenter.loadPixabayImages(categoryName, PixabayService.ORDER.popular, null, 1);
        }
    };

    private PixabayAdapter.PixabayAdapterListener mPixabayListener = new PixabayAdapter.PixabayAdapterListener() {
        @Override
        public void onItemClicked(String imageUrl, PixabayImageView mImage) {
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            int location[] = new int[2];
            mImage.requestLayout();
            mImage.getLocationOnScreen(location);
            intent.putExtra("left", location[0]);
            intent.putExtra("top", location[1]);
            intent.putExtra("height", mImage.getHeight());
            intent.putExtra("width", mImage.getWidth());
            Bundle bundle = new Bundle();
            bundle.putString("url", imageUrl);
            intent.putExtras(bundle);
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
                mPresenter.loadPixabayImages(mCategory, PixabayService.ORDER.popular, mQuery, page);
            }
        };
        mNestedScrollView.setOnScrollChangeListener(mEndlessScrollListener);
        mHost.invalidateOptionsMenu();
    }

    /**
     * Interface between fragment and activity
     */
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
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
