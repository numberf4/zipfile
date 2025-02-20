package vn.tapbi.zazip.ui.menu;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.FragmentPolicyBinding;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.main.MainViewModel;

public class PolicyFragment extends BaseBindingFragment<FragmentPolicyBinding, MainViewModel> {
    @Override
    protected Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_policy;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        setupView();
        eventBack();
    }
    private void setupView(){
        binding.webview.loadUrl(getContext().getResources().getString(R.string.link_policy));
    }
    private void eventBack(){

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    ((MainActivity) requireActivity()).backToScreen(R.id.homeFragment);
                    mainViewModel.showDrawer.postValue(false);
                    return true;
                }
                return false;
            }
        });
        binding.ivPolicyBack.setOnClickListener(view ->{
            ((MainActivity) requireActivity()).backToScreen(R.id.homeFragment);
            mainViewModel.showDrawer.postValue(false);
        });
    }

    @Override
    protected void onPermissionGranted() {

    }
}
