package org.qtum.wallet.ui.base.base_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public interface BasePresenter {

    void onCreate(Context context);

    void onStart(Context context);

    void onResume();

    void onPause();

    void onStop(Context context);

    void onDestroy(Context context);

    void onPostCreate(Context context);

    void initializeViews();

    void onRestoreInstanceState(Bundle savedState);

    void restoreState(Bundle savedState);

    void saveState(Bundle outState);

    void onPostResume(Context context);

    void handleInitialArguments(Bundle arg);

    void startActivity(Intent intent);

    BaseContextView getView();

}
