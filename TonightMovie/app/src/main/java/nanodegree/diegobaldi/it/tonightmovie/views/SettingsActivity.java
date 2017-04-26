package nanodegree.diegobaldi.it.tonightmovie.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ActivitySettingsBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.UserSettingsViewModel;

/**
 * Created by diego on 12/04/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE_REQUEST_CODE = 124;
    private static final String BUNDLE_NOTIFICATION = "notification";
    private static final String BUNDLE_DISPLAY_NAME = "displayName";
    private static final String BUNDLE_BIO = "bio";
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static final String PREFS_NAME = "MovieMasterPrefs";
    private Uri outputFileUri, resultUri, downloadUrl;
    private ActivitySettingsBinding binding;
    private User changedUser;
    private UserProfileChangeRequest profileUpdates;
    private boolean notifications;



    private ProgressDialog updateDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        notifications = settings.getBoolean("notifications", true);
        if(savedInstanceState==null)
            binding.setUser(new UserSettingsViewModel(TonightMovieApp.getUser(), this, notifications));
        else {
            User user = TonightMovieApp.getUser();
            user.setDisplayName(savedInstanceState.getString(BUNDLE_DISPLAY_NAME));
            if(savedInstanceState.containsKey(BUNDLE_BIO))
                user.setBio(savedInstanceState.getString(BUNDLE_BIO));
            binding.setUser(new UserSettingsViewModel(user, this, savedInstanceState.getBoolean(BUNDLE_NOTIFICATION)));
        }
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        binding.changePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "CLICKED!");
                changePicture(viewGroup);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    updateProfile();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_DISPLAY_NAME, binding.nameInput.getText().toString());
        if(binding.bioInput.getText()!=null)
            outState.putString(BUNDLE_BIO, binding.bioInput.getText().toString());
        outState.putBoolean(BUNDLE_NOTIFICATION, binding.notificationSwitch.isChecked());
        super.onSaveInstanceState(outState);
    }

    private void changePicture(ViewGroup parent) {
        final PermissionListener snackbarPermissionListener =
                SnackbarOnDeniedPermissionListener.Builder
                        .with(parent, R.string.permission_request_storage)
                        .withOpenSettingsButton("Settings")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                // Event handler for when the given Snackbar is visible
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Event handler for when the given Snackbar has been dismissed
                            }
                        })
                        .build();
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new CompositePermissionListener(new BasePermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                openImageIntent();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                super.onPermissionRationaleShouldBeShown(permission, token);
            }
        },snackbarPermissionListener)).check();
    }

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Fixapp" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.image_from_title));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.profilePicture.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }

                Uri selectedImageUri;
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "TonightMovie" + File.separator);
                root.mkdirs();
                final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                final Uri finalOutputFileUri = Uri.fromFile(sdImageMainDirectory);
                if (isCamera) {
                    selectedImageUri = outputFileUri;

                    UCrop.of(selectedImageUri, finalOutputFileUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(500, 500)
                            .start(this);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    UCrop.of(selectedImageUri, finalOutputFileUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(500, 500)
                            .start(this);
                }
            }
        }
    }


    private boolean validate() {
        boolean valid = true;

        String name = binding.nameInput.getText().toString();

        if (name.isEmpty()) {
            binding.nameInput.setError("cannot be empty");
            valid = false;
        } else {
            binding.nameInput.setError(null);
        }
        return valid;
    }

    private void updateProfile(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("notifications", binding.notificationSwitch.isChecked());
        // Commit the edits!
        editor.commit();
        updateDialog = new ProgressDialog(this, R.style.darkAlertDialog);
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        // set indeterminate style
        updateDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // set title and message
        updateDialog.setMessage(getString(R.string.profile_updating));
        updateDialog.show();
        if(resultUri!=null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://tonightmovie-84e20.appspot.com").child("uploads/users");
            Uri file = resultUri;
            StorageReference riversRef = storageRef.child(TonightMovieApp.getUser().getId()+"_"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    updateDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, R.string.profile_update_error, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //noinspection VisibleForTests
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(binding.nameInput.getText().toString())
                            .setPhotoUri(downloadUrl)
                            .build();
                    updateUserObject();
                }
            });
        }
        else{
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(binding.nameInput.getText().toString())
                    .build();
            updateUserObject();
        }
    }
    private void updateUserObject(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String displayName = binding.nameInput.getText().toString();
                            String bio = binding.bioInput.getText().toString();
                            if(downloadUrl!=null){
                                changedUser = new User(firebaseUser.getUid(), displayName, downloadUrl.toString());
                            }
                            else{
                                changedUser = new User(firebaseUser.getUid(), displayName, firebaseUser.getPhotoUrl().toString());

                            }
                            changedUser.setBio(bio);
                            Map<String, Object> userValues = changedUser.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/users/" + firebaseUser.getUid(), userValues);
                            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        updateDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, R.string.profile_update_error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        TonightMovieApp.setUser(changedUser);
                                        Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                                        intent.putExtra("profile", changedUser);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        updateDialog.dismiss();
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            updateDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, R.string.profile_update_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
