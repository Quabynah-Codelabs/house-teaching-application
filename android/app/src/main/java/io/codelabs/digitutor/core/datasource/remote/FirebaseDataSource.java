package io.codelabs.digitutor.core.datasource.remote;


import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.codelabs.digitutor.core.util.AsyncCallback;

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

    public static void updateUserAvatar(Activity host, FirebaseFirestore firestore, String type, String avatar, AsyncCallback<Void> callback) {
        callback.onStart();

        // TODO: 030 30.04.19 Update user's profile information
    }
}
