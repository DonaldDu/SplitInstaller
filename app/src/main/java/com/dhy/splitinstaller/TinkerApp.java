package com.dhy.splitinstaller;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public class TinkerApp extends TinkerApplication {
    protected TinkerApp() {
        super(ShareConstants.TINKER_DISABLE);
    }
}
