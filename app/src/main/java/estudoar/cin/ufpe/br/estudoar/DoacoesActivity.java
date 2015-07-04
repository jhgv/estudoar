package estudoar.cin.ufpe.br.estudoar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;


public class DoacoesActivity extends ActionBarActivity {

    protected Fragment fragment;
    protected FragmentManager fm;

    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragment = new DoacoesFragment();

        if (!fragment.isInLayout()) {
            fm.beginTransaction().replace(R.id.fragment_doacoes, fragment, "mContent").commit();
        }

        currentUser = ParseUser.getCurrentUser();

        setContentView(R.layout.activity_materiais);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_materiais, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id){
            case R.id.local_search:
                break;
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
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void goToMeuPerfilPage(){
        Intent i = new Intent(this, MeuPerfil.class);
        i.putExtra("id_usuario",currentUser.getObjectId());
        startActivity(i);
        finish();
    }

    public void goToMinhasDoacoesPage(){
        Intent i = new Intent(this, DoacoesActivity.class);
        i.putExtra("filter",1);
        i.putExtra("title", "Doacoes");
        startActivity(i);
        finish();
    }

    public void goToMenuPrincipal(){
        Intent i = new Intent(this, HomePageActivity.class);
        startActivity(i);
        finish();
    }

}
