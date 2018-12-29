package com.example.browser.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.eventbus.Subscribe;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.browser.FavAndHisActivity;
import com.example.browser.MainActivity;
import com.example.browser.PopupWindowUrl;
import com.example.browser.R;
import com.example.browser.constance.fragConst;
import com.example.browser.custom.PaintActivity;
import com.example.browser.event.baseEvent;
import com.example.browser.event.deleteFragEvent;
import com.example.browser.event.fragEvent;
import com.example.browser.event.showDelImg;
import com.example.browser.event.windowEvent;
import com.example.browser.event.zoomEvent;
import com.example.browser.file.FileActivity;
import com.example.browser.file.ImageCaptureManager;
import com.example.browser.file.ImageDownloadManager;
import com.example.browser.file.PageAttributesActivity;
import com.example.browser.file.RequestShowImageOnline;
import com.example.browser.other.FavAndHisManager;
import com.example.browser.other.ItemLongClickedPopWindow;
import com.example.browser.other.PopupWindowTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/11/28.
 */
public class mainFrag extends baseFrag implements SwipeRefreshLayout.OnRefreshListener {

    private View view;//缓存Fragment view

    private LinearLayout mainLayout;
    private FrameLayout rootLayout;
    private DisplayMetrics dm2;
    private String fragTag = "";
    private LinearLayout webToolsLayout;
    private LinearLayout delThisPageLayout;
    private ImageView delThisPage;
    private EditText delTitle;

    private boolean isPrivateBrowsing = false;
    private boolean isNewFragment = false;


    private static Boolean isQuit = false;
    int flag = 0;
    private int pageScreenShotCount = 0;
    private int windowScreenShotCount = 0;
    private String url = "";
    private String title = "";
    private String size = "";
    private String encoding = "";
    private Bitmap icon = null;

    //WebView
    private SwipeRefreshLayout swipeLayout;
    private WebView webHolder;

    //WebUrlLayout
    private LinearLayout webUrlLayout;
    private FrameLayout frameLayout;
    private EditText webUrlStr;
    private ImageButton webUrlSearch;
    private ImageButton webUrlCancel;
    private ImageButton webUrlFresh;

    //bottom button
    private ImageButton pagePre;
    private ImageButton pageNext;
    private ImageButton pageHome;
    private TextView pageWindow;
    private ImageButton tools;

    //listener
    private ButtonClickedListener buttonClickedListener;
    private WebUrlStrWatcher webUrlStrWatcher;
    private WebViewTouchListener webViewTouchListener;
    private ViewTouchListener viewTouchListner;
    private WebViewLongClickedListener webViewLongClickedListener;
    private ImageClickedListener imageClickedListener;
    private ToolsClickedListener toolsClickedListener;
    private AchorClickedListener achorClickedListener;
    private WebViewClickListener webViewClickListener;

    //progress bar
    private ProgressBar webProgressBar;

    //gesture
    private GestureDetector myGestureDetector;
    private GestureListener gestureListener;

    //tools popup window
    private PopupWindowTools toolsPopWindow;

    //set long clicked popup window
    private ItemLongClickedPopWindow itemLongClickedPopWindow;

    //favorite and history manager
    private FavAndHisManager favAndHisManager;

    //dialog for saving images
    private Dialog saveImageToChoosePath;

    //button to save images
    private TextView choosePath;
    private TextView imgSaveName;

    //剪贴板
    private ClipboardManager clipboardManager;

