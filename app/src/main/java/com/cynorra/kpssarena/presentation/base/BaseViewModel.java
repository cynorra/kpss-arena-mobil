package com.cynorra.kpssarena.presentation.base;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;
public abstract class BaseViewModel extends ViewModel {
    protected final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    protected final MutableLiveData<String> successMessage = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<String> getSuccessMessage() {

        return successMessage;
    }
    protected void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }
    protected void setError(String message) {
        errorMessage.postValue(message);
        // A failed operation is over: never leave a spinner running after an error
        isLoading.postValue(false);
        Timber.e("ViewModel Error: %s", message);
    }
    protected void setSuccess(String message) {
        successMessage.postValue(message);
    }
    protected void clearError() {
        errorMessage.postValue(null);
    }
    protected void clearSuccess() {
        successMessage.postValue(null);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.d("ViewModel cleared: %s", getClass().getSimpleName());
    }
}
