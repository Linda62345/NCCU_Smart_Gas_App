package glotech.smartgasapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class DeviceInfoPagerAdapter extends FragmentStateAdapter {
    private List<DeviceInfo> deviceInfoList;

    public DeviceInfoPagerAdapter(FragmentActivity fragmentActivity, List<DeviceInfo> deviceInfoList) {
        super(fragmentActivity);
        this.deviceInfoList = deviceInfoList;
    }

    @Override
    public int getItemCount() {
        return deviceInfoList.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the existing fragment from the list
        return deviceInfoList.get(position);
    }
}
