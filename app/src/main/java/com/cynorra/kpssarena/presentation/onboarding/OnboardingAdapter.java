package com.cynorra.kpssarena.presentation.onboarding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemOnboardingBinding;
import java.util.ArrayList;
import java.util.List;
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.Vie
    private final List<OnboardingPage> pages = new ArrayList<>();
    public OnboardingAdapter() {
        pages.add(new OnboardingPage(
                R.raw.onboarding_test,
                "Günlük Senkron Test",
                "Her gün saat 09:00'da yayınlanan 12 soruluk testi çöz, tüm Türkiye ile aynı and
        ));
        pages.add(new OnboardingPage(
                R.raw.onboarding_leaderboard,
                "Canlı Liderlik Tablosu",
                "Puanını gör, sıralamadaki yerini takip et, arkadaşlarınla rekabet et!"

        ));
        pages.add(new OnboardingPage(
                R.raw.onboarding_arena,
                "1v1 Arena",
                "Rakibine meydan oku, aynı anda aynı soruları çöz, kim daha iyi gör!"
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
