package estudoar.cin.ufpe.br.estudoar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseUser;


public class VerDoacaoActivity extends ActionBarActivity {

    protected Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_doacao);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {

            if (savedInstanceState != null){
                fragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
            }else{
                fragment = new VerDoacaoFragment();
                ft.add(R.id.fragment_ver_doacao,fragment);
                ft.commit();
            }

        }else {
            redirectToLogin();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.user_logout:
                redirectToLogin();
                break;
            case R.id.user_profile:
                goToMeuPerfilPage();
                break;
            case R.id.user_doacoes:
                goToMinhasDoacoesPage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


/*
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnDoar:
                goToDoarPage(v);
                break;
        }
    }
*/

    public void redirectToLogin(){
        ParseUser.logOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void goToMeuPerfilPage(){
        Intent i = new Intent(this, MeuPerfil.class);
        startActivity(i);
    }

    public void goToMinhasDoacoesPage(){
        Intent i = new Intent(this, DoacoesActivity.class);
        i.putExtra("id_doador",1);
        startActivity(i);
    }
}


