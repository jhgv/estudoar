package estudoar.cin.ufpe.br.estudoar.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseFacebookUtils;

import estudoar.cin.ufpe.br.estudoar.fragments.LoginFragment;
import estudoar.cin.ufpe.br.estudoar.R;

public class LoginActivity extends Activity {

    protected Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        if(savedInstanceState != null){
            fragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
        }else{
            fragment = new LoginFragment();
            ft.add(R.id.fragment_login_signup, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            getFragmentManager().putFragment(outState, "fragment", fragment);
        }catch (Exception e){
            Log.d("My App", e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}
