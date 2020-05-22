package com.example.barbershop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import android.accounts.Account;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.barbershop.Common.Common;
import com.example.barbershop.fragment.ChartFragment;
import com.example.barbershop.fragment.HomeFragment;
import com.example.barbershop.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenu extends AppCompatActivity {

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference collectionReference;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        collectionReference = FirebaseFirestore.getInstance().collection("Users");
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                DocumentReference currentUser = collectionReference.document(user.getPhoneNumber());
                currentUser.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userSnapshot = task.getResult();
                                    if (!userSnapshot.exists()) {
                                        showUpdateDialog(user.getPhoneNumber());
                                    }
                                    else
                                    {
                                        Common.currentUser = userSnapshot.toObject(User.class);
                                        bottomNavigationView.setSelectedItemId(R.id.action_home);
                                    }
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                }
                            }
                        });
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home)
                    fragment = new HomeFragment();
                else if (item.getItemId() == R.id.action_chart)
                    fragment = new ChartFragment();
                return loadFragment(fragment);
            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(String phoneNumber) {
        if (dialog.isShowing())
            dialog.dismiss();

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One more step!");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);
        Button btn_update = (Button) sheetView.findViewById(R.id.btnUpdate);
        TextInputEditText edt_name = (TextInputEditText) sheetView.findViewById(R.id.edtName);
        TextInputEditText edt_address = (TextInputEditText) sheetView.findViewById(R.id.edtAddress);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();
                User user = new User(edt_name.getText().toString(),
                        edt_address.getText().toString(), phoneNumber);
                collectionReference.document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                Common.currentUser = user;
                                bottomNavigationView.setSelectedItemId(R.id.action_home);
                                Toast.makeText(MainMenu.this, "Thank you", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Toast.makeText(MainMenu.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}
