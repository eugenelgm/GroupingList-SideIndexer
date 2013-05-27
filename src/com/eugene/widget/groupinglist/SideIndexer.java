package com.eugene.widget.groupinglist;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eugene.widget.grouppinglist.R;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SideIndexer extends LinearLayout implements OnTouchListener {

	public interface SideToastListener {
		public void removed();
	}
	
	private SideToastListener toastListener;
	private ExpandableListView expandableListView;

	private static float sideIndexX;
	private static float sideIndexY;

	private boolean isUsing = true;

	private static String selectedChar;

	/**
	 * Group index Value (ex, A = 0, C = 1, E = 2)
	 */
	private TreeMap<String, Integer> groupIndexSet;
	private TreeMap<Integer, String> indexPosition;
	private SideIndexerAdapter indexAdapter;
	private List<ExpandableItem> group;

	private TextView dialogPosition;
	private boolean isShowing;
	private SideIndexerCompare sideIndexerCompare;

	/**
	 * SideIndexer is scrolling
	 */
	private boolean scrolling = false;

	public SideIndexer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public SideIndexer(Context context) {
		super(context);
		initializeView();
	}

	private void initializeView() {
		singleToast.initailizeToast(getContext(), R.layout.expandable_list_position);
		dialogPosition = (TextView) singleToast.getView();
		sideIndexerCompare = new SideIndexerCompare();
	}

	public void setExpandableListview(ExpandableListView listview) {
		this.expandableListView = listview;
	}

	public void setGroupData(List<ExpandableItem> group) {
		this.group = group;
		groupIndexSet = new TreeMap<String, Integer>(sideIndexerCompare);
		createIndex(groupIndexSet, this.group);
	}

	public void init() {
		indexAdapter = new SideIndexerAdapter(getContext(), R.array.side_indexer, R.array.side_indexer_landscape);
		indexPosition = new TreeMap<Integer, String>();

		indexAdapter.makeIndex(this, indexPosition);
	}
	
	public void init(SideToastListener toastListener) {
		this.toastListener = toastListener;
		init();
	}

	public void setConfigurationChanged(Configuration config) {
		indexAdapter.changeIndexSet(config, this, indexPosition);
	}

	public static void createIndex(TreeMap<String, Integer> index, List<ExpandableItem> group) {
		index.clear();
		String groupName = "";
		for (int i = 0; i < group.size(); i++) {
			groupName = group.get(i).getName();
			// It's cannot create index
			// if groupName is over 1 character.
			if (groupName.length() > 1)
				continue;
			index.put(groupName, i);
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		sideIndexX = event.getX();
		sideIndexY = event.getY();

		if (sideIndexX > 0 && sideIndexY > 0) {
			displayItem();
		}
		return true;
	}

	private boolean isKeyboardUp = false;
	private int inputRight;
	private int inputBottom;
	private int DUMMY_LAYOUT_BOTTOM = 20;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// Log.d(ExpandableListWidget.TAG, "left:"+left+", top:"+top+", right:"+right+", bottom:"+bottom);
		if (changed) {
			if (inputRight == right && inputBottom > DUMMY_LAYOUT_BOTTOM) {
				if (inputBottom < bottom) {
					// Keboard hide
					isKeyboardUp = false;
				} else {
					// keyboard show
					this.setVisibility(View.INVISIBLE, false);
					isKeyboardUp = true;
				}
			} else {
				isKeyboardUp = false; // ConfigurationChanged
			}
		}

		inputRight = right;
		inputBottom = bottom;
	}

	public void displayItem() {
		int xHeight = (int) (sideIndexY - getPaddingTop());

		selectedChar = getHigherValue(indexPosition, xHeight);
		if (selectedChar == null)
			return;

		// Dialog
		if (isShowing || selectedChar.equals(SideIndexerAdapter.MAGNIFIER)) {
			// DONT SET VISIBLITIEY
		} else {
			scrolling = true;
			isShowing = true;
			singleToast.setVisibility(View.VISIBLE);
		}
		if (!selectedChar.equals(SideIndexerAdapter.MAGNIFIER)) {
			dialogPosition.setText(selectedChar);
		}
		singleToast.removeToast(1500);

		// setPosition
		Integer position = getHigherValue(groupIndexSet, selectedChar);
		if (selectedChar.equals(SideIndexerAdapter.MAGNIFIER))
			position = 0;
		else if (selectedChar.equals(SideIndexerAdapter.ETC))
			position = expandableListView.getCount();

		Log.v("GROUPPINGLIST", "SideIndexer.displayItem() xHeight: " + xHeight + ", selectedChar : " + selectedChar + ", position:" + position);
		if (position != null) {
			if (position == 0 || xHeight <= 0)
				expandableListView.setSelection(0); // Move to SearchBar
			else
				expandableListView.setSelectedGroup(position);
		}
	}

	public boolean isScolling() {
		return scrolling;
	}

	public void onDestroy() {
		singleToast.destory();
	}

	@Override
	public void setVisibility(int visibility) {
		setVisibility(visibility, true);
	}

	public void setVisibility(int visibility, boolean usingAnim) {
		if (isUsing) {
			if (usingAnim) {
				Animation anim = null;
				switch (visibility) {
				case INVISIBLE:
				case GONE:
					anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_short);
					setOnTouchListener(null);
					break;
				case VISIBLE:
					this.setOnTouchListener(this);
					anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_short);
					break;
				}
				this.setAnimation(anim);
				this.startAnimation(anim);
			}
		} else {
			visibility = View.GONE;
		}
		super.setVisibility(visibility);
	}

	public boolean isKeyboardUp() {
		return this.isKeyboardUp;
	}

	public boolean isUsing() {
		return isUsing;
	}

	public void setUsing(boolean isUsing) {
		this.isUsing = isUsing;
		if (!isUsing)
			this.setVisibility(View.GONE);
	}

	private class SideIndexerCompare implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			int i1 = GrouppingUtils.startsWith(s1);
			int i2 = GrouppingUtils.startsWith(s2);

			int ret = 0;
			switch (i1) {
			case GrouppingUtils.HANGUL:
				if (i2 == GrouppingUtils.ALPHABET) {
					ret = -1;
				} else {
					ret = s1.compareTo(s2);
				}
				break;
			case GrouppingUtils.ALPHABET:
				if (i2 == GrouppingUtils.ALPHABET) {
					ret = s1.compareTo(s2);
				} else {
					ret = 1;
				}
				break;
			default:
				ret = s1.compareTo(s2);
			}
			return ret;
		}
	}

	private static <K, V> V getHigherValue(TreeMap<K, V> map, K key) {

		if (key == null)
			return null;
		try {
			K firstKey = map.firstKey();
			K lastKey = map.lastKey();

			if (lastKey.equals(key)) {
				return map.get(lastKey);
			}

			if (firstKey.equals(key)) {
				return map.get(firstKey);
			}

			SortedMap<K, V> sortedMap = map.tailMap(key);

			if (sortedMap == null || sortedMap.size() == 0) {
				return null;
			}

			for (Map.Entry<K, V> item : sortedMap.entrySet()) {
				return item.getValue();
			}
		} catch (NoSuchElementException nse) {
			Log.e("GROUPPINGLIST", "", nse);
		}
		return null;
	}

	private SingleToast singleToast = new SingleToast() {

		@Override
		public void removeWindow() {
			if (isShowing) {
				isShowing = false;
				scrolling = false;
				this.setVisibility(View.INVISIBLE);
				if (toastListener != null)
					toastListener.removed();
			}
		}
	};

}
