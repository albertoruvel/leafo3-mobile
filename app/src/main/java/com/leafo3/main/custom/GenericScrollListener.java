package com.leafo3.main.custom;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;

import com.leafo3.task.BaseTask;

/**
 * Created by root on 6/08/15.
 */
public class GenericScrollListener<
        T extends BaseTask> implements AbsListView.OnScrollListener {


    private boolean loadingNextPage;
    private int lastVisibleItem = 0;
    private int lastY = 0;
    //last item visible before updating next page
    private int prePosition;
    private int pageNumber;
    private int pagesAvailable;

    private Context context;
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;


    private ScrollListenerLooping loopingListener;

    private T task;

    /**
     * The Task must be already instantiated and NOT executed
     * @param context
     * @param listView
     */
    public GenericScrollListener(Context context, SwipeRefreshLayout refreshLayout,
                                 ListView listView, T asyncTask, ScrollListenerLooping loopingListener){
        this.context = context;
        this.listView =listView;
        this.task = asyncTask;
        this.refreshLayout = refreshLayout;
        this.loopingListener = loopingListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    private void dispatchScroll(boolean scrollingUp){
        if(scrollingUp){

        }
        else{

        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int top = 0;
        if (absListView.getChildAt(0) != null) {
            top = absListView.getChildAt(0).getTop();
        }

        if (firstVisibleItem > lastVisibleItem) {
            //scroll down
            dispatchScroll(false);
        } else if (firstVisibleItem < lastVisibleItem) {
            //scroll up
            dispatchScroll(true);
        } else {
            if (top < lastY) {
                //scroll down
                dispatchScroll(false);
            } else if (top > lastY) {
                //scroll up
                dispatchScroll(true);
            }
        }

        lastVisibleItem = firstVisibleItem;
        lastY = top;

        boolean enable = true;
        if (listView != null && listView.getChildCount() > 0) {
            // check if the first item of the list is visible
            boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
            // Sample calculation to determine if the last
            // item is fully visible.
            final int lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem == totalItemCount) {
                if (prePosition != lastItem) { //to avoid multiple calls for last item
                    prePosition = lastItem;
                    if(loadingNextPage)return;
                    //calls next page
                    getNextPage();
                }
            }
        }
        refreshLayout.setEnabled(enable);
    }

    private void getNextPage(){
        if(pagesAvailable > pageNumber){
            //means there are more pages available
            //get the next one
            pageNumber ++;
            loopingListener.onNeedTaskInstance(pageNumber);
            task.execute();
            loadingNextPage = true;
        }
    }

    public void setNewTask(T task){
        this.task = task;
    }

    public boolean isLoadingNextPage(){
        return this.loadingNextPage;
    }

    public void setPagesAvailable(int pagesAvailable){
        this.pagesAvailable = pagesAvailable;
    }

    public void setPageNumber(int pageNumber){
        this.pageNumber = pageNumber;
    }

    /**
     * with this interface, the activity with the listview,
     * will be notified in need of another instance of the
     * task
     */
    public interface ScrollListenerLooping{
        void onNeedTaskInstance(int pageNumber);
    }
}

