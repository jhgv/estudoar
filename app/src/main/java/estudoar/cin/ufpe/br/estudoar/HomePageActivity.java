package estudoar.cin.ufpe.br.estudoar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.List;


public class HomePageActivity extends ActionBarActivity {

    protected Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {

            if (savedInstanceState != null){
                fragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
            }else{
                fragment = new HomePageFragment();
                ft.add(R.id.fragment_home_page,fragment);
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
            case R.id.btnProcurar:
                goToProcurarPage(v);
                break;
            case R.id.btnPrimeiraDoacao:
                goToVerDoacaoPage(v);
                break;
        }
    }
*/

    public void redirectToLogin(){
        ParseUser.logOut();
        Intent i = new Intent(HomePageActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

/*
    public void goToDoarPage(View view) {
        Intent i = new Intent(HomePageActivity.this, DoarActivity.class);
        startActivity(i);

    }

    public void goToProcurarPage(View view) {

    }

    public void goToVerDoacaoPage(View view){

        Toast.makeText(this, "ID Desconhecido!", Toast.LENGTH_LONG).show();

        final Intent i = new Intent(HomePageActivity.this, VerDoacaoActivity.class);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.whereEqualTo("doador", currentUser);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    i.putExtra("id_doacao", doacao.getObjectId());
                    startActivity(i);
                } else {
                    Toast.makeText(HomePageActivity.this, "Você ainda não fez uma doação!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
*/

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
