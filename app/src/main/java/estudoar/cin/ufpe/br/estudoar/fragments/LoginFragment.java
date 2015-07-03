package estudoar.cin.ufpe.br.estudoar.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

import estudoar.cin.ufpe.br.estudoar.R;
import estudoar.cin.ufpe.br.estudoar.activities.HomePageActivity;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText loginInput;
    private EditText passwordInput;
    private Button loginBtn;
    private TextView openSignupBtn;
    private TextView openForgotPassword;
    private Button facebookBtn;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        if (savedInstanceState == null) {

        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            goToHomePage();
        }

        loginInput = (EditText) loginView.findViewById(R.id.authLogin);
        passwordInput = (EditText) loginView.findViewById(R.id.authPassword);

        loginBtn = (Button) loginView.findViewById(R.id.btnLogin);
        openSignupBtn = (TextView) loginView.findViewById(R.id.openSignup);
        facebookBtn = (Button) loginView.findViewById(R.id.facebookBtn);
        openForgotPassword = (TextView) loginView.findViewById(R.id.openForgotPassword);

        loginBtn.setOnClickListener(this);
        openSignupBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        openForgotPassword.setOnClickListener(this);

        return loginView;
    }

    @Override
      public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnLogin:
                normalLogin(v);
                break;
            case R.id.openSignup:
                goToSignup(v);
                break;
            case R.id.facebookBtn:
                facebookLogin(v);
                break;
            case R.id.openForgotPassword:
                goToForgotPassword(v);
                break;
        }
    }

    public void normalLogin(View view) {
        final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Entrando...", true);

        String username = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();
                if (user != null) {
                    goToHomePage();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    //builder.setMessage(e.getMessage());
                    builder.setMessage("Usuario ou Senha incorreta!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void facebookLogin(View view) {
        final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Entrando...", true);
        Collection<String> permissions = Arrays.asList("public_profile", "email", "user_hometown");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d("MyApp", "Login com Facebook falhou");
                } else if (user.isNew()) {
                    requestFacebookUserDetails();
                } else {
                    goToHomePage();
                }
            }
        });
    }

    public void requestFacebookUserDetails() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject profile,
                            GraphResponse response) {
                        //JSONObject userProfile = new JSONObject();
                        if (profile == null) {
                            Log.wtf("FIRST", "PROFILE VAZIO");
                        } else {
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            try {
                                currentUser.put("name", profile.get("name"));
                                currentUser.saveInBackground();
                                goToHomePage();
                            } catch (JSONException e) {
                                Log.wtf("ERROR DETAIL", "Erro ao pegar detalhes");
                            }
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void goToForgotPassword(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_login_signup, new ForgotPasswordFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void goToHomePage() {
        Intent i = new Intent(getActivity(), HomePageActivity.class);
        startActivity(i);
        getActivity().finish();//Tira LoginActivity da pilha de activities para o usuario n�o poder voltar para a tela de login
    }

    public void goToSignup(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_login_signup, new RegisterFragment()); //Container -> R.id.contentFragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
