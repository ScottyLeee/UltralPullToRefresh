package com.example.mvp.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity {

        private PtrFrameLayout mPtrFrameLayout;
        private LoadMoreListViewContainer mLoadMoreListViewContainer;
        private ListView mListView;

        private List<String> mockStrList = new ArrayList<>();
        private int start = 0;
        private int count = 15;
        private PagedListViewDataAdapter<String> mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //1.find the listview
            mListView = (ListView) findViewById(R.id.load_more_listview);
            // 为listview的创建一个headerview,注意，如果不加会影响到加载的footview的显示！
            View headerMarginView = new View(this);
            headerMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(20)));
            mListView.addHeaderView(headerMarginView);
            //2.绑定模拟的数据
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mockStrList);
            mListView.setAdapter(adapter);
            //3.设置下拉刷新组件和事件监听
            mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
            mPtrFrameLayout.setLoadingMinTime(1000);
            mPtrFrameLayout.setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    // here check list view, not content.
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    //实现下拉刷新的功能
                    Log.i("test", "-----onRefreshBegin-----");
                    mPtrFrameLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mockStrList.clear();
                            start = 0;
                            mockStrList.addAll(getMockData(start, count));
                            mPtrFrameLayout.refreshComplete();
                            //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                            mLoadMoreListViewContainer.loadMoreFinish(false, true);
                            adapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            });
            //设置延时自动刷新数据
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 200);
            //4.加载更多的组件
            mLoadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view_container);
            mLoadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
            mLoadMoreListViewContainer.useDefaultHeader();
            //5.添加加载更多的事件监听
            mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
                @Override
                public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                    //模拟加载更多的业务处理
                    mLoadMoreListViewContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start++;
                            mockStrList.addAll(getMockData(start * 10, count));
                            if (start * 10 > 30) {
//                          mLoadMoreListViewContainer.loadMoreFinish(true, false);
                                //以下是加载失败的情节
                                int errorCode = 0;
                                String errorMessage = "加载失败，点击加载更多";
                                mLoadMoreListViewContainer.loadMoreError(errorCode, errorMessage);
                            } else {
                                mLoadMoreListViewContainer.loadMoreFinish(false, true);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
            });
        }

        /**
         * 做一个简单的内容数据
         * @param start 开始位置
         * @param count 每次拉取的数量
         * @return
         */
        private List<String> getMockData(int start, int count) {
            List<String> slist = new ArrayList<String>();
            for (int i = start; i < start + count; i++) {
                slist.add("内容编号：" + i);
            }
            return slist;
        }


}
