package com.android.bmob.signup;

import cn.bmob.v3.BmobUser;

public class YTTUser extends BmobUser{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
	
}
