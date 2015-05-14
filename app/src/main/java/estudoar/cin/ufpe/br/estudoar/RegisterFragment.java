package estudoar.cin.ufpe.br.estudoar;


import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegisterFragment extends Fragment implements View.OnClickListener{

    private EditText uName;
    private EditText uLogin;
    private EditText uPassword;
    private EditText uEmail;

    private Button btnSignup;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View signup = inflater.inflate(R.layout.fragment_register, container, false);

        uName = (EditText) signup.findViewById(R.id.uName);
        uLogin = (EditText) signup.findViewById(R.id.uLogin);
        uPassword = (EditText) signup.findViewById(R.id.uPassword);
        uEmail = (EditText) signup.findViewById(R.id.uEmail);
        btnSignup = (Button) signup.findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(this);

        return signup;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key){
            case R.id.btnSignup:
                signupUser(v);
                break;
        }
    }

    public void signupUser(View view){
        final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Cadsatrando...", true);

        String name = uName.getText().toString().trim();
        String username = uLogin.getText().toString().trim();
        String password = uPassword.getText().toString().trim();
        String email = uEmail.getText().toString().trim();

        ParseUser user = new ParseUser();
        user.put("name", name);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if(e == null) {
                    Toast.makeText(getActivity(), "Usuario cadastrado", Toast.LENGTH_LONG).show();
                    goToLogin();
                }else {
                    Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToLogin(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_login_signup,  new LoginFragment()); //Container -> R.id.contentFragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
