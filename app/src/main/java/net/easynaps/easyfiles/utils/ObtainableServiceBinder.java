package net.easynaps.easyfiles.utils;

import android.app.Service;
import android.os.Binder;

public class ObtainableServiceBinder<T extends Service> extends Binder {

    private final T service;

    public ObtainableServiceBinder(T service) {
        this.service = service;
    }

    public T getService() {
        return service;
    }

}
