package io.codelabs.digitutor.core.datasource.remote;


import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.data.model.Parent;
import io.codelabs.digitutor.data.model.Tutor;

/**
 * Firebase data source class
 * Required for all firebase transactions
 */
public final class FirebaseDataSource {

    // Login function: Email & password authentication
    public static void login(Activity host,
                             FirebaseAuth auth,
                             @NotNull LoginCredentials credentials,
                             @NotNull final AsyncCallback<Void> callback) {
        // Start process
        callback.onStart();

        if (credentials.validate()) {
            auth.signInWithEmailAndPassword(credentials.getEmail(), credentials.getPassword())
                    .addOnCompleteListener(host, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            if (user != null) {
                                callback.onSuccess(null);
                                callback.onComplete();
                            }
                        } else {
                            callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                            callback.onComplete();
                        }
                    }).addOnFailureListener(host, e -> {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
            });
        } else {
            callback.onError("There seems to be a problem with your credentials. Please check your email and password fields properly");
            callback.onComplete();
        }
    }


    public static void resetPassword(Activity host, FirebaseAuth auth, String email, AsyncCallback<Void> callback) {
        callback.onStart();
        if (email != null && !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(host, task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess(null);
                    callback.onComplete();
                } else {
                    callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    callback.onComplete();
                }
            }).addOnFailureListener(host, e -> {
                callback.onError(Objects.requireNonNull(e.getLocalizedMessage()));
                callback.onComplete();
            });
            return;
        }

        callback.onError("Please enter a valid email address");
        callback.onComplete();
    }

    public static void uploadImage(StorageReference storage, Uri uri, AsyncCallback<String> callback) {
        callback.onStart();

        final StorageReference ref = storage.child(System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = ref.putFile(uri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                callback.onComplete();
            }

            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.onSuccess(downloadUri != null ? downloadUri.toString() : null);
                callback.onComplete();
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                callback.onComplete();
            }
        });

    }

    public static void updateUserAvatar(Activity host, FirebaseFirestore firestore, String type, String key, String avatar, AsyncCallback<Void> callback) {
        callback.onStart();
        String collection = type.equals(BaseUser.Type.PARENT) ? Constants.PARENTS : Constants.TUTORS;

        // Create a hash map of the fields we are interested in
        HashMap<String, Object> hashMap = new HashMap<>(0);
        hashMap.put("avatar", avatar);
        hashMap.put("token", FirebaseInstanceId.getInstance().getToken());

        // Push data to the database
        firestore.collection(collection).document(key)
                .update(hashMap)
                .addOnCompleteListener(host, task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                    callback.onComplete();
                }).addOnFailureListener(host, e -> {
            callback.onError(e.getLocalizedMessage());
            callback.onComplete();
        });
    }

    /**
     * Create a new user account
     *
     * @param host        Calling Activity
     * @param auth        Firebase authentication
     * @param firestore   Database
     * @param credentials Login credentials
     * @param type        User type (Parent / Tutor
     * @param username    Username of account holder
     * @param callback    Callback for async process
     */
    public static void createUser(Activity host, FirebaseAuth auth, FirebaseFirestore firestore, @NotNull LoginCredentials credentials,
                                  String type, String username, @NotNull AsyncCallback<Void> callback) {
        callback.onStart();
        if (credentials.validate()) {
            auth.createUserWithEmailAndPassword(credentials.getEmail(), credentials.getPassword()).addOnCompleteListener(host,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Create user model and set database collection path
                                String collection = type.equals(BaseUser.Type.PARENT) ? Constants.PARENTS : Constants.TUTORS;
                                BaseUser model = type.equals(BaseUser.Type.PARENT) ? new Parent() : new Tutor();
                                model.setName(username);
                                model.setEmail(credentials.getEmail());
                                model.setType(model instanceof Parent ? BaseUser.Type.PARENT : BaseUser.Type.TUTOR);

                                // Store user information in  the database
                                firestore.collection(collection).document(Objects.requireNonNull(task.getResult()).getUser().getUid())
                                        .set(model).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        callback.onSuccess(null);
                                        callback.onComplete();
                                    } else {
                                        callback.onError(Objects.requireNonNull(task1.getException()).getLocalizedMessage());
                                        callback.onComplete();
                                    }
                                }).addOnFailureListener(e -> {
                                    callback.onError(e.getLocalizedMessage());
                                    callback.onComplete();
                                });
                            } else {
                                callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                callback.onComplete();
                            }
                        }
                    }).addOnFailureListener(host, e -> {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
            });
        } else {
            callback.onError("Invalid login credentials. Please check your email, username and password");
            callback.onComplete();
        }
    }
}
