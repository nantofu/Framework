package cn.xuhao.android.lib.cockroach;

/**
 * Created by xuhao on 2017/2/15.
 */

final class QuitCockroachException extends RuntimeException {
    public QuitCockroachException(String message) {
        super(message);
    }
}
