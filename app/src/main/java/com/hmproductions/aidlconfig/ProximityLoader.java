package com.hmproductions.aidlconfig;

import android.content.Context;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

public class ProximityLoader extends AsyncTaskLoader<Boolean> {

    private IProximityInterface proximityInterface;

    ProximityLoader(@NonNull Context context, IProximityInterface proximityInterface) {
        super(context);
        this.proximityInterface = proximityInterface;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            return proximityInterface.isNear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }
}
