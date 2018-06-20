package com.harun.android.loginpin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.background.BaseActivity;
import com.example.android.background.sync.CashTrackSyncUtils;
import com.example.android.modellibrary.CashTrackResultReceiver;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.harun.android.loginpin.Fragments.EnterPhoneFragment;
import com.harun.android.loginpin.Fragments.LoginPINFragment;
import com.harun.android.loginpin.databinding.ActivityAuthBinding;

import java.util.concurrent.TimeUnit;

import static com.example.android.modellibrary.model.User.TOKEN_KEY;

public class AuthActivity extends BaseActivity implements LoginPINFragment.OnLocalAuthenticationListener,
        EnterPhoneFragment.OnMobileNumberEnteredListener, CashTrackResultReceiver.OnTokenReceived {
    private static final String LOG_TAG = AuthActivity.class.getSimpleName();
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    public static final String PASS_TOKEN = "pass-token";
    CashTrackResultReceiver tokenReceiver;
    private User user;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mVerificationInProgress = false;
    private FirebaseAuth mAuth;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthBinding authBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        LocalStore userLocalStore = new LocalStore(getBaseContext());
        tokenReceiver = new CashTrackResultReceiver(new Handler());
        tokenReceiver.setTokenReceiver(this);

        mAuth = FirebaseAuth.getInstance();
        getCallback();

        if (!userLocalStore.getStoredUser().getPhoneNo().isEmpty()) {
            user = userLocalStore.getStoredUser();
            displayLoginPinFragment(user);
        } else {
            displayPhoneFragment();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            displayLoginPinFragment(user);
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            Log.w(LOG_TAG, "firebaseUser: " + firebaseUser.getPhoneNumber()+"... "+user.getName());
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    public void displayLoginPinFragment(User user) {
        LoginPINFragment loginPINFragment = LoginPINFragment.newInstance(user);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_fragment_container);

        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_fragment_container, loginPINFragment)
                    .commit();
            Log.w(LOG_TAG, "add: " + user);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_fragment_container, loginPINFragment)
                    .addToBackStack(null)
                    .commit();
            Log.w(LOG_TAG, "replace: " + user);
        }
    }

    public void displayPhoneFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_fragment_container, new EnterPhoneFragment())
                .commit();
    }

    @Override
    public void onMobileNumberEntered(User user) {
        Log.w(LOG_TAG, "onMobileNumberEntered: " + user.getPhoneNo());
        this.user = user;
        //TODO: restore after testing
//        displayLoginPinFragment(user);
        startPhoneNumberVerification(this.user);
    }

    private void getCallback(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(LOG_TAG, "onVerificationCompleted:" + phoneAuthCredential);

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(LOG_TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...

            }
        };
    }

    private void startPhoneNumberVerification(final User user) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                user.getPhoneNo(),       //Phone Number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks );// ;        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }


    @Override
    public void onLocalAuthentication(User user) {
        Log.w(LOG_TAG, "onLocalAuthentication: " + user.getPhoneNo());
        CashTrackSyncUtils.initialize(this.getBaseContext(), tokenReceiver);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTokenReceivedListener(String token) {
        Log.w(LOG_TAG, "onTokenReceivedListener: " + token);
        startMainActivity(token);
        tokenReceiver.setTokenReceiver(null);
    }

    protected void startMainActivity(String token) {
        Log.w(LOG_TAG, "startMainActivity ...");
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN_KEY, token);
        try {
            startActivity(new Intent(getBaseContext(), Class.forName("com.harun.android.cashtrack.activities.MainActivity"))
                    .putExtra(EXTRA_INTENT, bundle)
                    .setAction(PASS_TOKEN));
            finish();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