    public mainFrag() {
        this.fragTag = fragConst.new_mainfrag_count + "";
        fragConst.new_mainfrag_count++;
        isNewFragment = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment_main, null);
            init(view);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        return view;

    }

    private void init(View view) {
        mainLayout = (LinearLayout) view.findViewById(R.id.main_lt);
        mainLayout.setOnTouchListener(this);
        rootLayout = (FrameLayout) view.findViewById(R.id.root_lt);
        rootLayout.setOnTouchListener(this);
        webToolsLayout = (LinearLayout) view.findViewById(R.id.web_tool_lt);
        delThisPageLayout = (LinearLayout) view.findViewById(R.id.del_this_page_lt);
        delThisPage = (ImageView) view.findViewById(R.id.del_this_page);
        delTitle = (EditText) view.findViewById(R.id.del_title_lt);

        webHolder = (WebView) view.findViewById(R.id.web_holder);

        //WebUrlLayout
        webUrlLayout = (LinearLayout) view.findViewById(R.id.web_url_layout);
        frameLayout = (FrameLayout) view.findViewById(R.id.Mask);
        webUrlStr = (EditText) view.findViewById(R.id.web_url_str);
        webUrlSearch = (ImageButton) view.findViewById(R.id.web_url_search);
        webUrlCancel = (ImageButton) view.findViewById(R.id.web_url_cancel);
        webUrlFresh = (ImageButton) view.findViewById(R.id.web_url_fresh);
        webUrlStr.setCursorVisible(false);
        webUrlStr.clearFocus();
        webUrlStr.setFocusableInTouchMode(false);

        //listener
        buttonClickedListener = new ButtonClickedListener();
        webUrlStrWatcher = new WebUrlStrWatcher();
        webViewTouchListener = new WebViewTouchListener();
        viewTouchListner = new ViewTouchListener();
        toolsClickedListener = new ToolsClickedListener();
        webViewLongClickedListener = new WebViewLongClickedListener();

        //progress bar
        webProgressBar = (ProgressBar) view.findViewById(R.id.web_process_bar);
        webProgressBar.setVisibility(View.GONE);

        //gesture
        gestureListener = new GestureListener();
        // myGestureDetector = new GestureDetector(this,gestureListener);

        //bottom button
        pagePre = (ImageButton) view.findViewById(R.id.pre_button);
        pageNext = (ImageButton) view.findViewById(R.id.next_button);
        pageHome = (ImageButton) view.findViewById(R.id.home_button);
        pageWindow = (TextView) view.findViewById(R.id.window_button);
        tools = (ImageButton) view.findViewById(R.id.tools_button);

        //tools popup window
        toolsPopWindow = new PopupWindowTools(getActivity());

        //favorite and history manager
        favAndHisManager = new FavAndHisManager(getActivity().getApplicationContext());

        //button setting
        pagePre.setEnabled(false);
        pageNext.setEnabled(false);

        //WebView setting
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        webHolder.getSettings().setDefaultTextEncodingName("UTF-8");
        webHolder.getSettings().setJavaScriptEnabled(true);
        webHolder.getSettings().setSupportZoom(false);
        webHolder.getSettings().setUseWideViewPort(false);
        webHolder.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webHolder.getSettings().setLoadWithOverviewMode(false);

        webHolder.setWebViewClient(new MyWebViewClient());
        webHolder.setWebChromeClient(new MyChromeClient()); //set progressbar
        webHolder.loadUrl("https://www.jianyang995.com");
        //webHolder.setOnTouchListener(webViewTouchListener);
        webHolder.setOnLongClickListener(webViewLongClickedListener);

        webUrlSearch.setOnClickListener(buttonClickedListener);
        webUrlCancel.setOnClickListener(buttonClickedListener);
        webUrlFresh.setOnClickListener(buttonClickedListener);
        webUrlStr.addTextChangedListener(webUrlStrWatcher);
        webUrlStr.setOnTouchListener(viewTouchListner);
        frameLayout.setOnTouchListener(viewTouchListner);

        pagePre.setOnClickListener(buttonClickedListener);
        pageNext.setOnClickListener(buttonClickedListener);
        pageHome.setOnClickListener(buttonClickedListener);
        pageWindow.setOnClickListener(buttonClickedListener);
        tools.setOnClickListener(buttonClickedListener);

        EventBus.getDefault().register(this);

        dm2 = getResources().getDisplayMetrics();


//        delThisPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                delAnime();
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        EventBus.getDefault().post(new delThisFrag());
//                    }
//                }, 300);
//                EventBus.getDefault().post(new delThisFrag());
//            }
//        });

        swipeLayout.setOnRefreshListener(this);

        if ((fragConst.new_mainfrag_count > 1) && isNewFragment) {
            //缩小
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.1f, 1f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.1f, 1f);
            ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(rootLayout, pvhX, pvhY);
            scale.setDuration(500);
            scale.start();
        }

        isNewFragment = false;
    }

    public String getFragTag() { // 被反射的方法
        return fragTag;
    }

    public void setFragTag(String fragTag) {
        this.fragTag = fragTag;
    }

    private float mov_x, mov_y; //相对于手指移动了的位置
    private int left, right, top, bottom;
    private List<int[]> positionlist = new ArrayList<>();

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        super.onTouch(v, event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            positionlist.clear();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //     Logger.v("x:  " + event.getX() + "   y:  " + event.getY());
            mov_x = event.getX() - super.point_x;
            mov_y = event.getY() - super.point_y;

            left = rootLayout.getLeft();
            right = rootLayout.getRight();
            top = rootLayout.getTop();
            bottom = rootLayout.getBottom();

            if (Math.abs(dm2.widthPixels - rootLayout.getWidth()) > 5) {
                rootLayout.layout(left, top + (int) mov_y, right, bottom + (int) mov_y);

                int[] position = {left, top + (int) mov_y, right, bottom + (int) mov_y};
                positionlist.add(position);
                // Logger.v("left " + position[0] + " top " + position[1] + " right " + position[2] + "  bottom " + position[3] );

                if (Math.abs(position[1]) > rootLayout.getWidth() / 2) {
                    //  Logger.v("-    显示  删除 按钮     -");
                    EventBus.getDefault().post(new showDelImg(true));   //  发送消息
                } else {
                    EventBus.getDefault().post(new showDelImg(false));   //  发送消息
                }
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (positionlist.size() >= 2) {
                if (fragConst.fraglist.size()>1 &&  Math.abs(positionlist.get(positionlist.size() - 1)[1]) > rootLayout.getWidth() / 2) {
                    //  Logger.v("-      删除  fragment    -");
                    delAnime();
                    EventBus.getDefault().post(new showDelImg(false));   //  发送消息
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new deleteFragEvent(getFragTag()));   //  发送消息
                        }
                    },200);
                    return true;
                }
            } else {

                //放大或者缩小fragment
                EventBus.getDefault().post(new fragEvent(getFragTag()));   //  发送消息
            }

            for (int i = positionlist.size() - 1; i >= 0; i--) {
                rootLayout.layout(positionlist.get(i)[0], positionlist.get(i)[1], positionlist.get(i)[2], positionlist.get(i)[3]);
            }
