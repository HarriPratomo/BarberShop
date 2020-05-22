package com.example.barbershop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.barbershop.Common.Common;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int APP_REQUEST_CODE = 8099;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @BindView(R.id.login_button)
    Button login_btn;

    @OnClick(R.id.login_button)
    void Login() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), APP_REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth1 -> {
            FirebaseUser user = firebaseAuth1.getCurrentUser();
            if (user != null) {
                checkUserFromFirebase(user);
            }
        };
        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_CALENDAR,
                        android.Manifest.permission.WRITE_CALENDAR).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    FirebaseInstanceId.getInstance()
                            .getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    Common.updateToken(getBaseContext(),task.getResult().getToken());
                                    Log.d("EDMTToken", task.getResult().getToken());
                                    Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                    intent.putExtra(Common.IS_LOGIN, true);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainMenu.class);
                            intent.putExtra(Common.IS_LOGIN, true);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

    }

    private void checkUserFromFirebase(FirebaseUser user) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()&&user !=null) {
                            //missing
                          //  com.example.barbershop.Common.Common.updateToken(getBaseContext(), task.getResult().getToken());
                            Log.d("EDMTToken", task.getResult().getToken());
                            Intent intent = new Intent(MainActivity.this, MainMenu.class);
                            intent.putExtra(com.example.barbershop.Common.Common.IS_LOGIN, true);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                intent.putExtra(com.example.barbershop.Common.Common.IS_LOGIN, true);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Failed to sign in !", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
