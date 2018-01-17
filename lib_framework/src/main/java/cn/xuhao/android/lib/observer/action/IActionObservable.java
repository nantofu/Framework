package cn.xuhao.android.lib.observer.action;


/**
 * 行为观察者(Andorid组件的行为,例如onBackPressed,onActivityResult等)
 */
public interface IActionObservable<T extends IActionObserver> {
    void addActionObserver(T listener);

    boolean removeActionObserver(T listener);
}
