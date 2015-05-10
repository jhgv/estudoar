package estudoar.cin.ufpe.br.estudoar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Permitir o armazenamento local das variaveis de identificacao
        Parse.enableLocalDatastore(this);
        //App e Client id
        String appId = "YUXgV0oMFzoDWkh7hgvw5T9TwRC49vvEBhZWqEQo";
        String clientId = "PCCHPtCstEbqBu3Xn5KIgNv9mEWu3OanByDsOZ01";
        //Inicializa conexao com o Parse.com
        Parse.initialize(this, appId, clientId);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToSignup(View view){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
