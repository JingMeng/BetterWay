package com.android.betterway.recyclerview;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.android.betterway.R;
import com.android.betterway.data.LocationPlan;
import com.android.betterway.utils.LatLngUtil;
import com.android.betterway.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Jackdow
 * @version 1.0
 *          BetterWay
 */

public class LocationPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LocationPlan> mLocationPlanList;
    private static final int CONTENT = 1;
    private static final int BOTTOM = 2;
    private AddLocationButton mAddLocationButton;

    /**
     * content绫?
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.location_name_in_item)
        TextView mLocationNameInItem;
        @BindView(R.id.thing_statement_in_item)
        TextView mThingStatementInItem;
        @BindView(R.id.spend_money_in_item)
        TextView mSpendMoneyInItem;
        @BindView(R.id.spend_time_in_item)
        TextView mSpendTimeInItem;
        @BindView(R.id.start_time_in_item)
        TextView mStartTimeInItem;
        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public void setAddLocationButton(AddLocationButton addLocationButton) {
        mAddLocationButton = addLocationButton;
    }

    /**
     * 搴曢儴鎸夐挳绫?
     */
    public static class BottomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.add_location_button)
        Button mAddLocationButton;
        BottomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public LocationPlanAdapter() {
        mLocationPlanList = new ArrayList<>();
    }
    /**
     * 鏍规嵁浣嶇疆鍒ゆ柇item鐨勭被鍨?
     *
     * @param position 浣嶇疆
     * @return 绫诲瀷
     */
    @Override
    public int getItemViewType(int position) {
        if (position == mLocationPlanList.size()) {
            return BOTTOM;
        } else {
            return CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BOTTOM) {
            View bottomView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_recycler_bottom_item, parent, false);
            return new BottomViewHolder(bottomView);
        } else {
            View contentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_item, parent, false);
            return new ViewHolder(contentView);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int mPosition = position;
        if (holder instanceof ViewHolder) {
            final LocationPlan locationPlan = mLocationPlanList.get(position);
            ((ViewHolder) holder).mLocationNameInItem.setText(locationPlan.getLocation());
            if (locationPlan.getStatement() != null) {
                ((ViewHolder) holder).mThingStatementInItem.setText(locationPlan.getStatement());
            } else {
                ((ViewHolder) holder).mThingStatementInItem.setVisibility(View.GONE);
            }
            if (locationPlan.getMoneySpend() != 0) {
                String moneySpend = locationPlan.getMoneySpend() + "元";
                ((ViewHolder) holder).mSpendMoneyInItem.setText(moneySpend);
            } else {
                ((ViewHolder) holder).mSpendMoneyInItem.setVisibility(View.GONE);
            }
            if (locationPlan.getStayMinutes() != 0) {
                String stayMinutes = locationPlan.getStayMinutes() + "鍒嗛挓";
                ((ViewHolder) holder).mSpendTimeInItem.setText(stayMinutes);
            } else {
                ((ViewHolder) holder).mSpendTimeInItem.setVisibility(View.GONE);
            }
            if (locationPlan.getStartTime() != 0) {
                ((ViewHolder) holder).mStartTimeInItem.setText(TimeUtil.longToMyTime(locationPlan.getStartTime()).getSingleTime());
            } else {
                ((ViewHolder) holder).mStartTimeInItem.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postLatLngPointEvent(LatLngUtil.converLocationPlanToLatLngPoint(mLocationPlanList.get(mPosition)));
                }
            });
        }
        if (holder instanceof BottomViewHolder) {
            ((BottomViewHolder) holder).mAddLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAddLocationButton != null) {
                        mAddLocationButton.buttonClick();
                    }
                }
            });
        }
    }

    /**
     * button瀹炵幇鐨勬帴鍙?
     */
    public interface AddLocationButton {
        /**
         * 鐢卞閮ㄥ疄鐜扮殑鎺ュ彛鍑芥暟
         */
        void buttonClick();
    }

    @Override
    public int getItemCount() {
        return mLocationPlanList.size() + 1;
    }

    /**
     * 澧炲姞鍦扮偣
     * @param locationPlan 澧炲姞鐨勫湴鐐?
     */
    public void addLocationPlan(LocationPlan locationPlan) {
        mLocationPlanList.add(locationPlan);
        notifyItemInserted(mLocationPlanList.size());
    }

    /**
     * 杩斿洖鏄惁涓虹涓€涓湴鐐?
     * @return 鍒ゆ柇
     */
    public boolean getIsFirst() {
        return mLocationPlanList.size() == 0;
    }

    /**
     * 绉婚櫎鍦扮偣
     * @param position 闇€瑕佺Щ闄ゅ湴鐐圭殑浣嶇疆
     */
    public void removeLocationPlan(int position) {
        mLocationPlanList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 绉婚櫎鎵€鏈夌殑鍦扮偣
     */
    public void removeAllLocationPlan() {
        int i = mLocationPlanList.size();
        mLocationPlanList.clear();
        for (int j = 0; j <= i; j++) {
            notifyItemRemoved(j);
        }
    }

    /**
     * 鍙戦€佹秷鎭?
     * @param latLonPoint 鍦扮偣淇℃伅
     */
    private void postLatLngPointEvent(LatLonPoint latLonPoint) {
        EventBus.getDefault().post(latLonPoint);
    }

    public List<LocationPlan> getLocationPlanList() {
        return mLocationPlanList;
    }
}

