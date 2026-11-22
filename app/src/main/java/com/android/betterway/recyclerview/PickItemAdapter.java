package com.android.betterway.recyclerview;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.betterway.R;
import com.android.betterway.data.LocationItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Jackdow
 * @version 1.0
 *          BetterWay
 */

public class PickItemAdapter extends RecyclerView.Adapter<PickItemAdapter.ViewHolder> {
    private List<LocationItemBean> mLocationItemBeanList;
    private SelectItem mSelectItem;

    /**
     * 鍐呴儴ViewHolder绫?
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pickitem_name)
        TextView mPickitemName;
        @BindView(R.id.pickitem_address)
        TextView mPickitemAddress;
        View mainView;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mainView = itemView;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int i = position;
        LocationItemBean locationItemBean = mLocationItemBeanList.get(i);
        holder.mPickitemName.setText(locationItemBean.getName());
        holder.mPickitemAddress.setText(locationItemBean.getAddress());
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSelectItem) {
                    mSelectItem.select(v, i);
                }
            }
        });
    }

    public PickItemAdapter(List<LocationItemBean> locationItemBeans) {
        mLocationItemBeanList = locationItemBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_pick_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mLocationItemBeanList.size();
    }

    public void setSelectItem(SelectItem selectItem) {
        mSelectItem = selectItem;
    }

    /**
     * 鍦ㄦ椿鍔ㄤ腑瀹炵幇鐨勬帴鍙?
     */
    public interface SelectItem {
        /**
         * 鍦ㄦ椿鍔ㄤ腑瀹氫箟鐨勬柟娉?
         *
         * @param view     view瀵硅薄
         * @param position item鐨勪綅缃?
         */
        void select(View view, int position);
    }
}

