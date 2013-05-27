package com.eugene.widget.groupinglist;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.eugene.widget.grouppinglist.R;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SideIndexerAdapter {

	public static final String MAGNIFIER = "!";
	public static final String ETC = "#";
	public static String[] INDEX_SET;
	public static String[] INDEX_SET_LANDSCAPE;

	/**
	 * Current SideIndexer Character Set
	 */
	public static String[] thisSet;

	private static final float SIZE_BIG_HEIGHT = 17f;

	final int ALPHABET_LAYOUT_HEIGHT;

	private List<String> indexItem;
	private LayoutInflater inflater;
	
	private float density;

	public SideIndexerAdapter(Context context, int indexRes, int landscapeIndexRes) {
		INDEX_SET = context.getResources().getStringArray(indexRes);// R.array.side_indexer
		INDEX_SET_LANDSCAPE = context.getResources().getStringArray(landscapeIndexRes);// R.array.side_indexer_landscape
		density = context.getResources().getDisplayMetrics().density;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (windowManager.getDefaultDisplay().getOrientation() % 2 > 0) { // landscape
			thisSet = INDEX_SET_LANDSCAPE;
		} else {
			thisSet = INDEX_SET;
		}
		ALPHABET_LAYOUT_HEIGHT = (int) (SIZE_BIG_HEIGHT * density);

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setIndexItem();
	}

	private void setIndexItem() {
		indexItem = new ArrayList<String>();
		// Log.d(ExpandableListWidget.TAG, "SideIndexAdapter.setIndexItem() thisSet : "+thisSet.length);

		for (int i = 0; i < thisSet.length; i++) {
			indexItem.add(thisSet[i]);
		}
	}

	public void changeIndexSet(Configuration config, ViewGroup parent, TreeMap<Integer, String> indexPosition) {
		// Log.v(ExpandableListWidget.TAG, "SideIndexAdapter.changeIndexSet():" + config.orientation);
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			thisSet = INDEX_SET_LANDSCAPE;
		} else {
			thisSet = INDEX_SET;
		}
		setIndexItem();
		makeIndex(parent, indexPosition);
	}

	public void makeIndex(ViewGroup parent, TreeMap<Integer, String> indexPosition) {
		indexPosition.clear();
		LinearLayout.LayoutParams params;

		parent.removeAllViews();

		int currentHeight = parent.getTop();// parent.getPaddingTop() + parent.get;

		LinearLayout layout = null;

		// ALPHABET_LIST
		String text = "";
		for (int i = 0; i < indexItem.size(); i++) {
			layout = (LinearLayout) inflater.inflate(R.layout.side_indexer_item, null, false);

			text = indexItem.get(i);
			if (text.equals(MAGNIFIER)) {
				((View) layout.findViewById(R.id.side_index_text)).setVisibility(View.GONE);
				((View) layout.findViewById(R.id.side_search_magifier)).setVisibility(View.VISIBLE);
			} else {
				((TextView) layout.findViewById(R.id.side_index_text)).setText(text);
			}

			params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
			parent.addView(layout, params);

			currentHeight = currentHeight + ALPHABET_LAYOUT_HEIGHT;
			indexPosition.put(currentHeight, text);
		}
	}
}
