package com.exercise.eugene.pixabay.viewimage;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exercise.eugene.pixabay.R;
import com.exercise.eugene.pixabay.model.Hit;
import com.exercise.eugene.pixabay.util.DragPhotoView;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity implements View.OnClickListener {
    private DragPhotoView mDragPhotoView;
    private Toolbar mToolbar;
    private LinearLayout mBottomView;
    private Hit mHit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_image);
        mHit = getIntent().getParcelableExtra("hit");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomView = (LinearLayout) findViewById(R.id.bottomView);
        mDragPhotoView = (DragPhotoView) findViewById(R.id.dragPhotoView);
        Picasso.with(this)
                .load(mHit.getWebformatURL())
                .priority(Picasso.Priority.HIGH)
                .noFade()
                .into(mDragPhotoView);
        initPhotoEnterAnim();
        initViews();
        initToolbar();
        initAnimation();
    }

    private void initViews() {
        if (!TextUtils.isEmpty(mHit.getUserImageURL())) {
            Picasso.with(this)
                    .load(mHit.getUserImageURL())
                    .priority(Picasso.Priority.NORMAL)
                    .into((CircularImageView) findViewById(R.id.userImage));
        } else {
            findViewById(R.id.userImage).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.userName)).setText(mHit.getUser());
        ((TextView) findViewById(R.id.views)).setText(String.valueOf(mHit.getViews()));
        ((TextView) findViewById(R.id.thumbsUp)).setText(String.valueOf(mHit.getLikes()));
        ((TextView) findViewById(R.id.star)).setText(String.valueOf(mHit.getFavorites()));
        ((TextView) findViewById(R.id.comment)).setText(String.valueOf(mHit.getComments()));
        findViewById(R.id.holderViews).setOnClickListener(this);
        findViewById(R.id.holderStar).setOnClickListener(this);
        findViewById(R.id.holderThumbs).setOnClickListener(this);
        findViewById(R.id.holderComment).setOnClickListener(this);
        mDragPhotoView.setOnMoveListener(new DragPhotoView.OnMoveLister() {
            @Override
            public void onMove(float alpha) {
                mToolbar.setAlpha(alpha);
                findViewById(R.id.bottomView).setAlpha(alpha);
            }
        });
    }

    private void initPhotoEnterAnim() {
        mDragPhotoView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mDragPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mOriginLeft = getIntent().getIntExtra("left", 0);
                        mOriginTop = getIntent().getIntExtra("top", 0);
                        mOriginHeight = getIntent().getIntExtra("height", 0);
                        mOriginWidth = getIntent().getIntExtra("width", 0);
                        mOriginCenterX = mOriginLeft + mOriginWidth / 2;
                        mOriginCenterY = mOriginTop + mOriginHeight / 2;
                        int[] location = new int[2];
                        mDragPhotoView.getLocationOnScreen(location);
                        mTargetHeight = (float) mDragPhotoView.getHeight();
                        mTargetWidth = (float) mDragPhotoView.getWidth();
                        mScaleX = (float) mOriginWidth / mTargetWidth;
                        mScaleY = (float) mOriginHeight / mTargetHeight;
                        float targetCenterX = location[0] + mTargetWidth / 2;
                        float targetCenterY = location[1] + mTargetHeight / 2;
                        mTranslationX = mOriginCenterX - targetCenterX;
                        mTranslationY = mOriginCenterY - targetCenterY;
                        mDragPhotoView.setTranslationX(mTranslationX);
                        mDragPhotoView.setTranslationY(mTranslationY);
                        mDragPhotoView.setScaleX(mScaleX);
                        mDragPhotoView.setScaleY(mScaleY);
                        performEnterAnimation();
                        mDragPhotoView.setMinScale(mScaleX);
                    }
                });
    }

    private void initToolbar() {
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        mToolbar.inflateMenu(R.menu.menu_view_image);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithAnimation();
            }
        });
    }

    /**
     * Animation Listeners
     */

    private void initAnimation() {
        mDragPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator animFadeTop;
                ObjectAnimator animFadeBottom;
                if (mToolbar.getVisibility() == View.VISIBLE) {
                    animFadeTop = ObjectAnimator.ofFloat(mToolbar, "alpha", 1, 0);
                    animFadeBottom = ObjectAnimator.ofFloat(mBottomView, "alpha", 1, 0);
                } else {
                    animFadeTop = ObjectAnimator.ofFloat(mToolbar, "alpha", 0, 1);
                    animFadeBottom = ObjectAnimator.ofFloat(mBottomView, "alpha", 0, 1);
                }
                AnimatorSet animSet = new AnimatorSet();
                animSet.setDuration(200);
                animSet.playTogether(animFadeTop, animFadeBottom);
                animSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animator.removeAllListeners();
                        if (mToolbar.getVisibility() == View.VISIBLE) {
                            mToolbar.setVisibility(View.GONE);
                            mBottomView.setVisibility(View.GONE);
                        } else {
                            mToolbar.setVisibility(View.VISIBLE);
                            mBottomView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animSet.start();
            }
        });
        mDragPhotoView.setOnTapListener(new DragPhotoView.OnTapListener() {
            @Override
            public void onTap(DragPhotoView view) {
                ObjectAnimator animFadeTop;
                ObjectAnimator animFadeBottom;
                if (mToolbar.getVisibility() == View.VISIBLE) {
                    animFadeTop = ObjectAnimator.ofFloat(mToolbar, "alpha", 1, 0);
                    animFadeBottom = ObjectAnimator.ofFloat(mBottomView, "alpha", 1, 0);
                } else {
                    animFadeTop = ObjectAnimator.ofFloat(mToolbar, "alpha", 0, 1);
                    animFadeBottom = ObjectAnimator.ofFloat(mBottomView, "alpha", 0, 1);
                }
                AnimatorSet animSet = new AnimatorSet();
                animSet.setDuration(200);
                animSet.playTogether(animFadeTop, animFadeBottom);
                animSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animator.removeAllListeners();
                        if (mToolbar.getVisibility() == View.VISIBLE) {
                            mToolbar.setVisibility(View.GONE);
                            mBottomView.setVisibility(View.GONE);
                        } else {
                            mToolbar.setVisibility(View.VISIBLE);
                            mBottomView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animSet.start();
            }
        });
        mDragPhotoView.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onExit(DragPhotoView view, float x, float y, float w, float h) {
                performExitAnimation(view, x, y, w, h);
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    int mOriginLeft;
    int mOriginTop;
    int mOriginHeight;
    int mOriginWidth;
    int mOriginCenterX;
    int mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;


    private void performExitAnimation(final DragPhotoView view, float x, float y, float w, float h) {
        view.finishAnimationCallBack();
        float viewX = mTargetWidth / 2 + x - mTargetWidth * mScaleX / 2;
        float viewY = mTargetHeight / 2 + y - mTargetHeight * mScaleY / 2;
        view.setX(viewX);
        view.setY(viewY);

        float centerX = view.getX() + mOriginWidth / 2;
        float centerY = view.getY() + mOriginHeight / 2;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;


        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), view.getX() + translateX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), view.getY() + translateY);
        ObjectAnimator animFade = ObjectAnimator.ofFloat(view, "alpha", 1, 0);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(200);
        animSet.playTogether(animX, animY, animFade);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSet.start();

    }

    private void finishWithAnimation() {
        ObjectAnimator animFadeT = ObjectAnimator.ofFloat(mToolbar, "alpha", 1, 0);
        ObjectAnimator animFadeB = ObjectAnimator.ofFloat(mBottomView, "alpha", 1, 0);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(200);
        animSet.playTogether(animFadeT, animFadeB);
        animSet.start();
        final DragPhotoView photoView = mDragPhotoView;
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();

        ObjectAnimator fadeOutLoadingCircle = ObjectAnimator.ofFloat(photoView, photoView.ALPHA, 1, 0);
        fadeOutLoadingCircle.setDuration(200);
        fadeOutLoadingCircle.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        AnimatorSet fadeOut = new AnimatorSet();
        fadeOut.play(fadeOutLoadingCircle);
        fadeOut.start();
    }

    private void performEnterAnimation() {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(mDragPhotoView.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDragPhotoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(mDragPhotoView.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDragPhotoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDragPhotoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDragPhotoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();


        ObjectAnimator animFadeT = ObjectAnimator.ofFloat(mToolbar, "alpha", 0, 1);
        ObjectAnimator animFadeB = ObjectAnimator.ofFloat(mBottomView, "alpha", 0, 1);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(600);
        animSet.playTogether(animFadeT, animFadeB);
        animSet.start();
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }

}
