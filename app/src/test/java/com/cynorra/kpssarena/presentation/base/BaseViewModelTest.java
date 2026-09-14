package com.cynorra.kpssarena.presentation.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

public class BaseViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class TestableViewModel extends BaseViewModel {
        void loading(boolean loading) {
            setLoading(loading);
        }

        void error(String message) {
            setError(message);
        }

        void success(String message) {
            setSuccess(message);
        }

        void resetError() {
            clearError();
        }

        void resetSuccess() {
            clearSuccess();
        }
    }

    @Test
    public void isLoading_defaultsToFalse() {
        TestableViewModel viewModel = new TestableViewModel();
        assertFalse(viewModel.getIsLoading().getValue());
    }

    @Test
    public void setLoading_updatesIsLoading() {
        TestableViewModel viewModel = new TestableViewModel();
        viewModel.loading(true);
        assertTrue(viewModel.getIsLoading().getValue());
        viewModel.loading(false);
        assertFalse(viewModel.getIsLoading().getValue());
    }

    @Test
    public void setError_updatesErrorMessage() {
        TestableViewModel viewModel = new TestableViewModel();
        viewModel.error("something went wrong");
        assertEquals("something went wrong", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void clearError_setsErrorMessageToNull() {
        TestableViewModel viewModel = new TestableViewModel();
        viewModel.error("boom");
        viewModel.resetError();
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void setSuccess_updatesSuccessMessage() {
        TestableViewModel viewModel = new TestableViewModel();
        viewModel.success("done");
        assertEquals("done", viewModel.getSuccessMessage().getValue());
    }

    @Test
    public void clearSuccess_setsSuccessMessageToNull() {
        TestableViewModel viewModel = new TestableViewModel();
        viewModel.success("done");
        viewModel.resetSuccess();
        assertNull(viewModel.getSuccessMessage().getValue());
    }
}