//            rootLayout.layout(0, 0, rootLayout.getWidth(), rootLayout.getHeight());
        }
        return true;
    }


    //删除动画
    private void delAnime() {
        if (fragConst.fraglist.size() <= 1) {
            return;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.01f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.01f);
        ObjectAnimator scalexy = ObjectAnimator.ofPropertyValuesHolder(rootLayout, pvhX, pvhY);

        ObjectAnimator scale = ObjectAnimator.ofFloat(rootLayout, "translationY", 0, -2500);

        scale.setDuration(200);
        scalexy.setDuration(200);
        scale.start();
        scalexy.start();

    }

    @Subscribe
    public void onEventMainThread(baseEvent event) {
        // Toast.makeText(getActivity(), " 收到 event 数据  ", 0).show();

        if (event instanceof zoomEvent) {
            if (((zoomEvent) event).isMatchParent()) {
                mainLayout.setVisibility(View.INVISIBLE);
            } else {
                mainLayout.setVisibility(View.VISIBLE);
                delTitle.setText(title);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //EventBus.getDefault().unregister(this);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webUrlStr.setText(title);
            webUrlStr.setHint(title);
            changeStatusOfBottomButton();
            //add history
            if (!isPrivateBrowsing) {
                String date = new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date()).toString();
                favAndHisManager.addHistory(title, url, Long.parseLong(date));
            }
        }
    }

    private class ViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(view.getId() == R.id.web_url_str) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP && !webUrlStr.hasFocus()) {
                    webUrlStr.setFocusableInTouchMode(true);
                    webUrlStr.requestFocus();
                    webUrlStr.setText(url);
                    webUrlStr.selectAll();
                }
                webUrlStr.setCursorVisible(true);
                frameLayout.setVisibility(View.VISIBLE);
                setStatusOfSearch(1);
                if(flag == 0) {
                    PopupWindowUrl morePopWindow = new PopupWindowUrl(getActivity(), webHolder.getUrl(), webHolder.getFavicon());
                    morePopWindow.showPopupWindow(webUrlLayout);
                }
                flag = 1;

            } else if (view.getId() == R.id.Mask) {
                url = webHolder.getUrl();
                webUrlStr.setCursorVisible(false);
                webUrlStr.clearFocus();
                webUrlStr.setFocusableInTouchMode(false);
                webUrlStr.setText(title);
                frameLayout.setVisibility(View.GONE);
                setStatusOfSearch(3);
                flag = 0;
            }
            return false;
        }
    }

    //页面点击
    private class WebViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }

    private class ButtonClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.web_url_search) {
                flag = 0;
                frameLayout.setVisibility(View.GONE);
                webUrlStr.setFocusableInTouchMode(false);
                webUrlStr.clearFocus();
                url = webUrlStr.getText().toString();
                if(!(url.startsWith("http://")||url.startsWith("https://"))){
                    url = "https://www.google.com#q="+url;
                }
                webHolder.loadUrl(url);
            } else if(view.getId() == R.id.web_url_cancel) {
                url = webHolder.getUrl();
                webUrlStr.setText(url);
            } else if (view.getId() == R.id.web_url_fresh) {
                url = webHolder.getUrl();
                webHolder.loadUrl(url);
            } else if (view.getId() == R.id.pre_button) {
                if(webHolder.canGoBack())
                    webHolder.goBack();
            } else if (view.getId() == R.id.next_button) {
                if(webHolder.canGoForward())
                    webHolder.goForward();
            } else if (view.getId() == R.id.home_button) {
                webHolder.loadUrl("https://www.jianyang995.com");
            } else if (view.getId() == R.id.window_button) {
                EventBus.getDefault().post(new windowEvent());
            } else if (view.getId() == R.id.tools_button) {
                LayoutInflater toolsInflater = LayoutInflater.from(getActivity().getApplicationContext());
                View toolsView = toolsInflater.inflate(R.layout.pop_window_tools, null);
                toolsPopWindow.showAtLocation(toolsView, Gravity.BOTTOM| Gravity.RIGHT, 20, tools.getHeight()+40);
                Button privateBrowsing = (Button) toolsPopWindow.getView(R.id.private_browsing);
                Button addFavorite = (Button) toolsPopWindow.getView(R.id.add_favorite_button);
                Button showFavorites = (Button) toolsPopWindow.getView(R.id.show_favorite_button);
                Button showHistories = (Button) toolsPopWindow.getView(R.id.show_history_button);
                Button pageScreenshot = (Button) toolsPopWindow.getView(R.id.page_screenshot);
                Button windowScreenshot = (Button) toolsPopWindow.getView(R.id.window_screenshot);
                Button pageEdit = (Button) toolsPopWindow.getView(R.id.page_edit);
                privateBrowsing.setOnClickListener(toolsClickedListener);
                addFavorite.setOnClickListener(toolsClickedListener);
                showFavorites.setOnClickListener(toolsClickedListener);
                showHistories.setOnClickListener(toolsClickedListener);
                pageScreenshot.setOnClickListener(toolsClickedListener);
                windowScreenshot.setOnClickListener(toolsClickedListener);
                pageEdit.setOnClickListener(toolsClickedListener);
            }
        }
    }
    //功能弹出窗口按钮
    private class ToolsClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.private_browsing) {
                //无痕浏览
                if (isPrivateBrowsing){
                    LayoutInflater toolsInflater = LayoutInflater.from(getActivity().getApplicationContext());
                    View toolsView = toolsInflater.inflate(R.layout.pop_window_tools, null);
                    toolsPopWindow.showAtLocation(toolsView, Gravity.BOTTOM| Gravity.RIGHT, 20, tools.getHeight()+40);
                    Button privateBrowsing = (Button) toolsPopWindow.getView(R.id.private_browsing);
                    privateBrowsing.setBackgroundColor(Color.parseColor("#EFEFF1"));
                    Toast.makeText(getActivity(), "Private mode closed", Toast.LENGTH_SHORT).show();
                    isPrivateBrowsing = false;
                } else {
                    LayoutInflater toolsInflater = LayoutInflater.from(getActivity().getApplicationContext());
                    View toolsView = toolsInflater.inflate(R.layout.pop_window_tools, null);
                    toolsPopWindow.showAtLocation(toolsView, Gravity.BOTTOM| Gravity.RIGHT, 20, tools.getHeight()+40);
                    Button privateBrowsing = (Button) toolsPopWindow.getView(R.id.private_browsing);
                    privateBrowsing.setBackgroundColor(Color.parseColor("#DDFFFF"));
                    Toast.makeText(getActivity(), "Private mode on", Toast.LENGTH_SHORT).show();
                    isPrivateBrowsing = true;
                }

            } else if (view.getId() == R.id.add_favorite_button) {
                //添加书签
                favAndHisManager.addFavorite(title, url);
                favAndHisManager.getAllFavorites();
                Toast.makeText(getActivity(), "Page saved", Toast.LENGTH_SHORT).show();
            } else if (view.getId() ==R.id.show_favorite_button) {
                //查看编辑书签
                toolsPopWindow.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), FavAndHisActivity.class);
                intent.putExtra("type", "favorite");
                startActivityForResult(intent, MainActivity.REQUEST_OPEN_FAV_OR_HIS);
            } else if (view.getId() == R.id.show_history_button) {
                //查看编辑历史
                toolsPopWindow.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), FavAndHisActivity.class);
                intent.putExtra("type", "history");
                startActivityForResult(intent, MainActivity.REQUEST_OPEN_FAV_OR_HIS);
            } else if (view.getId() == R.id.page_screenshot || view.getId() == R.id.window_screenshot) {
                //网页截图或全屏截图
                toolsPopWindow.dismiss();
                verifyStoragePermissions(getActivity());
                View sView;
                String tempImgName;
                if (view.getId() == R.id.window_screenshot) {
                    sView = getActivity().getWindow().getDecorView();
                    tempImgName = "window_capture" + windowScreenShotCount + ".jpg";
                    windowScreenShotCount++;
                }
                else {
                    sView = webHolder;
                    tempImgName = "webview_capture" + pageScreenShotCount + ".jpg";
                    pageScreenShotCount++;
                }
                final Bitmap sBitmap = Bitmap.createBitmap(sView.getWidth(), sView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas sCanvas = new Canvas(sBitmap);
                sView.draw(sCanvas);
                View dialogSaveImg = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_saveimg, null);
                choosePath = (TextView) dialogSaveImg.findViewById(R.id.dialog_savePath_enter);
                imgSaveName = (TextView) dialogSaveImg.findViewById(R.id.dialog_fileName_input);
                final String imgName = tempImgName;
                imgSaveName.setText(imgName);
                choosePath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.dialog_savePath_enter) {
                            Intent imgSavePath = new Intent(getActivity(), FileActivity.class);
                            imgSavePath.putExtra("savePath", choosePath.getText().toString());
                            startActivityForResult(imgSavePath, MainActivity.REQUEST_SAVE_IMAGE_PATH);
                        }
                    }
                });
                saveImageToChoosePath = new AlertDialog.Builder(getActivity())
                        .setTitle("Choose path")
                        .setView(dialogSaveImg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ImageCaptureManager(getActivity(), imgName, choosePath.getText().toString(), sBitmap);
                                Toast.makeText(getActivity(), "Screenshot saved", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                saveImageToChoosePath.show();

            } else if (view.getId() == R.id.page_edit) {
                //编辑网页
                toolsPopWindow.dismiss();
                View sView;
                sView = getActivity().getWindow().getDecorView();
                final Bitmap sBitmap = Bitmap.createBitmap(sView.getWidth(), sView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas sCanvas = new Canvas(sBitmap);
                sView.draw(sCanvas);
                Intent intent = new Intent();
                PaintActivity.originalBitmap = sBitmap;
                intent.setClass(getActivity(), PaintActivity.class);
                startActivity(intent);
            }
        }
    }

    //图片长按弹出窗口操作
    private class ImageClickedListener implements View.OnClickListener {

        private int type;
        private String value;

        public ImageClickedListener(int type, String value) {
            this.type = type;
            this.value =value;
        }

        @Override
        public void onClick(View v) {
            itemLongClickedPopWindow.dismiss();
            if (v.getId() ==R.id.item_longclicked_viewImage) {
                //查看图片
                new RequestShowImageOnline(getActivity()).execute(value);
            } else if (v.getId() == R.id.item_longclicked_saveImage) {
                //保存图片
                verifyStoragePermissions(getActivity());
                View dialogSaveImg = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_saveimg, null);
                choosePath = (TextView) dialogSaveImg.findViewById(R.id.dialog_savePath_enter);
                imgSaveName = (TextView) dialogSaveImg.findViewById(R.id.dialog_fileName_input);
                final String imgName = value.substring(value.lastIndexOf("/") + 1);
                imgSaveName.setText(imgName);
                choosePath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.dialog_savePath_enter) {
                            Intent imgSavePath = new Intent(getActivity(), FileActivity.class);
                            imgSavePath.putExtra("savePath", choosePath.getText().toString());
                            startActivityForResult(imgSavePath, MainActivity.REQUEST_SAVE_IMAGE_PATH);
                        }
                    }
                });
                saveImageToChoosePath = new AlertDialog.Builder(getActivity())
                        .setTitle("Choose path")
                        .setView(dialogSaveImg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ImageDownloadManager(getActivity()).execute(imgName, value, choosePath.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                saveImageToChoosePath.show();
            } else if (v.getId() == R.id.item_longclicked_viewImageAttributes) {
                //查看图片属性
                size = String.valueOf(webHolder.getContentHeight()) + "×" + String.valueOf(getActivity().getWindowManager().getDefaultDisplay().getHeight());
                encoding = webHolder.getSettings().getDefaultTextEncodingName();
                Intent intent = new Intent(getActivity(), PageAttributesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putString("typeUrl", value);
                bundle.putString("title", title);
                bundle.putString("url", url);
                bundle.putString("size", size);
                bundle.putString("encoding", encoding);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }


    //超链接长按弹出窗口
    private class AchorClickedListener implements View.OnClickListener {

        private int type;
        private String value;

        public AchorClickedListener(int type, String value) {
            this.type = type;
            this.value =value;
        }

        @Override
        public void onClick(View v) {
            itemLongClickedPopWindow.dismiss();
            if (v.getId() == R.id.item_longclicked_openAchor) {
                webHolder.loadUrl(value);
            } else if (v.getId() == R.id.item_longclicked_copyAchor) {
                clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, url));
                Toast.makeText(getActivity(), "URL copied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class WebUrlStrWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }
        public void afterTextChanged(Editable editable) {

        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }
    }


    /*
    * WebChromeClient
    * ProgressBar
    * */
    private class MyChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                setStatusOfSearch(3);
                webProgressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            } else {
                url = webHolder.getUrl();
                webUrlStr.setText(url);
                setStatusOfSearch(2);
                webProgressBar.setVisibility(View.VISIBLE);
                webProgressBar.setProgress(newProgress);
            }
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mainFrag.this.title = title;
        }
    }

    private class WebViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //only for WebView
            if (v.getId() == R.id.web_holder) {
                return myGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    }

    /*
    * TODO add gesture
    * distinguish the gesture on WebView
    * hide the webUrlLayout when Fling down
    * appear the webUrlLayout when Fling up
    * */
    private class GestureListener implements GestureDetector.OnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (webHolder.getScrollY() == 0) {
                webUrlLayout.setVisibility(View.VISIBLE);
            }
            if (webHolder.getScrollY() > 0) {
                webUrlLayout.setVisibility(View.GONE);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }

    private static class PointerXY{
        public static int x;
        public static int y;
        public static int getX() {
            return x;
        }
        public static int getY() {
            return y;
        }

    }

    /*
     * WebView Long Clicked Listener
     */
    private class WebViewLongClickedListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (null == result)
                return false;
            int type = result.getType();
            if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                return false;
            if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                return true;
            }

            //Setup custon handling depending on the type
            switch (type) {
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                    itemLongClickedPopWindow = new ItemLongClickedPopWindow(getActivity(), ItemLongClickedPopWindow.ACHOR_VIEW_POPUPWINDOW);
                    itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, PointerXY.getX(), PointerXY.getY() + 10);
                    TextView openAchor = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_openAchor);
                    TextView copyAchor = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_copyAchor);
                    achorClickedListener = new AchorClickedListener(result.getType(), result.getExtra());
                    openAchor.setOnClickListener(achorClickedListener);
                    copyAchor.setOnClickListener(achorClickedListener);
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                case WebView.HitTestResult.IMAGE_TYPE:
                    itemLongClickedPopWindow = new ItemLongClickedPopWindow(getActivity(), ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW);
                    itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, PointerXY.getX(), PointerXY.getY() + 10);
                    TextView viewImage = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImage);
                    TextView saveImage = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage);
                    TextView viewImageAttributes = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImageAttributes);
                    imageClickedListener = new ImageClickedListener(result.getType(), result.getExtra());
                    viewImage.setOnClickListener(imageClickedListener);
                    saveImage.setOnClickListener(imageClickedListener);
                    viewImageAttributes.setOnClickListener(imageClickedListener);
                    break;
                default:
                    break;

            }
            return true;
        }
    }
    /*
    * back button(mobile)
    * one: the last page
    * twice: exit program
    * */
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            isQuit = false;
//        }
//    };
//
//    @Override
//    public void getActivity().onBackPressed() {
//        if (webHolder.canGoBack()) {
//            webHolder.goBack();
//        } else {
//            if (!isQuit) {
//                isQuit = true;
//                Toast.makeText(getActivity().getApplicationContext(), "press again exit program",
//                        Toast.LENGTH_SHORT).show();
//                mHandler.sendEmptyMessageDelayed(0,2000);
//            } else {
//                getActivity().finish();
//                System.exit(0);
//            }
//        }
//    }

    /*
    * change the status of search button
    * search
    * cancel
    * fresh
    * three status
    * */
    public void setStatusOfSearch(int status) {
        if (status == 1) {
            //search status
            webUrlSearch.setVisibility(View.VISIBLE);
            webUrlCancel.setVisibility(View.GONE);
            webUrlFresh.setVisibility(View.GONE);
        } else if(status == 2) {
            //cancel status
            webUrlSearch.setVisibility(View.GONE);
            webUrlCancel.setVisibility(View.VISIBLE);
            webUrlFresh.setVisibility(View.GONE);
        } else {
            //fresh status
            webUrlSearch.setVisibility(View.GONE);
            webUrlCancel.setVisibility(View.GONE);
            webUrlFresh.setVisibility(View.VISIBLE);
        }
    }

    /*
         *set the status of bottom buttons
         * GoBack
         * GoForward
         */
    public void changeStatusOfBottomButton() {
        if (webHolder.canGoBack()) {
            pagePre.setEnabled(true);
        } else {
            pagePre.setEnabled(false);
        }
        if (webHolder.canGoForward()) {
            pageNext.setEnabled(true);
        } else {
            pageNext.setEnabled(false);
        }
    }

    //接收书签/历史返回处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REQUEST_DEFAULT:
                break;


            case FavAndHisActivity.RESULT_FAV_HIS:
                webHolder.loadUrl(data.getStringExtra("url"));
                break;

            case FileActivity.RESULT_FILEMANAGER:
                choosePath.setText(data.getStringExtra("savePath"));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取存储权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE ={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
    public void onRefresh() {
        url = webHolder.getUrl();
        webHolder.loadUrl(url);
    }
}
