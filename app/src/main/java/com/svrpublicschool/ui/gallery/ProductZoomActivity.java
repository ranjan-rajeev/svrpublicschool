package com.svrpublicschool.ui.gallery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.svrpublicschool.BaseActivity;
import com.svrpublicschool.R;
import com.svrpublicschool.ui.chat.adapter.ProductZoomThumbnailAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductZoomActivity extends BaseActivity implements ProductZoomThumbnailAdapter.CallbacksProductThumbnail {

    private ViewPager viewPager;
    private RecyclerView thumbnailRecyclerView;
    private int lastPage;
    private LinearLayoutManager mLayoutManager;
    private ImageView leftArrow, rightArrow;

    protected String getTagName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_viewchat);

        init_views();

        //inflateGalleryViewPager((ProductDetail) getIntent().getSerializableExtra(Constants.INTENT_PARAM_PRODUCT_DETAILS));
        viewPager.setCurrentItem(0);
    }

    private void init_views() {
        viewPager = findViewById(R.id.view_pager);
        thumbnailRecyclerView = findViewById(R.id.rv_thumbnail_images);
        leftArrow = findViewById(R.id.pdpzoom_left_arrow);
        rightArrow = findViewById(R.id.pdpzoom_right_arrow);
    }


/*    private void inflateGalleryViewPager(final ProductDetail productDetail) {


        if (productDetail != null) {
            final String productName = productDetail.getProductName();
            if (!TextUtils.isEmpty(productName)) {
                toolbarTitle.setText(productName);
            }

            final ArrayList<String> imagesList = getGalleryImageList(productDetail);
            String firstImage = imagesList.get(0);

            FragmentStatePagerAdapter tutorialPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return GalleryZoomFragment.newInstance(firstImage, imagesList.get(position), productDetail.getIsProductNew(), productName, productDetail.getLuxIndicator() != null, productDetail.getProductListingId());
                }

                @Override
                public int getCount() {
                    return imagesList.size();
                }
            };
            viewPager.setAdapter(tutorialPagerAdapter);
            ProductZoomThumbnailAdapter adapterProductThumbnail = new ProductZoomThumbnailAdapter(this, imagesList);
            adapterProductThumbnail.setCallback(this);
            thumbnailRecyclerView.setAdapter(adapterProductThumbnail);
            thumbnailRecyclerView.setItemViewCacheSize(0);
            mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            thumbnailRecyclerView.setLayoutManager(mLayoutManager);
            ((TextView) findViewById(R.id.textViewCount)).setText((1) + "/" + tutorialPagerAdapter.getCount());
            leftArrow.setVisibility(View.GONE);
            rightArrow.setVisibility((viewPager.getAdapter().getCount() == 1) ? View.GONE : View.VISIBLE);
            final int inActiveColor = ContextCompat.getColor(this, R.color.colorGreyE2);
            final int activeColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);

            LinearLayout dotsLayout = findViewById(R.id.viewPagerCountDots);
            dotsLayout.removeAllViews();
            final int dotsCount = tutorialPagerAdapter.getCount();
            final TextView[] dots = new TextView[dotsCount];
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new TextView(getBaseContext());
                dots[i].setTextSize(36);
                dots[i].setTypeface(TypeFaceProvider.regular(this));
                dots[i].setText("\u2022");
                dots[i].setTextColor(inActiveColor);
                dotsLayout.addView(dots[i]);
            }
            if (dotsCount > 0) {
                dots[0].setTextColor(activeColor);
            }

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    try {

                        for (int i = 0; i < dotsCount; i++) {
                            dots[i].setTextColor(inActiveColor);
                        }
                        dots[position].setTextColor(activeColor);
                        adapterProductThumbnail.selectThumbnail(viewPager.getCurrentItem(), lastPage);
                        try {
                            thumbnailRecyclerView.smoothScrollToPosition(position);
                        } catch (RuntimeException ignored) {
                            //Silent catch
                        }
                        if (position > 0 || lastPage != 0) {
                            lastPage = position;
                        }

                        ((TextView) findViewById(R.id.textViewCount)).setText((position + 1) + "/" + tutorialPagerAdapter.getCount());
                        leftArrow.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                        rightArrow.setVisibility((position == viewPager.getAdapter().getCount() - 1) ? View.GONE : View.VISIBLE);
                    } catch (Exception e) {
                        lastPage = 0;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            thumbnailRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        //Dragging
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // viewPager.setCurrentItem(mLayoutManager.findFirstVisibleItemPosition());

                    }
                }


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                }
            });
            (leftArrow).setOnClickListener(new SingleClickListener() {
                @Override
                public void onClicked(View v) {
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                }
            });
            (rightArrow).setOnClickListener(new SingleClickListener() {
                @Override
                public void onClicked(View v) {
                    if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
            });
            thumbnailRecyclerView.setOnKeyListener((v, keyCode, event) -> {
                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                } else if (event.getAction() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return true;
                }
                return false;
            });
        }

    }

    private ArrayList<String> getGalleryImageList(ProductDetail productDetail) {
        ArrayList<String> imagesList = new ArrayList<>(8);
        for (GalleryImagesList galleryImages : productDetail.getGalleryImagesList()) {
            if ("Video".equalsIgnoreCase(galleryImages.getMediaType())) {
                for (Classification2 classificationList : galleryImages.getGalleryImages()) {
                    if ("thumbnail".equals(classificationList.getKey())) {
                        imagesList.add("Video" + classificationList.getValue());
                    }
                }
            } else {
                Map<String, String> imageMap = new HashMap<>(10);
                for (Classification2 classificationList : galleryImages.getGalleryImages()) {
                    if (null != classificationList.getKey() && null != classificationList.getValue()) {
                        imageMap.put(classificationList.getKey(), "https:" + classificationList.getValue());
                    }
                }
                String imageUrl = "";

                if (null != imageMap.get("mobilePdpView")) {
                    imageUrl = imageMap.get("mobilePdpView");
                } else
                if (null != imageMap.get("superZoom")) {
                    imageUrl = imageMap.get("superZoom");
                } else if (null != imageMap.get("zoom")) {
                    imageUrl = imageMap.get("zoom");
                } else if (null != imageMap.get("luxuryZoom")) {
                    imageUrl = imageMap.get("luxuryZoom");
                }
                if (null != imageUrl)
                    imagesList.add(imageUrl);
            }
        }

        return imagesList;
    }*/

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void ThumbnailClick(int position) {
        // Set the pager position when thumbnail clicked
        viewPager.setCurrentItem(position);
    }
}
