package com.cynorra.kpssarena.presentation.admin;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemScheduledTestBinding;
public class ScheduledTestAdapter extends ListAdapter<ScheduledTestItem, ScheduledTestAdapter.ViewHolder> {
    public ScheduledTestAdapter() {
        super(DIFF_CALLBACK);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduledTestBinding binding = ItemScheduledTestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemScheduledTestBinding binding;
        ViewHolder(ItemScheduledTestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(ScheduledTestItem item) {
            binding.tvDate.setText(item.getDate());
            binding.tvQuestionCount.setText(binding.getRoot().getContext()
                    .getString(R.string.scheduled_test_question_count_format, item.getQuestionCount()));
            binding.tvStatusBadge.setText(item.isActive() ? R.string.label_active : R.string.label_inactive);
            binding.tvStatusBadge.setTextColor(binding.getRoot().getContext()
                    .getColor(item.isActive() ? R.color.success : R.color.on_surface_variant));
        }
    }
    private static final DiffUtil.ItemCallback<ScheduledTestItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ScheduledTestItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ScheduledTestItem oldItem, @NonNull ScheduledTestItem newItem) {
                    return oldItem.getDate().equals(newItem.getDate());
                }
                @Override
                public boolean areContentsTheSame(@NonNull ScheduledTestItem oldItem, @NonNull ScheduledTestItem newItem) {
                    return oldItem.getQuestionCount() == newItem.getQuestionCount()
                            && oldItem.isActive() == newItem.isActive();
                }
            };
}
