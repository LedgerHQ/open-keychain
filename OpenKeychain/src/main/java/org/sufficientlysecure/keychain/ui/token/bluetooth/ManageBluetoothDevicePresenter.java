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


import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;

import org.sufficientlysecure.keychain.operations.results.GenericOperationResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.ui.token.bluetooth.ManageBluetoothDeviceContract.ManageSecurityTokenMvpPresenter;
import org.sufficientlysecure.keychain.ui.token.bluetooth.ManageBluetoothDeviceContract.ManageSecurityTokenMvpView;
import org.sufficientlysecure.keychain.ui.token.bluetooth.ManageBluetoothDeviceFragment.StatusLine;


class ManageBluetoothDevicePresenter implements ManageSecurityTokenMvpPresenter {
    private final Context context;

    private final ManageBluetoothDeviceViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;


    private ManageSecurityTokenMvpView view;

    private boolean checkedKeyStatus;
    private boolean searchedLocally;
    private boolean searchedAtUri;
    private boolean searchedKeyservers;

    private byte[] importKeyData;
    private Long masterKeyId;

    private OperationLog log;
    private Uri selectedContentUri;

    ManageBluetoothDevicePresenter(Context context, LifecycleOwner lifecycleOwner, ManageBluetoothDeviceViewModel viewModel) {
        this.context = context.getApplicationContext();
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = viewModel;

        this.log = new OperationLog();
    }

    @Override
    public void setView(ManageSecurityTokenMvpView view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;

    }

    @Override
    public void onActivityCreated() {
        if (!checkedKeyStatus || !searchedLocally || !searchedAtUri || !searchedKeyservers) {
            continueBonding();
        }
    }

    private void continueBondingAfterError() {
        view.statusLineError();
        continueBonding();
    }

    private void resetAndcontinueBonding() {
        view.hideAction();
        view.resetStatusLines();
        continueBonding();
    }

    // TODO(LEDGER): Bluetooth bonding logic entry point
    private void continueBonding() {
        // Check bluetooth status
        view.statusLineAdd(StatusLine.CHECK_BLUETOOTH_STATUS);
        view.statusLineOk(); // demo

        // Discover devices
        view.statusLineAdd(StatusLine.DISCOVER_DEVICES);
        delaySimulatedNegativeCheck(); // demo

        // Show devices found
        // TODO(LEDGER)

        // Pair device
        // TODO(LEDGER)

        return;
    }

    private void delaySimulatedNegativeCheck() {
        new Handler().postDelayed(() -> {
            if (view == null) {
                return;
            }

            performSimulatedCheck(false);
        }, 10000);
    }

    private void performSimulatedCheck(Boolean isSuccess) {
        if (isSuccess) {
            view.statusLineOk();
        }
        else {
            view.statusLineError();
        }
        return;
    }

    @Override
    public void onClickRetry() {
        resetAndcontinueBonding();
    }

    @Override
    public void onMenuClickViewLog() {
        OperationResult result = new GenericOperationResult(GenericOperationResult.RESULT_OK, log);
        view.showDisplayLogActivity(result);
    }

    @Override
    public void onBondDevice() {
        // TODO(LEDGER): pair the device
    }

    @Override
    public void onBondSuccess(OperationResult result) {
        // TODO(LEDGER)
    }

    @Override
    public void onBondError(OperationResult result) {
        // TODO(LEDGER)
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }
}
