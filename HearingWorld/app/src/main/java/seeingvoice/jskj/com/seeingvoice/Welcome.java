package seeingvoice.jskj.com.seeingvoice;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Welcome extends AppCompatActivity {
    private int currentItem;//current item of view pager
    private int itemLength;//length item of view pager
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private Button btnSkip, btnNext;
    private ViewPagerAdapter adapter;//adapter of viewPager

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_welcome1);
        MySP.getInstance().init(this);
        if(MySP.getInstance().getWelcome().equals("DONE")){
            goToHome();
        }

        image = findViewById(R.id.background);
        Drawable[] backgrounds = new Drawable[10];
        backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.w1);
        backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.w2);
        backgrounds[2] = ContextCompat.getDrawable(this, R.drawable.w3);
        backgrounds[3] = ContextCompat.getDrawable(this, R.drawable.w4);
        backgrounds[4] = ContextCompat.getDrawable(this, R.drawable.w5);
        backgrounds[5] = ContextCompat.getDrawable(this, R.drawable.w6);
        backgrounds[6] = ContextCompat.getDrawable(this, R.drawable.w7);
        backgrounds[7] = ContextCompat.getDrawable(this, R.drawable.w8);
        backgrounds[8] = ContextCompat.getDrawable(this, R.drawable.w9);
        backgrounds[9] = ContextCompat.getDrawable(this, R.drawable.w10);

        Crossfade(image, backgrounds, 2500);

        statusBar();
        viewPager = findViewById(R.id.view_pager);
        layoutDots = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        itemLength = viewPager.getAdapter().getCount();
        showDots(viewPager.getCurrentItem());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showDots(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (viewPager.getCurrentItem() == itemLength - 1){
                    btnSkip.setVisibility(View.GONE);
                    btnNext.setText(R.string.gotit);
                } else {
                    btnSkip.setVisibility(View.VISIBLE);
                    btnNext.setText(R.string.next);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySP.getInstance().setWelcome("DONE");
                goToHome();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == itemLength - 1) {//got it
                    MySP.getInstance().setWelcome("DONE");
                    goToHome();
                } else {//next
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }

    private void showDots(int pageNumber) {
        TextView [] dots = new TextView[itemLength];
        layoutDots.removeAllViews();
        for (int i = 0; i< dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            dots[i].setTextColor(ContextCompat.getColor(this,
                    (i == pageNumber ? R.color.dot_active : R.color.dot_incative)));
            layoutDots.addView(dots[i]);
        }
    }

    private void goToHome() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {
        String [] titles = getResources().getStringArray(R.array.slide_titles);
        String [] desc = getResources().getStringArray(R.array.slide_descriptions);
        int [] icons = {R.mipmap.icon_transparent, R.mipmap.icon_transparent, R.mipmap.icon_transparent,  R.drawable.ic_sv};

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(Welcome.this).inflate(R.layout.a_welcome, container, false);
            //background color
            ConstraintLayout layout = view.findViewById(R.id.bgLayout);
            // layout.setBackgroundColor(ContextCompat.getColor(Welcome1.this, bgColor[position]));
            //background color
            //titles
            TextView lblTitle = view.findViewById(R.id.lblTitle);
            lblTitle.setText(titles[position]);
            //titles
            //decs
            TextView lblDesc = view.findViewById(R.id.lblDesc);
            lblDesc.setText(desc[position]);
            //decs
            //icons
            ImageView imgIconSlider = view.findViewById(R.id.imgIconSlider);
            imgIconSlider.setImageResource(icons[position]);
            //icons
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void Crossfade(final ImageView image, final Drawable layers[], final int speedInMs) {
        class BackgroundGradientThread implements Runnable {
            Context mainContext;
            TransitionDrawable crossFader;
            boolean first = true;

            BackgroundGradientThread(Context c) {
                mainContext = c;
            }

            public void run() {
                Handler mHandler = new Handler(mainContext.getMainLooper());
                boolean reverse = false;

                while (true) {
                    if (!reverse) {
                        for (int i = 0; i < layers.length - 1; i++) {
                            Drawable tLayers[] = new Drawable[2];
                            tLayers[0] = layers[i];
                            tLayers[1] = layers[i + 1];

                            final TransitionDrawable tCrossFader = new TransitionDrawable(tLayers);
                            tCrossFader.setCrossFadeEnabled(true);

                            Runnable transitionRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    image.setImageDrawable(tCrossFader);
                                    tCrossFader.startTransition(speedInMs);
                                }
                            };

                            mHandler.post(transitionRunnable);

                            try {
                                Thread.sleep(speedInMs);
                            } catch (Exception e) {
                            }
                        }

                        reverse = true;
                    }
                    else if (reverse) {
                        for (int i = layers.length - 1; i > 0; i--) {
                            Drawable tLayers[] = new Drawable[2];
                            tLayers[0] = layers[i];
                            tLayers[1] = layers[i - 1];

                            final TransitionDrawable tCrossFader = new TransitionDrawable(tLayers);
                            tCrossFader.setCrossFadeEnabled(true);

                            Runnable transitionRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    image.setImageDrawable(tCrossFader);
                                    tCrossFader.startTransition(speedInMs);
                                }
                            };

                            mHandler.post(transitionRunnable);

                            try {
                                Thread.sleep(speedInMs);
                            } catch (Exception e) {
                            }
                        }

                        reverse = false;
                    }
                }
            }
        }

        Thread backgroundThread = new Thread(new BackgroundGradientThread(this));
        backgroundThread.start();
    }

}