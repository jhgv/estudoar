package estudoar.cin.ufpe.br.estudoar.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

import estudoar.cin.ufpe.br.estudoar.R;
import estudoar.cin.ufpe.br.estudoar.fragments.MeuPerfilFragment;


public class MeuPerfilActivity extends ActionBarActivity {

    private final int MEU_PERFIL = 1;
    protected Fragment fragment;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {

            if (savedInstanceState != null){
                fragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
            }else{
                fragment = new MeuPerfilFragment();
                ft.add(R.id.fragment_meu_perfil,fragment);
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
            case R.id.menu_principal:
                goToMenuPrincipal();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void redirectToLogin(){
        ParseUser.logOut();
        Intent i = new Intent(MeuPerfilActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void goToMeuPerfilPage(){
        Intent i = new Intent(this, MeuPerfilActivity.class);
        i.putExtra("id_usuario",currentUser.getObjectId());
        startActivity(i);
    }

    public void goToMinhasDoacoesPage(){
        Intent i = new Intent(this, DoacoesActivity.class);
        i.putExtra("filter", MEU_PERFIL);
        i.putExtra("title", "Doacoes");
        startActivity(i);
    }

    public void goToMenuPrincipal(){
        Intent i = new Intent(this, HomePageActivity.class);
        startActivity(i);
    }

}