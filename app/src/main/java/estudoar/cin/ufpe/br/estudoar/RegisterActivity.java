package estudoar.cin.ufpe.br.estudoar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void signupUser(View view){

        final EditText uLogin = (EditText) findViewById(R.id.uLogin);
        final EditText uPassword = (EditText) findViewById(R.id.uPassword);
        final EditText uEmail = (EditText) findViewById(R.id.uEmail);

        String username = uLogin.getText().toString().trim();
        String password = uPassword.getText().toString().trim();
        String email = uEmail.getText().toString().trim();

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(RegisterActivity.this, "Usuario cadastrado", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(RegisterActivity.this, "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
