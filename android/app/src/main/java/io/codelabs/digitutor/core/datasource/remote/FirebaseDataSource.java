package io.codelabs.digitutor.core.datasource.remote;


import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.codelabs.digitutor.core.datasource.local.UserSharedPreferences;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.core.util.InputValidator;
import io.codelabs.digitutor.data.BaseDataModel;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.data.model.Complaint;
import io.codelabs.digitutor.data.model.Parent;
import io.codelabs.digitutor.data.model.Report;
import io.codelabs.digitutor.data.model.Request;
import io.codelabs.digitutor.data.model.Subject;
import io.codelabs.digitutor.data.model.Tutor;
import io.codelabs.sdk.util.ExtensionUtils;

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
                                model.setKey(Objects.requireNonNull(task.getResult()).getUser().getUid());
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

    public static void getCurrentUser(Activity host, FirebaseFirestore firestore, @NotNull UserSharedPreferences prefs, @NotNull AsyncCallback<BaseUser> callback) {
        callback.onStart();
        if (prefs.isLoggedIn()) {
            ExtensionUtils.debugLog(host, prefs.getType());
            String collection = prefs.getType().equals(BaseUser.Type.PARENT) ? Constants.PARENTS : Constants.TUTORS;
            firestore.collection(collection).document(prefs.getKey()).addSnapshotListener(host, (documentSnapshot, e) -> {
                if (e != null) {
                    callback.onError(e.getLocalizedMessage());
                    callback.onComplete();
                    return;
                }

                BaseUser user;
                if (prefs.getType().equals(BaseUser.Type.PARENT))
                    user = documentSnapshot.toObject(Parent.class);
                else {
                    user = documentSnapshot.toObject(Tutor.class);
                }

                // Send live data to the callback
                callback.onSuccess(user);
                callback.onComplete();
            });
        } else {
            callback.onError("Please sign in first");
            callback.onComplete();
        }
    }

    public static void getUser(Activity host, @NotNull FirebaseFirestore firestore, String key, @NotNull String type, @NotNull AsyncCallback<BaseUser> callback) {
        callback.onStart();
        String collection = type.equals(BaseUser.Type.PARENT) ? Constants.PARENTS : Constants.TUTORS;
        firestore.collection(collection).document(key).addSnapshotListener(host, (documentSnapshot, e) -> {
            if (e != null) {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
                return;
            }

            BaseUser user;
            if (type.equals(BaseUser.Type.PARENT))
                user = documentSnapshot.toObject(Parent.class);
            else {
                user = documentSnapshot.toObject(Tutor.class);
            }

            // Send live data to the callback
            callback.onSuccess(user);
            callback.onComplete();
        });
    }

    public static void fetchAllSubjects(Activity host, @NotNull FirebaseFirestore firestore, @NotNull AsyncCallback<List<Subject>> callback) {
        callback.onStart();
        firestore.collection(Constants.SUBJECTS)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(host, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        callback.onError(e.getLocalizedMessage());
                        callback.onComplete();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Subject> subjects = queryDocumentSnapshots.toObjects(Subject.class);
                        callback.onSuccess(subjects);
                    } else {
                        callback.onError("Unable to load subjects");
                    }
                    callback.onComplete();
                });
    }

    public static void getAllClients(Activity host, @NotNull FirebaseFirestore firestore, @NotNull UserSharedPreferences prefs, @NotNull AsyncCallback<List<Parent>> callback) {
        callback.onStart();
        firestore.collection(String.format(Constants.CLIENTS, prefs.getKey())).orderBy("name", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(host, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Parent> parents = task.getResult().toObjects(Parent.class);
                        callback.onSuccess(parents);
                        callback.onComplete();
                    } else {
                        callback.onError(task.getException().getLocalizedMessage() == null ? "Could not load all clients for this tutor" : task.getException().getLocalizedMessage());
                        callback.onComplete();
                    }
                }).addOnFailureListener(host, e -> {
            callback.onError(e.getLocalizedMessage());
            callback.onComplete();
        });
    }

    public static void searchFor(/*Activity host,*/ FirebaseFirestore firestore, String query, @NotNull AsyncCallback<List<? extends BaseDataModel>> callback) {
        callback.onStart();
        if (InputValidator.INSTANCE.hasValidInput(query)) {
            // Get all subjects
            Task<QuerySnapshot> subjectQueryTask = firestore.collection(Constants.SUBJECTS).get();

            // Get all tutors
            Task<QuerySnapshot> tutorQueryTask = firestore.collection(Constants.TUTORS).get();

            // Get all reports
            Task<QuerySnapshot> reportQueryTask = firestore.collection(Constants.REPORTS).get();
            Task<QuerySnapshot> complaintQueryTask = firestore.collection(Constants.COMPLAINTS).get();
            try {
                List<Subject> subjects = Tasks.await(subjectQueryTask).toObjects(Subject.class);
                List<Tutor> tutors = Tasks.await(tutorQueryTask).toObjects(Tutor.class);
                List<Report> reports = Tasks.await(reportQueryTask).toObjects(Report.class);
                List<Complaint> complaints = Tasks.await(complaintQueryTask).toObjects(Complaint.class);

                // Results
                List<BaseDataModel> response = new ArrayList<>(0);
                for (Subject subject : subjects) {
                    if (subject.getName().contains(query)) response.add(subject);
                }

                for (Tutor tutor : tutors) {
                    if (Objects.requireNonNull(tutor.getName()).contains(query) || Objects.requireNonNull(tutor.getEmail()).contains(query))
                        response.add(tutor);
                }

                for (Report report : reports) {
                    if (report.getWard().contains(query)) response.add(report);
                }

                for (Complaint complaint : complaints) {
                    if (complaint.getDescription().contains(query)) response.add(complaint);
                }

                callback.onSuccess(response);
                callback.onComplete();

            } catch (ExecutionException | InterruptedException | IllegalStateException e) {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
            }
        } else {
            callback.onError("Please enter a valid input to query");
            callback.onComplete();
        }
    }

    public static void requestService(@NotNull FirebaseFirestore firestore, @NotNull UserSharedPreferences prefs, String tutor, @NotNull AsyncCallback<Void> callback) {
        callback.onStart();

        // Document reference created
        DocumentReference document = firestore.collection(Constants.REQUESTS).document();
        // Get key from document
        String key = document.getId();

        // Create new request data model
        Request request = new Request(key, prefs.getKey(), tutor, System.currentTimeMillis());


        document.set(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
                callback.onComplete();
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                callback.onComplete();
            }
        }).addOnFailureListener(e -> {
            callback.onError(e.getLocalizedMessage());
            callback.onComplete();
        });
    }

    /**
     * Requests received by a Tutor
     *
     * @param host      calling activity
     * @param firestore Firebase firestore
     * @param prefs     User shared prefs. Contains user's login key and type
     * @param callback  Callback function after the process is carried out
     */
    public static void getReceivedRequests(Activity host, FirebaseFirestore firestore, @NotNull UserSharedPreferences prefs, @NotNull AsyncCallback<List<Request>> callback) {
        callback.onStart();

        if (prefs.isLoggedIn()) {
            firestore.collection(Constants.REQUESTS)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("tutor", prefs.getKey())
                    .get()
                    .addOnCompleteListener(host, task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess(Objects.requireNonNull(task.getResult()).toObjects(Request.class));
                            callback.onComplete();
                        } else {
                            callback.onError(task.getException() != null ? task.getException().getLocalizedMessage() : "Could not load requests");
                        }
                    }).addOnFailureListener(host, e -> {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
            });
        } else {
            callback.onError("You need to be logged in first");
            callback.onComplete();
        }
    }

    /**
     * Requests sent by a Parent
     *
     * @param host      calling activity
     * @param firestore Firebase firestore
     * @param prefs     User shared prefs. Contains user's login key and type
     * @param callback  Callback function after the process is carried out
     */
    public static void getSentRequests(Activity host, FirebaseFirestore firestore, @NotNull UserSharedPreferences prefs, @Nullable String tutor, @NotNull AsyncCallback<Boolean> callback) {
        callback.onStart();
        if (prefs.isLoggedIn()) {

            // Get all requests sent by the current parent
            Query query = firestore.collection(Constants.REQUESTS)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("parent", prefs.getKey())
                    .whereEqualTo("tutor", tutor);

            // Get results for a particular tutor
            /*if (tutor != null && !TextUtils.isEmpty(tutor)) */

            query.get().addOnCompleteListener(host, task -> {
                if (task.isSuccessful()) {
                    ExtensionUtils.debugLog(host, Objects.requireNonNull(task.getResult()).toObjects(Request.class));
                    callback.onSuccess(Objects.requireNonNull(task.getResult()).toObjects(Request.class).isEmpty());
                    callback.onComplete();
                } else {
                    callback.onError(task.getException() != null ? task.getException().getLocalizedMessage() : "Could not load requests");
                }
            }).addOnFailureListener(host, e -> {
                callback.onError(e.getLocalizedMessage());
                callback.onComplete();
            });
        } else {
            callback.onError("You need to be logged in first");
            callback.onComplete();
        }
    }

    /**
     * Gets the current request by its ID
     */
    public static void getCurrentRequest(Activity host, @NotNull FirebaseFirestore firestore, String requestId, @NotNull AsyncCallback<Request> callback) {
        callback.onStart();
        firestore.collection(Constants.REQUESTS).document(requestId).get()
                .addOnCompleteListener(host, task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.exists()) {
                            Request request = snapshot.toObject(Request.class);
                            callback.onSuccess(request);
                            callback.onComplete();
                        }
                    } else {
                        callback.onError("Unable to retrieve the desired request. Please try again later");
                        callback.onComplete();
                    }
                }).addOnFailureListener(host, e -> {
            callback.onError(e.getLocalizedMessage());
            callback.onComplete();
        });

    }
}
