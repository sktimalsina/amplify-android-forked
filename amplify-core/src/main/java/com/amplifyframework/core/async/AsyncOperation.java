package com.amplifyframework.core.async;

import com.amplifyframework.core.provider.Provider;
import com.amplifyframework.core.task.Options;
import com.amplifyframework.core.task.Result;

public interface AsyncOperation {
    AsyncOperation callback(Callback<? extends Result> callback);

    AsyncOperation options(Options options);

    AsyncOperation provider(Class<? extends Provider> providerClass);

    AsyncOperation start();

    AsyncOperation pause();

    AsyncOperation resume();

    AsyncOperation cancel();
}
