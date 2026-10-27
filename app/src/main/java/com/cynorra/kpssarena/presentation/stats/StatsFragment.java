package com.cynorra.kpssarena.presentation.stats;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentStatsBinding;
import com.cynorra.kpssarena.domain.model.StatsData;

import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class StatsFragment extends BaseFragment<FragmentStatsBinding, StatsViewModel> {
    @Override
    protected FragmentStatsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentStatsBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<StatsViewModel> getViewModelClass() {
        return StatsViewModel.class;
    }
    @Override
    protected void initViews() {
        setupChart();
    }
    private void setupChart() {
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.setDrawGridBackground(false);
        binding.lineChart.setTouchEnabled(true);
        binding.lineChart.setDragEnabled(true);
        binding.lineChart.setScaleEnabled(true);
        binding.lineChart.setPinchZoom(true);
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);
        // Dark theme: the chart's default black text is unreadable on the dark card
        int textColor = Color.parseColor("#C1C9D9");
        xAxis.setTextColor(textColor);
        binding.lineChart.getAxisLeft().setTextColor(textColor);
        binding.lineChart.getAxisLeft().setGridColor(Color.parseColor("#2A3346"));
        binding.lineChart.getAxisRight().setEnabled(false);
        binding.lineChart.getLegend().setTextColor(textColor);
    }
    @Override
    protected void observeData() {
        viewModel.getStatsData().observe(getViewLifecycleOwner(), this::displayStats);
    }
    private void displayStats(StatsData stats) {
        if (stats == null) return;
        binding.tvTotalTests.setText(String.valueOf(stats.getTotalTests()));
        binding.tvTotalQuestions.setText(String.valueOf(stats.getTotalQuestionsSolved()));
        binding.tvOverallAccuracy.setText(String.format("%.1f%%", stats.getOverallAccuracy()));
        binding.tvCurrentStreak.setText(getResources().getQuantityString(R.plurals.streak_days, stats.getCurrentStreak(), stats.getCurrentStreak()));
        binding.tvHighestScore.setText(String.valueOf(stats.getHighestScore()));
        binding.tvMatchesWon.setText(String.format("%d/%d", stats.getTotalMatchesWon(), stats.getTotalMatchesPlayed()));
        // Update line chart
        updateLineChart(stats.getDailyScores());
    }
    private void updateLineChart(List<StatsData.DailyScoreEntry> dailyScores) {
        if (dailyScores == null || dailyScores.isEmpty()) return;
        List<Entry> netScoreEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < dailyScores.size(); i++) {
            StatsData.DailyScoreEntry entry = dailyScores.get(i);
            netScoreEntries.add(new Entry(i, entry.getNetScore()));
            labels.add(entry.getDate().length() > 5 ? entry.getDate().substring(5, Math.min(10, entry.getDate().length())) : entry.getDate()); // MM-DD format
        }
        LineDataSet netDataSet = new LineDataSet(netScoreEntries, getString(R.string.chart_net_score_legend));
        netDataSet.setColor(Color.parseColor("#C9A84C"));
        netDataSet.setLineWidth(2f);
        netDataSet.setCircleColor(Color.parseColor("#C9A84C"));
        netDataSet.setCircleRadius(4f);
        netDataSet.setDrawValues(true);

        netDataSet.setValueTextColor(Color.parseColor("#E2E8F0"));
        netDataSet.setValueTextSize(10f);
        LineData lineData = new LineData(netDataSet);
        binding.lineChart.setData(lineData);
        binding.lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.lineChart.invalidate();
    }
}
