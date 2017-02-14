package org.qtum.mromanovsky.qtum.ui.fragment.PinFragment;


import android.content.Context;

import org.bitcoinj.wallet.Wallet;
import org.qtum.mromanovsky.qtum.R;
import org.qtum.mromanovsky.qtum.ui.activity.MainActivity.MainActivity;
import org.qtum.mromanovsky.qtum.ui.fragment.BackUpWalletFragment.BackUpWalletFragment;
import org.qtum.mromanovsky.qtum.ui.fragment.BaseFragment.BaseFragmentPresenterImpl;
import org.qtum.mromanovsky.qtum.ui.fragment.WalletFragment.WalletFragment;


public class PinFragmentPresenterImpl extends BaseFragmentPresenterImpl implements PinFragmentPresenter {

    private PinFragmentView mPinFragmentView;
    private PinFragmentInteractorImpl mPinFragmentInteractor;
    private int pinForRepeat;

    public PinFragmentPresenterImpl(PinFragmentView pinFragmentView) {
        mPinFragmentView = pinFragmentView;
        mPinFragmentInteractor = new PinFragmentInteractorImpl(getView().getContext());
    }

    @Override
    public void confirm(String pin) {
        switch (PinFragment.sAction) {
            case PinFragment.CREATING: {
                if (pin.length() < 4) {
                    getView().confirmError(getView().getContext().getString(R.string.pin_is_not_long_enough));
                } else {
                    switch (PinFragment.currentState){
                        case 0:
                            pinForRepeat = Integer.parseInt(pin);
                            PinFragment.currentState=1;
                            getView().clearError();
                            getView().updateState();
                            break;
                        case 1:
                            if(Integer.parseInt(pin) == pinForRepeat) {
                                getView().clearError();
                                final BackUpWalletFragment backUpWalletFragment = BackUpWalletFragment.newInstance(true);
                                getView().setProgressDialog("Key generation");
                                getView().hideKeyBoard();
                                getInteractor().createWallet(getView().getContext(), new PinFragmentInteractorImpl.CreateWalletCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        getInteractor().savePassword(pinForRepeat);
                                        getView().openFragment(backUpWalletFragment);
                                        getView().dismissProgressDialog();
                                        PinFragmentInteractorImpl.isDataLoaded = false;
                                    }
                                });
                            } else {
                                getView().confirmError(getView().getContext().getString(R.string.incorrect_repeated_pin));
                            }
                            break;
                    }
                }
                break;
            }
            case PinFragment.IMPORTING: {
                if (pin.length() < 4) {
                    getView().confirmError(getView().getContext().getString(R.string.pin_is_not_long_enough));
                } else {
                    switch (PinFragment.currentState){
                        case 0:
                            pinForRepeat = Integer.parseInt(pin);
                            PinFragment.currentState=1;
                            getView().clearError();
                            getView().updateState();
                            break;
                        case 1:
                            if(Integer.parseInt(pin) == pinForRepeat) {
                                getView().clearError();
                                final WalletFragment walletFragment = WalletFragment.newInstance();
                                getView().hideKeyBoard();
                                getInteractor().savePassword(pinForRepeat);
                                getInteractor().setKeyGeneratedInstance(true);
                                getView().openFragment(walletFragment);
                            } else {
                                getView().confirmError(getView().getContext().getString(R.string.incorrect_repeated_pin));
                            }
                            break;
                    }
                }
                break;
            }

            case PinFragment.AUTHENTICATION: {
                if (pin.length() < 4) {
                    getView().confirmError(getView().getContext().getString(R.string.pin_is_not_long_enough));
                } else {
                    int intPassword = Integer.parseInt(pin);
                    if (intPassword == getInteractor().getPassword()) {
                        getView().clearError();
                        final WalletFragment walletFragment = WalletFragment.newInstance();
                        getView().setProgressDialog("Loading key");
                        getView().hideKeyBoard();
                        getInteractor().loadWalletFromFile(new PinFragmentInteractorImpl.LoadWalletFromFileCallBack() {
                            @Override
                            public void onSuccess() {
                                getView().openFragment(walletFragment);
                                getView().dismissProgressDialog();
                                PinFragmentInteractorImpl.isDataLoaded = false;
                            }
                        });
                    } else {
                        getView().confirmError(getView().getContext().getString(R.string.incorrect_pin));
                    }
                }
                break;
            }
            case PinFragment.CHANGING: {

                if (pin.length() < 4) {
                    getView().confirmError(getView().getContext().getString(R.string.pin_is_not_long_enough));
                } else {
                    switch (PinFragment.currentState){
                        case 0:
                            int intPassword = Integer.parseInt(pin);
                            if (intPassword == getInteractor().getPassword()){
                                PinFragment.currentState=1;
                                getView().clearError();
                                getView().updateState();
                            } else {
                                getView().confirmError(getView().getContext().getString(R.string.incorrect_pin));
                            }
                            break;
                        case 1:
                            pinForRepeat = Integer.parseInt(pin);
                            PinFragment.currentState=2;
                            getView().clearError();
                            getView().updateState();
                            break;
                        case 2:
                            if(Integer.parseInt(pin) == pinForRepeat) {
                                getView().clearError();
                                getView().hideKeyBoard();
                                getInteractor().savePassword(Integer.parseInt(pin));
                                getView().getFragmentActivity().onBackPressed();
                            } else {
                                getView().confirmError(getView().getContext().getString(R.string.incorrect_repeated_pin));
                            }
                            break;
                    }
                }
                break;
            }
        }
    }


    @Override
    public void cancel() {
        switch (PinFragment.sAction) {

            case PinFragment.AUTHENTICATION: {
                getView().finish();
                break;
            }
            case PinFragment.CREATING: {

            }
            case PinFragment.CHANGING: {
                getView().getFragmentActivity().onBackPressed();
                break;
            }
        }
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        pinForRepeat = 0;
        PinFragment.currentState = 0;
    }

    @Override
    public void onResume(Context context) {
        super.onResume(context);
        getView().updateState();
        ((MainActivity) getView().getFragmentActivity()).hideBottomNavigationView();
        if(PinFragmentInteractorImpl.isDataLoaded) {
            switch (PinFragment.sAction) {
                case PinFragment.CREATING: {
                    BackUpWalletFragment backUpWalletFragment = BackUpWalletFragment.newInstance(true);
                    getInteractor().savePassword(pinForRepeat);
                    getView().openFragment(backUpWalletFragment);
                    getView().dismissProgressDialog();
                    break;
                }
                case PinFragment.AUTHENTICATION: {
                    WalletFragment walletFragment = WalletFragment.newInstance();
                    getView().openFragment(walletFragment);
                    getView().dismissProgressDialog();
                    break;
                }
            }
            PinFragmentInteractorImpl.isDataLoaded = false;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(PinFragment.sAction.equals(PinFragment.CHANGING)) {
            ((MainActivity) getView().getFragmentActivity()).showBottomNavigationView();
        }
    }

    @Override
    public PinFragmentView getView() {
        return mPinFragmentView;
    }

    public PinFragmentInteractorImpl getInteractor() {
        return mPinFragmentInteractor;
    }
}