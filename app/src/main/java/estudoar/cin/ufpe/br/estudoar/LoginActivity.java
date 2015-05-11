package estudoar.cin.ufpe.br.estudoar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Collection;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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

    public void normalLogin(View view){
        final Dialog progressDialog = ProgressDialog.show(this, "", "Entrando...", true);

        final EditText loginInput = (EditText) findViewById(R.id.authLogin);
        final EditText passwordInput = (EditText) findViewById(R.id.authPassword);

        String username = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();
                if (user != null) {
                    goToHomePage();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle("Desculpe!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void facebookLogin(View view){
        final Dialog progressDialog = ProgressDialog.show(this, "", "Entrando...", true);
       // List<String> permissions = Arrays.asList("public_profile", "email", "user_hometown");
        Collection<String> permissions = Arrays.asList("public_profile", "email", "user_hometown");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();

                if (user == null) {
                    Log.d("MyApp", "Login com Facebook falhou");
                } else if (user.isNew()) {
                    goToHomePage();
                } else {
                    goToHomePage();
                }
            }
        });
    }

    public void goToHomePage(){
        Intent i = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(i);
    }

    public void goToSignup(View view){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
