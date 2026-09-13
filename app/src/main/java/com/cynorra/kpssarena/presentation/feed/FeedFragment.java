package com.cynorra.kpssarena.presentation.feed;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cynorra.kpssarena.databinding.FragmentFeedBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class FeedFragment extends BaseFragment<FragmentFeedBinding, FeedViewModel> {
    private FeedAdapter adapter;
    @Override
    protected FragmentFeedBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFeedBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<FeedViewModel> getViewModelClass() {
        return FeedViewModel.class;
    }
    @Override
    protected void initViews() {
        adapter = new FeedAdapter();
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFeed.setAdapter(adapter);
        adapter.setOnLikeClickListener(post -> viewModel.toggleLike(post));
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadFeed());
    }
    @Override
    protected void observeData() {
        viewModel.getFeedPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                adapter.submitList(posts);
                binding.emptyStateFeed.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
                binding.swipeRefresh.setVisibility(posts.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                binding.swipeRefresh.setRefreshing(loading != null && loading));
    }
}
