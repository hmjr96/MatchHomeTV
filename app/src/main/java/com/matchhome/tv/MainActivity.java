package com.matchhome.tv;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final long SLIDE_INTERVAL_MS = 8000L;
    private static final int BG = Color.rgb(8, 11, 18);
    private static final int CARD = Color.rgb(20, 26, 38);
    private static final int CARD_FOCUSED = Color.rgb(31, 47, 69);
    private static final int ACCENT = Color.rgb(85, 167, 255);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<MatchSlide> slides = new ArrayList<>();

    private ImageView heroImage;
    private LinearLayout heroTextBlock;
    private TextView matchBadge;
    private TextView competition;
    private TextView teamOne;
    private TextView teamTwo;
    private TextView kickoff;
    private TextView dots;
    private GridLayout appGrid;
    private TextView appCount;
    private int slideIndex = 0;
    private boolean sliderRunning = false;

    private final Runnable slideRunnable = new Runnable() {
        @Override public void run() {
            if (!sliderRunning || slides.isEmpty()) return;
            slideIndex = (slideIndex + 1) % slides.size();
            showSlide(slideIndex, true);
            handler.postDelayed(this, SLIDE_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureWindow();
        createDemoSlides();
        setContentView(buildUi());
        showSlide(0, false);
        loadInstalledApps();
    }

    private void configureWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void createDemoSlides() {
        slides.clear();
        slides.add(new MatchSlide(
                "مباراة اليوم", "الدوري الرئيسي", "الفريق الأزرق", "الفريق الأحمر",
                "21:00", R.drawable.stadium_1));
        slides.add(new MatchSlide(
                "قمة الليلة", "بطولة الأندية", "نجوم المدينة", "اتحاد الساحل",
                "22:00", R.drawable.stadium_2));
        slides.add(new MatchSlide(
                "مباراة منتظرة", "كأس الموسم", "أسود الأطلس", "نسور العاصمة",
                "20:30", R.drawable.stadium_3));
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setPadding(dp(42), dp(26), dp(42), dp(28));

        root.addView(buildHero(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.54f));
        root.addView(buildAppsSection(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.46f));
        return root;
    }

    private View buildHero() {
        FrameLayout hero = new FrameLayout(this);
        hero.setClipToOutline(true);
        hero.setBackground(rounded(Color.rgb(13, 17, 25), dp(24), 0, Color.TRANSPARENT));

        heroImage = new ImageView(this);
        heroImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        hero.addView(heroImage, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View shade = new View(this);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                new int[]{0xEE080B12, 0xB8080B12, 0x40080B12, 0x08080B12});
        shade.setBackground(gradient);
        hero.addView(shade, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        overlay.setPadding(dp(56), dp(28), dp(56), dp(32));
        hero.addView(overlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout topLine = new LinearLayout(this);
        topLine.setOrientation(LinearLayout.HORIZONTAL);
        topLine.setGravity(Gravity.CENTER_VERTICAL);
        topLine.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView brand = text("MATCH HOME", 18, Color.WHITE, Typeface.BOLD);
        brand.setLetterSpacing(0.08f);
        topLine.addView(brand);

        TextView date = text(new SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(new Date()),
                15, 0xFFB9C5D6, Typeface.NORMAL);
        LinearLayout.LayoutParams dateLp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        date.setGravity(Gravity.LEFT);
        topLine.addView(date, dateLp);
        overlay.addView(topLine, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        heroTextBlock = new LinearLayout(this);
        heroTextBlock.setOrientation(LinearLayout.VERTICAL);
        heroTextBlock.setGravity(Gravity.RIGHT);
        heroTextBlock.setPadding(0, dp(26), 0, 0);
        overlay.addView(heroTextBlock, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        matchBadge = text("مباراة اليوم", 16, Color.WHITE, Typeface.BOLD);
        matchBadge.setGravity(Gravity.CENTER);
        matchBadge.setPadding(dp(18), dp(7), dp(18), dp(7));
        matchBadge.setBackground(rounded(0xCC245A92, dp(18), 0, Color.TRANSPARENT));
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeLp.gravity = Gravity.RIGHT;
        heroTextBlock.addView(matchBadge, badgeLp);

        competition = text("", 16, 0xFFB8C7DA, Typeface.NORMAL);
        competition.setPadding(0, dp(12), 0, 0);
        heroTextBlock.addView(competition);

        LinearLayout matchLine = new LinearLayout(this);
        matchLine.setOrientation(LinearLayout.HORIZONTAL);
        matchLine.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        matchLine.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        matchLine.setPadding(0, dp(5), 0, 0);

        teamOne = text("", 31, Color.WHITE, Typeface.BOLD);
        teamTwo = text("", 31, Color.WHITE, Typeface.BOLD);
        TextView versus = text("  VS  ", 16, 0xFF8BA1BA, Typeface.BOLD);
        matchLine.addView(teamOne);
        matchLine.addView(versus);
        matchLine.addView(teamTwo);
        heroTextBlock.addView(matchLine);

        kickoff = text("", 22, 0xFFEAF3FF, Typeface.BOLD);
        kickoff.setPadding(0, dp(12), 0, 0);
        heroTextBlock.addView(kickoff);

        TextView hint = text("الواجهة تتبدل تلقائياً • استخدم الريموت لاختيار التطبيق", 14,
                0xFFAFC0D2, Typeface.NORMAL);
        hint.setPadding(0, dp(8), 0, 0);
        heroTextBlock.addView(hint);

        dots = text("●  ○  ○", 14, 0xFFD8E8FF, Typeface.NORMAL);
        dots.setGravity(Gravity.LEFT);
        overlay.addView(dots, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return hero;
    }

    private View buildAppsSection() {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(0, dp(18), 0, 0);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView title = text("التطبيقات", 22, Color.WHITE, Typeface.BOLD);
        header.addView(title);
        appCount = text("جاري التحميل…", 14, 0xFF8FA2B9, Typeface.NORMAL);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        appCount.setGravity(Gravity.LEFT);
        header.addView(appCount, countLp);
        section.addView(header);

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setFillViewport(true);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, dp(12), 0, dp(4));

        appGrid = new GridLayout(this);
        appGrid.setRowCount(2);
        appGrid.setColumnCount(GridLayout.UNDEFINED);
        appGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        appGrid.setUseDefaultMargins(false);
        appGrid.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(appGrid, new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        section.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return section;
    }

    private void showSlide(final int index, boolean animate) {
        if (slides.isEmpty() || heroImage == null || heroTextBlock == null) return;
        final MatchSlide s = slides.get(index);
        Runnable update = new Runnable() {
            @Override public void run() {
                heroImage.setImageResource(s.backgroundRes);
                matchBadge.setText(s.badge);
                competition.setText(s.competition);
                teamOne.setText(s.teamOne);
                teamTwo.setText(s.teamTwo);
                kickoff.setText("⏱  " + s.kickoff);
                StringBuilder indicator = new StringBuilder();
                for (int i = 0; i < slides.size(); i++) {
                    if (i > 0) indicator.append("   ");
                    indicator.append(i == index ? "●" : "○");
                }
                dots.setText(indicator.toString());
            }
        };

        if (!animate) {
            update.run();
            return;
        }
        heroTextBlock.animate().alpha(0f).setDuration(170).withEndAction(new Runnable() {
            @Override public void run() {
                update.run();
                heroTextBlock.animate().alpha(1f).setDuration(260).start();
            }
        }).start();
        heroImage.animate().alpha(0.72f).setDuration(170).withEndAction(new Runnable() {
            @Override public void run() {
                heroImage.setImageResource(s.backgroundRes);
                heroImage.animate().alpha(1f).setDuration(320).start();
            }
        }).start();
    }

    private void startSlider() {
        if (sliderRunning) return;
        sliderRunning = true;
        handler.removeCallbacks(slideRunnable);
        handler.postDelayed(slideRunnable, SLIDE_INTERVAL_MS);
    }

    private void stopSlider() {
        sliderRunning = false;
        handler.removeCallbacks(slideRunnable);
    }

    private void loadInstalledApps() {
        executor.execute(new Runnable() {
            @Override public void run() {
                final List<ResolveInfo> apps = queryTvApps();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        populateApps(apps);
                    }
                });
            }
        });
    }

    private List<ResolveInfo> queryTvApps() {
        PackageManager pm = getPackageManager();
        Map<String, ResolveInfo> unique = new LinkedHashMap<>();

        Intent tv = new Intent(Intent.ACTION_MAIN);
        tv.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        addResults(unique, pm.queryIntentActivities(tv, PackageManager.MATCH_ALL));

        Intent regular = new Intent(Intent.ACTION_MAIN);
        regular.addCategory(Intent.CATEGORY_LAUNCHER);
        addResults(unique, pm.queryIntentActivities(regular, PackageManager.MATCH_ALL));

        unique.remove(getPackageName());
        List<ResolveInfo> list = new ArrayList<>(unique.values());
        Collections.sort(list, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                CharSequence la = a.loadLabel(getPackageManager());
                CharSequence lb = b.loadLabel(getPackageManager());
                return String.valueOf(la).compareToIgnoreCase(String.valueOf(lb));
            }
        });
        return list;
    }

    private void addResults(Map<String, ResolveInfo> target, List<ResolveInfo> results) {
        if (results == null) return;
        for (ResolveInfo info : results) {
            if (info == null || info.activityInfo == null) continue;
            String pkg = info.activityInfo.packageName;
            if (pkg == null || pkg.equals(getPackageName())) continue;
            if (!target.containsKey(pkg)) target.put(pkg, info);
        }
    }

    private void populateApps(List<ResolveInfo> apps) {
        if (appGrid == null) return;
        appGrid.removeAllViews();
        appCount.setText(apps.size() + " تطبيق");
        if (apps.isEmpty()) {
            TextView empty = text("لم يتم العثور على تطبيقات قابلة للتشغيل.", 17,
                    0xFFB0C0D0, Typeface.NORMAL);
            appGrid.addView(empty);
            return;
        }

        View first = null;
        int col = 0;
        int row = 0;
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            View card = buildAppCard(info);
            if (first == null) first = card;
            row = i % 2;
            col = i / 2;
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                    GridLayout.spec(row), GridLayout.spec(col));
            lp.width = dp(164);
            lp.height = dp(132);
            lp.setMargins(dp(6), dp(6), dp(6), dp(6));
            appGrid.addView(card, lp);
        }
        if (first != null) first.requestFocus();
    }

    private View buildAppCard(final ResolveInfo info) {
        final LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(12), dp(12), dp(12), dp(10));
        card.setFocusable(true);
        card.setClickable(true);
        card.setBackground(rounded(CARD, dp(18), dp(1), 0x223B82C4));

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        try {
            Drawable d = info.loadIcon(getPackageManager());
            icon.setImageDrawable(d);
        } catch (Throwable ignored) {
            icon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        card.addView(icon, new LinearLayout.LayoutParams(dp(66), dp(66)));

        CharSequence label;
        try {
            label = info.loadLabel(getPackageManager());
        } catch (Throwable t) {
            label = info.activityInfo.packageName;
        }
        TextView name = text(String.valueOf(label), 14, Color.WHITE, Typeface.NORMAL);
        name.setSingleLine(true);
        name.setEllipsize(android.text.TextUtils.TruncateAt.END);
        name.setGravity(Gravity.CENTER);
        name.setPadding(0, dp(7), 0, 0);
        card.addView(name, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        card.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                card.animate().scaleX(hasFocus ? 1.08f : 1f)
                        .scaleY(hasFocus ? 1.08f : 1f)
                        .setDuration(120).start();
                card.setBackground(rounded(hasFocus ? CARD_FOCUSED : CARD,
                        dp(18), hasFocus ? dp(3) : dp(1),
                        hasFocus ? ACCENT : 0x223B82C4));
                card.setElevation(hasFocus ? dp(12) : dp(1));
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                launchApp(info);
            }
        });
        return card;
    }

    private void launchApp(ResolveInfo info) {
        if (info == null || info.activityInfo == null) return;
        try {
            Intent launch = new Intent(Intent.ACTION_MAIN);
            launch.setComponent(new ComponentName(
                    info.activityInfo.packageName, info.activityInfo.name));
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(launch);
        } catch (Throwable firstError) {
            try {
                Intent fallback = getPackageManager().getLaunchIntentForPackage(
                        info.activityInfo.packageName);
                if (fallback != null) {
                    fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(fallback);
                } else {
                    throw new IllegalStateException("No launch intent");
                }
            } catch (Throwable ignored) {
                Toast.makeText(this, "تعذر فتح هذا التطبيق", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private TextView text(String value, int sp, int color, int style) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans", style));
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(false);
        t.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        return t;
    }

    private GradientDrawable rounded(int fill, float radius, int strokeWidth, int strokeColor) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(radius);
        if (strokeWidth > 0) g.setStroke(strokeWidth, strokeColor);
        return g;
    }

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override protected void onResume() {
        super.onResume();
        startSlider();
    }

    @Override protected void onPause() {
        stopSlider();
        super.onPause();
    }

    @Override protected void onDestroy() {
        stopSlider();
        executor.shutdownNow();
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
        }
    }

    private static class MatchSlide {
        final String badge;
        final String competition;
        final String teamOne;
        final String teamTwo;
        final String kickoff;
        final int backgroundRes;

        MatchSlide(String badge, String competition, String teamOne,
                   String teamTwo, String kickoff, int backgroundRes) {
            this.badge = badge;
            this.competition = competition;
            this.teamOne = teamOne;
            this.teamTwo = teamTwo;
            this.kickoff = kickoff;
            this.backgroundRes = backgroundRes;
        }
    }
}
