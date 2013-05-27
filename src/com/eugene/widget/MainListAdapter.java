package com.eugene.widget;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eugene.widget.groupinglist.ExpandableItem;
import com.eugene.widget.groupinglist.ExpandableListAdapter;
import com.eugene.widget.grouppinglist.R;

public class MainListAdapter extends ExpandableListAdapter {

	public MainListAdapter(Context context, List<ExpandableItem> groupList, List<List<ExpandableItem>> childList) {
		super(context, groupList, childList);
	}

	protected static class GroupViewHolder {
		public TextView groupTextView;
	}

	protected static class ChildViewHolder {
		public ImageView imgProfile;
		public TextView name;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_section_header, parent, false);
			holder = new GroupViewHolder();
			holder.groupTextView = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		if (groupPosition >= groupList.size())
			return convertView;

		holder.groupTextView.setText(groupList.get(groupPosition).getName());
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_child_item, parent, false);
			holder = new ChildViewHolder();
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.img_profile);
			holder.name = (TextView) convertView.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}
		
		ExpandableItem childItem = childList.get(groupPosition).get(childPosition);
		
		holder.name.setText(childItem.getName());
		return convertView;
	}

}
