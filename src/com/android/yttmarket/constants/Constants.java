package com.android.yttmarket.constants;

public class Constants {

	// 接口
	public static final String WEB_SERVICES_URL = "";
	public static final String IMAGE_URL = "";
	// head image
	public final static String UPLOAD_URL = "";
	public final static String HEAD_IMAGE_URL = "";
	// 文章
	public static final String article_url = "";
	// 注册协议，开源组件许可
	public final static String USER_AGREEMENT = "";
	// 隐私策略
	public final static String USER_PRIVACY = "";
	// 分享默认地址
	public final static String SHARE_DEFAULT_URL = "";
	

	public static final class INTENT_KEY {
		public static final String MENU_TO_GOODS_LIST = "category_menu";
		public static final int LOGIN_REQUEST_CODE = 20000;
		public static final int LOGIN_RESULT_SUCCESS_CODE = 20001;
		public static final int REQUEST_CART_TO_DETAIL = 50000;
		/** 从我的云天团到更多，返回时刷新登录信息 */
		public static final int REQUEST_MOREACTIVITY = 60000;
		/** 将GoodsInfo对象传给商品列表界面 */
		public static final String INFO_TO_DETAIL = "goodsinfo_to_detail";
		/** 从我的关注跳转到首页 */
		public static final String FROM_FAVOR = "from_favor";
		/** 从详情跳转到购物车 */
		public static final String FROM_DETAIL = "from_detail";
		/** 刷新购物车商品数 */
		public static final String REFRESH_INCART = "refresh_incart";
		/** 通过Bmob登录成功 */
		public static final String LOGIN_BMOB_SUCCESS = "bmob_success";
		/** 注销 */
		public static final String LOGOUT = "logout";

	}

	public static final class BROADCAST_FILTER {
		public static final String FILTER_CODE = "broadcast_filter";
		public static final String EXTRA_CODE = "broadcast_intent";
	}

	public static final class URL {
		/** 应用推荐 */
		public static final String APPS = "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/apps/apps.txt";
		/** 菜单 */
		public static final String MENUJSON = "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/menujson/menujson.txt";
	}

}
