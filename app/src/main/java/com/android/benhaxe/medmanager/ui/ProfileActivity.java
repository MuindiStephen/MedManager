package com.android.benhaxe.medmanager.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.accountCreation.AccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.benhaxe.medmanager.ui.AllMed.toolbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ProfileActivity.class.getSimpleName();

    private CircleImageView profileImage;

    private EditText mName, mRegion, mEmail;
    private TextView mMedications, mMonths;

    private ProgressDialog mProgressDialog;

    //[Fire base]
    DatabaseReference userDp;
    FirebaseAuth mAuth;
    StorageReference userStore;

    //[Related to starting gallery]
    private final int GALLERY_REQUEST = 1;
    private Uri dpUri = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mProgressDialog = new ProgressDialog(this);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(this);

        mName = findViewById(R.id.user_name);
        mRegion = findViewById(R.id.region_country);
        mEmail = findViewById(R.id.user_email);

        mMedications = findViewById(R.id.medication_number);
        mMonths = findViewById(R.id.medication_month);

        userDp = FirebaseDatabase.getInstance().getReference(USER);
        mAuth = FirebaseAuth.getInstance();
        userStore = FirebaseStorage.getInstance().getReference("Images");

        userDp.keepSynced(true);

        retrieveUserContent();
    }

    public void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose display picture"), GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            dpUri = data.getData();
            CropImage.activity(dpUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                dpUri = result.getUri();
                profileImage.setImageURI(dpUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "Error code: " + error);
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateAccount() {
        //[Validate user]
        final String name = mName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String region = mRegion.getText().toString().trim();

        final String user_id = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(region) && dpUri != null) {
            mProgressDialog.setMessage("Updating Account...");
            mProgressDialog.show();
            // Save the image filePath into Class [Storage_Reference]
            /*This saves the image into fire_base storage*/
            StorageReference filePath = userStore.child(dpUri.getLastPathSegment());
            // Put the file path into the URI
            filePath.putFile(dpUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    userDp.child(user_id).child("name").setValue(name);
                    userDp.child(user_id).child("email").setValue(email);
                    userDp.child(user_id).child("region").setValue(region);
                    userDp.child(user_id).child("profile_pics").setValue(downloadUri);

                    mProgressDialog.dismiss();
                    Intent mainIntent = new Intent(ProfileActivity.this, AllMed.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Log.e(TAG, "Account setup error: " + e.getMessage());
                    /*Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mName.setError(REQUIRED);
            mEmail.setError(REQUIRED);
            mRegion.setError(REQUIRED);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.profile_image:
                selectImage();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent[] intent = new Intent[1];
        switch (item.getItemId()) {
            case R.id.menu_logout:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                intent[0] = new Intent(this, AccountActivity.class);
                intent[0].setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent[0]);
                finish();
                break;

            case R.id.menu_save:
                updateAccount();
                break;

            case R.id.menu_delete:
                mProgressDialog.setMessage("Deleting Account...");
                mProgressDialog.show();
                FirebaseUser user = mAuth.getCurrentUser();


                userDp.child(user.getUid()).removeValue();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            intent[0] = new Intent(ProfileActivity.this, AccountActivity.class);
                            intent[0].setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent[0]);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Can't delete this account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveUserContent() {
        String user_id = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "User ID" + user_id);
        if (user_id != null) {
            Log.d(TAG, "USer key: " + user_id);
            userDp.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String dp = (String) dataSnapshot.child("profile_pics").getValue();
                    String user_name = (String) dataSnapshot.child("name").getValue();
                    String user_email = (String) dataSnapshot.child("email").getValue();
                    String user_region = (String) dataSnapshot.child("region").getValue();

                    Picasso.with(ProfileActivity.this).load(dp).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(dp).into(profileImage);
                        }
                    });
                    mName.setText(user_name);
                    Log.d(TAG, "User name: " + user_name);
                    mEmail.setText(user_email);
                    mRegion.setText(user_region);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            profileImage.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AllMed.class));
        finish();
    }
}
