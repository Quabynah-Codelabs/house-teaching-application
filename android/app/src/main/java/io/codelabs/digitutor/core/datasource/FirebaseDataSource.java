package io.codelabs.digitutor.core.datasource;


import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.codelabs.digitutor.core.util.AsyncCallback;
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
                             final FirebaseFirestore firestore,
                             @NotNull LoginCredentials credentials,
                             final String type,
                             @NotNull final AsyncCallback<Void> callback) {
        // Start process
        callback.onStart();

        if (credentials.validate()) {
            auth.signInWithEmailAndPassword(credentials.getEmail(), credentials.getPassword())
                    .addOnCompleteListener(host, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                                if (user != null) {
                                    // Get the current user's path
                                    /*firestore.collection(type.equals(BaseUser.Type.PARENT) ? BaseUser.Type.PARENT : BaseUser.Type.TUTOR)
                                            .document(user.getUid())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // get document snapshot
                                                DocumentSnapshot snapshot = task.getResult();

                                                if (snapshot != null && snapshot.exists()) {
                                                    BaseUser result = null;
                                                    if (type.equals(BaseUser.Type.PARENT))
                                                        result = snapshot.toObject(Parent.class);
                                                    else result = snapshot.toObject(Tutor.class);
                                                    storeDataLocally(result, type);
                                                    callback.onSuccess(null);
                                                    callback.onComplete();
                                                }
                                            } else {
                                                callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                                callback.onComplete();
                                            }
                                        }
                                    }).addOnFailureListener(e -> {
                                        callback.onError(e.getLocalizedMessage());
                                        callback.onComplete();
                                    });*/
                                    callback.onSuccess(null);
                                    callback.onComplete();
                                }
                            } else {
                                callback.onError(task.getException().getLocalizedMessage());
                                callback.onComplete();
                            }
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


    private static void storeDataLocally(BaseUser result, String type) {
        //todo: store user's data locally
    }

}
