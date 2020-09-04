package cn.wzbos.android.chihiro.exception;

import org.gradle.api.GradleException;

import javax.annotation.Nullable;

/**
 * Chihiro Exception
 * Created by wuzongbo on 2020/09/02.
 */
public class ChihiroException extends GradleException {
    public ChihiroException() {
    }

    public ChihiroException(String message) {
        super(message);
    }

    public ChihiroException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
