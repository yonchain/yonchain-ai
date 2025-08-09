package com.yonchain.ai.console.context;

import com.yonchain.ai.api.sys.CurrentUser;

public class ConsoleContext {

    private CurrentUser currentUser;

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }
}
