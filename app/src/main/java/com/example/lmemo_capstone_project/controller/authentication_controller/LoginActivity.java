package com.example.lmemo_capstone_project.controller.authentication_controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private DatabaseReference databaseReference;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private FirebaseAuth mAuth;
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager mCallbackManager;
    private UserDAO userDAO;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        db = FirebaseFirestore.getInstance();
        loginWithFacebook();
        loginWithGoogle();
        listFUID = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private List<String> listFUID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        googleActivityResult(requestCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonFacebookSignout) {
            facebookSignOut();
        } else if (i == R.id.buttonGoogleLogin) {
            googleSignIn();
        } else if (i == R.id.goolgleSignOutButton) {
            googleSignOut();
        } else if (i == R.id.googleDisconnectButton) {
            googleRevokeAccess();
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            Toast.makeText(LoginActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            Toast.makeText(LoginActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToCloudFireStore();
                            addUserToSQLite();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void loginWithFacebook() {
        findViewById(R.id.buttonFacebookSignout).setOnClickListener(this);
        findViewById(R.id.buttonFacebookLogin);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                }
            }
        };
    }

    public void facebookSignOut() {
        mAuth.signOut();
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                SharedPreferences pref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                LoginManager.getInstance().logOut();

                Intent logoutint = new Intent(LoginActivity.this, LoginActivity.class);
                logoutint.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutint);
            }
        }).executeAsync();
        updateUI(null);
    }

    public void loginWithGoogle() {
        findViewById(R.id.buttonGoogleLogin).setOnClickListener(this);
        findViewById(R.id.goolgleSignOutButton).setOnClickListener(this);
        findViewById(R.id.googleDisconnectButton).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void googleActivityResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void googleRevokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToCloudFireStore();
                            addUserToSQLite();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

//    private List<String> listFireStoreID;
//    private List<String> listFUID;
//
//    private void addUserToCloudFireStore() {
//
//        String FID = mAuth.getCurrentUser().getUid();
//        String email = mAuth.getCurrentUser().getEmail();
//        String name = mAuth.getCurrentUser().getDisplayName();
//
//        // get all document id on cloud firestore
//        Log.w(TAG, "before getting listfirestore");
//        listFireStoreID = getAllDocumentID();
//        listFUID = getAllFID();
//        Log.w(TAG, "size of list fuid id" + listFUID.size());
//        Log.w(TAG, "size of list firestore id" + listFireStoreID.size());
//
//        Map<String, Object> addUser = new HashMap<>();
//        addUser.put("FUID", FID);
//        addUser.put("gender", true);
//        addUser.put("displayName", name);
//        addUser.put("email", email);
//        addUser.put("contributionPoint", 0);
//        db.collection("users").add(addUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Error adding document", e);
//            }
//        });
//
//
//    }
//
//    private List<String> getAllDocumentID() {
//        com.google.firebase.firestore.Query query = db.collection("users");
////        db.collection("users")
////                .get()
////                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()){
////                            for(QueryDocumentSnapshot document : task.getResult()){
////                                listFireStoreID.add(document.getId());
////                                Log.w(TAG, "get in loop of getting firebase id"+listFireStoreID.size());
////                            }
////                        }
////                    }
////
////                });
//        db.collection("users")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                            listFireStoreID.add(document.getId());
//                            Log.w(TAG, "get in loop of getting firebase id" + listFireStoreID.size() + "id" + document.getId());
//                        }
//                    }
//                });
//
//        Log.w(TAG, "get out loop of getting firebase id" + listFireStoreID.size());
//        return listFireStoreID;
//    }
//
//    private List<String> getAllFID() {
//        if (!listFireStoreID.isEmpty()) {
//            for (int i = 0; i < listFireStoreID.size(); i++) {
//                Log.w(TAG, "get in loop of getting list FUID" + listFireStoreID.get(i));
//                DocumentReference docRef = db.collection("users").document(listFireStoreID.get(i));
//                Log.w(TAG, "firebase id of FUID" + listFireStoreID.get(i));
//                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            if (documentSnapshot.exists()) {
//                                listFUID.add(documentSnapshot.getString("FUID"));
//                                Log.w(TAG, "size of list FUID" + listFUID.size());
//                            }
//                        }
//                    }
//                });
//            }
//        }
//        return listFUID;
//    }

    private void addUserToSQLite() {
        final User user = new User();
        String FID = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();
        String name = mAuth.getCurrentUser().getDisplayName();
        user.setUserID(FID);
        user.setDisplayName(name);
        user.setMale(true);
        user.setContributionPoint(0);
        user.setEmail(email);
        // initialize new thread to add to sqlite, because sqlite doesn't allow to run command in Activity
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userDAO = LMemoDatabase.getInstance(getApplicationContext()).userDAO();
                userDAO.insertUser(user);
                Log.w(TAG, "create on sqlite how to snoop dog");
                User[] localUser = userDAO.getLocalUser();
                Log.w(TAG, "create on sqlite how to snoop dog" + localUser[0].getUserID());
            }
        });


    }

    private void addUserToCloudFireStore() {

        // get all document id on cloud firestore
        Log.w(TAG, "before getting listfirestore");
        getAllDocumentID();
    }

    private List<String> getAllDocumentID() {
        com.google.firebase.firestore.Query query = db.collection("users");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listFUID.add(document.getData().get("FUID").toString());
                            }
                            putInFirestore();
                        }
                    }

                });

        return listFUID;
    }

    private void putInFirestore() {
        String FID = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();
        String name = mAuth.getCurrentUser().getDisplayName();

        Map<String, Object> addUser = new HashMap<>();
        addUser.put("FUID", FID);
        addUser.put("gender", true);
        addUser.put("displayName", name);
        addUser.put("email", email);
        addUser.put("contributionPoint", 0);
        db.collection("users").add(addUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

}
