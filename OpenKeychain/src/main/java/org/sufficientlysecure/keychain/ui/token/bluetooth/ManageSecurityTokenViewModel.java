package org.sufficientlysecure.keychain.ui.token.bluetooth;


import android.content.Context;
import androidx.lifecycle.ViewModel;

import org.sufficientlysecure.keychain.securitytoken.SecurityTokenInfo;


public class ManageSecurityTokenViewModel extends ViewModel {
    private static final long MIN_OPERATION_TIME_MILLIS = 700;

    SecurityTokenInfo tokenInfo;

    void setTokenInfo(Context context, SecurityTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
