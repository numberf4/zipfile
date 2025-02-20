package vn.tapbi.zazip.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.main.MainViewModel;

public abstract class BaseBindingDialogFragment<B extends ViewDataBinding, VM extends BaseViewModel> extends BaseDialogFragment {
    public B binding;
    public VM viewModel;
    public MainViewModel mainViewModel;

    public abstract int getLayoutId();
    public abstract Class<VM> getViewModel();
    protected abstract void onCreatedView(View view, Bundle savedInstanceState);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        viewModel = new ViewModelProvider(this).get(getViewModel());
        mainViewModel = new ViewModelProvider((MainActivity)(requireActivity())).get(MainViewModel.class);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onCreatedView(view,savedInstanceState);

    }
}
