package com.cynorra.kpssarena.presentation.mistakes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemMistakeBinding;
import com.cynorra.kpssarena.domain.model.Mistake;
public class MistakesAdapter extends ListAdapter<Mistake, MistakesAdapter.ViewHolder> {
    public MistakesAdapter() {
        super(DIFF_CALLBACK);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMistakeBinding binding = ItemMistakeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMistakeBinding binding;
        ViewHolder(ItemMistakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Mistake mistake) {
            binding.tvQuestionText.setText(mistake.getQuestionText());
            binding.tvCategory.setText(mistake.getCategory());
            binding.tvUserAnswer.setText(mistake.getUserAnswer() != null && !mistake.getUserAnswer().isEmpty()
                    ? mistake.getUserAnswer() : binding.getRoot().getContext().getString(R.string.mistake_left_blank));
            binding.tvCorrectAnswer.setText(mistake.getCorrectAnswer());
            binding.tvExplanation.setText(mistake.getExplanation());
            binding.tvExplanation.setVisibility(
                    mistake.getExplanation() != null && !mistake.getExplanation().isEmpty()
                            ? View.VISIBLE : View.GONE);
            binding.tvRetriedBadge.setVisibility(mistake.isRetried() ? View.VISIBLE : View.GONE);
        }
    }
    private static final DiffUtil.ItemCallback<Mistake> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Mistake>() {
                @Override
                public boolean areItemsTheSame(@NonNull Mistake oldItem, @NonNull Mistake newItem) {
                    return oldItem.getQuestionId().equals(newItem.getQuestionId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Mistake oldItem, @NonNull Mistake newItem) {
                    return oldItem.isRetried() == newItem.isRetried()
                            && oldItem.isRetryCorrect() == newItem.isRetryCorrect();
                }
            };
}
