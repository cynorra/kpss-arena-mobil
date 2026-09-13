package com.cynorra.kpssarena.presentation.onboarding;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemOnboardingBinding;
import java.util.ArrayList;
import java.util.List;
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private final List<OnboardingPage> pages = new ArrayList<>();
    public OnboardingAdapter(Context context) {
        pages.add(new OnboardingPage(
                R.raw.onboarding_test,
                context.getString(R.string.onboarding_page1_title),
                context.getString(R.string.onboarding_page1_desc)
        ));
        pages.add(new OnboardingPage(
                R.raw.onboarding_leaderboard,
                context.getString(R.string.onboarding_page2_title),
                context.getString(R.string.onboarding_page2_desc)

        ));
        pages.add(new OnboardingPage(
                R.raw.onboarding_arena,
                context.getString(R.string.onboarding_page3_title),
                context.getString(R.string.onboarding_page3_desc)
        ));
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOnboardingBinding binding = ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }
    @Override
    public int getItemCount() {
        return pages.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOnboardingBinding binding;
        ViewHolder(ItemOnboardingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(OnboardingPage page) {
            binding.lottieAnimation.setAnimation(page.getAnimationRes());
            binding.lottieAnimation.playAnimation();
            binding.tvTitle.setText(page.getTitle());
            binding.tvDescription.setText(page.getDescription());
        }

    }
    static class OnboardingPage {
        private final int animationRes;
        private final String title;
        private final String description;
        OnboardingPage(int animationRes, String title, String description) {
            this.animationRes = animationRes;
            this.title = title;
            this.description = description;
        }
        public int getAnimationRes() { return animationRes; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }
}
