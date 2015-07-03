package estudoar.cin.ufpe.br.estudoar.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import estudoar.cin.ufpe.br.estudoar.R;


public class ForgotPasswordFragment extends Fragment implements View.OnClickListener{

    private Button requestPassword;
    private EditText accountEmail;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forgotPassword = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        accountEmail = (EditText) forgotPassword.findViewById(R.id.accountEmail);
        requestPassword = (Button) forgotPassword.findViewById(R.id.btnRequestPassword);

        requestPassword.setOnClickListener(this);

        return forgotPassword;
    }

    @Override
    public void onClick(View v) {

        int key = v.getId();

        switch (key){
            case R.id.btnRequestPassword:
                requestNewPassword();
                break;
        }

    }

    public void requestNewPassword(){
        final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Enviando email...", true);

        String email = accountEmail.getText().toString().trim();

        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(getActivity(), "Email enviado", Toast.LENGTH_LONG).show();
                    goToLoginPage();
                } else {
                    Toast.makeText(getActivity(), "Erro ao enviar email", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void goToLoginPage(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_login_signup, new LoginFragment()); //Container -> R.id.contentFragment
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
