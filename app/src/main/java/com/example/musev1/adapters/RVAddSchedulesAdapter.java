package com.example.musev1.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musev1.R;
import com.example.musev1.model.ScheduleModel;

public class RVAddSchedulesAdapter extends ListAdapter<ScheduleModel, RVAddSchedulesAdapter.ASViewHolder> {
    private final Context context;
    private static final DiffUtil.ItemCallback<ScheduleModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ScheduleModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScheduleModel oldItem, @NonNull ScheduleModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScheduleModel oldItem, @NonNull ScheduleModel newItem) {
            return oldItem.getDeviceName().equals(newItem.getDeviceName()) &&
                    oldItem.getPictureId() == (newItem.getPictureId()) &&
                    oldItem.getState().equals(newItem.getState());
        }
    };

    public RVAddSchedulesAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ASViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ASViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_schedules, parent, false));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ASViewHolder holder, int position) {
        ScheduleModel schedule_model = getItem(position);
        holder.iv_icon.setImageResource(schedule_model.getPictureId());
        holder.tv_name.setText(schedule_model.getDeviceName());
        holder.tv_state.setText(schedule_model.getState());
        holder.tv_days.setText(schedule_model.getRepeat());

        if (schedule_model.getAtTime() != null) {
            holder.tv_later.setText("At");
            holder.tv_period.setText(schedule_model.getAtTime());
        }

        if (schedule_model.getAfterPeriod() != null) {
            holder.tv_later.setText("After");
            holder.tv_period.setText(schedule_model.getAfterPeriod());
        }

        if (schedule_model.getRepeat().equals(""))
            holder.repeatGroup.setVisibility(View.GONE);
        else
            holder.tv_days.setText(schedule_model.getRepeat());
    }

    public ScheduleModel getItemAt(int position) {
        return getItem(position);
    }

    static class ASViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_state, tv_later, tv_period, tv_days;
        private ImageView iv_icon;
        private Group repeatGroup;

        public ASViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.itemAS_iv_icon);
            tv_name = itemView.findViewById(R.id.itemAS_tv_name);
            tv_state = itemView.findViewById(R.id.itemAS_tv_state);
            tv_later = itemView.findViewById(R.id.itemAS_tv_later);
            tv_period = itemView.findViewById(R.id.itemAS_tv_period);
            tv_days = itemView.findViewById(R.id.itemAS_tv_days);
            repeatGroup = itemView.findViewById(R.id.itemAS_repeatGroup);
        }
    }
}
