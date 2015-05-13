package estudoar.cin.ufpe.br.estudoar;

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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Collection;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText loginInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button openSignupBtn;
    private Button facebookBtn;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        if(savedInstanceState == null){

        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            goToHomePage();
        }

        loginInput = (EditText) loginView.findViewById(R.id.authLogin);
        passwordInput = (EditText) loginView.findViewById(R.id.authPassword);

        loginBtn = (Button) loginView.findViewById(R.id.btnLogin);
        openSignupBtn = (Button) loginView.findViewById(R.id.openSignup);
        facebookBtn = (Button) loginView.findViewById(R.id.facebookBtn);

        loginBtn.setOnClickListener(this);
        openSignupBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);

        return loginView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key){
            case R.id.btnLogin:
                normalLogin(v);
                break;
            case R.id.openSignup:
                goToSignup(v);
                break;
            case R.id.facebookBtn:
                facebookLogin(v);
                break;
        }
    }

    public void normalLogin(View view){
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
                    builder.setMessage(e.getMessage());
                    builder.setTitle("Desculpe!");
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

    public void facebookLogin(View view){
        final Dialog progressDialog = ProgressDialog.show(getActivity() , "", "Entrando...", true);
        Collection<String> permissions = Arrays.asList("public_profile", "email", "user_hometown");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();

                if (user == null) {
                    Log.d("MyApp", "Login com Facebook falhou");
                } else if (user.isNew()) {
                    goToHomePage();
                } else {
                    goToHomePage();
                }
            }
        });
    }

    public void goToHomePage(){
        Intent i = new Intent(getActivity(), HomePageActivity.class);
        startActivity(i);
        getActivity().finish();//Tira LoginActivity da pilha de activities para o usu�rio n�o poder voltar para a tela de login
    }

    public void goToSignup(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_login_signup,  new RegisterFragment()); //Container -> R.id.contentFragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

}