package cn.xuhao.android.lib.cockroach;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by xuhao on 2017/2/14.
 */
public final class Cockroach {

    public interface ExceptionHandler {
        void handlerException(Thread thread, Throwable throwable);
    }

    private Cockroach() {
    }

    /**
     * 自定义异常捕获
     */
    private static ExceptionHandler sExceptionHandler;
    /**
     * 标记位，避免重复安装卸载
     */
    private static boolean sInstalled = false;

    /**
     * 当主线程或子线程抛出异常时会调用exceptionHandler.handlerException(Thread thread, Throwable throwable)
     * <p>
     * exceptionHandler.handlerException可能运行在非UI线程中。
     * <p>
     * 若设置了Thread.setDefaultUncaughtExceptionHandler则可能无法捕获子线程异常。
     *
     * @param exceptionHandler
     */
    public static synchronized void install(ExceptionHandler exceptionHandler) {
        if (sInstalled) {
            return;
        }
        sInstalled = true;
        sExceptionHandler = exceptionHandler;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        if (e instanceof QuitCockroachException) {
                            return;
                        }
                        if (sExceptionHandler != null) {
                            sExceptionHandler.handlerException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });
    }

    public static synchronized void uninstall() {
        if (!sInstalled) {
            return;
        }
        sInstalled = false;
        sExceptionHandler = null;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                throw new QuitCockroachException("Quit Cockroach.....");
            }
        });

    }
}
