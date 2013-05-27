package com.eugene.widget.groupinglist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.eugene.widget.grouppinglist.R;

public abstract class ExpandableListAdapter extends BaseExpandableListAdapter {

	protected Context context;
	protected LayoutInflater inflater;
	protected List<ExpandableItem> groupList;
	protected List<List<ExpandableItem>> childList;

	/**
	 * 
	 * @param context
	 * @param groupList
	 *            - result of origin's First Character List
	 * @param childList
	 *            - result of origin's child List
	 * @param origin
	 *            - resource that we use
	 */
	public ExpandableListAdapter(Context context, List<ExpandableItem> groupList, List<List<ExpandableItem>> childList) {
		this.context = context;
		this.groupList = groupList;
		this.childList = childList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setSource(List<ExpandableItem> groups, List<List<ExpandableItem>> friends) {
		this.groupList = groups;
		this.childList = friends;
		this.notifyDataSetChanged();
	}

	public int getGroupCount() {
		if( this.groupList != null )
			return this.groupList.size();
		
		return 0;
	}

	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition);
	}

	public List<ExpandableItem> getChildList(int groupPosition) {
		return childList.get(groupPosition);
	}
	
	public List<ExpandableItem> getGroupList() {
		return this.groupList;
	}

	public List<List<ExpandableItem>> getChildList() {
		return this.childList;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public boolean hasStableIds() {
		return true;
	}

	public abstract View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

	public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	/**
	 * 
	 * @param groupList
	 *            - out
	 * @param childList
	 *            - out
	 * @param aphabeticalList
	 *            - source
	 */
	public void initGrouping(List<? extends ExpandableItem> aphabeticalList) {
		initGrouping(groupList, childList, aphabeticalList);
	}
	
	/**
	 * 
	 * @param groupList
	 *            - out
	 * @param childList
	 *            - out
	 * @param aphabeticalList
	 *            - source
	 */
	public void initGrouping(List<ExpandableItem> groupList, List<List<ExpandableItem>> childList, List<? extends ExpandableItem> aphabeticalList) {

		// Child
		List<ExpandableItem> tempChild = new ArrayList<ExpandableItem>();
		Map<String, List<ExpandableItem>> alphabetListHistory = new TreeMap<String, List<ExpandableItem>>();
		Map<String, List<ExpandableItem>> hangulListHistory = new TreeMap<String, List<ExpandableItem>>();
		Map<String, List<ExpandableItem>> etcListHistory = new TreeMap<String, List<ExpandableItem>>();

		char curLetter;
		String etcSection = context.getString(R.string.title_for_etc_section);
		for (ExpandableItem expandableItem : aphabeticalList) {
			curLetter = GrouppingUtils.getFirstPhoneme(expandableItem.getName());
			int result = GrouppingUtils.startsWith(curLetter);
			
			switch (result) {
			case GrouppingUtils.UNKNOWN:
			case GrouppingUtils.NUMERIC:
				if (etcListHistory.containsKey(String.valueOf(etcSection)) == false) {
					tempChild = new ArrayList<ExpandableItem>(); // re-initialize
					etcListHistory.put(String.valueOf(etcSection), tempChild);
				} else {
					tempChild = etcListHistory.get(String.valueOf(etcSection));
				}
				break;
			case GrouppingUtils.ALPHABET:
				if (alphabetListHistory.containsKey(String.valueOf(curLetter)) == false) {
					tempChild = new ArrayList<ExpandableItem>(); // re-initialize
					alphabetListHistory.put(String.valueOf(curLetter), tempChild);
				} else {
					tempChild = alphabetListHistory.get(String.valueOf(curLetter));
				}
				break;
			default:
				if (hangulListHistory.containsKey(String.valueOf(curLetter)) == false) {
					tempChild = new ArrayList<ExpandableItem>(); // re-initialize
					hangulListHistory.put(String.valueOf(curLetter), tempChild);
				} else {
					tempChild = hangulListHistory.get(String.valueOf(curLetter));
				}
				break;
			}
			tempChild.add(expandableItem);
		}
		
		generateGroups(groupList, childList, hangulListHistory);
		generateGroups(groupList, childList, alphabetListHistory);
		generateGroups(groupList, childList, etcListHistory);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void generateGroups(HashMap<String, ? extends List<? extends ExpandableItem>> source, String sectionName) {
		if (source.get(sectionName) != null && source.get(sectionName).size() > 0) {
			groupList.add(new ExpandableGroup(sectionName));
			childList.add(groupList.size() - 1, (ArrayList) source.get(sectionName));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void generateGroups(TreeMap<String, ? extends List<? extends ExpandableItem>> source) {
		if (source != null && source.size() > 0) {
			Set<String> keyset = source.keySet();
			Iterator<String> iterator = keyset.iterator();
			String curKey;
			while (iterator.hasNext()) {
				curKey = iterator.next();
				groupList.add(new ExpandableGroup(curKey));
				childList.add(groupList.size() - 1, (ArrayList) source.get(curKey));
			}
		}
	}
	
	protected void generateGroups(List<ExpandableItem> groupList, List<List<ExpandableItem>> childList, Map<String, List<ExpandableItem>> source) {
		if (source != null && source.size() > 0) {
			Set<String> keyset = source.keySet();
			Iterator<String> iterator = keyset.iterator();
			String curKey;
			while (iterator.hasNext()) {
				curKey = iterator.next();
				groupList.add(new ExpandableGroup(curKey.toUpperCase()));
				childList.add(source.get(curKey));
			}
		}
	}

}
