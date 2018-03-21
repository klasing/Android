package com.example.fir_sqlite;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// for logging use: adb shell setprop log.tag.FA VERBOSE
// for logging use: adb shell setprop debug.firebase.analytics.app com.example.fir_sqlite

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "*** " + MainActivity.class.getSimpleName();

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mImagePickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mImageStorageReference;

    private String mImageName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, ".onCreate()");

        // the eagle has landed, we have an icon on the actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.firebase_logo);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessagesDatabaseReference =
                mFirebaseDatabase.getReference().child("messages");

        Log.d(TAG, mMessagesDatabaseReference.toString());

        mImageStorageReference =
                mFirebaseStorage.getReference().child("images");

        Log.d(TAG, mImageStorageReference.toString());


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mImagePickerButton = (ImageButton) findViewById(R.id.imagePickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        List<FirebaseMessage> firebaseMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, firebaseMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mImagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessage firebaseMessage =
                        new FirebaseMessage(null, mMessageEditText.getText().toString(), mUsername, null);

                mMessagesDatabaseReference.push().setValue(firebaseMessage);

                // Clear input box
                mMessageEditText.setText("");
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Toast.makeText(MainActivity.this, "You're now signed in. Welcome to Firebase.", Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                      new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                      new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                //Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            // place actual image into google-cloud-storage
            Uri selectedImageUri = data.getData();
            StorageReference imageRef =
                    mImageStorageReference.child(selectedImageUri.getLastPathSegment());

            mImageName = selectedImageUri.getLastPathSegment();

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            FirebaseMessage firebaseMessage =
                                    new FirebaseMessage(null, null, mUsername,
                                            "https://www.google.com/images/spin-32.gif");
                            // upload spinner to google-cloud-storage
                            String key = mMessagesDatabaseReference.push().getKey();
                            mMessagesDatabaseReference.child(key).setValue(firebaseMessage);

                            // upload image to google-cloud-storage
                            firebaseMessage.setImageUrl(downloadUrl.toString());
                            mMessagesDatabaseReference.child(key).setValue(firebaseMessage);
                        }
                    });
        }

   }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

   private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, ".onChildAdded()");

                    final FirebaseMessage firebaseMessage = dataSnapshot.getValue(FirebaseMessage.class);
                    if (firebaseMessage.getText() != null) {
                        // received message, add to listview
                        mMessageAdapter.add(firebaseMessage);
                        return;
                    }
                    if (firebaseMessage.getImageUrl() != null) {
                        if (firebaseMessage.getImageUrl().equals("https://www.google.com/images/spin-32.gif")) {
                            // do nothing, wait for onChildChanged event
                            return;
                        } else {
                            receiveImageOnEvent(firebaseMessage);
                        }
                    }
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, ".onChildChanged()");

                    final FirebaseMessage firebaseMessage = dataSnapshot.getValue(FirebaseMessage.class);

                    receiveImageOnEvent(firebaseMessage);

                }
                private void receiveImageOnEvent(final FirebaseMessage firebaseMessage) {
                    Log.d(TAG, ".receiveImageOnEvent()");

                    String imageName = null;
                    if (mImageName != null) {
                        Log.d(TAG, "android imageName: " + mImageName);

                        // get name of image, produced by this app
                        imageName = mImageName;
                        mImageName = null;

                    } else {
                        Log.d(TAG, firebaseMessage.getImageUrl());

                        // get name of image, stored in google-cloud-storage
                        String pathName = firebaseMessage.getImageUrl();
                        int lengthPathName = pathName.length();
                        int i = pathName.lastIndexOf('/');
                        imageName = pathName.substring(i + 1, lengthPathName);
                        if (pathName.startsWith("https://")) {
                            int j = imageName.indexOf('?');
                            imageName = imageName.substring("images%2F".length(), j);
                        }
                        imageName = imageName.replace("%3A", ":");
                    }

                    Log.d(TAG, "imageName: " + imageName);

                    // received image, add to listview
                    mImageStorageReference.child(imageName).getDownloadUrl().
                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseMessage fbm = new FirebaseMessage();
                                    fbm.setPhotoUrl(firebaseMessage.getPhotoUrl());
                                    fbm.setText(firebaseMessage.getText());
                                    fbm.setName(firebaseMessage.getName());
                                    fbm.setImageUrl(uri.toString());
                                    mMessageAdapter.add(fbm);
                                }
                            });
                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, ".onChildRemoved()");
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

}
