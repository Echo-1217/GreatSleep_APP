package com.example.greatsleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //Google
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private final int RC_SIGN_IN=100;
    //Facebook
    private FirebaseAuth mAuthFB;
    CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    TextView great_sleep;
    private TransitionButton google_login_btn;
    private TransitionButton fb_login_btn;
    Typeface typeface;
    Typeface typeface2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        google_login_btn = findViewById(R.id.google_login);
        fb_login_btn=findViewById(R.id.fb_login);

        great_sleep=findViewById(R.id.great_sleep);
        typeface= TypeFaceProvider.getTypeFace(this,"font_style1.ttf");
        typeface2= TypeFaceProvider.getTypeFace(this,"introtype.ttf");
        great_sleep.setTypeface(typeface);
        google_login_btn.setTypeface(typeface2);
        fb_login_btn.setTypeface(typeface2);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();

        callbackManager = CallbackManager.Factory.create();
        mAuthFB=FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();

        fb_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fb_login_btn.setStateListAnimator(null);
                }
                fb_login_btn.startAnimation();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v("a","FB  on success ");
                        handleFacebookToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        fb_login_btn.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        Log.v("vc","FB  Cancel ");
                    }
                    @Override
                    public void onError(FacebookException error) {
                    }
                });
            }
        });

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        checkUser();

        google_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    google_login_btn.setStateListAnimator(null);
                }
                google_login_btn.startAnimation();
                Intent intent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
                // Do your networking task or background work here.
            }
        });
    }

    private void handleFacebookToken(AccessToken token) {
        Log.v("a","Handle Facebook Token ");
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        mAuthFB.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("a","sign with credential Facebook successful");
                            FirebaseUser user=mAuthFB.getCurrentUser();
                            String uid=user.getUid();
                            String email=user.getEmail();
                            Log.d("ss", "success on uid"+uid);
                            Log.d("ss", "success on email"+email);
                            updateUI();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> data = new HashMap<>();
                            data.put("UID",uid);
                            data.put("Email", email);
                            //抓取Email(檔案名稱)供使用者存取資料
                            editor.putString("userdocument",email);
                            editor.apply();

                            db.collection("User").document(email).collection("sleepdata").document("userdata")
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("ss", "Google Document successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("ss", "Google Error writing document", e);
                                        }
                                    });
                        }
                        else{
                            Log.v("a","sign with credential Facebook Fail");
                            fb_login_btn.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            Toast.makeText(LoginActivity.this,"登入失敗",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI() {
        fb_login_btn.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
            @Override
            public void onAnimationStopEnd() {
                editor.putBoolean("introduce",true);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, IntroduceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Google Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("ss", "on activity:Google signin intent result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("ss", "Google sign in failed" + e.getMessage());
            }
        }
        //Facebook
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("ss", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid=user.getUid();
                        String email=user.getEmail();
                        Log.d("ss", "success on uid"+uid);
                        Log.d("ss", "success on email"+email);
                        //判斷使用者是否已存在
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d("ss", "create new user"+email);
                        }
                        else{
                            Log.d("ss", "Existing user"+email);
                        }
                        google_login_btn.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                            @Override
                            public void onAnimationStopEnd() {
                                editor.putBoolean("introduce",true);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, IntroduceActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                finish();
                            }
                        });
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> data = new HashMap<>();
                        data.put("UID",uid);
                        data.put("Email", email);
                        editor.putString("userdocument",email);
                        editor.apply();
                        db.collection("User").document(email).collection("userinfo").document("userdata")
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ss", "Document successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ss", "Error writing document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        google_login_btn.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        Log.d("ss", "signInWithCredential:failure"+e.getMessage());
                    }
                });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        checkUser();
    }
    private void checkUser() {
        FirebaseUser user=mAuth.getCurrentUser();
        //如果使用者存在直接登入
        if(user!=null) {
            Log.v("ss", "already login");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuthFB.getCurrentUser();
        if(user!=null){
            updateUI();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}