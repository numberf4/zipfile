package vn.tapbi.zazip.ui.menu;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.FragmentUpgradeVersionBinding;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.main.MainViewModel;

public class UpgradeVersionFragment extends BaseBindingFragment<FragmentUpgradeVersionBinding, MainViewModel> {
    @Override
    protected Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_upgrade_version;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        setupView();
    }

    private void setupView() {
        binding.btnSubscribe.setOnClickListener(v ->{
            ((MainActivity) requireActivity()).backToScreen(R.id.homeFragment);
            mainViewModel.showDrawer.postValue(false);
        });
        binding.cslContentUser.setOnClickListener(v ->{
            resetSelect();
            binding.cslContentUser.setBackgroundResource(R.drawable.bg_best_offer_select);
            binding.ivCheckUser.setImageResource(R.drawable.ic_select_item);
        });
        binding.cslContentMonth.setOnClickListener(v ->{
            setupSelect(binding.cslContentMonth,binding.ivCheckMonth);
        });
        binding.cslContentLifeTime.setOnClickListener(v ->{
            setupSelect(binding.cslContentLifeTime,binding.ivCheckLifeTime);
        });
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    ((MainActivity) requireActivity()).backToScreen(R.id.homeFragment);
                    mainViewModel.showDrawer.postValue(false);
                    return true;
                }
                return false;
            }
        });
    }
    private void setupSelect(View view1, ImageView view2){
        resetSelect();
        view1.setBackgroundResource(R.drawable.bg_offer_select);
        view2.setImageResource(R.drawable.ic_select_item);
    }

    private void resetSelect(){
        binding.cslContentUser.setBackgroundResource(R.drawable.bg_best_offer);
        binding.cslContentMonth.setBackgroundResource(R.drawable.bg_offer);
        binding.cslContentLifeTime.setBackgroundResource(R.drawable.bg_offer);
        binding.ivCheckUser.setImageResource(R.drawable.ic_not_select_item);
        binding.ivCheckMonth.setImageResource(R.drawable.ic_not_select_item);
        binding.ivCheckLifeTime.setImageResource(R.drawable.ic_not_select_item);
    }

    @Override
    protected void onPermissionGranted() {

    }
}
