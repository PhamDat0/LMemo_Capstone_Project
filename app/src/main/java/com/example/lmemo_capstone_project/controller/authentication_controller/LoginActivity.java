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
import com.example.lmemo_capstone_project.controller.database_controller.Converters;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.facebook.AccessToken;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private UserDAO userDAO;
    private FirebaseFirestore db;
    private List<String> listFUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        db = FirebaseFirestore.getInstance();
        loginWithFacebook();
        createActivityWithGoogle();
        listFUID = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            useAppAsGuest();
        }
        else{

        }
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        googleActivityResult(requestCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "local user is: "+userDAO.getLocalUser()[0].getDisplayName());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        // direct  event of button
        if (i == R.id.buttonSignout) {
            facebookSignOut();
            googleSignOut();
            useAppAsGuest();
        } else if (i == R.id.buttonGoogleLogin) {
            googleSignIn();
        }

    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //update UI if user login successful
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            Toast.makeText(LoginActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            findViewById(R.id.buttonFacebookLogin).setVisibility(View.INVISIBLE);
            findViewById(R.id.buttonGoogleLogin).setVisibility(View.INVISIBLE);
            findViewById(R.id.buttonSignout).setVisibility(View.VISIBLE);
        } else {
            //update UI if user login fail or log out
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            Toast.makeText(LoginActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonGoogleLogin).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSignout).setVisibility(View.INVISIBLE);
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
                            handlingLogin();
                            updateUI(user);
                            findViewById(R.id.buttonFacebookLogin).setVisibility(View.GONE);
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
        findViewById(R.id.buttonSignout).setOnClickListener(this);
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

    public void createActivityWithGoogle() {
        //create google login activity
        findViewById(R.id.buttonGoogleLogin).setOnClickListener(this);
        findViewById(R.id.buttonSignout).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void googleActivityResult(int requestCode, Intent data) {
        //result of google login activity
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
        //active google login activity when button is clicked
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //add google to firebase with user authentication id
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
                            handlingLogin();
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


    private void addUserToSQLite(final User user ) {
        // initialize new thread to add to sqlite, because sqlite doesn't allow to run command in Activity
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userDAO = LMemoDatabase.getInstance(getApplicationContext()).userDAO();
                userDAO.insertUser(user);
            }
        });


    }
    private User createUser(){
        // create new user with personal information from firebase authentication
        Date date = new Date();
        User user = new User();
        String FID = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();
        String name = mAuth.getCurrentUser().getDisplayName();
        user.setUserID(FID);
        user.setDisplayName(name);
        user.setMale(true);
        user.setContributionPoint(0);
        user.setEmail(email);
        user.setLoginTime(date);
        return user;
    }

    private void handlingLogin() {
        // handling login activity when login with google/facebook is successful
        getAllDocumentID();

    }

    private List<String> getAllDocumentID() {
        // get all document id from cloud firestore
        com.google.firebase.firestore.Query query = db.collection("users");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listFUID.add(document.getData().get("userID").toString());
                            }
                            addUserToDatabase();
                        }
                    }

                });

        return listFUID;
    }

    private void addUserToDatabase() {
        // add user to sqlite at local database and cloud firebase at online database
        String FID = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();
        String name = mAuth.getCurrentUser().getDisplayName();

        boolean isExisted = true;
        // check if user is existed
        if(listFUID.isEmpty() ){
            isExisted = false;
            Log.w(TAG, "this is empty "+ listFUID.size());
        }
        else{
            for (int i=0;i<listFUID.size();i++){
            if(listFUID.get(i).equals(FID)){
                    isExisted = true;
                    break;
                }
                else {
                    isExisted = false;
                }
            }
        }

        if (!isExisted){
            final User user = createUser();
            addUserToSQLite(user);
            Map<String, Object> addUser = new HashMap<>();
            addUser.put("userID", FID);
            addUser.put("isMale", true);
            addUser.put("displayName", name);
            addUser.put("email", email);
            addUser.put("contributionPoint", 0);
            db.collection("users").document(FID).set(addUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.w(TAG, "Logged in "+ user.getDisplayName()+ " at time "+ user.getLoginTime());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document");
                }
            });

        }
        else{
            DocumentReference docRef = db.collection("users").document(FID);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    Date date = new Date();
                    user.setLoginTime(date);
                    addUserToSQLite(user);
                    Log.w(TAG, "Logged in after add to sqlite with updated"+ user.getDisplayName()+ "at time "+ user.getLoginTime()+ " gender " + user.isMale());

                }
            });
        }
        listFUID.clear();

    }
    private void useAppAsGuest(){
        Date date = new Date();
        final User user = new User();
        String FID = "GUEST";
        String email = "GUEST";
        String name = "GUEST";
        user.setUserID(FID);
        user.setDisplayName(name);
        user.setMale(true);
        user.setContributionPoint(0);
        user.setEmail(email);
        user.setLoginTime(date);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userDAO = LMemoDatabase.getInstance(getApplicationContext()).userDAO();
                userDAO.insertUser(user);
            }
        });
    }
}
