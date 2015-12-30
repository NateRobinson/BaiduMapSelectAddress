package com.gu.test.adapter;

import java.util.List;

import android.content.Context;

import com.baidu.mapapi.search.core.PoiInfo;
import com.gu.test.R;
import com.gu.test.adapter.base.BaseViewHolder;
import com.gu.test.adapter.base.MyBaseAdapter;

/**
 * @desc 附近的地址列表适配器
 * @author Nate
 * @date 2015-12-20
 */
public class NearAddressAdapter extends MyBaseAdapter<PoiInfo> {

	public NearAddressAdapter(Context context, int resource, List<PoiInfo> list) {
		super(context, resource, list);
	}

	@Override
	public void setConvert(BaseViewHolder viewHolder, PoiInfo info) {
		viewHolder.setTextView(R.id.item_address_name_tv, info.name);
		viewHolder.setTextView(R.id.item_address_detail_tv, info.address);
	}

}
