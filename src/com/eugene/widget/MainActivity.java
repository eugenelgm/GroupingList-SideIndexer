package com.eugene.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.eugene.widget.groupinglist.ExpandableItem;
import com.eugene.widget.groupinglist.ExpandableListAdapter;
import com.eugene.widget.groupinglist.ExpandableListWidget;
import com.eugene.widget.grouppinglist.R;

public class MainActivity extends Activity {
	
	private ExpandableListWidget mainList;
	private static List<ExpandableItem> source = new ArrayList<ExpandableItem>();
	
	private List<ExpandableItem> groupList = new ArrayList<ExpandableItem>();
	protected List<List<ExpandableItem>> childList = new ArrayList<List<ExpandableItem>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainList = (ExpandableListWidget) findViewById(R.id.main_list);
		ExpandableListAdapter adapter = new MainListAdapter(this, groupList, childList);
		adapter.initGrouping(groupList, childList, source);
		mainList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mainList != null) 
			mainList.onDestroy();
	}
	
	public static class ContactItem implements ExpandableItem {
		
		private final String name;
		
		public ContactItem(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
		
	}

	static {
		// Gathering Sources
		source.add(new ContactItem("Alice"));
		source.add(new ContactItem("Bob"));
		source.add(new ContactItem("Bada"));
		source.add(new ContactItem("Eugene"));
		source.add(new ContactItem("Janice"));
		source.add(new ContactItem("Nick"));
		source.add(new ContactItem("Deren"));
		source.add(new ContactItem("Dicaprio"));
		source.add(new ContactItem("Danny"));
		source.add(new ContactItem("Dy"));
		source.add(new ContactItem("한석봉"));
		source.add(new ContactItem("장영실"));
		source.add(new ContactItem("세종대왕"));
		source.add(new ContactItem("성주현"));
		source.add(new ContactItem("사유리"));
		source.add(new ContactItem("장근석"));
		source.add(new ContactItem("정현석"));
		source.add(new ContactItem("장가석"));
		source.add(new ContactItem("장나석"));
		source.add(new ContactItem("장다석"));
		source.add(new ContactItem("12345"));
		source.add(new ContactItem("*124832"));
	}
}
