package com.android.yttmarket.activity;

import com.android.yttmarket.R;
import com.android.yttmarket.fragment.FragmentAssistant;
import com.android.yttmarket.fragment.FragmentNews;
import com.android.yttmarket.customView.SegmentControlView;
import com.android.yttmarket.customView.SegmentControlView.onSegmentControlViewClickListener;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class MessageCenterActivity extends FragmentActivity implements OnClickListener{

	private SegmentControlView SegmentControlView = null;
	/* 等会要用于切换的2个Fragment */
	private FragmentNews fragmentnews;
	private FragmentAssistant fragmentassistant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_center);
		findViewById(R.id.img_back).setOnClickListener(this);
		// findViewById(R.id.img_back).setOnClickListener(this);
		FindById();
		// 设置默认的Fragment
		setDefaultFragment();
		Listener();
	}

	private void Listener() {
    	SegmentControlView.setOnSegmentControlViewClickListener(new onSegmentControlViewClickListener() {
   			
   			@Override
   			public void onSegmentControlViewClick(View view, int position) {
   				FragmentManager fm = getSupportFragmentManager();  
   		        // 开启Fragment事务  
   		        FragmentTransaction transaction = fm.beginTransaction(); 
   		        
   				switch (position) {
   					case 0:
   						if (fragmentnews == null)  
   						{  
   							fragmentnews = new FragmentNews();  
   						}  
   						// 使用当前Fragment的布局替代id_content的控件  
   						transaction.replace(R.id.fragmentlayout, fragmentnews);  
   						// 事务提交  
   						transaction.commit(); 
   						break;
   					case 1:
   					 if (fragmentassistant == null)  
    		            {  
   						fragmentassistant = new FragmentAssistant();  
    		            }  
    		            // 使用当前Fragment的布局替代id_content的控件  
    		            transaction.replace(R.id.fragmentlayout, fragmentassistant);  
    		            // 事务提交  
    	   		        transaction.commit(); 		
   						break;
   					default:
   						break;
   				}
   			}
   		});
    }

	 private void setDefaultFragment(){
	    	FragmentManager fm = getSupportFragmentManager();  
	        FragmentTransaction transaction = fm.beginTransaction(); 
	        fragmentnews=new FragmentNews();
	        transaction.replace(R.id.fragmentlayout, fragmentnews);  
	        transaction.commit();  
	    }

	private void FindById() {
		SegmentControlView = (SegmentControlView) findViewById(R.id.SegmentControlView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;

		default:
			break;
		}
		
	}
}
