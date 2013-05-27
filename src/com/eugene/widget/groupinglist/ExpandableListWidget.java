package com.eugene.widget.groupinglist;

import com.eugene.widget.groupinglist.SideIndexer.SideToastListener;
import com.eugene.widget.grouppinglist.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ExpandableListWidget extends RelativeLayout implements ListView.OnScrollListener, SideToastListener {
	
	private View root;
	private ExpandableListView listView;
	private boolean foldable = false;
	private OnScrollListener scrollListener;
	private SideIndexer sideIndexer;
	
	private Handler handler = new Handler();

	public ExpandableListWidget(Context context) {
		super(context);
		initailizeWidget();
	}

	public ExpandableListWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initailizeWidget();
	}

	public ExpandableListWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initailizeWidget();
	}
	
	public void setAdapter(ExpandableListAdapter adapter) {
		listView.setAdapter(adapter);
		
		if (sideIndexer != null && sideIndexer.isUsing())
			sideIndexer.setGroupData(adapter.getGroupList());
		
		expandList();
	}

	private void initailizeWidget() {
		LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = inflater.inflate(R.layout.expandable_list_view, this, true);

		listView = (ExpandableListView) root.findViewById(R.id.ex_list_view);
		listView.setOnScrollListener(this);
		listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				if (foldable == false)
					listView.expandGroup(groupPosition);
			}
		});
		
		sideIndexer = (SideIndexer) root.findViewById(R.id.sideindexer);
		sideIndexer.setExpandableListview(listView);
		sideIndexer.setUsing(true);
		sideIndexer.init(this);
	}
	
	public void expandList() {
		for (int i = 0; i < this.listView.getExpandableListAdapter().getGroupCount(); i++) {
			listView.expandGroup(i);
		}
	}
	
	private boolean listScrolling = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollListener != null) {
			scrollListener.onScrollStateChanged(view, scrollState);
		}
		Log.v("GROUPPINGLIST", "ExpandableListWidget.onScrollStateChanged : " +scrollState +", keyboardUP : "+sideIndexer.isKeyboardUp()
		 +", listScrolling:"+listScrolling+", sideIndexer.scrolling:"+sideIndexer.isScolling());
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			listView.requestFocus();
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			if (!listScrolling) {
				listScrolling = true;
				if (sideIndexer != null) {
					sideIndexer.setVisibility(View.VISIBLE);
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (listScrolling && sideIndexer != null && !sideIndexer.isScolling()) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (sideIndexer.isScolling())
							return;
						listScrolling = false;
						if (sideIndexer.getVisibility() >= 4)
							return;
						sideIndexer.setVisibility(View.INVISIBLE);
					}
				}, 1000);
			}
			break;
		}		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollListener != null) {
			scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	@Override
	public void removed() {
		onScrollStateChanged(null, OnScrollListener.SCROLL_STATE_IDLE);
	}
	
	public void onDestroy() {
		if (sideIndexer != null)
			sideIndexer.onDestroy();
	}

}
