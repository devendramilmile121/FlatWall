package com.d.h.milmile.wallpaper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.d.h.milmile.wallpaper.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsFragment extends Fragment {

    private static final int GOOGLE_CODE = 312;
    private GoogleSignInClient googleSignInClient;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){

            return inflater.inflate(R.layout.fragment_setting_default,container,false);
        }else {
            return inflater.inflate(R.layout.fragment_setting_loggedin, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(),gso);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            TextView name = view.findViewById(R.id.tv_uname);
            TextView email = view.findViewById(R.id.tv_email1);
            ImageView imageView = view.findViewById(R.id.imageView);

            FirebaseUser fbobj = FirebaseAuth.getInstance().getCurrentUser();
            Glide.with(getActivity())
                    .load(fbobj.getPhotoUrl().toString())
                    .into(imageView);
            name.setText(fbobj.getDisplayName());
            email.setText(fbobj.getEmail());


            view.findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area,new SettingsFragment())
                                    .commit();
                        }
                    });

                }
            });
        }else {

            view.findViewById(R.id.btn_signin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = googleSignInClient.getSignInIntent();
                    startActivityForResult(i,GOOGLE_CODE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        auth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_area,new SettingsFragment()).commit();
                }else {
                    Toast.makeText(getActivity(),"Failed to sign in",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
