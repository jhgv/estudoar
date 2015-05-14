package estudoar.cin.ufpe.br.estudoar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseUser;


public class HomePageActivity extends ActionBarActivity {
    protected TextView welcomeUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        welcomeUsername = (TextView) findViewById(R.id.welcomeUsername);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            welcomeUsername.setText("Bem vindo, " + currentUser.get("name") + "!");
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
        }

        return super.onOptionsItemSelected(item);
    }



    public void redirectToLogin(){
        ParseUser.logOut();
        Intent i = new Intent(HomePageActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}
