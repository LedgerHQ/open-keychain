/*
 * Copyright (C) 2017 Schürmann & Breitmoser GbR
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain.ui.token.bluetooth;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.operations.results.OperationResult;
import org.sufficientlysecure.keychain.securitytoken.SecurityTokenInfo;
import org.sufficientlysecure.keychain.service.ImportKeyringParcel;
import org.sufficientlysecure.keychain.service.PromoteKeyringParcel;
import org.sufficientlysecure.keychain.ui.LogDisplayActivity;
import org.sufficientlysecure.keychain.ui.LogDisplayFragment;
import org.sufficientlysecure.keychain.ui.token.bluetooth.ManageBluetoothDeviceContract.ManageSecurityTokenMvpPresenter;
import org.sufficientlysecure.keychain.ui.token.bluetooth.ManageBluetoothDeviceContract.ManageSecurityTokenMvpView;
import org.sufficientlysecure.keychain.ui.widget.StatusIndicator;
import org.sufficientlysecure.keychain.ui.widget.StatusIndicator.Status;
import org.sufficientlysecure.keychain.ui.widget.ToolableViewAnimator;

import java.util.Objects;


public class ManageBluetoothDeviceFragment extends Fragment implements ManageSecurityTokenMvpView, OnClickListener {
    private static final String ARG_TOKEN_INFO = "token_info";
    public static final int PERMISSION_LOCATION = 0;

    ManageSecurityTokenMvpPresenter presenter;
    private ViewGroup statusLayoutGroup;
    private ToolableViewAnimator actionAnimator;

    ImportKeyringParcel currentImportKeyringParcel;
    PromoteKeyringParcel currentPromoteKeyringParcel;
    private LayoutInflater layoutInflater;
    private StatusIndicator latestStatusIndicator;

    public static Fragment newInstance(SecurityTokenInfo tokenInfo) {
        ManageBluetoothDeviceFragment frag = new ManageBluetoothDeviceFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_TOKEN_INFO, tokenInfo);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        SecurityTokenInfo tokenInfo = Objects.requireNonNull(args).getParcelable(ARG_TOKEN_INFO);

        ManageBluetoothDeviceViewModel viewModel = ViewModelProviders.of(this).get(ManageBluetoothDeviceViewModel.class);

        presenter = new ManageBluetoothDevicePresenter(requireContext(), this, viewModel);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layoutInflater = inflater;

        View view = inflater.inflate(R.layout.create_security_token_bluetooth_device, container, false);

        statusLayoutGroup = view.findViewById(R.id.status_indicator_layout);
        actionAnimator = view.findViewById(R.id.action_animator);

        view.findViewById(R.id.button_retry).setOnClickListener(this);

        setHasOptionsMenu(true);

        presenter.setView(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detach();
        currentImportKeyringParcel = null;
        currentPromoteKeyringParcel = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.token_setup, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_log: {
                presenter.onMenuClickViewLog();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.onActivityCreated();
    }

    @Override
    public void statusLineAdd(StatusLine statusLine) {
        if (latestStatusIndicator != null) {
            throw new IllegalStateException("Cannot set next status line before completing previous!");
        }

        View line = layoutInflater.inflate(R.layout.status_indicator_line, statusLayoutGroup, false);

        latestStatusIndicator = line.findViewById(R.id.status_indicator);
        latestStatusIndicator.setDisplayedChild(Status.PROGRESS);
        TextView latestStatusText = line.findViewById(R.id.status_text);
        latestStatusText.setText(statusLine.stringRes);

        statusLayoutGroup.addView(line);
    }

    @Override
    public void statusLineOk() {
        latestStatusIndicator.setDisplayedChild(Status.OK);
        latestStatusIndicator = null;
    }

    @Override
    public void statusLineError() {
        latestStatusIndicator.setDisplayedChild(Status.ERROR);
        latestStatusIndicator = null;
    }

    @Override
    public void resetStatusLines() {
        latestStatusIndicator = null;
        statusLayoutGroup.removeAllViews();
    }

    @Override
    public void hideAction() {
        actionAnimator.setDisplayedChild(0);
    }




    @Override
    public void showDisplayLogActivity(OperationResult result) {
        Intent intent = new Intent(getActivity(), LogDisplayActivity.class);
        intent.putExtra(LogDisplayFragment.EXTRA_RESULT, result);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_retry: {
                presenter.onClickRetry();
                break;
            }
            // cast R.id.button_ . . .
        }
    }

    enum StatusLine {
        CHECK_BLUETOOTH_STATUS (R.string.status_bluetooth),
        DISCOVER_DEVICES (R.string.discover_bluetooth_devices);

        @StringRes
        private int stringRes;

        StatusLine(@StringRes int stringRes) {
            this.stringRes = stringRes;
        }
    }
}
